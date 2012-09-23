package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import utils.Validate

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
  def update()  = User.update(this)
  def save()    = User.save(this)
  def delete()  = User.delete(this)

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

  def validate(user: User): List[Error] = {

    var errors = List[Error]()

    // nickname
    if (Validate.isEmptyWord(user.nickname)) {
      errors ::= ValidationError("nickname", "Nickname not specified")
    } else {
      if (isDuplicate(user, "nickname")) {
        errors ::= ValidationError("nickname", "There already exists a user with the nickname '%s'".format(user.name))
      }
    }

    // name
    if (Validate.isEmptyWord(user.name)) {
      errors ::= ValidationError("name", "Name not specified")
    } else {
      if (isDuplicate(user, "name")) {
        errors ::= ValidationError("name", "There already exists a user with the name '%s'".format(user.name))
      }
    }

    // email
    if (Validate.isEmptyWord(user.email)) {
      errors ::= ValidationError("email", "User's email not specified")
    }

    errors.reverse
  }

}