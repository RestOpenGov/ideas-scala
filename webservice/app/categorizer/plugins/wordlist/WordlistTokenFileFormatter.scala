package categorizer.plugins.wordlist

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.JsArray
import play.api.libs.json.Json.toJson

import categorizer.SimpleTokenFormatter._
import categorizer.SimpleToken

object WordlistTokenFileFormatter {

  implicit object JsonWordlistTokenFileFormatter extends Format[WordlistTokenFile] {

    def writes(o: WordlistTokenFile): JsValue = {
      toJson(Map(
        "tags"    -> toJson(o.tags),
        "tokens"  -> toJson(o.tokens)
      ))
    }

    def reads(j: JsValue): WordlistTokenFile = {
      WordlistTokenFile(
        tags    = (j \ "tags")   .asOpt[List[String]]        .getOrElse(List[String]()),
        tokens  = (j \ "tokens") .asOpt[List[SimpleToken]]   .getOrElse(List[SimpleToken]())
      )
    }

  }
}