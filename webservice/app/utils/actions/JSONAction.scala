package utils.actions

import play.api.mvc._
import play.api.mvc.Results
import play.api.mvc.Results.Ok
import play.api.http.Status

import play.api.libs.json.{JsValue, Reads, Writes, Format}
import play.api.libs.json.Json.toJson

import utils.JsonBadRequest

object JSONAction extends BodyParsers {

  import CORSAction.ResultWithHeaders

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

  // passes the request to the block
  def fromRequest[T: Writes](block: Request[AnyContent] => T): Action[AnyContent] = {
    fromRequest(Status.OK)(block)
  }

  def withErr[E: Writes, T: Writes](block: => Either[E, T]): Action[AnyContent] = {
    withErr(Status.OK, Status.BAD_REQUEST)(block)
  }

  def withErr[E: Writes, T: Writes](statusOk: Int)(block: => Either[E, T]): Action[AnyContent] = {
    withErr(statusOk, Status.BAD_REQUEST)(block)
  }

  def withErr[E: Writes, T: Writes](statusOk: Int, statusErr: Int)     // Status.OK
    (block: => Either[E, T]): Action[AnyContent] = {

    CORSAction {
      block.fold(
        errors          => resultWithStatus(statusErr)(toJson(errors)),
        responseEntity  => resultWithStatus(statusOk)(toJson(responseEntity))
      )
    }
  }

  def parseWithErr[E: Writes, T: Format](block: T => Either[E, T]): Action[JsValue] = {
    parseWithErr(Status.OK, Status.BAD_REQUEST)(block)
  }

  def parseWithErr[E: Writes, T: Format](statusOk: Int)(block: T => Either[E, T]): Action[JsValue] = {
    parseWithErr(statusOk, Status.BAD_REQUEST)(block)
  }

  def parseWithErr[E: Writes, T: Format](statusOk: Int, statusErr: Int)     // Status.OK
    (block: T => Either[E, T]): Action[JsValue] = {

    CORSAction(parse.json) { request =>
      request.body.asOpt[T].map { requestEntity =>
        block(requestEntity).fold(
          errors          => resultWithStatus(statusErr)(toJson(errors)),
          responseEntity  => resultWithStatus(statusOk)(toJson(responseEntity))
        )
      }.getOrElse       (JsonBadRequest("Invalid entity"))
    }
  }

}
