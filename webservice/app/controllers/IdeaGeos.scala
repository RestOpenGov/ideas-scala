package controllers

import formatters.json.ErrorFormatter._
import formatters.json.SuccessFormatter._
import formatters.json.IdeaGeoFormatter._

import models.IdeaGeo

import play.api.mvc.Controller

import utils.actions.{CrudAction, CrudAuthAction}

object IdeaGeos extends Controller {

  def list = CrudAction.list { request =>
    IdeaGeo.find(request.queryString)
  }

  def count = CrudAction.count { request =>
    IdeaGeo.count(request.queryString)
  }

  def show(id: Long) = CrudAction.show { request =>
    IdeaGeo.findByIdWithErr(id)
  }

  def save() = CrudAuthAction.save { ideaGeo: IdeaGeo =>
    ideaGeo.save
  }

  def update(id: Long) = CrudAuthAction.update { ideaGeo: IdeaGeo =>
    ideaGeo.withId(id).update
  }

  def delete(id: Long) = CrudAuthAction.delete {
    IdeaGeo.deleteWithErr(id)
  }

}
