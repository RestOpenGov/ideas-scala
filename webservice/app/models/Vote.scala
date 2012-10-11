package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import utils.Validate

import utils.Conversion.pkToLong

import java.util.Date

import play.api.i18n.Lang

import play.Logger

case class Vote (

  val id: Pk[Long] = NotAssigned,

  val voteType:     String = "idea",
  val ideaId:       Int = 0,
  val commentId:    Int = 0,
  val userId:       Int = 0,
  val pos:          Boolean = true,
  val created:      Date = new Date()
)
  extends Entity
{
  val url: String = id.map(controllers.routes.Votes.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = Vote.update(this)
  def save()    (implicit lang: Lang) = Vote.save(this)
  def delete()  (implicit lang: Lang) = Vote.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"            -> pkToLong(id),
    "voteType"      -> voteType,
    "ideaId"        -> ideaId,
    "commentId"     -> commentId,
    "userId"        -> userId,
    "pos"           -> pos,
    "created"       -> created
  )
}

object Vote extends EntityCompanion[Vote] {

  def countForIdea(ideaId: Long): Tuple2[Int, Int] = {
    val cond = "idea_id = %s and pos = ".format(ideaId)
    (
      Vote.count(condition=cond + "true").toInt,
      Vote.count(condition=cond + "false").toInt
    )
  }

  def countForComment(commentId: Long): Tuple2[Int, Int] = {
    val cond = "comment_id = %s and pos = ".format(commentId)
    (
      Vote.count(condition=cond + "true").toInt,
      Vote.count(condition=cond + "false").toInt
    )
  }

  val table = "vote"

  val defaultOrder = "created desc"

  val filterFields = List()

  val saveCommand = """
    insert into vote (
      vote_type, idea_id, comment_id, user_id, pos, created
    ) values (
      {voteType}, {ideaId}, {commentId}, {userId}, {pos}, {created}
    )
  """

  val updateCommand = """
    update vote set
      vote_typed    = {voteType},
      idea_id       = {ideaId},
      comment_id    = {commentId},
      user_id       = {userId},
      pos           = {pos}
    where 
      id            = {id}
  """

  def parser(as: String = "vote."): RowParser[Vote] = {
    get[Pk[Long]]   (as + "id") ~
    get[String]     (as + "vote_type") ~
    get[Int]        (as + "idea_id") ~
    get[Int]        (as + "comment_id") ~
    get[Int]        (as + "user_id") ~
    get[Boolean]    (as + "pos") ~
    get[Date]       (as + "created") map {
      case id~voteType~ideaId~commentId~userId~pos~created => Vote(
        id, voteType, ideaId, commentId, userId, pos, created
      )
    }
  }

  def validate(vote: Vote)(implicit lang: Lang): List[Error] = {

    import utils.Comparison.implicits._

    var errors = List[Error]()

    // vote type, should also validate foreign key!
    if (Validate.isEmptyWord(vote.voteType)) {
      errors ::= ValidationError("type", "Vote type not specified")
    } else {
      if (!vote.voteType.toLowerCase.isOneOf("idea", "comment")) {
        errors ::= ValidationError("type", "Invalid vote type specified. Valid values: idea, comment")
      }
    }

    // user, should also validate foreign key!
    if (vote.ideaId == 0 && vote.commentId == 0) {
      errors ::= ValidationError("", "No idea nor comment specified")
    }

    // user, should also validate foreign key!
    if (vote.ideaId != 0 && vote.commentId != 0) {
      errors ::= ValidationError("", "Idea and comment specified, you can only vote for one of them")
    }

    // user, should also validate foreign key!
    if (vote.userId == 0) {
      errors ::= ValidationError("user", "Author of the vote not specified")
    }

    // check for duplicate vote
    val duplicateVoteCondition = (
      "user_id = %s and ".format(vote.userId) +
      ( if (vote.ideaId != 0) "idea_id = %s".format(vote.ideaId)
        else "comment_id = %s".format(vote.commentId)
      ) +
      ( if (vote.id == NotAssigned) "" else " and id <> %s".format(vote.id) )
    )
    if (count(condition=duplicateVoteCondition) > 0) {
      errors ::= ValidationError("", "You've already voted for that!")
    }

    // user cannot vote for his own idea!
    if (vote.ideaId!=0) {
      Idea.findById(vote.ideaId).map { idea =>
        if (idea.author.id.get == vote.userId) {
          if (vote.pos) errors ::= ValidationError("", "Be more humble! You can't vote your own idea")
          else          errors ::= ValidationError("", "Don't you like your own idea? You can't vote your own idea")
        }
      }
    }

    // user cannot vote for his own comment!
    if (vote.commentId!=0) {
      Comment.findById(vote.ideaId).map { comment =>
        if (comment.author==vote.userId) {
          if (vote.pos) errors ::= ValidationError("", "Be more humble! You can't vote your own comment")
          else          errors ::= ValidationError("", "Don't you like your own comment? You can't vote your own comment")
        }
      }
    }

    errors.reverse
  }

}