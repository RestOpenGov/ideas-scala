package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

import test.matchers.ErrorSpec

class TagSpec extends Specification with ErrorSpec {

  import models.{Tag, Idea, Error}

  "Tag model" should {

    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(tag) = Tag.findById(1)

        tag must not be none
        tag.name must equalTo("tecnología")
        tag.description must equalTo("Informática, tecnología y nuevas tendencias")

        Tag.findById(5000) must be none //equalTo(None)
      }
    }

    "retrieve a list of entities by query, filter, page, len and order" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // no params
        Tag.find().size must equalTo(9)

        //query
        Tag.find(q = "id:1").size must equalTo(1)
        Tag.find(q = "id:2..3").size must equalTo(2)

        //filter
        Tag.find(filter = "ciudad").size must equalTo(3)

        // pagination
        Tag.find(page = 3, len = 3).size must equalTo(3)
        Tag.find(page = 4, len = 3).size must equalTo(0)

        val ordered = Tag.find(order = "name desc")

        ordered.size must equalTo(9)
        ordered(0).name must equalTo("trámites")
        ordered(6).name must equalTo("cultura")
      }
    }

    "allow to add, modify and delete an entity" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val originalCount = Tag.count

        val entity = Tag(
          name = "new entity's name", 
          description = "new entity's description"
        )

        // save new entity
        entity.save() should beRight

        // check new entity
        Tag.count must equalTo(originalCount+1)

        val newEntity = Tag.find(q = "name:new entity's name")(0)
        newEntity.name must equalTo("new entity's name")
        newEntity.description must equalTo("new entity's description")

        // update existing entity
        newEntity.copy(
          name = "new entity's name modified", 
          description = "new entity's description modified"
        ).update() must beRight

        val Some(modifiedEntity) = Tag.findById(newEntity.id.get)
        modifiedEntity.name must equalTo("new entity's name modified")
        modifiedEntity.description must equalTo("new entity's description modified")

        // check validations
        modifiedEntity.copy(name="").save() must beLeft

        // on error (emtpy name), no modification is issued
        Tag.findById(modifiedEntity.id.get).get must equalTo(modifiedEntity)

        // delete existing entity
        Tag.delete(modifiedEntity.id.get)

        Tag.findById(modifiedEntity.id.get) must beNone
        Tag.count must equalTo(originalCount)
      }
    }

    "check for required values" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // check update
        Tag.findById(1).get.copy(name="").update must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.REQUIRED)
            error.field must equalTo("name")
            error.message must contain("""campo "nombre" no puede estar vacío""")
          }
        }

        // check save
        Tag(name="", description="some description").save must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.REQUIRED)
            error.field must equalTo("name")
            error.message must contain("""campo "nombre" no puede estar vacío""")
          }
        }

        // // check https://groups.google.com/d/topic/specs2-users/z7Dyn9C2hwM/discussion
        // // check correct error
        // Tag(name="", description="some description").save must not(haveError.like {
        //   case error => {
        //     error.field must equalTo("description")
        //   }
        // })

      }

    }

    "check for duplicate description only if it's not empty" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val originalCount = Tag.count

        // action: create, description: NOT EMPTY
        // if it's not empty check for duplicate values
        Tag(name = "new tag's name", description = "new tag's description").save must beRight

        Tag.count must equalTo(originalCount + 1)

        // error if trying to CREATE a new tag with duplicate description
        Tag(name = "duplicated tag's name", description = "new tag's description").save must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.DUPLICATE)
            error.field must equalTo("description")
            error.message must contain("""Ya existe un tag con el campo descripción""")
          }
        }

        // create another tag
        Tag(name = "another tag's name", description = "another tag's description").save must beRight

        Tag.count must equalTo(originalCount + 2)

        val anotherTag = Tag.find(q = "name=another tag's name")(0)

        // action: update, description: NOT EMPTY
        // error if trying to update a new tag with duplicate description
        anotherTag.copy(description = "new tag's description").update must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.DUPLICATE)
            error.field must equalTo("description")
            error.message must contain("""Ya existe un tag con el campo descripción""")
          }
        }

        // remove another tag
        anotherTag.delete
        Tag.count must equalTo(originalCount + 1)

        // modify original new tag, set description empty
        val newTag = Tag.find(q = "name=new tag's name")(0)

        newTag.copy(description = "").update must beRight

        // action: create, description: EMPTY
        // if it IS empty, don't check for duplicates values
        Tag(name = "another tag's name", description = "").save must beRight
        Tag.count must equalTo(originalCount + 2)

        val anotherEmptyTag = Tag.find(q = "name=another tag's name")(0)

        // action: update, description: EMPTY
        anotherEmptyTag.copy(description = "another tag's description").update must beRight
        anotherEmptyTag.copy(description = "").update must beRight

        // remove created tag
        anotherEmptyTag.delete
        Tag.count must equalTo(originalCount + 1)

        newTag.delete
        Tag.count must equalTo(originalCount)

      }

    }

  }

}