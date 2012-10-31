package controllers

import play.api._
import play.api.mvc._

import utils.actions.CORSAction
import utils.actions.SecuredAction
import utils.actions.CrudAuthAction
import utils.actions.CrudAction

import formatters.json.AccessTokenFormatter._
import formatters.json.ApplicationTokenFormatter._
import formatters.json.ErrorFormatter._

import formatters.json.UserFormatter._

import play.api.libs.json.Json.toJson

import utils.{JsonBadRequest, JsonNotFound, JsonOk}

import services.security.{AccessToken, ApplicationToken}

import services.security.SecurityManager.{applicationTokenFromRequest, createApplicationToken}

object Security extends Controller {

  def readToken = CORSAction { implicit request =>
    val token = applicationTokenFromRequest(request).getOrElse("application token not found")
    Ok(toJson(Map("token" -> token)))
  }

  def secured = SecuredAction { implicit request =>
    Ok(views.html.api())
  }

  def logged = CrudAuthAction.list { request =>
    request.user
  }

  def auth = CORSAction(parse.json) { implicit request =>
    request.body.asOpt[AccessToken].map { accessToken =>
      createApplicationToken(accessToken).fold(
        errors            => JsonBadRequest(errors),
        applicationToken  => Ok(toJson(applicationToken))
      )
    }.getOrElse (JsonBadRequest("Invalid access token"))
  }

  // def auth = CrudAction.save { accessToken: AccessToken =>
  //   createApplicationToken(accessToken)
  // }

}
