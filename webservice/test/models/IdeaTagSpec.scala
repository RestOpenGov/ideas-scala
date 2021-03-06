package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

import test.matchers.ErrorSpec

class IdeaTagSpec extends Specification with ErrorSpec {

  import models.{Idea, Tag, User, Error}

  "Tag.findByIdea" should {

    "should retrieve the tags for an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val tags = Tag.findByIdea(Idea.findById(1).get)

        tags.size must equalTo(2)
        tags(0).name must equalTo("internet")
        tags(1).name must equalTo("tecnologia")
      }
    }

    "should retrieve an empty list of tags for a new idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val tags = Tag.findByIdea(Idea())

        tags.size must equalTo(0)
      }
    }

  }

  "Idea.tags" should {

    "should retrieve a list of tag names for the idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

      }
    }

    "should retrieve an empty List of tag names" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        Idea().tags must equalTo(List[String]())
      }
    }

    "should allow to bulk add and remove tags" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        implicit val Some(user) = User.findById(2)

        // initially remove all tags
        Idea.findById(1).get.updateTags(List()) must beRight
        Idea.findById(1).get.tags must equalTo(List())

        // add internet and tecnologia initial tags
        Idea.findById(1).get.updateTags(List("internet", "tecnologia")) must beRight
        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

        // remove internet, add bicisendas, keep tecnologia
        Idea.findById(1).get.updateTags(List("tecnologia", "bicisendas")) must beRight
        Idea.findById(1).get.tags must equalTo(List("bicisendas", "tecnologia"))

        // test creating a new tag
        val origCount = Tag.count

        Tag.findByName("new tag") must be none

        Idea.findById(1).get.updateTags(List("tecnologia", "bicisendas", "new tag")) must beRight
        Idea.findById(1).get.tags must equalTo(List("bicisendas", "new tag", "tecnologia"))

        Tag.count must equalTo(origCount + 1)
        Tag.findByName("new tag") must not be none
      }
    }

    "should allow to automatically create new tags when being assigned to an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        implicit val Some(user) = User.findById(2)

        val origCount = Tag.count
        Tag.findByName("new tag") must be none

        Idea.findById(1).get.updateTags(List("internet", "tecnologia", "new tag")) must beRight
        Idea.findById(1).get.tags must equalTo(List("internet", "new tag", "tecnologia"))

        Tag.count must equalTo(origCount + 1)
        Tag.findByName("new tag") must not be none
      }
    }

    "should prevent a user without enough reputation to bulk create new tags when being assigned to an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        implicit val Some(user) = User.findById(1)

        val origCount = Tag.count
        Tag.findByName("new tag") must be none
        
        Idea.findById(1).get.updateTags(List("internet", "tecnologia")) 
        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

        (
          Idea.findById(1).get.updateTags(List("internet", "tecnologia", "new tag")) 
          must haveError.like {
            case error => {
              error.errorCode must equalTo(Error.PERMISSION)
              error.field must equalTo("tags")
              error.message must contain("don't have enough reputation to create new tags")
            }
          }
        )
        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

        Tag.count must equalTo(origCount)
        Tag.findByName("new tag") must be none
      }
    }

    "should allow remove tags one by one" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // add internet and tecnologia initial tags
        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

        // remove internet
        Idea.findById(1).get.deleteTag("internet") must beRight
        Idea.findById(1).get.tags must equalTo(List("tecnologia"))

        // prevent removing a tag that is not associated with the idea

        (
          Idea.findById(1).get.deleteTag("internet") 
          must haveError.like {
            case error => {
              error.errorCode must equalTo(Error.NOT_FOUND)
              error.field must equalTo("tags")
              error.message must contain("is not assigned to this idea")
            }
          }
        )
        Idea.findById(1).get.tags must equalTo(List("tecnologia"))

      }
    }

    "should allow to add a tag one by one" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        implicit val Some(user) = User.findById(1)

        // add internet and tecnologia initial tags
        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

        // remove internet, add bicisendas, keep tecnologia
        Idea.findById(1).get.saveTag("bicisendas") must beRight
        Idea.findById(1).get.tags must equalTo(List("bicisendas", "internet", "tecnologia"))

        // duplicate tag
        (
          Idea.findById(1).get.saveTag("bicisendas")
          must haveError.like {
            case error => {
              error.errorCode must equalTo(Error.DUPLICATE)
              error.field must equalTo("tags")
              error.message must contain("is already assigned to this idea")
            }
          }
        )

      }
    }

    "should allow a user with enough reputation to create a new tag when being assigned to an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        implicit val Some(user) = User.findById(2)

        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

        val origCount = Tag.count
        Tag.findByName("new tag") must be none

        Idea.findById(1).get.saveTag("new tag") must beRight
        Idea.findById(1).get.tags must equalTo(List("internet", "new tag", "tecnologia"))

        Tag.count must equalTo(origCount + 1)
        Tag.findByName("new tag") must not be none
      }
    }

    "should prevent a user without enough reputation to create a new tag when being assigned to an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        implicit val Some(user) = User.findById(1)

        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

        val origCount = Tag.count
        Tag.findByName("new tag") must be none

        (
          Idea.findById(1).get.saveTag("new tag")
          must haveError.like {
            case error => {
              error.errorCode must equalTo(Error.PERMISSION)
              error.field must equalTo("tags")
              error.message must contain("don't have enough reputation to create new tags")
            }
          }
        )
        Idea.findById(1).get.tags must equalTo(List("internet", "tecnologia"))

        Tag.count must equalTo(origCount)
        Tag.findByName("new tag") must be none
      }
    }

  }

}
