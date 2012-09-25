package models


import exceptions.VoteException

case class VoteCounter (
  val pos: Int = 0,
  val neg: Int = 0
)

object VoteCounter {

  def forIdea(ideaId: Int): VoteCounter = {
    if (!Idea.findById(ideaId).isDefined) 
      throw new VoteException("Could not find idea with id '%s'.".format(ideaId))

    VoteCounter(forIdea(ideaId, true), forIdea(ideaId, false))
  }

  def forIdea(ideaId: Int, pos: Boolean): Int = {
    Vote.count(
      condition="ideaId = %s and pos = %s".
      format(ideaId, if (pos) "true" else "false")
    ).toInt
  }

  def forComment(commentId: Int): VoteCounter = {
    if (!Comment.findById(commentId).isDefined) 
      throw new VoteException("Could not find comment with id '%s'.".format(commentId))

    VoteCounter(forComment(commentId, true), forComment(commentId, false))
  }

  def forComment(commentId: Int, pos: Boolean): Int = {
    Vote.count(
      condition="commentId = %s and pos = %s".
      format(commentId, if (pos) "true" else "false")
    ).toInt
  }

  def forUser(userId: Int): VoteCounter = {
    VoteCounter(0,0)
  }

}