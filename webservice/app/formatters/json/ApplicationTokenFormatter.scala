package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import services.security.ApplicationToken

import java.util.Date

import DateFormatter._

object ApplicationTokenFormatter {

  implicit object JsonApplicationTokenFormatter extends Format[ApplicationToken] {

    def writes(o: ApplicationToken): JsValue = {
      toJson( Map(
        "token"             -> toJson(o.token),
        "expiration"        -> toJson(Option(o.expiration))
      ))
    }

    def reads(j: JsValue): ApplicationToken = {
      new ApplicationToken(
        token       = (j \ "token").as[Option[String]]       .getOrElse(""),
        expiration  = (j \ "expiration").as[Option[Date]]    .getOrElse(new Date)
      )
    }

  }

}