package test.models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.api.i18n.Lang

import utils.I18n.{isSpanish, isEnglish}

import test.matchers.ErrorSpec

class UserSpec extends Specification with ErrorSpec {

  import models.{User, Error}

  "User model" should {

    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        User.findById(1) must beSome.which { user =>
          user.nickname must equalTo("nardoz")
          user.name must equalTo("Mister Nardoz")
        }

        User.findById(5000) must be none

      }
    }

    val user = User(
      nickname = "new nickname",
      name = "new name",
      email = "email@email.com",
      avatar = "new avatar"
    )

    "return error if nickname is empty" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val noNickname = user.copy(nickname="")

        implicit val lang = Lang("en")

        // val saved: Either[List[Error],User] = noNickname.save()(Lang("es"))
        user.copy(nickname="").save()(Lang("es")) must beLeft.like {
          case errors => {
            val fieldErrors = errors.filter(_.field == "nickname")
            fieldErrors.size must equalTo(1)
            val error = fieldErrors(0)
            error.errorCode must equalTo(Error.REQUIRED)
            error.field must equalTo("nickname")
          }
        }

        user.copy(nickname="").save must beLeft.like {
          case errors => atLeastOnceWhen(errors) {
            case error => {
              error.errorCode must equalTo(Error.REQUIRED)
              error.field must equalTo("nickname")
            }
          }
        }

        user.copy(nickname="").save must beLeft

        user.copy(nickname="").save must beLeft { 
          List[Error](Error(
            400, Error.REQUIRED, "nickname","nickname not specified.","Error performing operation"
          ))
        }

        user.copy(nickname="").save must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.REQUIRED)
            error.field must equalTo("nickname")
          }
          // case error => error.field must equalTo("nickname")
        }

        user.copy(nickname="").save must haveError.containing { 
          Error(400, Error.REQUIRED, "nickname","nickname not specified.","Error performing operation")
        }

      }
    }

    "return error in the correct language if nickname is empty" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        // create a def to avoid ambiguos implicit with lang
        def withImplicitEn() = {
          implicit val lang = Lang("en")
          user.copy(nickname="").save() must haveError.like { 
            case error => {
              error.errorCode must equalTo(Error.REQUIRED)
              error.field must equalTo("nickname")
              isEnglish(error.message) must beTrue
              isSpanish(error.message) must beFalse
            }
          }
        }

        def withImplicitEs() = {
          implicit val lang = Lang("es")
          user.copy(nickname="").save() must haveError.like { 
            case error => {
              error.errorCode must equalTo(Error.REQUIRED)
              error.field must equalTo("nickname")
              isSpanish(error.message) must beTrue
            }
          }

        }

        withImplicitEn
        withImplicitEs
        // explicit param
        user.copy(nickname="").save()(Lang("en")) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.REQUIRED)
            error.field must equalTo("nickname")
            isEnglish(error.message) must beTrue
            isSpanish(error.message) must beFalse
          }
        }

        // explicit param
        user.copy(nickname="").save()(Lang("es")) must haveError.like { 
          case error => {
            error.errorCode must equalTo(Error.REQUIRED)
            error.field must equalTo("nickname")
            isSpanish(error.message) must beTrue
            isEnglish(error.message) must beFalse
          }
        }

      }
    }

  }

}
