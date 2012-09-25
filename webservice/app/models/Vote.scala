package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import exceptions.VoteException

import utils.Validate

import utils.Conversion.pkToLong

import java.util.Date

import play.Logger

case class Vote (

  val id: Pk[Long] = NotAssigned,

  val voteTypeId:   Int = 0,
  val ideaId:       Int = 0,
  val commentId:    Int = 0,
  val userId:       Int = 0,
  val pos:          Boolean = true,
  val created:      Date = new Date()
)
  extends Entity
{
  def update()  = Vote.update(this)
  def save()    = Vote.save(this)
  def delete()  = Vote.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"            -> pkToLong(id),
    "voteTypeId"    -> voteTypeId,
    "ideaId"        -> ideaId,
    "commentId"     -> commentId,
    "userId"        -> userId,
    "pos"           -> pos,
    "created"       -> created
  )
}

object Vote extends EntityCompanion[Vote] {

  val tableName = "vote"

  val defaultOrder = "created desc"

  val filterFields = List()

  val saveCommand = """
    insert into vote (
      vote_type_id, idea_id, comment_id, user_id, pos, created
    ) values (
      {voteTypeId}, {ideaId}, {commentId}, {userId}, {pos}, {created}
    )
  """

  val updateCommand = """
    update vote set
      vote_type_id  = {voteTypeId},
      idea_id       = {ideaId},
      comment_id    = {commentId},
      user_id       = {userId},
      pos           = {pos}
    where 
      id            = {id}
  """

  val simpleParser: RowParser[Vote] = {
    get[Pk[Long]]("id") ~
    get[Int]("vote_type_id") ~
    get[Int]("idea_id") ~
    get[Int]("comment_id") ~
    get[Int]("user_id") ~
    get[Boolean]("pos") ~
    get[Date]("created") map {
      case id~voteTypeId~ideaId~commentId~userId~pos~created => Vote(
        id, voteTypeId, ideaId, commentId, userId, pos, created
      )
    }
  }

  def validate(vote: Vote): List[Error] = {

    var errors = List[Error]()

    // vote type, should also validate foreign key!
    if (vote.voteTypeId == 0) {
      errors ::= ValidationError("type", "Vote type not specified")
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

    errors.reverse
  }

}

case class Votes (
  val pos: Int = 0,
  val neg: Int = 0
)

object Votes {

  def forIdea(ideaId: Int): Votes = {
    if (!Idea.findById(ideaId).isDefined) 
      throw new VoteException("Could not find idea with id '%s'.".format(ideaId))

    Votes(forIdea(ideaId, true), forIdea(ideaId, false))
  }

  def forIdea(ideaId: Int, pos: Boolean): Int = {
    Vote.count(
      condition="ideaId = %s and pos = %s".
      format(ideaId, if (pos) "true" else "false")
    ).toInt
  }

  def forComment(commentId: Int): Votes = {
    if (!Comment.findById(commentId).isDefined) 
      throw new VoteException("Could not find comment with id '%s'.".format(commentId))

    Votes(forComment(commentId, true), forComment(commentId, false))
  }

  def forComment(commentId: Int, pos: Boolean): Int = {
    Vote.count(
      condition="commentId = %s and pos = %s".
      format(commentId, if (pos) "true" else "false")
    ).toInt
  }

  def forUser(userId: Int): Votes = {
    Votes(0,0)
  }

}