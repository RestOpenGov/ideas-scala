package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import services.security.AccessToken

object AccessTokenFormatter {

  implicit object JsonAccessTokenFormatter extends Format[AccessToken] {

    def writes(o: AccessToken): JsValue = {
      toJson( Map(
        "provider"          -> toJson(o.provider),
        "token"             -> toJson(o.token)
      ))
    }

    def reads(j: JsValue): AccessToken = {
      new AccessToken(
        provider   = (j \ "provider").as[Option[String]]    .getOrElse(""),
        token      = (j \ "token").as[Option[String]]       .getOrElse("")
      )
    }

  }

}