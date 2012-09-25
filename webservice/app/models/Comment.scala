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

  val created:            Date = new Date(),
  val comment:            String = "No Comment",
  val author:             Int = 0,
  var idea:               Long = 0
)
  extends Entity
{
  def update()  = Comment.update(this)
  def save()    = Comment.save(this)
  def delete()  = Comment.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"                -> pkToLong(id),
    "created"           -> created,
    "comment"           -> comment,
    "author"           -> author,
    "idea"           -> idea
  )
}

object Comment extends EntityCompanion[Comment] {

  val tableName = "comment"

  val defaultOrder = "author"

  val filterFields = List("author")

  val saveCommand = """
    insert into comment (
      created, comment, user_id, idea_id
    ) values (
      {created}, {comment}, {author}, {idea}
    )
  """

  val updateCommand = """
    update comment set
      comment  = {comment},
      user_id = {author},
      idea_id = {idea}
    where 
      id        = {id}
  """

  val simpleParser: RowParser[Comment] = {
    get[Pk[Long]]("id") ~
    get[Date]("created") ~
    get[String]("comment") ~
    get[Int]("user_id") ~
    get[Int]("idea_id") map {
      case id~created~comment~author~idea => Comment(
        id, created, comment, author, idea
      )
    }
  }

  def validate(comment: Comment): List[Error] = {

    var errors = List[Error]()

    // Validate author foreing key.
    if (comment.author == 0) {
      errors ::= ValidationError("author", "Comment author not specified")
    }

    // comment
    if (Validate.isEmptyWord(comment.comment)) {
      errors ::= ValidationError("comment", "Comment not specified")
    }

    // TODO: negative  values for votes.


    errors.reverse
  }

}