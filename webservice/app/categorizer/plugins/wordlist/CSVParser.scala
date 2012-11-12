package categorizer.plugins.wordlist

import play.api.libs.json.Json

import scala.io.Source
import play.api.Play
import utils.FileHelper
import utils.StringHelper.splitCSV

import categorizer.plugins.wordlist.WordlistTokenFileFormatter._
import categorizer.SimpleToken

/*
steps to process a file

play
console

new play.core.StaticApplication(new java.io.File("."))
import categorizer.plugins.wordlist.CSVParser._

parseToFile("teatros.es.csv")
parseToFile("sitios.es.csv")
parseToFile("politica.es.csv")
parseToFile("futbol.es.csv")

*/

object CSVParser {

  val CATEGORIZER_CSV_FOLDER = "conf/categorizer/csv/"

  def parseToFile(file: String): Unit = {
    val tokenFile = readFromFile(file)
    writeToFile(tokenFile)
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
    val values = splitCSV(line).toList.padTo(5, "")
    Some(SimpleToken(
      id      = "0",
      token   = values(0),
      alias   = splitComma(values(1)),
      tags    = splitComma(values(2)),
      lat     = if (values(3) == "") None else Some(values(3).toDouble),
      lng     = if (values(4) == "") None else Some(values(4).toDouble)
    ))
  }

  private def splitComma(text: String): List[String] = {
    text.split("""\s*,\s*""").toList.filter(_ != "")
  }
}