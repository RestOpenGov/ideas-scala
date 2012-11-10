package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import models.{IdeaGeo, Idea}

import anorm._

import PkFormatter._
import IdeaMinFormatter._

object IdeaGeoFormatter {

  implicit object JsonIdeaGeoFormatter extends Format[IdeaGeo] {

    def writes(o: IdeaGeo): JsValue = {
      toJson( Map(
        "id"         -> toJson(Option(o.id)),
        "idea"       -> toJson(o.idea),
        "name"       -> toJson(o.name),
        "lat"        -> toJson(o.lat),
        "lng"        -> toJson(o.lng)
      ))
    }

    def reads(j: JsValue): IdeaGeo = {
      IdeaGeo(
        id        = (j \ "id").as[Option[Pk[Long]]]           .getOrElse(NotAssigned),
        idea      = parseIdea(j),
        name      = (j \ "name").as[Option[String]]           .getOrElse(""),
        lat       = (j \ "lat").as[Option[Double]]            .getOrElse(0D),
        lng       = (j \ "lng").as[Option[Double]]            .getOrElse(0D)
      )
    }

    private def parseIdea(j: JsValue): Idea = {
      val idea: Option[Idea] = for {
        id    <- (j \ "idea" \ "id").as[Option[Long]]
        idea  <- Idea.findById(id)
      } yield idea
      idea.getOrElse(Idea())
    }

  }

}