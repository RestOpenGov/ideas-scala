package controllers

import play.api._
import play.api.mvc._

import utils.CORSAction
import utils.SecuredAction

import formatters.json.AccessTokenFormatter._
import formatters.json.ApplicationTokenFormatter._
import formatters.json.ErrorFormatter._

import play.api.libs.json.Json.toJson

import utils.{JsonBadRequest, JsonNotFound, JsonOk}

import services.security.{SecurityManager, AccessToken, ApplicationToken}

object Security extends Controller {

  def secured = SecuredAction { implicit request =>
    Ok(views.html.api())
  }

  def auth = CORSAction(parse.json) { implicit request =>
    request.body.asOpt[AccessToken].map { accessToken =>
      SecurityManager.createApplicationToken(accessToken).fold(
        errors => JsonBadRequest(errors),
        applicationToken => Ok(toJson(applicationToken))
      )
    }.getOrElse (JsonBadRequest("Invalid access token"))
  }

  def api = Action { implicit request =>
    Ok(views.html.api())
  }

}
