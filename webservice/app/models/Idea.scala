package models

import play.api.Play.current
import play.api.db._

import anorm._
import anorm.SqlParser._

import utils.Validate
import play.api.i18n.Lang

import utils.Conversion.pkToLong

import java.util.Date

import models.Error._
import exceptions.{ErrorList, ErrorListException}

import utils.Http

import play.Logger

case class Idea (

  val id: Pk[Long] = NotAssigned,

  val kind:         IdeaType = IdeaType(),
  val name:         String = "unknown idea",
  val description:  String = "no description",
  val author:       User = User(),
  val views:        Int = 0,
  val created:      Date = new Date()
)
  extends Entity
{

  lazy val votes: VoteCounter = VoteCounter.forIdea(this)

  lazy val tags: List[String] = {
    Tag.findByIdea(this).map(_.name).toList
  }

  def saveTag(tag: String)(implicit user: User, lang: Lang): Either[List[Error],List[String]] = {
    if (tags.contains(tag)) {
      Left(List(ValidationError(
        Error.DUPLICATE, "tags", "Tag '%s' is already assigned to this idea".format(tag))
      ))
    } else {
      updateTags(tags :+ tag)
    }
  }

  def deleteTag(tag: String)(implicit lang: Lang): Either[List[Error],List[String]] = {
    IdeaTag.findByIdeaAndTag(this, tag).map { ideaTag =>
      ideaTag.delete
      Right(this.tags)
    }.getOrElse {
      Left(List(ValidationError(
        Error.NOT_FOUND, "tags", "Tag '%s' is not assigned to this idea".format(tag))
      ))
    }
  }

  def updateTags(newTags: List[String])(implicit user: User, lang: Lang): Either[List[Error],List[String]] = {
    Right(List[String]())

    val currentTags = tags
    val addTags     = newTags diff currentTags
    val removeTags  = currentTags diff newTags

    // tags that first need to be created and THEN added to this idea
    val createTags = addTags.filter { tag =>
      !Tag.findByName(tag).isDefined
    }

    if(!createTags.isEmpty && !user.canCreateTags) {
      Left( List(ValidationError(PERMISSION, "tags", "you don't have enough reputation to create new tags")))
    } else {

      try {

        DB.withTransaction { implicit connection =>

          // create tags
          createTags.foreach { tag =>
            Tag(name = tag).save.fold(errors => throw ErrorList(errors), _ => ())
          }

          // add tags to idea
          addTags.foreach { tagName =>
            Tag.findByName(tagName).map { tag =>
              IdeaTag(idea = this, tag = tag).save.fold(errors => throw ErrorList(errors), _ => ())
            }.getOrElse {
              throw ErrorList(ValidationError(NOT_FOUND, "tag", "Could not find tag '%s'".format(tagName)))
            }
          }

          // remove tags from idea
          removeTags.foreach { tagName =>
            Tag.findByName(tagName).map { tag =>
              IdeaTag.findByIdeaAndTag(this, tag).map { ideaTag =>
                ideaTag.delete
              }.getOrElse {
                throw ErrorList(ValidationError(NOT_FOUND, "tag", "Could not find tag '%s' in this idea".format(tagName)))
              }
            }.getOrElse {
              throw ErrorList(ValidationError(NOT_FOUND, "tag", "Could not find tag '%s'".format(tagName)))
            }
          }
        }
        
      } catch {
        case e: ErrorListException => return Left(e.errors)
        case e => return Left(List(ValidationError(e.getMessage)))
      }

      // need a copy because tags is a lazy VAL, so it won't be recalculated
      Right(this.copy().tags)

    }

  }

  val url: String = id.map(controllers.routes.Ideas.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = Idea.update(this)
  def save()    (implicit lang: Lang) = Idea.save(this)
  def delete()  (implicit lang: Lang) = Idea.delete(this)

  def withId(newId: Long) = this.copy(id=Id(newId))
  def asSeq(): Seq[(String, Any)] = Seq(
    "id"              -> pkToLong(id),
    "idea_type_id"    -> kind.id.getOrElse(0L),
    "name"            -> name,
    "description"     -> description,
    "user_id"         -> author.id.getOrElse(0L),
    "views"           -> views,
    "created"         -> created
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

  val table = "idea"

  override lazy val view = """
    |idea                                               inner join 
    |idea_type  on idea.idea_type_id = idea_type.id     inner join 
    |user       on idea.user_id = user.id""".stripMargin

  override val tableMappings = Map("type" -> "idea_type", "author" -> "user")

  val defaultOrder = "name"

  val filterFields = List("idea.name", "idea.description")

  val saveCommand = """
    insert into idea (
      idea_type_id, name, description, user_id, views, created
    ) values (
      {idea_type_id}, {name}, {description}, {user_id}, {views}, {created}
    )
  """

  val updateCommand = """
    update idea set
      idea_type_id  = {idea_type_id},
      name          = {name},
      description   = {description},
      user_id       = {user_id},
      views         = {views}
    where 
      id        = {id}
  """

  def parser(as: String = "idea."): RowParser[Idea] = {
    get[Pk[Long]]   (as + "id") ~
    IdeaType.parser ("idea_type.") ~ 
    get[String]     (as + "name") ~
    get[String]     (as + "description") ~
    User.parser     ("user.") ~
    get[Int]        (as + "views") ~
    get[Date]       (as + "created") map {
      case id~kind~name~description~author~views~created => Idea(
        id, kind, name, description, author, views, created
      )
    }
  }

  def minParser(as: String = "idea."): RowParser[Idea] = {
    get[Pk[Long]]   (as + "id") ~
    get[String]     (as + "name") ~
    get[String]     (as + "description") ~
    get[Date]       (as + "created") map {
      case id~name~description~created => Idea(
        id = id, name = name, description = description
      )
    }
  }

  def validate(idea: Idea)(implicit lang: Lang): List[Error] = {

    var errors = List[Error]()

    // idea type, should also validate foreign key!
    if (idea.kind.id == NotAssigned) {
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
    if (idea.author.id == NotAssigned) {
      errors ::= ValidationError("user", "Author of the idea not specified")
    }

    errors.reverse
  }

  def up(id: Long)(implicit user: User) = {
    vote(id, true)
  }

  def down(id: Long)(implicit user: User) = {
    vote(id, false)
  }

  def vote(id: Long, pos: Boolean = true)(implicit user: User): Either[List[Error],Idea] = {
    user.voteIdea(id, pos)
  }

  private def byTagCondition(tag: Tag) = {
    "exists (select * from idea_tag where idea_id = idea.id and tag_id = %s)".format(tag.id.get)
  }

  def findByTag(tag: String, query: Map[String, Seq[String]]): List[Idea] = {
    val (page, len, order, filter, q) = Http.parseQuery(query)

    Tag.findByName(tag).map { tag =>
      find(page, len, order, filter, q, byTagCondition(tag))
    }.getOrElse(List[Idea]())

  }

  def countByTag(tag: String, query: Map[String, Seq[String]]): Long = {
    val (page, len, order, filter, q) = Http.parseQuery(query)

    Tag.findByName(tag).map { tag =>
      count(filter = filter, q = q, condition = byTagCondition(tag))
    }.getOrElse(0)

  }

}