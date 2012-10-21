package controllers

import play.api._
import play.api.mvc._

import models.{Entity, EntityCompanion, Error}
import anorm.Id

import play.api.libs.json.Format
import play.api.libs.json.Json.toJson

import utils.CORSAction
import utils.{JsonBadRequest, JsonNotFound, JsonOk}

abstract class CrudController[A <: Entity](
  val model: EntityCompanion[A],
  val name: String,
  implicit val formatter: Format[A]
) extends Controller {

  def list = CORSAction { request =>
    Ok(toJson(model.find(request.queryString)))
  }

  def count = CORSAction { request =>
    Ok(toJson(model.count(request.queryString)))
  }

  def show(id: Long) = CORSAction {
    model.findById(id).map { entity =>
      Ok(toJson(entity))
    }.getOrElse(JsonNotFound("%s with id %s not found".format(name, id)))
  }

  def save() = CORSAction { implicit request =>
    parse[A](request).map { entity: A =>
      entity.save.fold(
        errors => JsonBadRequest(errors),
        entity => Ok(toJson(entity.asInstanceOf[A]))
      )
    }.getOrElse     (JsonBadRequest("Invalid %s".format(name)))
  }

  def update(id: Long) = CORSAction { implicit request =>
    parse[A](request).map { entity: A =>
      entity.withId(id).update.fold(
        errors => JsonBadRequest(errors),
        entity => Ok(toJson(entity.asInstanceOf[A]))
      )
    }.getOrElse       (JsonBadRequest("Invalid User entity"))
  }

  def delete(id: Long) = CORSAction { implicit request =>
    model.delete(id)
    JsonOk("%s successfully deleted".format(name),"%s with id %s deleted".format(name, id))
  }

  def parse[T: play.api.libs.json.Reads](request: Request[AnyContent]): Option[T] = {
    request.body.asJson.map { json =>
      json.asOpt[T]
    }.getOrElse(None)
  }

}