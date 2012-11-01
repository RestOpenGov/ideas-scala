package utils.actions

import play.api.mvc._
import play.api.mvc.Results
import play.api.mvc.Results.Ok
import play.api.http.Status.{OK, CREATED, NOT_FOUND, BAD_REQUEST}

import play.api.libs.json.{JsValue, Reads, Writes, Format}
import play.api.libs.json.Json.toJson

import utils.JsonBadRequest

import JSONAction.{fromRequest, withErr, parseWithErr}

object CrudAction extends BodyParsers {

  def list[T: Writes](block: Request[AnyContent] => T): Action[AnyContent] = {
    fromRequest(OK)(block)
  }

  def count[T: Writes](block: Request[AnyContent] => T): Action[AnyContent] = {
    fromRequest(OK)(block)
  }

  def show[E: Writes, T: Writes](block: Request[AnyContent] => Either[E, T]): Action[AnyContent] = {
    withErr(OK, NOT_FOUND)(block)
  }

  def show[E: Writes, T: Writes](block: => Either[E, T]): Action[AnyContent] = {
    withErr(OK, NOT_FOUND)(request => block)
  }

  def save[E: Writes, T: Format]
    (block: (Request[JsValue], T) => Either[E, T])
  : Action[JsValue] = {
    parseWithErr(CREATED, BAD_REQUEST)(block)
  }

  def save[E: Writes, T: Format]
    (block: T => Either[E, T])
  : Action[JsValue] = {
    parseWithErr(CREATED, BAD_REQUEST)( (request, entity: T) => block(entity) )
  }

  def update[E: Writes, T: Format]
    (block: (Request[JsValue], T) => Either[E, T])
  : Action[JsValue] = {
    parseWithErr(OK, BAD_REQUEST)(block)
  }

  def update[E: Writes, T: Format]
    (block: T => Either[E, T])
  : Action[JsValue] = {
    parseWithErr(OK, BAD_REQUEST)( (request, entity: T) => block(entity) )
  }

  def delete[E: Writes, T: Writes](block: Request[AnyContent] => Either[E, T]): Action[AnyContent] = {
    withErr(OK, BAD_REQUEST)(block)
  }

  def delete[E: Writes, T: Writes](block: => Either[E, T]): Action[AnyContent] = {
    withErr(OK, BAD_REQUEST)(request => block)
  }

}
