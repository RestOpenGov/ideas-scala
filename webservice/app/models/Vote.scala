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

import notification._

case class Vote (

  val id: Pk[Long] = NotAssigned,

  val voteType:     String = "idea",
  val ideaId:       Option[Long] = None,
  val commentId:    Option[Long] = None,
  val userId:       Long = 0,
  val pos:          Boolean = true,
  val created:      Date = new Date()
)
  extends Entity
{
  val url: String = id.map(controllers.routes.Votes.show(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = Vote.update(this)
  def save()    (implicit lang: Lang) = {
    NotificationService(NewVoteNotification())
    Vote.save(this)
  }

  def delete()  (implicit lang: Lang) = Vote.delete(this)

  def withId(newId: Long) = this.copy(id=Id(newId))
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

  private def pkToOption[T](value: Pk[T]): Option[T] = {
    value.map(v => Some(v)).getOrElse(None)
  }

  def idea(id: Option[Long], user: User, pos: Boolean) = {
    Vote(
      voteType = "idea", ideaId = id,
      userId = user.id.getOrElse(0),
      pos = pos
    )
  }

  def apply(ideaEntity: Idea, user: User, pos: Boolean): Vote = {
    idea(pkToOption(ideaEntity.id), user, pos)
  }

  def comment(id: Option[Long], user: User, pos: Boolean) = {
    Vote(
      voteType = "comment", commentId = id,
      userId = user.id.getOrElse(0),
      pos = pos
    )
  }

  def apply(commentEntity: Comment, user: User, pos: Boolean): Vote = {
    comment(pkToOption(commentEntity.id), user, pos)
  }

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
      vote_type     = {voteType},
      idea_id       = {ideaId},
      comment_id    = {commentId},
      user_id       = {userId},
      pos           = {pos}
    where 
      id            = {id}
  """

  def parser(as: String = "vote."): RowParser[Vote] = {
    get[Pk[Long]]     (as + "id") ~
    get[String]       (as + "vote_type") ~
    get[Option[Long]] (as + "idea_id") ~
    get[Option[Long]] (as + "comment_id") ~
    get[Long]         (as + "user_id") ~
    get[Boolean]      (as + "pos") ~
    get[Date]         (as + "created") map {
      case id~voteType~ideaId~commentId~userId~pos~created => Vote(
        id, voteType, ideaId, commentId, userId, pos, created
      )
    }
  }

  override def save(vote: Vote)(implicit lang: Lang): Either[List[Error],Vote] = {

    val errors = validate(vote)
    if (errors.length > 0) {
      Left(errors)
    } else {
      duplicateVote(vote).map { voteToUpdate =>
        update(voteToUpdate.copy(pos=vote.pos))
      }.getOrElse {
        super.save(vote)
      }
    }
  }

  def duplicateVote(vote: Vote): Option[Vote] = {
    val votes = Vote.find(condition=alreadyVotedCondition(vote))
    if (votes.size <= 0) None 
    else Some(votes(0))
  }

  def alreadyVotedCondition(vote: Vote): String = {
    (
      "user_id = %s and ".format(vote.userId) +
      ( if (vote.ideaId.isDefined) "idea_id = %s".format(vote.ideaId.get)
        else "comment_id = %s".format(vote.commentId.get)
      ) +
      ( if (vote.id == NotAssigned) "" else " and id <> %s".format(vote.id) )
    )
  }

  def alreadyVoted(vote: Vote): Boolean = {
    (count(condition=alreadyVotedCondition(vote)) > 0)
  }

  def isChangingVoteCondition(vote: Vote): String = {
    (
      alreadyVotedCondition(vote) + 
      "and pos = " + (if (vote.pos) "true" else "false")
    )
  }

  def isChangingVote(vote: Vote): Boolean = {
    (count(condition=isChangingVoteCondition(vote)) > 0)
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
    if (!vote.ideaId.isDefined && !vote.commentId.isDefined) {
      errors ::= ValidationError("", "No idea nor comment specified")
    }

    // user, should also validate foreign key!
    if (vote.ideaId.isDefined && vote.commentId.isDefined) {
      errors ::= ValidationError("", "Idea and comment specified, you can only vote for one of them")
    }

    // user, should also validate foreign key!
    if (vote.userId == 0) {
      errors ::= ValidationError("user", "Author of the vote not specified")
    }

    // check for duplicate vote
    // if (alreadyVoted(vote)) {
    //   errors ::= ValidationError(Error.BUSINESS_RULE, "author", "You've already voted for that!")
    // }

    duplicateVote(vote).map { duplicated => 
      val entity = ( if (vote.ideaId.isDefined) "idea" else "comment" )
      if (vote.pos == duplicated.pos) {
        val up_down = ( if (vote.pos) "up" else "down" )
        errors ::= ValidationError(Error.BUSINESS_RULE, "author",
          "You've already voted %s for that %s!".format(up_down, entity))
      }
    }

    // user cannot vote for his own idea!
    vote.ideaId.map { id =>
      Idea.findById(id).map { idea =>
        if (idea.author.id.get == vote.userId) {
          if (vote.pos) errors ::= ValidationError(Error.BUSINESS_RULE, "author", "Be more humble! You can't vote your own idea")
          else          errors ::= ValidationError(Error.BUSINESS_RULE, "author", "Don't you like your own idea? You can't vote your own idea")
        }
      }
    }

    // user cannot vote for his own comment!
    vote.commentId.map { id =>
      Comment.findById(id).map { comment =>
        if (comment.author.id.get == vote.userId) {
          if (vote.pos) errors ::= ValidationError(Error.BUSINESS_RULE, "author", "Be more humble! You can't vote your own comment")
          else          errors ::= ValidationError(Error.BUSINESS_RULE, "author", "Don't you like your own comment? You can't vote your own comment")
        }
      }
    }

    errors.reverse
  }

}