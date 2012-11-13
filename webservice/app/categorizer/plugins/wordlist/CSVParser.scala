package categorizer.plugins.wordlist

import play.api.libs.json.Json

import scala.io.Source
import play.api.Play
import utils.FileHelper
import utils.StringHelper.splitCSV

import categorizer.plugins.wordlist.WordlistTokenFileFormatter._
import categorizer.SimpleToken

import utils.GeoHelper

/*
steps to process a file

play
console

new play.core.StaticApplication(new java.io.File("."))
import categorizer.plugins.wordlist.CSVParser._

parseFromFile("teatros.es.csv")
parseFromFile("sitios.es.csv")
parseFromFile("politica.es.csv")
parseFromFile("futbol.es.csv")

*/

object CSVParser {

  val CATEGORIZER_CSV_FOLDER = "conf/categorizer/csv/"

  def parseFromFile(file: String, locateUsingToken: Boolean = false): WordlistTokenFile = {
    val tokenFile = readFromFile(file, locateUsingToken)
    writeToFile(tokenFile)
    tokenFile
  }

  def writeToFile(tokenFile: WordlistTokenFile): Unit = {
    val destFile = Play.current.getFile(WordlistPlugin.CATEGORIZER_FOLDER + tokenFile.file).getAbsolutePath
    FileHelper.writeToFile(destFile, write(tokenFile))
  }

  def write(tokenFile: WordlistTokenFile): String = {
    Json.stringify(Json.toJson(tokenFile))
  }

  def readFromFile(file: String, locateUsingToken: Boolean = false): WordlistTokenFile = {
    val sourceFile = Play.current.getFile(CATEGORIZER_CSV_FOLDER + file).getAbsoluteFile
    csvToTokenFile(Source.fromFile(sourceFile).getLines.toList, locateUsingToken)
  }

  def csvToTokenFile(lines: List[String], locateUsingToken: Boolean = false): WordlistTokenFile = {
    val file    = splitCSV(lines(0))(1)
    val line2   = splitCSV(lines(2)).toList.padTo(2, "")
    val tags    = splitComma(line2(1))
    val tokens  = lines.slice(7, lines.size-1).flatMap { line =>
      csvToSimpleToken(line, locateUsingToken)
    }
    WordlistTokenFile(file, tags, tokens)
  }

  def csvToSimpleToken(line: String, locateUsingToken: Boolean = false): Option[SimpleToken] = {

    def calculateLocation(values: List[String], fromTokenlocateUsingToken: Boolean = false): (Option[Double], Option[Double]) = {

      def locate(address: String): (Option[Double], Option[Double]) = {
        GeoHelper.locate(address).map { location =>
          (Some(location._1), Some(location._2))
        }.getOrElse (None, None)
      }

      val TOKEN = 0
      val ALIAS = 1
      val TAGS = 2
      val LAT = 3
      val LNG = 4
      val ADDRESS = 5

      // locate using coordinates
      if (values(LAT) != "" && values(LNG) != "")
        (Some(values(LAT).toDouble), Some(values(LNG).toDouble))
      // locate using address
      else if (values(ADDRESS) != "") 
        locate(values(ADDRESS))
      // locate using token
      else if (locateUsingToken)
        locate (values(TOKEN))
      // do not locate
      else
        (None, None)

      //   // address specified
      //   if 
      //   else 
      //   if 
      //   // address missing
      //   if (values(ADDRESS) == "") {
      //     // get address from the token itself (let google handle it)
      //     if (fromToken) locate(values(TOKEN))
      //     else (None, None)
      //   } else {

      //   }

      //   (None, None)
      //   else {
      //     GeoHelper.locate(values(5)).map { location =>
      //       (Some(location._1), Some(location._2))
      //     }.getOrElse (None, None)
      //   }
      // }
    }

    val values = splitCSV(line).toList.padTo(6, "")

    val (lat, lng) = calculateLocation(values, locateUsingToken)
    Some(SimpleToken(
      id      = "0",
      token   = values(0),
      alias   = splitComma(values(1)),
      tags    = splitComma(values(2)),
      lat     = lat,
      lng     = lng
    ))
  }

  private def splitComma(text: String): List[String] = {
    text.split("""\s*,\s*""").toList.filter(_ != "")
  }
}