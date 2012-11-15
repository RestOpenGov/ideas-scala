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
import plugins.wordlist._

case class DispatcherResponse(json: JsValue)

class DispatcherActor extends Actor {

  // private val temporal      = context.actorOf(Props[TemporalWordlistPlugin],    name = "temporal")

  private val address       = context.actorOf(Props[AddressPlugin],             name = "direccion")
  private val teatros       = context.actorOf(Props[TeatrosWordlistPlugin],     name = "teatros")
  private val politica      = context.actorOf(Props[PoliticaWordlistPlugin],    name = "politica")
  private val sitios        = context.actorOf(Props[SitiosWordlistPlugin],      name = "sitios")
  private val hospitales    = context.actorOf(Props[HospitalesWordlistPlugin],  name = "hospitales")
  private val futbol        = context.actorOf(Props[FutbolWordlistPlugin],      name = "futbol")
  private val barrios       = context.actorOf(Props[BarriosWordlistPlugin],     name = "barrios")
  private val comisarias    = context.actorOf(Props[ComisariasWordlistPlugin],  name = "comisarias")
  private val disco         = context.actorOf(Props[DiscoWordlistPlugin],       name = "disco")
  private val tags          = context.actorOf(Props[TagsWordlistPlugin],        name = "tags")

  implicit val timeout  = Timeout(10 seconds)

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
        addressResult     <- address ? msg
        teatrosResult     <- teatros ? msg
        politicaResult    <- politica ? msg
        sitiosResult      <- sitios ? msg
        hospitalesResult  <- hospitales ? msg
        futbolResult      <- futbol ? msg
        barriosResult     <- barrios ? msg
        comisariasResult  <- comisarias ? msg
        discoResult       <- disco ? msg
        tagsResult        <- tags ? msg
      } yield {
        val addressTokens      = addressResult.asInstanceOf[Seq[Token]]
        val teatrosTokens      = teatrosResult.asInstanceOf[Seq[Token]]
        val politicaTokens     = politicaResult.asInstanceOf[Seq[Token]]
        val sitiosTokens       = sitiosResult.asInstanceOf[Seq[Token]]
        val hospitalesTokens   = hospitalesResult.asInstanceOf[Seq[Token]]
        val futbolTokens       = futbolResult.asInstanceOf[Seq[Token]]
        val barriosTokens      = barriosResult.asInstanceOf[Seq[Token]]
        val comisariasTokens   = comisariasResult.asInstanceOf[Seq[Token]]
        val discoTokens        = discoResult.asInstanceOf[Seq[Token]]
        val tagsTokens         = tagsResult.asInstanceOf[Seq[Token]]

        val tokens = (
          addressTokens ++ 
          teatrosTokens ++
          politicaTokens ++
          sitiosTokens ++
          hospitalesTokens ++
          futbolTokens ++
          barriosTokens ++
          comisariasTokens ++
          discoTokens ++
          tagsTokens
        ).distinct

        DispatcherResponse(toJson(tokens))
      }
      sender ! response
    }
  }

}