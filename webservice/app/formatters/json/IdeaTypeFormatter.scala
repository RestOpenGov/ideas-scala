package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.IdeaType

import anorm._

import PkFormatter._
import DateFormatter._

object IdeaTypeFormatter {

  implicit object JsonIdeaTypeFormatter extends Format[IdeaType] {

    def writes(o: IdeaType): JsValue = {
      toJson( Map(
        "url"         -> toJson(o.url),
        "id"          -> toJson(o.id),
        "name"        -> toJson(o.name),
        "description" -> toJson(o.description)
      ))
    }

    def reads(j: JsValue): IdeaType = {
      IdeaType(
        id            = (j \ "id").as[Option[Pk[Long]]]           .getOrElse(NotAssigned),
        name          = (j \ "name").as[Option[String]]           .getOrElse("unknown idea type"),
        description   = (j \ "description").as[Option[String]]    .getOrElse("no description")
      )
    }

  }

}