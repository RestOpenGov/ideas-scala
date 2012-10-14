package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

class IdeaTagSpec extends org.specs2.mutable.Specification {

  import models.{Idea, Tag}

  "Tag.findByIdea" should {

    "should retrieve the tags for an idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val tags = Tag.findByIdea(Idea.findById(1).get)

        tags.size must equalTo(2)
        tags(0).name must equalTo("internet")
        tags(1).name must equalTo("tecnología")
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

    "should retrieve a comma separated list of tags for the idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        Idea.findById(1).get.tags must equalTo("internet, tecnología")
      }
    }

    "should retrieve an empty string as the list of tags for a new idea" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        Idea().tags must equalTo("")
      }
    }

  }

}