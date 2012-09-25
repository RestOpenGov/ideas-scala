package models


import exceptions.VoteException

case class VoteCounter (
  val pos: Int = 0,
  val neg: Int = 0
)

object VoteCounter {

  def apply(): VoteCounter = VoteCounter(0,0)
  
  def apply(votes: Tuple2[Int, Int]): VoteCounter = VoteCounter(votes._1,votes._2)

  def forIdea(idea: Idea): VoteCounter = {
    if (idea.isNew) VoteCounter() 
    else VoteCounter(Vote.countForIdea(idea.id.get))
  }

  def forComment(comment: Comment): VoteCounter = {
    if (comment.isNew) VoteCounter() 
    else VoteCounter(Vote.countForComment(comment.id.get))
  }

  def forUser(userId: Long): VoteCounter = {
    VoteCounter(0,0)
  }

}