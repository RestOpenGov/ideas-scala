package test.models

import play.api.i18n.Lang

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

import test.matchers.ErrorSpec

class VoteSpec extends Specification with ErrorSpec {

  import models.{Vote, VoteCounter, User, Idea, Comment, Error}

  "Vote" should {

    implicit val lang = Lang("en")

    "allow a user to vote up an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(user) = User.findById(4)
        val Some(idea) = Idea.findById(2)

        idea.votes must equalTo(VoteCounter(2, 0))

        Vote(idea, user, true).save() must beRight

        Idea.findById(2).get.votes must equalTo(VoteCounter(3, 0))
      }
    }

    "allow a user to vote down an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(user) = User.findById(4)
        val Some(idea) = Idea.findById(2)

        idea.votes must equalTo(VoteCounter(2, 0))

        Vote(idea, user, false).save() must beRight

        Idea.findById(2).get.votes must equalTo(VoteCounter(2, 1))
      }
    }

    "prevent the author from voting for his own idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(author) = User.findById(1)
        val Some(idea) = Idea.findById(2)

        idea.votes must equalTo(VoteCounter(2, 0))

        Vote(idea, author, true).save() must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.BUSINESS_RULE)
            error.field must equalTo("author")
            // error.message must contain("No podés votar por tu propia idea")
            error.message must contain("can't vote your own idea")
          }
        }

        // nothing was saved
        Idea.findById(2).get.votes must equalTo(VoteCounter(2, 0))
      }
    }

    "prevent the user from voting more than once for the same idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(voter) = User.findById(2)
        val Some(idea) = Idea.findById(2)

        idea.votes must equalTo(VoteCounter(2, 0))

        Vote(idea, voter, true).save() must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.BUSINESS_RULE)
            error.field must equalTo("author")
            // error.message must contain("Ya ha votado por esa idea o comentario")
            error.message must contain("already voted")
          }
        }

        // nothing was saved
        Idea.findById(2).get.votes must equalTo(VoteCounter(2, 0))
      }
    }

    "alow a user to change it's vote" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(voter) = User.findById(2)
        val Some(idea) = Idea.findById(2)

        idea.votes must equalTo(VoteCounter(2, 0))

        Vote(idea, voter, false).save() must beRight

        // the vote was saved
        Idea.findById(2).get.votes must equalTo(VoteCounter(1, 1))

        // change it back
        Vote(idea, voter, true).save() must beRight

        // the vote was saved
        Idea.findById(2).get.votes must equalTo(VoteCounter(2, 0))
      }
    }

    "allow a user to vote up a comment" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(user) = User.findById(2)
        val Some(comment) = Comment.findById(2)

        comment.votes must equalTo(VoteCounter(1, 2))

        Vote(comment, user, true).save() must beRight

        Comment.findById(2).get.votes must equalTo(VoteCounter(2, 2))
      }
    }

    "allow a user to vote down a comment" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(user) = User.findById(2)
        val Some(comment) = Comment.findById(2)

        comment.votes must equalTo(VoteCounter(1, 2))

        Vote(comment, user, false).save() must beRight

        Comment.findById(2).get.votes must equalTo(VoteCounter(1, 3))
      }
    }

    "prevent the author from voting for his own comment" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(author) = User.findById(1)
        val Some(comment) = Comment.findById(1)

        comment.votes must equalTo(VoteCounter(2, 1))

        Vote(comment, author, true).save() must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.BUSINESS_RULE)
            error.field must equalTo("author")
            // error.message must contain(" No podés votar por tu propio comentario")
            error.message must contain("can't vote your own comment")
          }
        }

        // nothing was saved
        Comment.findById(1).get.votes must equalTo(VoteCounter(2, 1))
      }
    }

    "prevent the user from voting more than once for the same comment" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(voter) = User.findById(4)
        val Some(comment) = Comment.findById(2)

        comment.votes must equalTo(VoteCounter(1, 2))

        Vote(comment, voter, true).save() must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.BUSINESS_RULE)
            error.field must equalTo("author")
            // error.message must contain("Ya ha votado por esa idea o comentario")
            error.message must contain("already voted")
          }
        }

        // nothing was saved
        Comment.findById(2).get.votes must equalTo(VoteCounter(1, 2))
      }
    }

    "alow a user to change it's vote" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(voter) = User.findById(4)
        val Some(comment) = Comment.findById(2)

        comment.votes must equalTo(VoteCounter(1, 2))

        Vote(comment, voter, false).save() must beRight

        // the vote was saved
        Comment.findById(2).get.votes must equalTo(VoteCounter(0, 3))

        // change it back
        Vote(comment, voter, true).save() must beRight

        // the vote was saved
        Comment.findById(2).get.votes must equalTo(VoteCounter(1, 2))
      }
    }

  }

}