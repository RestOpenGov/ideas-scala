package test.models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import test.matchers.ErrorSpec

import models.Error

class SecurityManagerSpec extends Specification with ErrorSpec {

  import services.security.SecurityManager._
  import services.security.AccessToken

  "SecurityManager.createApplicationToken" should {

    "return an error if the provider is not specified in the token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createApplicationToken(AccessToken("", "some token")) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.AUTHENTICATION)
            error.field must equalTo("provider")
            error.message must contain("provider not specified")
          }
        }

        createApplicationToken(AccessToken("invalid_provider", "some token")) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.AUTHENTICATION)
            error.field must equalTo("provider")
            error.message must contain("Invalid authentication provider")
          }
        }

      }
    }

    "return an error if the token is not specified" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createApplicationToken(AccessToken("twitter", "")) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.AUTHENTICATION)
            error.field must equalTo("accessToken")
            error.message must contain("token not specified")
          }
        }

      }
    }

    "return an error if the user already exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

    "retrieve a valid token if everyting is ok" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

  }

  "SecurityManager.retrieveProviderInfo" should {

    "return an error if the provider is not specified in the token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        retrieveProviderInfo(AccessToken("", "some token")) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.AUTHENTICATION)
            error.field must equalTo("provider")
            error.message must contain("provider not specified")
          }
        }

        retrieveProviderInfo(AccessToken("invalid_provider", "some token")) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.AUTHENTICATION)
            error.field must equalTo("provider")
            error.message must contain("Invalid authentication provider")
          }
        }

      }
    }

    "return an error if the token is not specified" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        retrieveProviderInfo(AccessToken("twitter", "")) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.AUTHENTICATION)
            error.field must equalTo("accessToken")
            error.message must contain("token not specified")
          }
        }

      }
    }

  }

  "SecurityManager.findUserByApplicationToken" should {

    "return an error there's no user with that application token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

    "return an error if the application token has expired" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

    "return the user associated with that application token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

  }

}