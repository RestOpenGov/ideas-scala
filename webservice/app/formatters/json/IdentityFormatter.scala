package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.{Identity, User}

import anorm._

import UserFormatter._
import PkFormatter._
import DateFormatter._

object IdentityFormatter {

  implicit object JsonIdentityFormatter extends Format[Identity] {

    def writes(o: Identity): JsValue = {
      toJson( Map(
        "url"         -> toJson(o.url),
        "id"          -> toJson(o.id),
        "user"        -> toJson(o.user),
        "provider"    -> toJson(o.provider),
        "provider_id" -> toJson(o.provider_id),
        "created"     -> toJson(Option(o.created))
      ))
    }

    def reads(j: JsValue): Identity = {
      Identity(
        id            = (j \ "id").as[Option[Pk[Long]]]         .getOrElse(NotAssigned),
        user          = (j \ "user").as[Option[User]]           .getOrElse(User()),
        provider      = (j \ "provider").as[Option[String]]     .getOrElse(""),
        provider_id   = (j \ "provider_id").as[Option[String]]  .getOrElse(""),
        created       = (j \ "created").as[Option[Date]]        .getOrElse(new Date())
      )
    }

  }

}