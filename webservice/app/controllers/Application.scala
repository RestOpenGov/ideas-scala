package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  // no longer needed, handled at routes file
  // def app = Action {
  //   Redirect(routes.Assets.at("index.html"))
  // }

  def api = Action { implicit request =>
    Ok(views.html.api())
  }

  def options(url: String) = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Content-Type, X-Requested-With, Accept",
      // cache access control response for one day
      "Access-Control-Max-Age" -> (60 * 60 * 24).toString
    )
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Ideas.up, Ideas.down,
        Comments.up, Comments.down
      )
    ).as("text/javascript") 
  }

  def mail = Action { 
    val mail = "mail"
    play.Logger.info("Applecation.controller MAil" + mail)
    Ok(views.html.commentCreatedMailTemplate(mail, mail, mail, mail, mail, mail, mail, "1"))
  }

}
