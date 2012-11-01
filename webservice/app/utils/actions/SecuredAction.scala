package utils.actions

import play.api.mvc._
import models.{User, Error}

import utils.JsonUnauthorized

import models.ValidationError

import services.security.SecurityManager.validateUserFromRequest


/**
 * Created with IntelliJ IDEA.
 * User: sas
 * Date: 7/28/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */

object SecuredAction {

  type ResultWithHeaders = Result {def withHeaders(headers: (String, String)*): Result}

  class RequestWithUser[A](val user: User, val request: Request[A]) extends WrappedRequest(request)

  // def apply(block: Request[AnyContent] => ResultWithHeaders): Action[AnyContent] = {
  //   CORSAction { request =>
  //     userFromRequest(request).map { user =>
  //       block(new RequestWithUser(user, request))
  //     }.getOrElse(JsonUnauthorized())
  //   }
  // }

  def apply(block: RequestWithUser[AnyContent] => ResultWithHeaders): Action[AnyContent] = {
    CORSAction { request =>
      validateUserFromRequest(request).fold(
        errors  => JsonUnauthorized(errors),
        user    => block(new RequestWithUser(user, request))
      )
    }
  }

  def apply(block: => ResultWithHeaders): Action[AnyContent] = {
    this.apply(_ => block)
  }

}

