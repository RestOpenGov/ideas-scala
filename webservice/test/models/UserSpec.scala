package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

class UserSpec extends org.specs2.mutable.Specification {

  import models.User

  "User model" should {

    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val Some(user) = User.findById(1)

        println("user: %s".format(user.toString))

        user must not be none
        user.nickname must equalTo("nardoz")
        user.name must equalTo("Mister Nardoz")

        val noneUser: Option[User] = User.findById(5000)
        noneUser must be none //equalTo(None)
      }
    }
  }

}