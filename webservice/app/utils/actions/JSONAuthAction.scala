package utils.actions

import play.api.http.Status

import play.api.libs.json.{Format, JsValue, Writes}
import play.api.libs.json.Json.toJson

import play.api.mvc._

import utils.{JsonBadRequest, JsonUnauthorized}

import models.{User, Error}

import services.security.SecurityManager.validateUserFromRequest

object JSONAuthAction extends BodyParsers {

  class RequestWithUser[A](val user: User, val request: Request[A]) extends WrappedRequest(request)

  // helper function to create a result with a custom status
  private def resultWithStatus(status: Int = Status.OK) = {
    new Results.Status(status)
  }

  // passes the request to the block
  def fromRequest[T: Writes](status: Int)(block: RequestWithUser[AnyContent] => T): Action[AnyContent] = {
    CORSAction { request =>
      validateUserFromRequest(request).fold(
        errors  => JsonUnauthorized(errors),
        user    => {
          val authRequest = new RequestWithUser(user, request)
          resultWithStatus(status)(toJson(block(authRequest)))
        }
      )
    }
  }

  def withErr[E: Writes, T: Writes](statusOk: Int, statusErr: Int)     // Status.OK
    (block: RequestWithUser[AnyContent] => Either[E, T]): Action[AnyContent] = {

    CORSAction { request =>
      validateUserFromRequest(request).fold(
        errors  => JsonUnauthorized(errors),
        user    => {
          val authRequest = new RequestWithUser(user, request)
          block(authRequest).fold(
            errors          => resultWithStatus(statusErr)(toJson(errors)),
            responseEntity  => resultWithStatus(statusOk)(toJson(responseEntity))
          )
        }
      )
    }
  }

  def parseWithErr[E: Writes, T: Format](statusOk: Int, statusErr: Int)     // Status.OK
    (block: (RequestWithUser[JsValue], T) => Either[E, T]): Action[JsValue] = {

    CORSAction(parse.json) { request =>
      validateUserFromRequest(request).fold(
        errors  => JsonUnauthorized(errors),
        user    => {
          request.body.asOpt[T].map { requestEntity =>
            val authRequest = new RequestWithUser(user, request)
            block(authRequest, requestEntity).fold(
              errors          => resultWithStatus(statusErr)(toJson(errors)),
              responseEntity  => resultWithStatus(statusOk)(toJson(responseEntity))
            )
          }.getOrElse       (JsonBadRequest("Invalid entity"))
        }
      )
    }
  }

}
