package models

import play.api.Play.current
import play.api.db._

import anorm._
import anorm.SqlParser._

import utils.Validate._
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
  val url: String = id.map(controllers.routes.IdeaTypes.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = IdeaType.update(this)
  def save()    (implicit lang: Lang) = IdeaType.save(this)
  def delete()  (implicit lang: Lang) = IdeaType.delete(this)

  def withId(newId: Long) = this.copy(id=Id(newId))
  def asSeq(): Seq[(String, Any)] = Seq(
    "id"            -> pkToLong(id),
    "name"          -> name,
    "description"   -> description
  )
}

object IdeaType extends EntityCompanion[IdeaType] {

  val table = "idea_type"

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

  def parser(as: String = "idea_type.") = {
    get[Pk[Long]]   (as + "id") ~
    get[String]     (as + "name") ~ 
    get[String]     (as + "description") map {
      case id~name~description => IdeaType(
        id, name, description
      )
    }
  }

  def validate(ideaType: IdeaType)(implicit lang: Lang): List[Error] = {

    var errors = List[Error]()

    // name
    if (Validate.isEmptyWord(ideaType.name)) {
      errors ::= ValidationError(Error.REQUIRED, "name", "validate.empty", &("ideaType.name"))
    } else {
      if (isDuplicate(ideaType, "name")) {
        errors ::= ValidationError(Error.DUPLICATE, "name", 
          "validate.duplicate", &("ideaType"), &("ideaType.name"), ideaType.name)
      }
    }

    // description
    if (Validate.isEmptyWord(ideaType.description)) {
      errors ::= ValidationError(Error.REQUIRED, "description", "validate.empty", &("ideaType.description"))
    } else {
      if (isDuplicate(ideaType, "description")) {
        errors ::= ValidationError(Error.DUPLICATE, "description", 
          "validate.duplicate", &("ideaType"), &("ideaType.description"), ideaType.description)
      }
    }

    errors.reverse
  }

}