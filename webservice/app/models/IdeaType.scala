package models

import play.api.Play.current
import play.api.db._

import anorm._
import anorm.SqlParser._
import play.api.Play
import play.api.i18n.Lang

import utils.Http
import utils.Validate

import utils.Sql.sanitize

import utils.Conversion.pkToLong

import play.Logger

case class IdeaType (

  val id: Pk[Long] = NotAssigned,

  val name: String = "unknown idea type",
  val description: String = "no description"
)
  extends Entity
{
  def update()  (implicit lang: Lang) = IdeaType.update(this)
  def save()    (implicit lang: Lang) = IdeaType.save(this)
  def delete()  (implicit lang: Lang) = IdeaType.delete(this)
  
  def asSeq(): Seq[(String, Any)] = Seq(
    "id"            -> pkToLong(id),
    "name"          -> name,
    "description"   -> description
  )
}

object IdeaType extends EntityCompanion[IdeaType] {

  val tableName = "idea_type"

  val defaultOrder = "name"

  val filterFields = List("name", "description")

  val saveCommand = """
    insert into idea_type (
      name, description
    ) values (
      {name}, {description}
    )
  """

  val updateCommand = """
    update idea_type set
      name        = {name},
      description = {description}
    where 
      id          = {id}
  """

  val simpleParser = {
    get[Pk[Long]]("id") ~
    get[String]("name") ~ 
    get[String]("description") map {
      case id~description~name => IdeaType(
        id, name, description
      )
    }
  }

  def validate(ideaType: IdeaType)(implicit lang: Lang): List[Error] = {

    var errors = List[Error]()
    
    // name
    if (Validate.isEmptyWord(ideaType.name)) {
      errors ::= ValidationError("name", "Name not specified")
    } else {
      if (isDuplicate(ideaType, "name")) {
        errors ::= ValidationError("name", "There already exists an idea type with the name '%s'".format(ideaType.name))
      }
    }

    // description
    if (Validate.isEmptyWord(ideaType.description)) {
      errors ::= ValidationError("description", "Description not specified")
    } else {
      if (isDuplicate(ideaType, "description")) {
        errors ::= ValidationError("description", "There already exists an idea type with the description '%s'".format(ideaType.description))
      }
    }

    errors.reverse
  }

}