package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.i18n.Lang

import utils.Validate

import utils.Conversion.pkToLong

import java.util.Date

import play.Logger

case class Identity (
  id: Pk[Long] = NotAssigned,

  user:         User = User(),
  provider:     String = "",
  provider_id:  String = "",
  created:      Date   = new Date()
)
  extends Entity
{

  // val url: String = id.map(controllers.routes.SocialIdentities.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = Identity.update(this)
  def save()    (implicit lang: Lang) = Identity.save(this)
  def delete()  (implicit lang: Lang) = Identity.delete(this)

  def withId(newId: Long) = this.copy(id=Id(newId))
  def asSeq(): Seq[(String, Any)] = Seq(
    "id"          -> pkToLong(id),
    "user_id"     -> user.id.getOrElse(0L),
    "provider"    -> provider,
    "provider_id" -> provider_id,
    "created"     -> created
  )
}

object Identity extends EntityCompanion[Identity] {

  val table = "identity"

  override lazy val view = """
    |identity                                       inner join 
    |user     on identity.user_id = user.id""".stripMargin

  override val tableMappings = Map("identity" -> "identity", "user" -> "user")

  val defaultOrder = "user_id, provider"

  val filterFields = List("")

  val saveCommand = """
    insert into identity (
      user_id, provider, provider_id, created
    ) values (
      {user_id}, {provider}, {provider_id}, {created}
    )
  """

  val updateCommand = """
    update identity set
      user_id       = {user_id},
      provider      = {provider},
      provider_id   = {provider_id}
    where 
      id        = {id}
  """

  def parser(as: String = "identity."): RowParser[Identity] = {
    get[Pk[Long]]     (as + "id") ~
    User.parser       ("user.") ~
    get[String]       (as + "provider") ~
    get[String]       (as + "provider_id") ~
    get[Date]       (as + "created") map {
      case id~user~provider~provider_id~created => Identity(
        id, user, provider, provider_id, created
      )
    }
  }

  def validate(identity: Identity)(implicit lang: Lang): List[Error] = {

    import services.security.Social

    var errors = List[Error]()

    // Validate user.
    if (identity.user.id == NotAssigned) {
      errors ::= ValidationError("user", "User associated with this identity not specified")
    }

    // provider
    if (Validate.isEmptyWord(identity.provider)) {
      errors ::= ValidationError("provider", 
        "Identity provider not specified. Valid values: %s".format(Social.providers.mkString))
    } else {
      if (!Social.providers.contains(identity.provider)) {
        errors ::= ValidationError("provider", 
          "Invalid authentication provider. Valid values: %s".format(Social.providers.mkString)
        )
      }
    }

    // provider_id
    if (Validate.isEmptyWord(identity.provider_id)) {
      errors ::= ValidationError("provider_id", "User's id in specified provider not specified")
    }

    errors.reverse
  }

  def findByProviderId(provider: String, providerId: String): Option[Identity] = {

    import utils.Sql.sanitize

    val identities = Identity.find(
      condition = "provider = '%s' and provider_id = '%s'".
      format(sanitize(provider), sanitize(providerId)))
    if (identities.isEmpty) None else Some(identities(0))
  }
}