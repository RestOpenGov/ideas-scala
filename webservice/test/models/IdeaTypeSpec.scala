package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

import test.matchers.ErrorSpec

class IdeaTypeSpec extends Specification with ErrorSpec {

  import models.{IdeaType, Idea, Error}

  "IdeaType model" should {

    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(ideaType) = IdeaType.findById(1)

        ideaType must not be none
        ideaType.name must equalTo("idea")
        ideaType.description must equalTo("Una propuesta para mejorar la ciudad")

        IdeaType.findById(5000) must be none //equalTo(None)
      }
    }

    "retrieve a list of entities by query, filter, page, len and order" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // no params
        IdeaType.find().size must equalTo(4)

        //query
        IdeaType.find(q = "id:1").size must equalTo(1)
        IdeaType.find(q = "id:2..3").size must equalTo(2)

        //filter
        IdeaType.find(filter = "una").size must equalTo(2)

        // pagination
        IdeaType.find(page = 2, len = 3).size must equalTo(1)
        IdeaType.find(page = 3, len = 3).size must equalTo(0)

        val ordered = IdeaType.find(order = "name desc")

        ordered.size must equalTo(4)
        ordered(0).name must equalTo("recomendación")
        ordered(3).name must equalTo("idea")
      }
    }

    "allow to add, modify and delete an entity" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        IdeaType.count() must equalTo(4)

        val entity = IdeaType(
          name = "new idea's name", 
          description = "new idea's description"
        )

        // save new entity
        entity.save() should beRight

        // check new entity
        IdeaType.count() must equalTo(5)

        val newEntity = IdeaType.find(q = "name:new idea's name")(0)
        newEntity.name must equalTo("new idea's name")
        newEntity.description must equalTo("new idea's description")

        // update existing entity
        newEntity.copy(
          name = "new idea's name modified", 
          description = "new idea's description modified"
        ).update() must beRight

        val Some(modifiedEntity) = IdeaType.findById(newEntity.id.get)
        modifiedEntity.name must equalTo("new idea's name modified")
        modifiedEntity.description must equalTo("new idea's description modified")

        // check validations
        modifiedEntity.copy(name="").save() must beLeft

        // on error (emtpy name), no modification is issued
        IdeaType.findById(modifiedEntity.id.get).get must equalTo(modifiedEntity)

        // delete existing entity
        IdeaType.delete(modifiedEntity.id.get)

        IdeaType.findById(modifiedEntity.id.get) must beNone
        IdeaType.count() must equalTo(4)
      }
    }

    "check for required values" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // check update
        IdeaType.findById(1).get.copy(name="").update must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.REQUIRED)
            error.field must equalTo("name")
            error.message must contain("""campo "nombre" no puede estar vacío""")
          }
        }

        // check save
        IdeaType(name="", description="some description").save must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.REQUIRED)
            error.field must equalTo("name")
            error.message must contain("""campo "nombre" no puede estar vacío""")
          }
        }

        // // check https://groups.google.com/d/topic/specs2-users/z7Dyn9C2hwM/discussion
        // // check correct error
        // IdeaType(name="", description="some description").save must not(haveError.like {
        //   case error => {
        //     error.field must equalTo("description")
        //   }
        // })

      }
      
    }

  }

}