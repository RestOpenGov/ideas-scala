package categorizer

import categorizer.SimpleTokenFormatter._

case class SimpleToken(
  val id: String          = "0",
  val token: String       = "",
  val alias: List[String] = List(),
  val lat: Option[Double] = None,
  val lng: Option[Double] = None,
  val tags: List[String]  = List()

)
