package utils.actions

import play.api.mvc._
import models.{User, Error}

import utils.JsonUnauthorized

import models.ValidationError

import services.security.SecurityManager

import utils.Http.toFlatQueryString

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

  def validateUserFromRequest(request: Request[AnyContent]): Either[List[Error], User] = {
    applicationTokenFromRequest(request).map { applicationToken =>
      SecurityManager.findUserByApplicationToken(applicationToken)
    } getOrElse {
      Left(List(ValidationError(
        Error.AUTHENTICATION, "applicationToken", 
        "Token not found in authentication header nor in ideas_token querystring param"
      )))
    }
  }

  // first tries to find the token in a header: "authorization: ideas-token=xxxxx"
  // then tries to find the token in the querystring: "ideas_token=xxxxx"
  def applicationTokenFromRequest(request: Request[AnyContent]): Option[String] = {

    val Token = """^\s*ideas-token\s*=\s*(\w+)\s*$""".r

    request.headers.get("authorization") collect {
      case Token(t) => t
    } orElse toFlatQueryString(request.queryString).get("ideas-token")

  }

}

