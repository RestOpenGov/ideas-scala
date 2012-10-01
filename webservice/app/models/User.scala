package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._

import utils.Validate._
import play.api.i18n.{Messages, Lang}

import utils.Conversion.pkToLong

import java.util.Date

case class User (

  val id: Pk[Long] = NotAssigned,

  val nickname:     String = "unknown",
  val name:         String = "unknown user",
  val email:        String = "unknown email",
  val avatar:       String = "",
  val created:      Date = new Date()
)
  extends Entity
{
  def update()  (implicit lang: Lang) = User.update(this)
  def save()    (implicit lang: Lang) = User.save(this)
  def delete()  (implicit lang: Lang) = User.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"            -> pkToLong(id),
    "nickname"      -> nickname,
    "name"          -> name,
    "email"         -> email,
    "avatar"        -> avatar,
    "created"       -> created
  )
}

object User extends EntityCompanion[User] {

  val tableName = "user"

  val defaultOrder = "nickname"

  val filterFields = List("nickname", "name")

  val saveCommand = """
    insert into user (
      nickname, name, email, avatar, created
    ) values (
      {nickname}, {name}, {email}, {avatar}, {created}
    )
  """

  val updateCommand = """
    update user set
      nickname    = {nickname},
      name        = {name},
      email       = {email},
      avatar      = {avatar}
    where 
      id        = {id}
  """

  val simpleParser: RowParser[User] = {
    get[Pk[Long]]("id") ~
    get[String]("nickname") ~
    get[String]("name") ~
    get[String]("email") ~
    get[String]("avatar") ~
    get[Date]("created") map {
      case id~nickname~name~email~avatar~created => User(
        id, nickname, name, email, avatar, created
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

    // email
    if (isEmptyWord(user.email)) {
      errors ::= ValidationError(Error.REQUIRED, "email", "validate.empty", &("user.email"))
    } else {
      if (isDuplicate(user, "email")) {
        errors ::= ValidationError(Error.DUPLICATE, "name", 
          "validate.duplicate", &("user"), &("user.email"), user.email)
      }
    }

    errors.reverse
  }

}