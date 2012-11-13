package controllers.tests

import play.api._
import play.api.mvc._

import play.api.libs.json.Json.toJson

import utils.actions.CORSAction

import utils.FileHelper

import categorizer.plugins.wordlist.CSVParser
import categorizer.plugins.wordlist.WordlistTokenFileFormatter._

import utils.Conversion.toBoolean

/*
usage example:

import sitio.es.csv file
http://localhost:9000/api/tests/csv/import/sitios.es.csv

// use the token column to search in google maps web service
http://localhost:9000/api/tests/csv/import/sitios.es.csv?locateUsingToken=true
*/

object CSVImporter extends Controller {

  def CSVimport(file: String) = CORSAction { request =>
    val locateUsingToken = toBoolean(request.queryString.get("locateUsingToken").getOrElse(Seq(""))(0))
    val tokenFile = CSVParser.parseFromFile(file, locateUsingToken)
    Ok(toJson(tokenFile))
  }

}
