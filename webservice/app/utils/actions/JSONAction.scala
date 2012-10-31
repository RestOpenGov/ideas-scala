package utils.actions

import play.api.http.Status

import play.api.libs.json.{Format, JsValue, Writes}
import play.api.libs.json.Json.toJson

import play.api.mvc.{Action, AnyContent, BodyParsers, Request, Results}

import utils.JsonBadRequest

object JSONAction extends BodyParsers {

  // helper function to create a result with a custom status
  private def resultWithStatus(status: Int = Status.OK) = {
    new Results.Status(status)
  }

  // passes the request to the block
  def fromRequest[T: Writes](status: Int)(block: Request[AnyContent] => T): Action[AnyContent] = {
    CORSAction { request: Request[AnyContent] =>
      resultWithStatus(status)(toJson(block(request)))
    }
  }

  def withErr[E: Writes, T: Writes](statusOk: Int, statusErr: Int)     // Status.OK
    (block: Request[AnyContent] => Either[E, T]): Action[AnyContent] = {

    CORSAction { request =>
      block(request).fold(
        errors          => resultWithStatus(statusErr)(toJson(errors)),
        responseEntity  => resultWithStatus(statusOk)(toJson(responseEntity))
      )
    }
  }

  def parseWithErr[E: Writes, T: Format](statusOk: Int, statusErr: Int)     // Status.OK
    (block: (Request[JsValue], T) => Either[E, T]): Action[JsValue] = {

    CORSAction(parse.json) { request =>
      request.body.asOpt[T].map { requestEntity =>
        block(request, requestEntity).fold(
          errors          => resultWithStatus(statusErr)(toJson(errors)),
          responseEntity  => resultWithStatus(statusOk)(toJson(responseEntity))
        )
      }.getOrElse       (JsonBadRequest("Invalid entity"))
    }
  }

}
