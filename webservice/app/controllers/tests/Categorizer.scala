package controllers.tests

import play.api._
import play.api.mvc._

import formatters.json.CategorizerTokenFormatter._
import play.api.libs.json.Json.toJson

import categorizer.MockCategorizer

import utils.actions.CORSAction

object Categorizer extends Controller {

  def categorize = CORSAction { implicit request =>
    play.Logger.info(request.body.asText.getOrElse(""))
    Ok(toJson(MockCategorizer.categorize(request.body.asText.getOrElse(""))))
  }

}
