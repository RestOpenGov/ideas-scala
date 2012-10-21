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

}