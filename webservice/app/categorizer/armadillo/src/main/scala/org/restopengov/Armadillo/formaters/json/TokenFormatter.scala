package org.restopengov.Armadillo.formatters.json

import org.restopengov.Armadillo.backend.Token
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object TokenFormatter {

    implicit object JsonTokenFormatter extends Format[Token] {

        def writes(o: Token): JsValue = {
            toJson(Map(
                "original" -> toJson(o.original),
                "text"     -> toJson(o.text),
                "category" -> toJson(o.category),
                "lat"      -> toJson(o.lat),
                "long"     -> toJson(o.long),
                "tags"     -> toJson(o.tags)
            ))
        }

        def reads(j: JsValue): Token = {
            Token(
                original = (j \ "original") .as[String],
                text     = (j \ "text")     .as[String],
                category = (j \ "category") .as[String],
                lat      = (j \ "lat")      .as[Option[String]],
                long     = (j \ "long")     .as[Option[String]],
                tags     = (j \ "tags")     .as[Seq[String]]
            )
        }
    }
}