package test.controllers

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

import play.api.libs.json.Json.parse

import models.IdeaType
import formatters.json.IdeaTypeFormatter._

class IdeaTypesSpec extends Specification {

  "IdeaTypes controllers" should {

    "retrieve an ideaType by id" in {

      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(result) = routeAndCall(FakeRequest(GET, "/api/types/1"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
        val Some(ideaType) = parse(contentAsString(result)).asOpt[IdeaType]

        ideaType.name must equalTo("idea")
      }
    }
  }

}