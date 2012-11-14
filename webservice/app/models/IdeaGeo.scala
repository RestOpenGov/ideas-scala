package models

import play.api.Play.current
import play.api.db._

import anorm._
import anorm.SqlParser._

import utils.Validate
import play.api.i18n.Lang

import utils.Conversion.pkToLong

import java.util.Date

import play.Logger

case class IdeaGeo (

  val id: Pk[Long] = NotAssigned,

  val idea:   Idea = Idea(),
  val name:   String,
  val lat:    Double,
  val lng:    Double
)
  extends Entity
{

  //TODO
  //val url: String = id.map(controllers.routes.IdeasGeos.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = IdeaGeo.update(this)
  def save()    (implicit lang: Lang) = IdeaGeo.save(this)
  def delete()  (implicit lang: Lang) = IdeaGeo.delete(this)

  def withId(newId: Long) = this.copy(id=Id(newId))
  def asSeq(): Seq[(String, Any)] = Seq(
    "id"          -> pkToLong(id),
    "idea_id"     -> idea.id.getOrElse(0L),
    "name"        -> name,
    "lat"         -> lat,
    "lng"         -> lng
  )
}

object IdeaGeo extends EntityCompanion[IdeaGeo] {

  val table = "idea_geo"

  override lazy val view = """
    |idea_geo                                   inner join 
    |idea       on idea_geo.idea_id = idea.id   inner join
    |idea_type  on idea.idea_type_id = idea_type.id""".stripMargin

  override val tableMappings = Map("idea.type" -> "idea_type", "author" -> "user")

  val defaultOrder = "idea_id"

  val filterFields = List()

  val saveCommand = """
    insert into idea_geo (
      idea_id, name, lat, lng
    ) values (
      {idea_id}, {name}, {lat}, {lng}
    )
  """

  val updateCommand = """
    update idea_geo set
      idea_id    = {idea_id},
      name       = {name},
      lat        = {lat},
      lng        = {lng}
    where 
      id        = {id}
  """

  def parser(as: String = "idea_geo."): RowParser[IdeaGeo] = {
    get[Pk[Long]]   (as + "id") ~
    Idea.minParser ("idea.") ~ 
    get[String]     (as + "name") ~
    get[Double]     (as + "lat") ~
    get[Double]     (as + "lng") map {
      case id~idea~name~lat~lng => IdeaGeo(
        id, idea, name, lat, lng
      )
    }
  }

  def validate(ideaGeo: IdeaGeo)(implicit lang: Lang): List[Error] = {

    var errors = List[Error]()

    // idea, should also validate foreign key!
    if (ideaGeo.idea.id == NotAssigned) {
      errors ::= ValidationError("idea", "Idea not specified")
    }

    if (Validate.isEmptyWord(ideaGeo.name)) {
      errors ::= ValidationError("name", "Name not specified")
    }

    if (ideaGeo.lat == 0D) {
      errors ::= ValidationError("lat", "Idea's lattitude coordinate not specified")
    }

    if (ideaGeo.lng == 0D) {
      errors ::= ValidationError("lng", "Idea's longitude coordinate not specified")
    }

    //check duplicates
    val condition = "idea_geo.idea_id = %s and idea_geo.name = '%s'".
      format(ideaGeo.idea.id.getOrElse(0L), ideaGeo.name)

    if (isDuplicateCondition(ideaGeo, condition)) {
      errors ::= ValidationError("there already exists a geo reference for this idea with the name '%s'.".format(ideaGeo.name))
    }

    errors.reverse
  }

  def findByIdeaAndName(ideaId: Long, name: String): Option[IdeaGeo] = {
    Idea.findById(ideaId).map { idea =>
      findByIdeaAndName(idea, name)
    }.getOrElse(None)
  }

  def findByIdeaAndName(idea: Idea, name: String): Option[IdeaGeo] = {

    import utils.Sql.sanitize

    if (!idea.id.isDefined || name == "") {
      None
    } else {
      val condition = "idea_geo.idea_id = %s and idea_geo.name = '%s'".
        format(idea.id.getOrElse(0L), sanitize(name))
      val ideaGeos = IdeaGeo.find(condition = condition)
      if (ideaGeos.size == 0) None
      else Some(ideaGeos(0))
    }
  }

  def findByIdea(idea: Idea): List[IdeaGeo] = {
    idea.id.map { id =>
      val condition = "idea_geo.idea_id = %s".format(id)
      find(condition = condition, order = "idea_geo.name")
    }.getOrElse {
      List[IdeaGeo]()
    }
  }

}