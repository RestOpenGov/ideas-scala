package categorizer

import plugins._
import TokenFormatter._

import akka.actor.{Actor, ActorRef}
import akka.actor.Props
import akka.dispatch.{Await, Future}
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._

import play.api.libs.json.Json.toJson
import play.api.libs.json.JsValue

import plugins.address.AddressPlugin
import plugins.wordlist.TemporalWordlistPlugin

case class DispatcherResponse(json: JsValue)

class DispatcherActor extends Actor {

  private val address = context.actorOf(Props[AddressPlugin], name = "address")
  private val temporalWordlist = context.actorOf(Props[TemporalWordlistPlugin], name = "temporalWordlist")
  implicit val timeout = Timeout(5 seconds)

  def receive = {
    case msg: String => {

      val response = for {
        addressResult <- address ? msg
        temporalWordlistResult <- temporalWordlist ? msg
      } yield {
        val addr = addressResult.asInstanceOf[Seq[Token]]
        val tmpWords = temporalWordlistResult.asInstanceOf[Seq[Token]]
        DispatcherResponse(toJson(addr ++ tmpWords))
      }
      sender ! response
    }
  }

}