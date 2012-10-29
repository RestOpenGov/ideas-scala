package controllers

import play.api._
import play.api.mvc._

import models.{IdeaType, Error}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.IdeaTypeFormatter._
import formatters.json.ErrorFormatter._

import scala.collection.immutable.Map
import utils.actions.{CORSAction, JSONAction}
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

object IdeaTypes extends Controller {

  def list = CORSAction { request =>
    Ok(toJson(IdeaType.find(request.queryString)))
  }

  def count = CORSAction { request =>
    Ok(toJson(IdeaType.count(request.queryString)))
  }

  def show(id: Long) = CORSAction {
    IdeaType.findById(id).map { ideatype =>
      Ok(toJson(ideatype))
    }.getOrElse(JsonNotFound("Type of idea with id %s not found".format(id)))
  }

  def save() = JSONAction { ideatype: IdeaType =>
    ideatype.save.fold(
      errors => JsonBadRequest(errors),
      ideatype => Created(toJson(ideatype))
    )
  }

  // def save() = CORSAction(parse.json) { implicit request =>
  //   request.body.asOpt[IdeaType].map { ideatype =>
  //     ideatype.save.fold(
  //       errors => JsonBadRequest(errors),
  //       ideatype => Ok(toJson(ideatype))
  //     )
  //   }.getOrElse     (JsonBadRequest("Invalid type of idea entity"))
  // }

  def update(id: Long) = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[IdeaType].map { ideatype =>
        ideatype.copy(id=Id(id)).update.fold(
          errors => JsonBadRequest(errors),
          ideatype => Ok(toJson(ideatype))
        )
      }.getOrElse       (JsonBadRequest("Invalid type of idea entity"))
    }.getOrElse         (JsonBadRequest("Expecting JSON data"))
  }

  def delete(id: Long) = CORSAction { implicit request =>
    IdeaType.delete(id)
    JsonOk("IdeaType successfully deleted","Type of idea with id %s deleted".format(id))
  }

  // def parse[T: play.api.libs.json.Reads](request: Request[AnyContent]): Option[T] = {
  //   request.body.asJson.map { json =>
  //     json.asOpt[T]
  //   }.getOrElse(None)
  // }

}