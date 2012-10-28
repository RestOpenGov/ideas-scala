package test.services.security

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import test.matchers.ErrorSpec

import exceptions.SocialConnectorException
import models.Error

class SocialConnectorSpec extends Specification with ErrorSpec {

  import services.security.Social._
  import services.security.AccessToken

  "SocialConnector.retrieveSocialProviderInfo" should {

    "return an error if the provider is not supported" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        retrieveSocialProviderInfo(AccessToken("unsupported_provider", "some token")
        ) must throwA[SocialConnectorException].like { 
          case e => e.getMessage must matching(""".*Invalid social identity provider specified.*""")
        }

      }
    }

    "return None if an invalid token is passed" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        retrieveSocialProviderInfo(AccessToken("twitter", "invalid token")
        ) must beNone

      }
    }

    "return Some IdentityProviderInfo if a valid token is passed" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        implicit val socialAdapter = List(MockTwitterAdapter, MockFacebookAdapter)

        retrieveSocialProviderInfo(AccessToken("facebook", "valid mock facebook token")
        ) must beSome

      }
    }

  }

}