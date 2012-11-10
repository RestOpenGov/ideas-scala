package controllers

import play.api._
import play.api.mvc._
import play.libs.Akka
import play.api.libs.concurrent._
import akka.actor.{ ActorSystem, Props}
import akka.dispatch.{Await, Future}
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
//import utils.actions.AsyncCORSAction
import org.restopengov.Armadillo._

object Categorizer extends Controller {

	private lazy val dispatcher = Akka.system.actorOf(Props[DispatcherActor], name = "dispatcher")
	implicit val timeout = Timeout(5 seconds)

  	def categorize = Action { implicit request =>  

    	val input = request.queryString.get("input").getOrElse(Seq(""))(0)
		val futureResponse = ask(dispatcher, input).mapTo[Future[Future[DispatcherResponse]]]
	 	val dispatcherResponse = futureResponse flatMap { x => x }

        Async {
			dispatcherResponse.mapTo[DispatcherResponse].asPromise.map { r => 
				Ok(r.json)
			}
		}	
		
  	}
}
