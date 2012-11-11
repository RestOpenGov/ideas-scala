package categorizer.plugins.address

import categorizer.plugins.address.SimpleTokenFormatter._

case class SimpleToken(
  val id: String = "0",
  val token: String = "",
  val alias: List[String] = List(),
  val tags: List[String] = List()
  // val alias: List[String] = List(""),
  // val tags: List[String] = List("")
)
