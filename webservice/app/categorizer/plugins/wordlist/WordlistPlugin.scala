package categorizer.plugins.wordlist

import categorizer.{Plugin, Token}

import play.api.libs.json.Json
import play.api.libs.json.{JsArray, JsObject, JsValue}

import categorizer.SimpleTokenFormatter._
import categorizer.SimpleToken

import utils.StringHelper.replaceTildes

abstract class WordlistPlugin extends Plugin {

  import play.api.Play.current

  val file: String
  val category: String

  val CATEGORIZER_FOLDER = "conf/categorizer/"

  lazy val wordlist = current.getFile(CATEGORIZER_FOLDER + file).getAbsoluteFile
  def categorize(input: String): Seq[Token] = { 

    val search = replaceTildes(input.toLowerCase)

    lazy val globalTags = (jsonTokens \ "tags").as[List[String]]

    lazy val jsonTokens: JsValue = {
      Json.parse(scala.io.Source.fromFile(wordlist).mkString)
    }

    lazy val tokens: List[SimpleToken] = {
      val rawTokens = (jsonTokens \ "tokens").as[List[SimpleToken]]
      // rawTokens
      val normalizedTokens = rawTokens.map { token: SimpleToken => 
        token.copy(
          token = replaceTildes(token.token.toLowerCase),
          alias = token.alias.map { alias => replaceTildes(alias.toLowerCase) }
        )
      }
      normalizedTokens
    }

    def isMatchingAlias(search: String, alias: List[String]): Boolean = {
      alias.exists{ alias => 
        val r = """\b%s\b""".format(alias).r
        r.findFirstIn(search).isDefined
      }
    }

    tokens.collect {
      case item if 
        (search contains item.token) || 
        (isMatchingAlias(search, item.alias)) ||
        (search.split(" ") diff item.alias).length < search.split(" ").length => {

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

object WordlistPlugin {
  val CATEGORIZER_FOLDER = "conf/categorizer/"
}
