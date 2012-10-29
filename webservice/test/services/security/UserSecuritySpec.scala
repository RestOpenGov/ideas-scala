package test.services.security

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import test.matchers.ErrorSpec

import models.Error

class UserSecuritySpec extends Specification with ErrorSpec {

  import models.{User, Identity}
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

    import services.security.Social.retrieveSocialProviderInfo
    import services.security.AccessToken

    implicit val socialAdapter = List(MockTwitterAdapter, MockFacebookAdapter)
    val accessToken = AccessToken("twitter", "valid mock twitter token")
    val Some(providerInfo) = retrieveSocialProviderInfo(accessToken)

    "return the user if it already exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val beforeUsers = User.count()
        val beforeIdentities = Identity.count()

        val Right(originalUser) = User.findOrCreateFromProviderInfo(providerInfo)

        val originalUsers = User.count()
        val originalIdentities = Identity.count()

        // a user and an identity were created
        originalUsers must equalTo(beforeUsers + 1)
        originalIdentities must equalTo(beforeIdentities + 1)

        val Right(existingUser) = User.findOrCreateFromProviderInfo(providerInfo)
        existingUser.id.get must equalTo(originalUser.id.get)

        // no user nor identity were created
        User.count() must equalTo(originalUsers)
        Identity.count() must equalTo(originalIdentities)

      }
    }

    "create the user if it doesn't exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val usersBefore = User.count()
        val identitiesBefore = Identity.count()

        val Right(newUser) = User.findOrCreateFromProviderInfo(providerInfo)
        newUser.nickname must equalTo(providerInfo.nickname)

        User.count() must equalTo(usersBefore + 1)
        Identity.count() must equalTo(identitiesBefore + 1)

        val newIdentity = Identity.find(q = "user_id:%s".format(newUser.id.get))

        newIdentity.size must equalTo(1)
      }
    }

    "return an error if it has to create a user but another user with the same nickname exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        User(nickname = providerInfo.nickname).save must beRight

        User.findOrCreateFromProviderInfo(providerInfo) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.DUPLICATE)
            error.field must equalTo("nickname")
            error.message must contain("Ya existe un usuario")
          }
        }
      }
    }

  }

  "User.createFromProviderInfo" should {

    import services.security.Social.retrieveSocialProviderInfo
    import services.security.AccessToken

    implicit val socialAdapter = List(MockTwitterAdapter, MockFacebookAdapter)
    val accessToken = AccessToken("twitter", "valid mock twitter token")
    val Some(providerInfo) = retrieveSocialProviderInfo(accessToken)

    "create the user and also the identity" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val usersBefore = User.count()
        val identitiesBefore = Identity.count()

        val Right(newUser) = User.createFromProviderInfo(providerInfo)
        newUser.nickname must equalTo(providerInfo.nickname)

        User.count() must equalTo(usersBefore + 1)
        Identity.count() must equalTo(identitiesBefore + 1)

        val newIdentity = Identity.find(q = "user_id:%s".format(newUser.id.get))

        newIdentity.size must equalTo(1)

      }
    }

    "return an error when trying to create an already existing user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // create the user
        User(name = providerInfo.name).save must beRight

        val usersBefore = User.count()
        val identitiesBefore = Identity.count()

        User.createFromProviderInfo(providerInfo) must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.DUPLICATE)
            error.field must equalTo("name")
            error.message must contain("""Ya existe un usuario""")
          }
        }

        User.count() must equalTo(usersBefore)
        Identity.count() must equalTo(identitiesBefore)

      }
    }

  }

  "User.refreshApplicationToken" should {

    "generate a new application token and save it to the user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(originalUser) = User.findById(1)

        originalUser.refreshApplicationToken must beRight.like {
          case user => {
            user.applicationToken must not equalTo(originalUser.applicationToken)
            user.tokenExpiration.after(originalUser.tokenExpiration) must beTrue
          }
        }

      }
    }

  }

  "User.save" should {

    "create a new user with a fresh application token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        import utils.DateHelper.{now, addSeconds}

        val beforeNow = addSeconds(now, -1)    // one second before now

        User(nickname="new user").save must beRight.like {
          case user => {
            user.applicationToken must not equalTo("")
            user.tokenExpiration.after(beforeNow) must beTrue
          }
        }

      }
    }

  }

}