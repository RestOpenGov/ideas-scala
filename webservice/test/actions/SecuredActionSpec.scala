package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

class SecuredActionSpec extends Specification {

  import models.{Idea, Tag, User}

  import utils.actions.SecuredAction.applicationTokenFromRequest

  "SecuredAction.applicationTokenFromRequest" should {

    "retrieve the token from the querystring" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val requestWithToken = FakeRequest(GET, "/api/auth?ideas-token=token")
        applicationTokenFromRequest(requestWithToken) mustEqual Some("token")

        val requestWithOutToken = FakeRequest(GET, "/api/auth?no-token=token")
        applicationTokenFromRequest(requestWithOutToken) must beNone

      }
    }

    "retrieve the token from the authorization header" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val requestWithToken = FakeRequest(GET, "/api/auth").
            withHeaders("authorization" -> "ideas-token=token")
        applicationTokenFromRequest(requestWithToken) mustEqual Some("token")

        // ignore whitespaces
        val requestWithToken2 = FakeRequest(GET, "/api/auth").
            withHeaders("authorization" -> "   ideas-token  =  token  ")
        applicationTokenFromRequest(requestWithToken2) mustEqual Some("token")

        val requestWithOutToken = FakeRequest(GET, "/api/auth").
            withHeaders("no-authorization" -> "ideas-token = token")
        applicationTokenFromRequest(requestWithOutToken) must beNone

        val requestWithOutToken2 = FakeRequest(GET, "/api/auth").
            withHeaders("authorization" -> "no-token = token")
        applicationTokenFromRequest(requestWithOutToken2) must beNone

        val requestWithOutToken3 = FakeRequest(GET, "/api/auth").
            withHeaders("authorization" -> "ideas-token=")
        applicationTokenFromRequest(requestWithOutToken3) must beNone

        val requestWithOutToken4 = FakeRequest(GET, "/api/auth")
        applicationTokenFromRequest(requestWithOutToken4) must beNone

      }
    }

    "retrieve the token from the querystrin only if the authorization header is not present" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val requestWithToken = FakeRequest(GET, "/api/auth?ideas-token=token_from_querystring").
            withHeaders("authorization" -> "ideas-token=token_from_header")
        applicationTokenFromRequest(requestWithToken) mustEqual Some("token_from_header")

        val requestWithToken2 = FakeRequest(GET, "/api/auth?ideas-token=token_from_querystring").
            withHeaders("authorization" -> "no-ideas-token=token_from_header")
        applicationTokenFromRequest(requestWithToken2) mustEqual Some("token_from_querystring")

      }
    }

  }

}