package org.restopengov.Armadillo.backend.plugins

import org.restopengov.Armadillo.backend._
import org.restopengov.Armadillo.backend.plugins.Wordlist.formatters.json.WordlistFormatter._
import play.api.libs.json.Json
import play.api.libs.json.{JsArray, JsObject, JsValue}

case class WordlistToken(
	val token: String = "",
	val alias: List[String] = List(""),
	val tags: List[String] = List(""),
	val lat: String = "",
	val lng: String = ""
)

class WordlistPlugin extends Plugin {

	import play.api.Play.current

	val wordlist = current.getFile("conf/categorizer/tokenListSample.es.json").getAbsoluteFile
	def parse(input: String): Seq[Token] = { 

		val lines = scala.io.Source.fromFile(wordlist).mkString
		val json = Json.parse(lines)

		val globalTags = (json \ "tags").as[List[String]]

		(json \ "tokens").as[List[WordlistToken]].collect {
			case item if 
				(input.toLowerCase contains item.token.toLowerCase) ||  
				(input.split(" ").map(_.toLowerCase) diff item.alias.map(_.toLowerCase)).length < input.split(" ").length => {
				
				new Token(
					original = input,
					text     = item.token,
					lat      = Option(item.lat),
					long     = Option(item.lng),
					tags     = globalTags ++ item.tags,
					category = "wordlist"
				)
			}	
		}

	}

}