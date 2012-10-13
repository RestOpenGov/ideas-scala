package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

class VoteCounterSpec extends Specification {

  import models.VoteCounter
  import models.Idea
  import models.Comment

  "Idea model VoteCounter" should {

    "retrieve the votes for an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val Some(idea) = Idea.findById(1)

        idea must not be none

        idea.votes.pos must equalTo(2)
        idea.votes.neg must equalTo(1)
      }
    }

  }

  "Comment model VoteCounter" should {

    "retrieve the votes for a comment" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val Some(comment) = Comment.findById(1)

        comment must not be none
        comment.votes.pos must equalTo(2)
        comment.votes.neg must equalTo(1)
      }
    }

  }

}