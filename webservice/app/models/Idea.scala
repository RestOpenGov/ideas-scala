package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import utils.Validate

import utils.Conversion.pkToLong

import java.util.Date

import play.Logger

case class Idea (

  val id: Pk[Long] = NotAssigned,

  val ideaTypeId:   Int = 0,
  val name:         String = "unknown idea",
  val description:  String = "no description",
  val userId:       Int = 0,
  val views:        Int = 0,
  val created:      Date = new Date()
)
  extends Entity
{

  lazy val votes: VoteCounter = VoteCounter.forIdea(this)

  def update()  = Idea.update(this)
  def save()    = Idea.save(this)
  def delete()  = Idea.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"            -> pkToLong(id),
    "ideaTypeId"    -> ideaTypeId,
    "name"          -> name,
    "description"   -> description,
    "userId"        -> userId,
    "views"         -> views,
    "created"       -> created
  )
}

object Idea extends EntityCompanion[Idea] {

  override def findById(id: Long): Option[Idea] = {
    super.findById(id).map { idea => 
      idea.copy(views = idea.views+1).update.fold(
        errors => None,
        idea => Option(idea)
      )
    }.getOrElse(None)
  }

  val tableName = "idea"

  val defaultOrder = "name"

  val filterFields = List("name", "description")

  val saveCommand = """
    insert into idea (
      idea_type_id, name, description, user_id, views, created
    ) values (
      {ideaTypeId}, {name}, {description}, {userId}, {views}, {created}
    )
  """

  val updateCommand = """
    update idea set
      idea_type_id  = {ideaTypeId},
      name          = {name},
      description   = {description},
      user_id       = {userId},
      views         = {views}
    where 
      id        = {id}
  """

  val simpleParser: RowParser[Idea] = {
    get[Pk[Long]]("id") ~
    get[Int]("idea_type_id") ~
    get[String]("name") ~
    get[String]("description") ~
    get[Int]("user_id") ~
    get[Int]("views") ~
    get[Date]("created") map {
      case id~ideaTypeId~name~description~userId~views~created => Idea(
        id, ideaTypeId, name, description, userId, views, created
      )
    }
  }

  def validate(idea: Idea): List[Error] = {

    var errors = List[Error]()

    // idea type, should also validate foreign key!
    if (idea.ideaTypeId == 0) {
      errors ::= ValidationError("type", "Idea type not specified")
    }

    // name
    if (Validate.isEmptyWord(idea.name)) {
      errors ::= ValidationError("name", "Name not specified")
    } else {
      if (isDuplicate(idea, "name")) {
        errors ::= ValidationError("name", "There already exists an idea with the name '%s'".format(idea.name))
      }
    }

    // description
    if (Validate.isEmptyWord(idea.description)) {
      errors ::= ValidationError("description", "Idea's description not specified")
    }

    // user, should also validate foreign key!
    if (idea.userId == 0) {
      errors ::= ValidationError("user", "Author of the idea not specified")
    }

    errors.reverse
  }

}