package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.JsNull
import play.api.libs.json.Json.toJson

import categorizer.Token

object CategorizerTokenFormatter {

  implicit object JsonCategorizerTokenFormatter extends Format[Token] {

    def writes(o: Token): JsValue = {
      toJson( Map(
        "category"        -> toJson(o.category),
        "original"        -> toJson(o.original),
        "text"            -> toJson(o.text),
        "lat"             -> o.lat.map{ lat => toJson(lat) }.getOrElse(JsNull(0)),
        "long"            -> o.long.map{ long => toJson(long) }.getOrElse(JsNull(0)),
        "tags"            -> toJson(o.tags)
      ))
    }

    def reads(j: JsValue): Token = {
      new Token(
        category   = (j \ "category").as[Option[String]]    .getOrElse("undefined"),
        original   = (j \ "original").as[Option[String]]    .getOrElse("undefined"),
        text       = (j \ "text").as[Option[String]]        .getOrElse("undefined"),
        lat        = (j \ "lat").as[Option[Long]],
        long       = (j \ "lat").as[Option[Long]],
        tags       = (j \ "tags").as[Option[Seq[String]]]   .getOrElse(Seq[String]())
      )
    }

  }

}