package utils.actions

import play.api.mvc._
import models.User

import utils.JsonUnauthorized

/**
 * Created with IntelliJ IDEA.
 * User: sas
 * Date: 7/28/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */

object SecuredAction {

  type ResultWithHeaders = Result {def withHeaders(headers: (String, String)*): Result}

  class RequestWithUser(val user: User, val request: Request[AnyContent]) extends WrappedRequest(request)

  def apply(block: Request[AnyContent] => ResultWithHeaders): Action[AnyContent] = {
    CORSAction { request =>
      userFromRequest(request).map { user =>
        block(new RequestWithUser(user, request))
      }.getOrElse(JsonUnauthorized())
    }
  }

  def apply(block: => ResultWithHeaders): Action[AnyContent] = {
    this.apply(_ => block)
  }

  def userFromRequest(request: Request[AnyContent]): Option[User] = {
    request.session.get("token").flatMap(token => User.findByApplicationToken(token))
  }

}

