package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

class IdeaSpec extends org.specs2.mutable.Specification {

  import models.Idea

  "Idea model" should {

    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val Some(idea) = Idea.findById(1)

        idea must not be none
        idea.name must equalTo("Wifi libre en ba")
        idea.description must equalTo("Proveer acceso wifi gratuito en toda la ciudad")

        val noneIdea: Option[Idea] = Idea.findById(5000)
        noneIdea must be none //equalTo(None)
      }
    }

    "update views counter when retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val ideas: List[Idea] = Idea.find(q = "id:1")
        ideas.size must equalTo(1)

        val originalViews = ideas(0).views

        originalViews must equalTo(320)

        Idea.findById(1).get.views must equalTo(originalViews+1)

        // fetching with find does NOT update counter
        Idea.find(q = "id:1")(0).views must equalTo(originalViews+1)

        Idea.findById(1).get.views must equalTo(originalViews+2)
        Idea.findById(1).get.views must equalTo(originalViews+3)
        Idea.findById(1).get.views must equalTo(originalViews+4)

        Idea.find(q = "id:1")(0).views must equalTo(originalViews+4)
      }
    }

  }

}