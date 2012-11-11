package categorizer.plugins.address

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object SimpleTokenFormatter {

  implicit object JsonTokenFormatter extends Format[SimpleToken] {

    def writes(o: SimpleToken): JsValue = {
      toJson(Map(
        "id" -> toJson(o.id),
        "token" -> toJson(o.token),
        "alias" -> toJson(o.alias),
        "tags"  -> toJson(o.tags)
      ))
    }

    def reads(j: JsValue): SimpleToken = {
      SimpleToken(
        id = (j \ "id")       .asOpt[String]        .getOrElse("0"),
        token = (j \ "token") .asOpt[String]        .getOrElse(""),
        alias = (j \ "alias") .asOpt[List[String]]  .getOrElse(List("")),
        tags  = (j \ "tags")  .asOpt[List[String]]  .getOrElse(List(""))
      )
    }

    def writesToFile(tokens: List[SimpleToken]): JsValue = {
      toJson(Map(
        "tokens" -> toJson(tokens)
      ))
    }

  }
}