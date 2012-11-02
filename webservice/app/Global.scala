import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.Logger

import models.Error
import formatters.json.ErrorFormatter._

import play.api.libs.json.Json.toJson
import play.api.http.Status

import notification._
import akka.util.duration._
import play.api.libs.concurrent.Akka
import play.api.Play.current



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
    val greeting = 
    """
     _      _                                    ___    _____ 
    (_)    ( )                                  (  _`\ (  _  )
    | |   _| |   __     _ _   ___     ______    | (_) )| (_) |
    | | /'_` | /'__`\ /'_` )/',__)   (______)   |  _ <'|  _  |
    | |( (_| |(  ___/( (_| |\__, \              | (_) )| | | |
    (_)`\__,_)`\____)`\__,_)(____/              (____/'(_) (_)
                                                        
    IDEASBA IS STARTING ...
               ^^^^^^^^
    """
    Logger.info(greeting)

    Akka.system.scheduler.schedule(4 hours, 4 hours,
      NotificationService.reportActor, ReportNotification());
    Logger.info("Report Scheduler started.")

  }  

}