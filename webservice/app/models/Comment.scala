package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.i18n.Lang

import utils.Validate

import utils.Conversion.pkToLong

import java.util.Date

import play.Logger

case class Comment (

  val id: Pk[Long] = NotAssigned,

  val idea:       Idea = Idea(),
  val author:     User = User(),
  val comment:    String = "No Comment",
  val created:    Date = new Date()
)
  extends Entity
{

  lazy val votes: VoteCounter = VoteCounter.forComment(this)

  def update()  (implicit lang: Lang) = Comment.update(this)
  def save()    (implicit lang: Lang) = Comment.save(this)
  def delete()  (implicit lang: Lang) = Comment.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"        -> pkToLong(id),
    "idea_id"   -> idea.id.getOrElse(0L),
    "user_id"   -> author.id.getOrElse(0L),
    "comment"   -> comment,
    "created"   -> created
  )
}

object Comment extends EntityCompanion[Comment] {

  val table = "comment"

  override lazy val view = """
    |comment                                    inner join 
    |idea     on comment.idea_id = idea.id      inner join 
    |user     on comment.user_id = user.id""".stripMargin

  val defaultOrder = "created"

  val filterFields = List("comment")

  val saveCommand = """
    insert into comment (
      idea_id, user_id, comment, created
    ) values (
      {idea_id}, {user_id}, {comment}, {created}
    )
  """

  val updateCommand = """
    update comment set
      idea_id   = {idea_id},
      user_id   = {user_id},
      comment   = {comment}
    where 
      id        = {id}
  """

  val simpleParser: RowParser[Comment] = {
    get[Pk[Long]]("comment.id") ~
    Idea.minParser ~
    User.simpleParser ~
    get[String]("comment.comment") ~
    get[Date]("comment.created") map {
      case id~idea~author~comment~created => Comment(
        id, idea, author, comment, created
      )
    }
  }

  def validate(comment: Comment)(implicit lang: Lang): List[Error] = {

    var errors = List[Error]()

    // Validate author foreing key.
    if (comment.author.id == NotAssigned) {
      errors ::= ValidationError("author", "Comment author not specified")
    }

    // Validate idea foreing key.
    if (comment.idea.id == NotAssigned) {
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