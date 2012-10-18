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

case class IdeaTag (

  val id: Pk[Long] = NotAssigned,

  val idea:   Idea = Idea(),
  val tag:    Tag = Tag()
)
  extends Entity
{

  //TODO
  //val url: String = id.map(controllers.routes.IdeasTags.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = IdeaTag.update(this)
  def save()    (implicit lang: Lang) = IdeaTag.save(this)
  def delete()  (implicit lang: Lang) = IdeaTag.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"          -> pkToLong(id),
    "idea_id"     -> idea.id.getOrElse(0L),
    "tag_id"      -> tag.id.getOrElse(0L)
  )
}

object IdeaTag extends EntityCompanion[IdeaTag] {

  val table = "idea_tag"

  override lazy val view = """
    |idea_tag                                inner join 
    |idea       on idea_tag.idea_id = idea.id     inner join 
    |tag        on idea_tag.tag_id = tag.id""".stripMargin

  val defaultOrder = "idea_id"

  val filterFields = List()

  val saveCommand = """
    insert into idea_tag (
      idea_id, tag_id
    ) values (
      {idea_id}, {tag_id}
    )
  """

  val updateCommand = """
    update idea set
      idea_id       = {idea_id},
      tag_id        = {tag_id}
    where 
      id        = {id}
  """

  def parser(as: String = "idea_tag."): RowParser[IdeaTag] = {
    get[Pk[Long]]   (as + "id") ~
    Idea.minParser ("idea.") ~ 
    Tag.parser ("tag.") map {
      case id~idea~tag => IdeaTag(
        id, idea, tag
      )
    }
  }

  def validate(ideaTag: IdeaTag)(implicit lang: Lang): List[Error] = {

    var errors = List[Error]()

    val(idea, tag) = (ideaTag.idea, ideaTag.tag)

    // idea, should also validate foreign key!
    if (idea.id == NotAssigned) {
      errors ::= ValidationError("dea", "Idea not specified")
    }

    // tag, should also validate foreign key!
    if (tag.id == NotAssigned) {
      errors ::= ValidationError("tag", "Tag not specified")
    }

    //check duplicates
    val condition = "idea_id = %s and tag_id = %s".
      format(idea.id.getOrElse(0L), tag.id.getOrElse(0L))

    if (isDuplicateCondition(ideaTag, condition)) {
      errors ::= ValidationError("the tag '%s' is already associated with this idea.".format(tag.name))
    }

    errors.reverse
  }

  def findByIdeaAndTag(idea: Idea, tag: Tag): Option[IdeaTag] = {
    if (!idea.id.isDefined || !tag.id.isDefined) {
      None
    } else {
      val ideaTags = IdeaTag.find(condition = "idea_id = %s and tag_id = %s".
        format(idea.id.getOrElse(0L), tag.id.getOrElse(0L))
      )
      if (ideaTags.size == 0) None
      else Some(ideaTags(0))
    }
  }

  def findByIdeaAndTag(idea: Idea, tagName: String): Option[IdeaTag] = {
    Tag.findByName(tagName).map { tag => 
      findByIdeaAndTag(idea, tag)
    }.getOrElse(None)
  }

}