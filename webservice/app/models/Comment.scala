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
import notification._

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

  val url: String = id.map(controllers.routes.Comments.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = Comment.update(this)
  
  def save()    (implicit lang: Lang) = {
    val result = Comment.save(this)

    result match {
      case Right(comment) => {
            val ideaid = comment.idea.id.get
            val commentText = comment.comment
            val authorName = comment.author.name
            val authorAvatar = comment.author.avatar
            Logger.debug("Save Succesful, so we send a Notification for idea: " + ideaid)        
            NotificationService(NewCommentNotification(ideaid, authorName, commentText, authorAvatar))
      }
      case Left(l) => {
        Logger.debug("There was an error saving a comment, so we do NOT send a notification")
      }
    }

    result
  }
  
  def delete()  (implicit lang: Lang) = Comment.delete(this)

  def withId(newId: Long) = this.copy(id=Id(newId))
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

  override val tableMappings = Map("idea" -> "idea", "author" -> "user")

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

  def parser(as: String = "comment."): RowParser[Comment] = {
    get[Pk[Long]]     (as + "id") ~
    Idea.minParser    ("idea.") ~
    User.parser       ("user.") ~
    get[String]       (as + "comment") ~
    get[Date]         (as + "created") map {
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

  def up(id: Long)(implicit user: User) = {
    vote(id, true)
  }

  def down(id: Long)(implicit user: User) = {
    vote(id, false)
  }

  def vote(id: Long, pos: Boolean = true)(implicit user: User): Either[List[Error],Comment] = {
    user.voteComment(id, pos)
  }

  def findCommentByIdea(idea: Long) : List[Comment] = {
    DB.withConnection { implicit c => {
      val query = 
          """
          SELECT * FROM comment, idea, user  WHERE comment.idea_id = {idea}
          AND comment.user_id = user.id AND comment.idea_id = idea.id 
          """

         SQL(query.stripMargin).on("idea" -> idea).as(Comment.parser() *) 
      }  
    } 
  }



}