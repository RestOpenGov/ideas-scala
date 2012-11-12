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
import plugins.wordlist.TeatrosWordlistPlugin
import plugins.wordlist.PoliticaWordlistPlugin
import plugins.wordlist.SitiosWordlistPlugin
import plugins.wordlist.FutbolWordlistPlugin

case class DispatcherResponse(json: JsValue)

class DispatcherActor extends Actor {

  private val address   = context.actorOf(Props[AddressPlugin],           name = "address")
  private val temporal  = context.actorOf(Props[TemporalWordlistPlugin],  name = "temporal")
  private val teatros   = context.actorOf(Props[TeatrosWordlistPlugin],   name = "teatros")
  private val politica  = context.actorOf(Props[PoliticaWordlistPlugin],  name = "politica")
  private val sitios    = context.actorOf(Props[SitiosWordlistPlugin],    name = "sitios")
  private val futbol    = context.actorOf(Props[FutbolWordlistPlugin],    name = "futbol")

  implicit val timeout  = Timeout(5 seconds)

  def receive = {
    case msg: String => {
    //   val response = for {
    //     teatrosResult   <- teatros ? msg
    //   } yield {
    //     DispatcherResponse(toJson(
    //       teatrosResult.asInstanceOf[Seq[Token]]
    //     ))
    //   }
    //   sender ! response
    // }
      val response = for {
        addressResult   <- address ? msg
        temporalResult  <- temporal ? msg
        teatrosResult   <- teatros ? msg
        politicaResult  <- politica ? msg
        sitiosResult    <- sitios ? msg
        futbolResult    <- futbol ? msg
      } yield {
        val addressTokens   = addressResult.asInstanceOf[Seq[Token]]
        val temporalTokens  = temporalResult.asInstanceOf[Seq[Token]]
        val teatrosTokens   = teatrosResult.asInstanceOf[Seq[Token]]
        val politicaTokens  = politicaResult.asInstanceOf[Seq[Token]]
        val sitiosTokens    = sitiosResult.asInstanceOf[Seq[Token]]
        val futbolTokens    = futbolResult.asInstanceOf[Seq[Token]]
        DispatcherResponse(toJson(
          addressTokens ++ 
          temporalTokens ++ 
          teatrosTokens ++
          politicaTokens ++
          sitiosTokens ++
          futbolTokens
        ))
      }
      sender ! response
    }
  }

}