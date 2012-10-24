package test.services.security

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import test.matchers.ErrorSpec
import utils.DateHelper.{now, addSeconds}

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

    // WEIRD COMPILE ERROR!!! compilation never ends
    // if I have two operations on User ????
    // "return an error if the user already exists" in {
    //   running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

    //     import models.User

    //     // User(nickname = "twitter.nickname").save must beRight
    //     // createApplicationToken(AccessToken("twitter", "valid twitter token")) must beLeft

    //     User(nickname = "twitter.nickname").save must beRight
    //     createApplicationToken(AccessToken("twitter", "valid twitter token")) must haveError.like { 
    //       case error => {
    //         error.errorCode must equalTo(Error.DUPLICATE)
    //         error.field must equalTo("name")
    //         error.message must contain("Ya existe un usuario")
    //       }
    //     }
    //     todo
    //   }
    // }

    "retrieve a valid token if everyting is ok" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // retrieve a valid token
        createApplicationToken(AccessToken("twitter", "valid twitter token")) must beRight.like { 
          case applicationToken => {
            // token is not empty
            applicationToken.token must not equalTo("")
            // the expiration is after now
            applicationToken.expiration.after(now) must beTrue
            // I can get the user using that token
            findUserByApplicationToken(applicationToken.token) must beRight.like {
              case newUserFromToken => {
                newUserFromToken.nickname must equalTo("twitter.nickname")
              }
            }
          }
        }

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

    import services.security.ApplicationToken

    "return an error there's no user with that application token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        findUserByApplicationToken("made up token") must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.NOT_FOUND)
            error.field must equalTo("applicationToken")
            error.message must contain("""Invalid application token""")
          }
        }

      }
    }

    "return an error if the application token has expired" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(user) = models.User.findById(1)

        // generate a new token for user with id 1
        val Right(userWithFreshToken) = user.refreshApplicationToken

        val applicationToken = userWithFreshToken.applicationToken

        // force the token to expire
        val Right(userWithExpiredToken) = userWithFreshToken.copy(
          tokenExpiration = addSeconds(now, -1)
        ).update

        findUserByApplicationToken(applicationToken) must haveError.like {
          case error => {
            error.errorCode must equalTo(Error.AUTHENTICATION)
            error.field must equalTo("accessToken")
            error.message must contain("""Token expired""")
          }
        }
      }
    }

    "return the user associated with that application token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(user) = models.User.findById(1)

        // generate a new token for user with id 1
        val Right(userWithFreshToken) = user.refreshApplicationToken

        findUserByApplicationToken(userWithFreshToken.applicationToken) must beRight.like {
          case foundUser => {
            foundUser.id.get must equalTo(user.id.get)
          }
        }
      }
    }

  }

}