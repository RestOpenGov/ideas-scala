package categorizer.plugins.wordlist

import categorizer.{Plugin, Token}

import play.api.libs.json.Json
import play.api.libs.json.{JsArray, JsObject, JsValue}

import categorizer.SimpleTokenFormatter._
import categorizer.SimpleToken

abstract class WordlistPlugin extends Plugin {

  import play.api.Play.current

  val file: String
  val category: String

  val CATEGORIZER_FOLDER = "conf/categorizer/"

  lazy val wordlist = current.getFile(CATEGORIZER_FOLDER + file).getAbsoluteFile
  def categorize(input: String): Seq[Token] = { 

    val lines = scala.io.Source.fromFile(wordlist).mkString
    val json = Json.parse(lines)

    val globalTags = (json \ "tags").as[List[String]]

    (json \ "tokens").as[List[SimpleToken]].collect {
      case item if 
        (input.toLowerCase contains item.token.toLowerCase) || 
        (input.split(" ").map(_.toLowerCase) diff item.alias.map(_.toLowerCase)).length < input.split(" ").length => {

        new Token(
          original = input,
          text     = item.token,
          lat      = item.lat,
          lng      = item.lng,
          tags     = globalTags ++ item.tags,
          category = category
        )
      }
    }

  }

}