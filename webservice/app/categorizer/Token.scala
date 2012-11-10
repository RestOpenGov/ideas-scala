package categorizer

case class Token(
  val category: String        = "undefined",
  val original: String        = "undefined",
  val text: String            = "undefined",
  val lat: Option[Double]     = None,
  val lng: Option[Double]     = None,
  val tags: Seq[String]       = Seq[String]()
)