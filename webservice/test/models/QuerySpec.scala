package test.models

import org.specs2.mutable._
import org.specs2.mutable.After

import play.api.test._
import play.api.test.Helpers._

class QuerySpec extends org.specs2.mutable.Specification {

  import models.{Idea, Comment}

  "A model" should {

    "be retrieved by simple queries" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val ideasWifi = Idea.find(q="name:wifi")
        ideasWifi.size must equalTo(1)
        ideasWifi(0).name must equalTo("Wifi libre en ba")
        ideasWifi(0).description must equalTo("Proveer acceso wifi gratuito en toda la ciudad")
      }
    }

    "retrieve an empty list if no item matches the query" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Idea.find(q="name:non existent").size must equalTo(0)
      }
    }

    "retrieve a list with matching items" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val ideasLaAsc = Idea.find(q="name:la", order="id")
        ideasLaAsc.size must equalTo(2)
        ideasLaAsc(0).name must equalTo("Multas para los autos que no respetan las bicisendas")
        ideasLaAsc(1).name must equalTo("Cortar las ramas de corrientes y malabia")

        val ideasLaDesc = Idea.find(q="name:la", order="id desc")
        ideasLaDesc.size must equalTo(2)
        ideasLaDesc(0).name must equalTo("Cortar las ramas de corrientes y malabia")
        ideasLaDesc(1).name must equalTo("Multas para los autos que no respetan las bicisendas")
      }
    }

    "support complex queries" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val ideas1 = Idea.find(q="id:2..4", order="id")
        ideas1.size must equalTo(3)
        ideas1(0).name must equalTo("Multas para los autos que no respetan las bicisendas")
        ideas1(1).name must equalTo("Cortar las ramas de corrientes y malabia")
        ideas1(2).name must equalTo("Como hago para sacar el registro de conducir?")

        val ideas2 = Idea.find(q="id:2..4,views>100", order="id")
        ideas2.size must equalTo(1)
        ideas2(0).name must equalTo("Multas para los autos que no respetan las bicisendas")

      }
    }

    "support nested object queries" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val ideas1 = Idea.find(q="author.name:mister", order="id")
        ideas1.size must equalTo(3)
        ideas1(0).name must equalTo("Multas para los autos que no respetan las bicisendas")
        ideas1(1).name must equalTo("Cortar las ramas de corrientes y malabia")
        ideas1(2).name must equalTo("Como hago para sacar el registro de conducir?")

        val ideas2 = Idea.find(q="author.name:mister,type.name:reclamo", order="id")
        ideas2.size must equalTo(1)
        ideas2(0).name must equalTo("Cortar las ramas de corrientes y malabia")

      }
    }

  }

}
