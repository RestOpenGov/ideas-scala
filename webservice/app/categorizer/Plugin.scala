package categorizer

import akka.actor.{Actor, ActorRef}

trait Plugin extends Actor {

  def receive = {
    case msg: String => {
      sender ! categorize(msg)
    }
  }

  def categorize(freeText: String): Seq[Token]

}
