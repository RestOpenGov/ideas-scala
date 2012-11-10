package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.Category

import anorm._

import PkFormatter._
import DateFormatter._

object CategoryFormatter {

  implicit object JsonCategoryFormatter extends Format[Category] {

    def writes(o: Category): JsValue = {
      toJson( Map(
        "category"         -> toJson(o.category),
        "id"          -> toJson(o.id),
        "latitude"        -> toJson(o.latitude),
        "longitude" -> toJson(o.longitude)
      ))
    }

    def reads(j: JsValue): Category = {
      Category(
        id            = (j \ "id").as[Option[Pk[Long]]]           .getOrElse(NotAssigned),
        category          = (j \ "category").as[Option[String]]   .getOrElse("unknown category"),
        latitude   = (j \ "latitude").as[Option[Int]]             .getOrElse(-1),
        longitude   = (j \ "longitude").as[Option[Int]]           .getOrElse(-1)
      )
    }

  }

}