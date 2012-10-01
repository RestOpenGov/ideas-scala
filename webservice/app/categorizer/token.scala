package categorizer

case class Token(
  val category: String        = "undefined",
  val original: String        = "undefined",
  val text: String            = "undefined",
  val lat: Option[Long]       = None,
  val long: Option[Long]      = None,
  val tags: Seq[String]       = Seq[String]()
)