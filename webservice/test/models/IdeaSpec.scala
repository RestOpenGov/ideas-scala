package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

class IdeaSpec extends Specification {

  import models.{Idea, Tag, User}

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

    "allow to bulk update tags" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        implicit val Right(user) = User.findByIdWithErr(1)

        val Some(idea) = Idea.findById(1)

        val tags = idea.tags
        tags mustEqual List("internet", "tecnología")
        val tagsCount = Tag.count

        // keep 1 tag (Internet), remove 1 tag (teconologia), add 1 tag (multas), create 2 tags(new_tag, another_new_tag)
        val updatedTagsResult = idea.updateTags( List("Internet", "new_tag", "multas", "another_new_tag") )
        updatedTagsResult must beRight

        val updatedTags = updatedTagsResult.right.get

        // it should return the updated tags
        updatedTags mustEqual List("another_new_tag", "internet", "multas", "new_tag")

        // retrieve the updated tags from the idea
        Idea.findById(1).get.tags mustEqual List("another_new_tag", "internet", "multas", "new_tag")

        // two tags must have been created
        Tag.count mustEqual tagsCount + 2

      }
    }

  }

}