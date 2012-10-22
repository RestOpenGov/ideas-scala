package test.models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import test.matchers.ErrorSpec

import models.Error

class UserSecuritySpec extends Specification with ErrorSpec {

  import models.User
  import models.User._

  "User.findByProviderId" should {

    "return the user using the social provider id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        findByProviderId("twitter", "twitter_id_for_user_1") should beSome[User].like {
          case user => user.id.get must equalTo(1)
        }

        findByProviderId("facebook", "facebook_id_for_user_1") should beSome[User].like {
          case user => user.id.get must equalTo(1)
        }

        findByProviderId("facebook", "token_not_found") should beNone

      }
    }

  }

  "User.findOrCreateFromProviderInfo" should {

    "return the user if it already exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

    "create the user if it doesn't exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

    "return an error if i has to create a user but another user with the same nickname exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

  }

  "User.createFromProviderInfo" should {

    "create the user and also the identity" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

  }

  "User.refreshApplicationToken" should {

    "generate a new application token and save it to the user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

  }

  "User.save" should {

    "create a new user with a fresh application token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        // TODO
        1 must equalTo(1)
      }
    }

  }

}