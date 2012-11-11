package org.restopengov.Armadillo.backend

import akka.actor.{Actor, ActorRef}

trait Plugin extends Actor {

  def receive = {
    case msg: String => {
      sender ! parse(msg)
    }
  }

  def parse(input: String): Seq[Token]
}

case class Token(
  val category: String    = "undefined",
  val original: String    = "undefined",
  val text: String        = "undefined",
  val lat: Option[String] = None,
  val long: Option[String]= None,
  val tags: Seq[String]   = Seq[String]()
) 