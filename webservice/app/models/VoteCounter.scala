package models


import exceptions.VoteException

case class VoteCounter (
  val pos: Int = 0,
  val neg: Int = 0
)

object VoteCounter {

  def apply(): VoteCounter = VoteCounter(0,0)

  def forIdea(idea: Idea): VoteCounter = {
    if (idea.isNew) VoteCounter() else VoteCounter.forIdea(idea.id.get)
  }

  def forIdea(ideaId: Long): VoteCounter = {
    if (!Idea.findById(ideaId).isDefined) 
      throw new VoteException("Could not find idea with id '%s'.".format(ideaId))

    VoteCounter(forIdea(ideaId, true), forIdea(ideaId, false))
  }

  def forIdea(ideaId: Long, pos: Boolean): Int = {
    Vote.count(
      condition="idea_id = %s and pos = %s".
      format(ideaId, if (pos) "true" else "false")
    ).toInt
  }

  def forComment(comment: Comment): VoteCounter = {
    if (comment.isNew) VoteCounter() else VoteCounter.forComment(comment.id.get)
  }

  def forComment(commentId: Long): VoteCounter = {
    if (!Comment.findById(commentId).isDefined) 
      throw new VoteException("Could not find comment with id '%s'.".format(commentId))

    VoteCounter(forComment(commentId, true), forComment(commentId, false))
  }

  def forComment(commentId: Long, pos: Boolean): Int = {
    Vote.count(
      condition="comment_id = %s and pos = %s".
      format(commentId, if (pos) "true" else "false")
    ).toInt
  }

  def forUser(userId: Long): VoteCounter = {
    VoteCounter(0,0)
  }

}