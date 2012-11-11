package categorizer

import akka.actor.{Actor, ActorRef}

trait Plugin extends Actor {

  def receive = {
    case msg: String => {
      sender ! categorize(msg)
    }
  }

  def categorize(freeText: String): Seq[Token]

  // get rid of html tags
  // normalize spaces
  def normalizeInput(text: String): String = {
    import utils.StringHelper._
    stripHtmlTags(normalizeHtmlSpaces(text))
  }

}
