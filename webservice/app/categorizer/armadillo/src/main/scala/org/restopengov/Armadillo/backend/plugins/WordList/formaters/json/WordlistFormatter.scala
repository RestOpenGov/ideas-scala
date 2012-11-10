package org.restopengov.Armadillo.backend.plugins.Wordlist.formatters.json

import org.restopengov.Armadillo.backend.plugins._
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object WordlistFormatter {

    implicit object JsonWordlistFormatter extends Format[WordlistToken] {

        def writes(o: WordlistToken): JsValue = {
            toJson(Map(
                "token" -> toJson(o.token),
                "alias" -> toJson(o.alias),
                "tags"  -> toJson(o.tags),
                "lat"   -> toJson(o.lat),
                "lng"   -> toJson(o.lng)
            ))
        }

        def reads(j: JsValue): WordlistToken = {
            WordlistToken(
                token = (j \ "token") .asOpt[String]        .getOrElse(""),
                alias = (j \ "alias") .asOpt[List[String]]  .getOrElse(List("")),
                tags  = (j \ "tags")  .asOpt[List[String]]  .getOrElse(List("")),
                lat   = (j \ "lat")   .asOpt[String]        .getOrElse(""),
                lng   = (j \ "lng")   .asOpt[String]        .getOrElse("")
            )
        }
    }
}