import play.api._
import play.api.mvc._
import play.api.mvc.Results._

import models.Error
import formatters.json.ErrorFormatter._

import play.api.libs.json.Json.toJson
import play.api.http.Status

import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import akka.routing.RoundRobinRouter
import notification.NotificationActor
import notification.NewCommentNotification


object Global extends GlobalSettings {

  override def onError(request: RequestHeader, ex: Throwable) = {
    InternalServerError(toJson(
      Error(
        status = Status.INTERNAL_SERVER_ERROR, 
        message = "Internal server error",
        developerMessage = ex.getMessage()
      )
    ))
  } 

  override def onBadRequest(request: RequestHeader, error: String) = {
    BadRequest(toJson(
      Error(status = Status.BAD_REQUEST, message = error)
    ))
  }

  override def onStart(app: Application) {
    Logger.info("Ideas-ba is Starting...")
    val props = Props[NotificationActor].withRouter(RoundRobinRouter(nrOfInstances = 10))
    val a = Akka.system.actorOf(props, name = "notificationActor");
  }  

}