package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import utils.Validate

import utils.Conversion.pkToLong

import java.util.Date

import play.Logger

case class Comment (

  val id: Pk[Long] = NotAssigned,

  val idea:       Long = 0,
  val author:     Int = 0,
  val comment:    String = "No Comment",
  val created:    Date = new Date()
)
  extends Entity
{
  def update()  = Comment.update(this)
  def save()    = Comment.save(this)
  def delete()  = Comment.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"        -> pkToLong(id),
    "idea"      -> idea,
    "author"    -> author,
    "comment"   -> comment,
    "created"   -> created
  )
}

object Comment extends EntityCompanion[Comment] {

  val tableName = "comment"

  val defaultOrder = "created"

  val filterFields = List("comment")

  val saveCommand = """
    insert into comment (
      idea_id, user_id, comment, created
    ) values (
      {idea}, {author}, {comment}, {created}
    )
  """

  val updateCommand = """
    update comment set
      idea_id   = {idea},
      user_id   = {author},
      comment   = {comment}
    where 
      id        = {id}
  """

  val simpleParser: RowParser[Comment] = {
    get[Pk[Long]]("id") ~
    get[Int]("idea_id") ~
    get[Int]("user_id") ~
    get[String]("comment") ~
    get[Date]("created") map {
      case id~idea~author~comment~created => Comment(
        id, idea, author, comment, created
      )
    }
  }

  def validate(comment: Comment): List[Error] = {

    var errors = List[Error]()

    // Validate author foreing key.
    if (comment.author == 0) {
      errors ::= ValidationError("author", "Comment author not specified")
    }

    // Validate idea foreing key.
    if (comment.idea == 0) {
      errors ::= ValidationError("idea", "Comment idea not specified")
    }

    // comment
    if (Validate.isEmptyWord(comment.comment)) {
      errors ::= ValidationError("comment", "Comment not specified")
    }

    // TODO: negative  values for votes.
    // TODO: spam validation, have to wait 15 seconds before commenting again

    errors.reverse
  }

}