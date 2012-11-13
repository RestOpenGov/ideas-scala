package categorizer.plugins.wordlist

import categorizer.{Plugin, Token}
import play.api.libs.json.Json
import play.api.libs.json.{JsArray, JsObject, JsValue}
import categorizer.SimpleTokenFormatter._
import categorizer.SimpleToken
import utils.StringHelper.replaceTildes
import categorizer.SimpleToken
import categorizer.Token
import scala.util.matching.Regex
import categorizer.SimpleToken

abstract class WordlistPlugin extends Plugin {

  import play.api.Play.current

  val file: String
  val category: String

  val CATEGORIZER_FOLDER = "conf/categorizer/"

  // Read JSON file
  lazy val jsonTokens: JsValue = {
    val wordlist = current.getFile(CATEGORIZER_FOLDER + file).getAbsoluteFile
    Json.parse(scala.io.Source.fromFile(wordlist).mkString)
  }
  
  // Extract globalTags
  lazy val globalTags = (jsonTokens \ "tags").as[List[String]]
  
  // Create regex list. Each regex is used to search for a Token
  lazy val regexes: List[(Regex, SimpleToken)] = {

    // escapeRegExpChars("hola.vamos a+escapar") = "hola\.vamos\ a\+escapar"
    def escapeRegExpChars(regExp: String): String = {
      val charsToEscape = List(".", "+", " ", "*", "/", "(", ")", "{", "}", ":", "?", "\\")
      charsToEscape.foldRight(regExp){ (char, text) => 
        val repChar = if (char == """\""") """\\""" else char
        text.replaceAll("""\""" + char, """\\""" + repChar)
      }
    }

    val rawTokens = (jsonTokens \ "tokens").as[List[SimpleToken]]

    rawTokens map { item =>
      val values = (item.alias :+ item.token) map { value => 
        escapeRegExpChars(replaceTildes(value.toLowerCase))
      }

      // Create regex using the token and the aliases
      val r = """\b(?:%s)\b""".format(values.mkString("|")).r

      (r, item)
    }
  }
  
  def categorize(input: String): Seq[Token] = { 
    val search = replaceTildes(input.toLowerCase)

    // Create a list of tokens with only the tokens found
    regexes flatMap { case (r, item) =>
      for {
        text <- r.findFirstIn(search)
      } yield buildToken(text, item)
    }

  }
  
  def buildToken(text: String, item: SimpleToken): Token = {
    var token = Token(
          original = text,
          text     = item.token,
          lat      = item.lat,
          lng      = item.lng,
          tags     = globalTags ++ item.tags,
          category = category
        )
     play.Logger.debug("Token built: "+ token)
     
     token
  }
}

object WordlistPlugin {
  val CATEGORIZER_FOLDER = "conf/categorizer/"

}
