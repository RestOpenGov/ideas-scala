package controllers.tests

import play.api._
import play.api.mvc._

import play.api.libs.json.Json.toJson

import utils.actions.CORSAction

import utils.FileHelper

import categorizer.plugins.wordlist.CSVParser
import categorizer.plugins.wordlist.WordlistTokenFileFormatter._

object CSVImporter extends Controller {

  def CSVimport(file: String) = CORSAction {
    val tokenFile = CSVParser.parseFromFile(file)
    Ok(toJson(tokenFile))
  }

}
