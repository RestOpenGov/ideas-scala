package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._

import utils.Validate._
import utils.Sql.sanitize
import play.api.i18n.{Messages, Lang}

import utils.Conversion.pkToLong

import java.util.Date

import services.security.{ApplicationToken, IdentityProviderInfo}

case class User (

  val id: Pk[Long] = NotAssigned,

  val nickname:         String = "unknown",
  val name:             String = "unknown user",
  val email:            String = "unknown email",
  val avatar:           String = "",
  val applicationToken: String = "",          // token issued by Ideas app (that's us!)
  val tokenExpiration:  Date = new Date(),
  val created:          Date = new Date()
)
  extends Entity
{
  val url: String = id.map(controllers.routes.Users.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = User.update(this)
  def save()    (implicit lang: Lang) = User.save(this)
  def delete()  (implicit lang: Lang) = User.delete(this)

  def withId(newId: Long) = this.copy(id=Id(newId))
  def asSeq(): Seq[(String, Any)] = Seq(
    "id"                -> pkToLong(id),
    "nickname"          -> nickname,
    "name"              -> name,
    "email"             -> email,
    "avatar"            -> avatar,
    "applicationToken"  -> applicationToken,
    "tokenExpiration"   -> tokenExpiration,
    "created"           -> created
  )

  // TODO: define a strategy to allow users to create new tags
  def canCreateTags: Boolean = id match {
    // hardcoded for testing, should use mocking library instead
    case Id(1) => true
    case _ => false
  }

  def upIdea(id: Long) = voteIdea(id, true)
  def downIdea(id: Long) = voteIdea(id, false)

  def voteIdea(ideaId: Long, pos: Boolean = true): Either[List[Error],Idea] = {
    Vote.idea(Some(ideaId), this, pos).save.fold(
      errors => Left(errors),
      vote => Idea.findById(ideaId).map { idea =>
        Right(idea)
      }.getOrElse(
        Left(List(ValidationError(
          "Could not find idea with id '%s'".format(ideaId)
        )))
      )
    )
  }

  def voteComment(commentId: Long, pos: Boolean = true): Either[List[Error], Comment] = {
    Vote.comment(Some(commentId), this, pos).save.fold(
      errors => Left(errors),
      vote => Comment.findById(commentId).map { comment =>
        Right(comment)
      }.getOrElse(
        Left(List(ValidationError(
          "Could not find comment with id '%s'".format(commentId)
        )))
      )
    )
  }

  def token: ApplicationToken = {
    ApplicationToken(this.applicationToken, this.tokenExpiration)
  }

  def withNewApplicationToken: User = {
    val newToken = ApplicationToken.refresh
    this.copy(
      applicationToken  = newToken.token,
      tokenExpiration   = newToken.expiration
    )
  }

  def refreshApplicationToken: Either[List[Error], User] = {
    User.saveOrUpdate(this.withNewApplicationToken)
  }

}

object User extends EntityCompanion[User] {

  val table = "user"

  val defaultOrder = "nickname"

  val filterFields = List("nickname", "name")

  val saveCommand = """
    insert into user (
      nickname, name, email, avatar, application_token, token_expiration, created
    ) values (
      {nickname}, {name}, {email}, {avatar}, {applicationToken}, {tokenExpiration}, {created}
    )
  """

  val updateCommand = """
    update user set
      nickname          = {nickname},
      name              = {name},
      email             = {email},
      avatar            = {avatar},
      application_token = {applicationToken},
      token_expiration  = {tokenExpiration}
    where 
      id        = {id}
  """

  def parser(as: String = "user."): RowParser[User] = {
    get[Pk[Long]]   (as + "id") ~
    get[String]     (as + "nickname") ~
    get[String]     (as + "name") ~
    get[String]     (as + "email") ~
    get[String]     (as + "avatar") ~
    get[String]     (as + "application_token") ~
    get[Date]       (as + "token_expiration") ~
    get[Date]       (as + "created") map {
      case id~nickname~name~email~avatar~applicationToken~tokenExpiration~created => User(
        id, nickname, name, email, avatar, applicationToken, tokenExpiration, created
      )
    }
  }

  def validate(user: User)(implicit lang: Lang): List[Error] = {

    var errors = List[Error]()

    // nickname
    if (isEmptyWord(user.nickname)) {
      errors ::= ValidationError(Error.REQUIRED, "nickname", "validate.empty", &("user.nickname"))
    } else {
      if (isDuplicate(user, "nickname")) {
        errors ::= ValidationError(Error.DUPLICATE, "nickname", 
          "validate.duplicate", &("user"), &("user.nickname"), user.nickname)
      }
    }

    // name
    if (isEmptyWord(user.name)) {
      errors ::= ValidationError(Error.REQUIRED, "name", "validate.empty", &("user.name"))
    } else {
      if (isDuplicate(user, "name")) {
        errors ::= ValidationError(Error.DUPLICATE, "name", 
          "validate.duplicate", &("user"), &("user.name"), user.name)
      }
    }

    // email can NOT be required
    // because twitter does NOT provide the email address
    // if (isEmptyWord(user.email)) {
    //   errors ::= ValidationError(Error.REQUIRED, "email", "validate.empty", &("user.email"))
    // } else {
    //   if (isDuplicate(user, "email")) {
    //     errors ::= ValidationError(Error.DUPLICATE, "name", 
    //       "validate.duplicate", &("user"), &("user.email"), user.email)
    //   }
    // }

    errors.reverse
  }

  def findByApplicationToken(applicationToken: String): Option[User] = {
    if (isEmptyWord(applicationToken)) None
    else {
      val users = User.find(condition = "application_token = '%s'".format(sanitize(applicationToken)))
      if (users.isEmpty) None else Some(users(0))
    }
  }

  def findByApplicationTokenWithErr(applicationToken: String): Either[Error, User] = {
    if (isEmptyWord(applicationToken)) {
      Left(ValidationError("No token specified"))
    } else {
      findByApplicationToken(applicationToken).map { entity =>
        Right(entity)
      } getOrElse {
        Left(ValidationError("User with token %s not found".format(applicationToken)))
      }
    }
  }

  def findByProviderId(provider: String, providerId: String): Option[User] = {
    val condition = """
    |exists( 
      |select * from identity where 
      |user.id = identity.user_id and
      |identity.provider = '%s' and
      |identity.provider_id = '%s'
    |)""".stripMargin.format(sanitize(provider), sanitize(providerId))

    val users = User.find(condition = condition)
    if (users.isEmpty) None else Some(users(0))
  }

  // create a new user from the information of the Social provider (twitter | facebook)
  // and also saves this information as an Identity
  def createFromProviderInfo(info: IdentityProviderInfo): Either[List[Error], User] = {

    import exceptions.{ErrorListException, ErrorList}

    try {

      DB.withTransaction { implicit connection =>

        User(
          nickname          = info.nickname,
          name              = info.name,
          email             = info.email,
          avatar            = info.avatar
        ).save.fold(
          errors    => throw ErrorList(errors),
          user      => {
            Identity(
              user        = user,
              provider    = info.provider,
              provider_id = info.id
            ).save.fold(
              errors    => throw ErrorList(errors),
              identity  => Right(user)
            )
          }
        )

      }
    } catch {
      case e: ErrorListException => return Left(e.errors)
      case e => return Left(List(ValidationError(e.getMessage)))
    }
  }

  def findOrCreateFromProviderInfo(info: IdentityProviderInfo): Either[List[Error], User] = {
    findByProviderId(info.provider, info.id).map { user =>
      Right(user)
    }.getOrElse {
      createFromProviderInfo(info)
    }
  }

  // before saving a new user, I create a new the token
  override def save(user: User)(implicit lang: Lang): Either[List[Error],User] = {
    val userToSave = if (user.isNew && user.applicationToken == "") {
      user.withNewApplicationToken
    } else {
      user
    }
    super.save(userToSave)
  }

}