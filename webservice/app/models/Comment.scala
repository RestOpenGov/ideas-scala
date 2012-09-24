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
  val positiveVote:       Int = 0,
  val negativeVote:       Int = 0,
  val author:             Int = 0
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
    "positiveVote"      -> positiveVote,
    "negativeVote"      -> negativeVote,
    "author"            -> author
  )
}

object Comment extends EntityCompanion[Comment] {

  val tableName = "comment"

  val defaultOrder = "author"

  val filterFields = List("author")

  val saveCommand = """
    insert into comment (
      created, comment, positiveVote, negativeVote, author
    ) values (
      {created}, {comment}, {positiveVote}, {negativeVote}, {author}
    )
  """

  val updateCommand = """
    update comment set
      comment  = {comment},
      positiveVote          = {positiveVote},
      negativeVote   = {negativeVote},
      author = {author}
    where 
      id        = {id}
  """

  val simpleParser: RowParser[Comment] = {
    get[Pk[Long]]("id") ~
    get[Date]("created") ~
    get[String]("comment") ~
    get[Int]("positiveVote") ~
    get[Int]("negativeVote") ~ 
    get[Int]("author") map {
      case id~created~comment~positiveVote~negativeVote~author => Comment(
        id, created, comment, positiveVote, negativeVote, author
      )
    }
  }

  def validate(comment: Comment): List[Error] = {

    Logger.info("validating errores")
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