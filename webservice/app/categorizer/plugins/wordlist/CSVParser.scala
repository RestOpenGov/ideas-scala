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

  def parseFromFile(file: String): WordlistTokenFile = {
    val tokenFile = readFromFile(file)
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

  def readFromFile(file: String): WordlistTokenFile = {
    val sourceFile = Play.current.getFile(CATEGORIZER_CSV_FOLDER + file).getAbsoluteFile
    csvToTokenFile(Source.fromFile(sourceFile).getLines.toList)
  }

  def csvToTokenFile(lines: List[String]): WordlistTokenFile = {
    val file    = splitCSV(lines(0))(1)
    val line2   = splitCSV(lines(2)).toList.padTo(2, "")
    val tags    = splitComma(line2(1))
    val tokens  = lines.slice(7, lines.size-1).flatMap { line =>
      csvToSimpleToken(line)
    }
    WordlistTokenFile(file, tags, tokens)
  }

  def csvToSimpleToken(line: String): Option[SimpleToken] = {

    def calculateLocation(values: List[String]): (Option[Double], Option[Double]) = {

      val TOKEN = 0
      val ALIAS = 1
      val TAGS = 2
      val LAT = 3
      val LNG = 4
      val ADDRESS = 5

      // coordinates specified
      if (values(LAT) != "" && values(LNG) != "") {
        (Some(values(LAT).toDouble), Some(values(LNG).toDouble))
      // some coordinate missing
      } else {
        if (values(ADDRESS) == "") (None, None)
        else {
          GeoHelper.locate(values(5)).map { location =>
            (Some(location._1), Some(location._2))
          }.getOrElse (None, None)
        }
      }
    }

    val values = splitCSV(line).toList.padTo(6, "")

    val (lat, lng) = calculateLocation(values)
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