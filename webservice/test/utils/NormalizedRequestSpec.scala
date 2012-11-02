package test.controllers


import org.specs2.mutable.Specification

import formatters.json.IdeaTypeFormatter.JsonIdeaTypeFormatter
import models.IdeaType

import play.api.libs.json.Json.parse
import play.api.libs.ws.WS.WSRequestHolder

import play.api.test._
import play.api.test.Helpers._

class NormalizedRequestSpec extends Specification {

  "NormalizedRequest " should {

    def get(uri: String, token: String = "") = request("GET", uri, "", token)
    def delete(uri: String, token: String = "") = request("DELETE", uri, "", token)
    def post(uri: String, json: String = "", token: String = "") = request("POST", uri, json, token)
    def put(uri: String, json: String = "", token: String = "") = request("PUT", uri, json, token)

    def request(method: String, uri: String, json: String = "", token: String = "") = {
      val headers: Map[String, Seq[String]] = {
        Map() ++
        (if (json == "") None else Map("Content-Type" -> Seq("application/json"))) ++
        (if (token == "") Map() else Map("Authorization" -> Seq("ideas-ba=" + token)))
        // (if (token == "") None else Map("Authorization", Seq("ideas-ba=" + token)))
      }
      val req = WSRequestHolder(
        url = "http://localhost:3333/api/tests/unsecured/" + uri.stripPrefix("/"),
        headers = headers, calc = None, auth = None,
        queryString = Map()
      )

      method match {
        case "GET" => await(req.get)
        case "PUT" => await(req.put(json))
        case "POST" => await(req.post(json))
        case "DELETE" => await(req.delete)
      }
    }

    "ignore trailing slashes when issuing GET requests" in {
      running(TestServer(3333)) {

        val response        = get("/types/count/")
        val slashResponse   = get("/types/count/")

        response.status mustEqual OK
        response.body mustEqual "4"

        slashResponse.status  mustEqual response.status
        slashResponse.body    mustEqual response.body
      }
    }

    "ignore trailing slashes when issuing GET requests with querystring params" in {
      running(TestServer(3333)) {

        val response        = get("/types?q=id:1..2&order=name")
        val slashResponse   = get("/types/?q=id:1..2&order=name")

        response.status mustEqual OK

        slashResponse.status  mustEqual response.status
        slashResponse.body    mustEqual response.body
      }
    }

    "ignore trailing slashes when issuing POST requests" in {
      running(TestServer(3333)) {

        running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

          val json = """{"name": "new name", "description": "new description"}"""

          val response = post("/types", json)
          response.status mustEqual CREATED

          // delete newly created type
          val Some(firstIdeaType) = parse(response.body).asOpt[IdeaType]
          val deleteResponse = delete("/types/%s".format(firstIdeaType.id))
          deleteResponse.status mustEqual OK

          val slashResponse  = post("/types/", json)
          slashResponse.status  mustEqual CREATED

          val Some(secondIdeaType) = parse(slashResponse.body).asOpt[IdeaType]

          // can't compare responses.body because a different id has been assigned to the second ideaType
          firstIdeaType.name          mustEqual secondIdeaType.name
          firstIdeaType.description   mustEqual secondIdeaType.description

        }
      }
    }

    "ignore trailing slashes when issuing PUT requests" in {
      running(TestServer(3333)) {

        val json = """{"name": "updated name", "description": "updated description"}"""

        val response = put("/types/1", json)
        response.status mustEqual OK

        val slashResponse  = put("/types/1", json)

        slashResponse.status  mustEqual response.status
        slashResponse.body    mustEqual response.body

      }
    }


    "ignore trailing slashes when issuing DELETE requests" in {
      running(TestServer(3333)) {

        running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

          val count = get("/types/count").body.toInt

          val json = """{"name": "new name", "description": "new description"}"""

          // create a new entity
          val response = post("/types", json)

          response.status mustEqual CREATED

          get("/types/count").body.toInt mustEqual count + 1

          // delete newly created type
          val Some(firstIdeaType) = parse(response.body).asOpt[IdeaType]
          val deleteResponse = delete("/types/%s".format(firstIdeaType.id))
          deleteResponse.status mustEqual OK

          // delete successful
          get("/types/count").body.toInt mustEqual count

          // create a new entity
          val secondResponse = post("/types", json)
          secondResponse.status mustEqual CREATED

          get("/types/count").body.toInt mustEqual count + 1

          // delete newly created type
          val Some(secondIdeaType) = parse(secondResponse.body).asOpt[IdeaType]
          //delete with a trailing slash
          val slashDeleteResponse = delete("/types/%s/".format(secondIdeaType.id))
          slashDeleteResponse.status mustEqual OK

          // delete successful
          get("/types/count").body.toInt mustEqual count

        }
      }
    }

  }

}
