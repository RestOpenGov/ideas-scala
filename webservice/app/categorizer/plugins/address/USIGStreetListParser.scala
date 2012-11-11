package categorizer.plugins.address

import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Reads

import scala.io.Source

import utils.StringHelper.{trim, normalizeSpaces}
import utils.Validate.isEmptyWord

import SimpleTokenFormatter.JsonTokenFormatter

import utils.FileHelper

object USIGStreetListParser {

  case class USIGStreetToken(id: String, token: String)

  // val USIG_SOURCE_FILE = "conf/categorizer/usig_streets-min.json"
  val USIG_SOURCE_FILE = "conf/categorizer/usig_streets.json"
  val CATEGORIZER_STREETS_FILE = "conf/categorizer/ba_streets.json"

  implicit object USIGStreetListJsonFormatter extends Reads[USIGStreetToken] {
    def reads(data: JsValue): USIGStreetToken = {
      USIGStreetToken(data(0).toString, data(1).toString)
    }
  }

  def parse: Option[List[SimpleToken]] = {
    readFromFile().map { USIGTokens => 
      USIGTokens.flatMap{ USIGToken => USIGTokenToSimpleToken(USIGToken) }
    }
  }

  def readFromFile(file: String = USIG_SOURCE_FILE): Option[List[USIGStreetToken]] = {
    read(Source.fromFile(file).mkString)
  }
  def read(input: String): Option[List[USIGStreetToken]] = {
    Json.parse(input).asOpt[List[USIGStreetToken]]
  }

  def writeToFile(tokens: List[SimpleToken], file: String = CATEGORIZER_STREETS_FILE): Unit = {
    FileHelper.writeToFile(file, write(tokens))
  }

  def write(tokens: List[SimpleToken]): String = {
    val json: JsValue = JsonTokenFormatter.writesToFile(tokens)
    Json.stringify(json)
  }

  def USIGTokenToSimpleToken(t: USIGStreetToken): Option[SimpleToken] = {
    val (token, alias) = getAliasFromStreetName(t.token)

    if (t.id == "0" || isEmptyWord(token)) None
    else {
      Some(SimpleToken(
        id = t.id, token = token, alias = alias
      ))
    }

  }

  def getAliasFromStreetName(name: String): (String, List[String]) = {
    val normalized = normalizeName(name).toLowerCase
    // split by ",", reverse order and rebuild the name
    val tokenParts = normalized.split("""\s*,\s*""")
    val token = tokenParts.reverse.mkString(" ")

    val alias = if (tokenParts.size > 1) List(tokenParts(0)) else List()
    (token, alias)
  }

  def normalizeName(name: String): String = {
    val noTilde   = name.stripPrefix("\"").stripSuffix("\"")
    val noParens  = stripParensText(noTilde)
    val noNoise   = stripNoise(noParens)
    cleanUp(noNoise)
  }

  // get rid of text between ( and )
  def stripParensText(name: String): String = {
    name.replaceAll("""(?imx) \([^\)]*\)""", "")
  }

  def stripNoise(name: String): String = {

    // get rid of abreviations
    val noAbrev = name.replaceAll("""(?imx)           #insensitive case, multiline, whitespaces and comments
      (?<=\W|^)           # look behind for non word char
      \w+\.               # any number of word chars followed by a dot
      (?=\W|$)            # look after for non word char
    """, "")

    // first remove noiseword followed by "."
    noAbrev.replaceAll("""(?imx)           #insensitive case, multiline, whitespaces and comments
      (?<=\W|^)
      (?:
        avenida|avda|avd|av|alte|
        presidente|pres|pte|mcal|mariscal|
        pasaje|pje|general|gral|
        particular|pje\.particular|comodoro|cdro|
        teniente|tte|ing|
        coronel|cnel|doctor|dr|
        sin\ nombre\ oficial|sno
      )
      (?=\W|$)        #noisewords followed by ".", delimited by word separator
    """, "")
    // // first remove noiseword followed by "."
    // noAbrev.replaceAll("""(?imx)           #insensitive case, multiline, whitespaces and comments
    //   (?<=\W|^)
    //   (?:
    //     avenida|avda\.|avd\.|av\.|avda|avd|av|alte\.|alte|
    //     presidente|pres\.|pte\.|pres|pte|mcal\.|mcal|
    //     pasaje|pje\.|pje|general|gral\.|gral|
    //     particular|pje\.particular|comodoro|cdro\.|cdro|
    //     teniente|tte\.|tte|ing\.|ing|
    //     coronel|cnel\.|cnel|doctor|dr\.|dr|
    //     sin\ nombre\ oficial|sno\.|sno|
    //     calle
    //   )
    //   (?=\W|$)        #noisewords followed by ".", delimited by word separator
    // """, "")
  }

  // removes double spaces, and trailing commas
  def cleanUp(name: String) = {
    val singleSpaced    = trim(normalizeSpaces(name))
    val noTrailingComma = singleSpaced.stripSuffix(",")
    trim(noTrailingComma)
  }

//"tilcara, AV. aa1 av ave avda aa3 avda. aa4 av.".replaceAll("""(?i)\b(?:av|avda|avd|avenida|pres|pte|pje|pasaje|gral|general|particular|cdro|cnel|coronel|dr|doctor|sno|calle|sin nombre oficial)\.?\b""", "")

}