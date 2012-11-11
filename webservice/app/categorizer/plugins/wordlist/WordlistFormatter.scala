package categorizer.plugins.wordlist

import play.api.libs.json.{Format, JsValue}
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
        lat   = (j \ "lat")   .asOpt[Double],
        lng   = (j \ "lng")   .asOpt[Double]
      )
    }
  }
}