package test.controllers

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

import play.api.http.HeaderNames

import play.api.libs.json.Json.parse

import test.utils.FakeJsonRequest
import test.utils.FakeRequestHelper._

import models.{IdeaType, Error}
import formatters.json.IdeaTypeFormatter._
import formatters.json.ErrorFormatter._

// import play.api.http.Status._

class UnsecuredCrudControllerSpec extends Specification {

  "Controllers using CrudActions (UnsecuredIdeaTypes)" should {

    "retrieve an ideaType by id, using route GET /api/tests/unsecured/types/:id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(result) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/1"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        val Some(ideaType) = parse(contentAsString(result)).asOpt[IdeaType]

        ideaType.name must equalTo("idea")
      }
    }

    "return an error if there's no ideaType with that id, using route GET /api/tests/unsecured/types/:id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(result) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/9999"))

        status(result) must equalTo(NOT_FOUND)
        contentType(result) must beSome("application/json")
        val Some(error) = parse(contentAsString(result)).asOpt[Error]

        error.message must equalTo("Idea_type with id 9999 not found.")
      }
    }

    "retrieve a list of ideaTypes , using route GET /api/tests/unsecured/types" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(result) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        val ideaTypes = parse(contentAsString(result)).as[List[IdeaType]]

        ideaTypes.size mustEqual 4
        ideaTypes.size mustEqual count("/api/tests/unsecured/types/count")

        val Some(resultWithSlash) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types"))
        contentAsString(result) mustEqual contentAsString(resultWithSlash)

      }
    }

    "retrieve a paginated list of ideaTypes, using route GET /api/tests/unsecured/types?page=:page&len=:len" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // check total count
        val Some(count) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/count"))
        parse(contentAsString(count)).as[Int] mustEqual 4

        // first page
        val Some(result) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?page=1&len=3"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        val ideaTypes = parse(contentAsString(result)).as[List[IdeaType]]

        ideaTypes.size must equalTo(3)

        // second page
        val Some(resultPage2) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?page=2&len=3"))
        parse(contentAsString(resultPage2)).as[List[IdeaType]].size mustEqual 1

        // third page
        val Some(resultPage3) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?page=3&len=3"))
        parse(contentAsString(resultPage3)).as[List[IdeaType]].size mustEqual 0

        val Some(resultWithSlash) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?page=1&len=3"))
        contentAsString(result) mustEqual contentAsString(resultWithSlash)
      }
    }

    "retrieve a list of ideaTypes with filter, using route GET /api/tests/unsecured/types?filter=:filter" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // first page
        val Some(result) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?filter=alg"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        val ideaTypes = parse(contentAsString(result)).as[List[IdeaType]]

        ideaTypes.size must equalTo(2)
        ideaTypes.size mustEqual count("/api/tests/unsecured/types/count?filter=alg")

        // no results
        val Some(resultNone) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?filter=no match"))
        parse(contentAsString(resultNone)).as[List[IdeaType]].size mustEqual 0

        val Some(resultWithSlash) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/?filter=alg"))
        contentAsString(result) mustEqual contentAsString(resultWithSlash)
      }
    }

    "retrieve a list of ideaTypes with query params, using route GET /api/tests/unsecured/types?q=:query" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // first page
        val Some(result) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?q=id:1..3,description$para"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        val ideaTypes = parse(contentAsString(result)).as[List[IdeaType]]

        ideaTypes.size mustEqual 2
        ideaTypes.size mustEqual count("/api/tests/unsecured/types/count?q=id:1..3,description$para")

        ideaTypes(0).id.get mustEqual 1
        ideaTypes(1).id.get mustEqual 3

        // no results
        val Some(resultNone) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?q=id:1..3,description$no match"))
        parse(contentAsString(resultNone)).as[List[IdeaType]].size mustEqual 0

        val Some(resultWithSlash) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/?q=id:1..3,description$para"))
        contentAsString(result) mustEqual contentAsString(resultWithSlash)
      }
    }

    "retrieve a list of ideaTypes specifying order, using route GET /api/tests/unsecured/types?order=:order" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(result) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?order=name"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        val ideaTypes = parse(contentAsString(result)).as[List[IdeaType]]

        ideaTypes.size mustEqual 4
        ideaTypes.size mustEqual count("/api/tests/unsecured/types/count?order=name")
        ideaTypes(0).name mustEqual "idea"
        ideaTypes(3).name mustEqual "recomendación"

        // order desc
        val Some(resultDesc) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types?order=name desc"))
        val ideaTypesDesc = parse(contentAsString(resultDesc)).as[List[IdeaType]]

        ideaTypesDesc.size mustEqual 4
        ideaTypes.size mustEqual count("/api/tests/unsecured/types/count?order=name desc")
        ideaTypesDesc(0).name mustEqual "recomendación"
        ideaTypesDesc(3).name mustEqual "idea"

        val Some(resultWithSlash) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/?order=name"))
        contentAsString(result) mustEqual contentAsString(resultWithSlash)
      }
    }

    "retrieve the count of ideaTypes, using route GET /api/tests/unsecured/types/count" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // check total count
        val Some(count) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/count"))
        parse(contentAsString(count)).as[Int] mustEqual 4

        // count paged - pagination should be ignored when using count
        val Some(countPaged) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/count?page=1&len=2"))
        parse(contentAsString(countPaged)).as[Int] mustEqual 4

        // count filtered
        val Some(countFiltered) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/count?filter=alg"))
        parse(contentAsString(countFiltered)).as[Int] mustEqual 2

        // count with query
        val Some(countQuery) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/count?q=id:3..4"))
        parse(contentAsString(countQuery)).as[Int] mustEqual 2

        val Some(countWithSlash) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/count/"))
        contentAsString(count) mustEqual contentAsString(countWithSlash)
      }
    }

    "save a new ideaType, using route POST /api/tests/unsecured/types" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val originalCount = count("/api/tests/unsecured/types/count")

        val json = """{"name": "new name", "description": "new description"}"""

        val Some(result) = routeAndCall(FakeJsonRequest(POST, "/api/tests/unsecured/types", json))

        status(result) must equalTo(CREATED)
        contentType(result) must beSome("application/json")
        val Some(ideaType) = parse(contentAsString(result)).asOpt[IdeaType]

        // check new total count
        val currentCount = count("/api/tests/unsecured/types/count") 
        currentCount mustEqual originalCount + 1

        ideaType.name mustEqual "new name"
        ideaType.description mustEqual "new description"
        ideaType.id.get mustEqual currentCount
      }
    }

    "return an error if a required field is missing, using route POST /api/tests/unsecured/types" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val originalCount = count("/api/tests/unsecured/types/count")

        val json = """{"name": "", "description": "new description"}"""

        val Some(result) = routeAndCall(FakeJsonRequest(POST, "/api/tests/unsecured/types", json))

        status(result) mustEqual BAD_REQUEST
        contentType(result) must beSome("application/json")

        val errors = parse(contentAsString(result)).as[List[Error]]
        errors.size mustEqual 1

        val error = errors(0)

        error.status mustEqual BAD_REQUEST
        error.field mustEqual "name"
        error.message must equalTo("""El campo "nombre" no puede estar vacío.""")

        // // check new total count
        count("/api/tests/unsecured/types/count") mustEqual originalCount

      }
    }

    "update an existing ideaType, using route PUT /api/tests/unsecured/types/:id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val json = """{"name": "edited name", "description": "edited description"}"""

        val Some(result) = routeAndCall(FakeJsonRequest(PUT, "/api/tests/unsecured/types/1", json))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        val Some(ideaType) = parse(contentAsString(result)).asOpt[IdeaType]
        ideaType.name must equalTo("edited name")
        ideaType.description must equalTo("edited description")

        val Some(updatedResult) = routeAndCall(FakeRequest(GET, "/api/tests/unsecured/types/1"))
        val Some(updatedIdeaType) = parse(contentAsString(updatedResult)).asOpt[IdeaType]
        updatedIdeaType.name must equalTo("edited name")
        updatedIdeaType.description must equalTo("edited description")

      }
    }

    "return an error if a required field is missing, using route PUT /api/tests/unsecured/types/:id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val json = """{"name": "", "description": "edited description"}"""

        val Some(result) = routeAndCall(FakeJsonRequest(PUT, "/api/tests/unsecured/types/1", json))

        status(result) mustEqual BAD_REQUEST
        contentType(result) must beSome("application/json")

        val errors = parse(contentAsString(result)).as[List[Error]]
        errors.size mustEqual 1

        val error = errors(0)

        error.status mustEqual BAD_REQUEST
        error.field mustEqual "name"
        error.message must equalTo("""El campo "nombre" no puede estar vacío.""")

      }
    }

    "delete an existing ideaType, using route DELETE /api/tests/unsecured/types/:id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val originalCount = count("/api/tests/unsecured/types/count")

        // create a new idea to delete
        val json = """{"name": "new name", "description": "new description"}"""

        val Some(result) = routeAndCall(FakeJsonRequest(POST, "/api/tests/unsecured/types", json))

        status(result) must equalTo(CREATED)
        contentType(result) must beSome("application/json")
        val Some(ideaType) = parse(contentAsString(result)).asOpt[IdeaType]

        count("/api/tests/unsecured/types/count") mustEqual originalCount + 1

        val id = ideaType.id.get

        val Some(deletedResult) = routeAndCall(FakeRequest(DELETE, "/api/tests/unsecured/types/%s".format(id)))

        status(deletedResult) mustEqual OK
        contentType(deletedResult) must beSome("application/json")
        val error = parse(contentAsString(deletedResult)).as[Error]

        error.status mustEqual OK
        error.field mustEqual ""
        error.errorCode mustEqual Error.NONE
        error.message must equalTo("Idea_type successfully deleted")
        error.developerMessage must equalTo("Idea_type with id 5 deleted")

        // // check new total count
        count("/api/tests/unsecured/types/count") mustEqual originalCount
      }
    }

    "return CORS headers when accessing the resource, using route ANY /api/tests/unsecured/types/*" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        todo
      }
    }

    "return CORS headers when accessing the resource, using route OPTIONS /api/tests/unsecured/types/*" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        todo
      }
    }

  }

}
