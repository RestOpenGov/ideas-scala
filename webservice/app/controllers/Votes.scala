package controllers

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.VoteFormatter.JsonVoteFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter

import models.Vote

import play.api.mvc.Controller

import utils.actions.{CrudAction, CrudAuthAction}

object Votes extends Controller {

  def list = CrudAction.list { request =>
    Vote.find(request.queryString)
  }

  def count = CrudAction.count { request =>
    Vote.count(request.queryString)
  }

  def show(id: Long) = CrudAction.show { request =>
    Vote.findByIdWithErr(id)
  }

  def save() = CrudAuthAction.save { (req, vote: Vote) =>
    vote.copy(userId = req.user.id.get).save
  }

  def update(id: Long) = CrudAuthAction.update { (req, vote: Vote) =>
    vote.withId(id).copy(userId = req.user.id.get).update
  }

  def delete(id: Long) = CrudAuthAction.delete {
    Vote.deleteWithErr(id)
  }

}