package categorizer


object AddressParser {

  // recibe un texto libre y busca patterns del tipo:
  //   xxx esquina yyy, xxx y yyy, xxx esq yyy, xxx esq. yyyy, xxxx 11111, xxxx al 1111
  // y retorna una lista de posibles direcciones
  // tomando las 5 palaras anteriores y posteriores al token de reconocimiento
  // ej: guess("en la esquina de Honorio pueyrredon y San Martin hay un bache sin tapar")
  // retorna (la esquina de honorio pueyrredon) (san martin hay un bache)
  // ej2: en la plaza que esta en hipolito irigoyen al 400 hay un semaforo roto
  // retorna: (que esta en hipolito irigoyen)(400)
  private def guess(text: String): Seq[Address] = {
    val corner = """.*?((?:\w+\W+){1,5})(?:y|esquina)((?:\W+\w+){1,5}).*""".r
    Seq[Address]()
  }

  // recibe un string que sospechamos contiene una calle
  // y realiza las busquedas parciales
  // ej: la esquina de honorio pueyrredon
  // intentara matchear las siguientes calles:
  // la esquina de honorio pueyrredon
  // esquina de honorio pueyrredon
  // de honorio pueyrredon
  // honorio pueyrredon -> retorna esta calle
  // pueyrredon
  private def streetMatchFromHead(street: String): Option[String] = None

  // recibe un string que sospechamos contiene una calle
  // y realiza las busquedas parciales
  // ej: san martin hay un bache
  // intentara matchear las siguientes calles:
  // san martin hay un bache
  // san martin hay un
  // san martin hay
  // san martin -> retorna esta calle
  private def streetMatchFromTail(street: String): Option[String] = None

  def parse(text: String): Seq[Address] = Seq[Address]()

}

// algunas expresiones regulares que pueden ser utiles
// """^\W*?(\w+(?:\W+|$)){1,4}""".r.findAllIn("a ver las primeras cuatro palabras").toList
// """((?:\w+(?:\W+|$)){1,4})$""".r.findAllIn("a ver las ultimas cuatro palabras").toList

// pre y pos
// scala> """((?:\w+(?:\W+|$)){1,3})y\W+((?:\w+(?:\W+|$)){1,3})""".r.findAllIn("pre1, pre2, pre3, pre4, pre5 y pos1 pos2 pos3 pos4 pos5, ah claro y tambien esto no se olviden de y tambien otro mas").toList
// res90: List[String] = List("pre3, pre4, pre5 y pos1 pos2 pos3 ", "pos5, ah claro y tambien esto no ", se olviden de y tambien otro mas)

// scala> val r = """((?:\w+(?:\W+|$)){1,3})y\W+((?:\w+(?:\W+|$)){1,3})""".r
// r: scala.util.matching.Regex = ((?:\w+(?:\W+|$)){1,3})y\W+((?:\w+(?:\W+|$)){1,3})

// scala> val r(x,y) = "pre3, pre4, pre5 y pos1 pos2 pos3 "
// x: String = "pre3, pre4, pre5 "
// y: String = "pos1 pos2 pos3 "

// more complete example:

// scala> val r = """((?:\w+(?:\W+|$)){1,3})(?:y|esq|esquina|esq\.)\W+((?:\w+(?:\W+|$)){1,3})""".r
// r: scala.util.matching.Regex = ((?:\w+(?:\W+|$)){1,3})(?:y|esq|esquina|esq\.)\W+((?:\w+(?:\W+|$)){1,3})

// scala> r.findAllIn("en corrientes y callao, cerca de rivadavia esq. aramburu, y no muy lehos de larrea esquina san martin") 
// res91: scala.util.matching.Regex.MatchIterator = non-empty iterator

// scala> r.findAllIn("en corrientes y callao, cerca de rivadavia esq. aramburu, y no muy lehos de larrea esquina san martin").toList
// res92: List[String] = List("en corrientes y callao, cerca de ", "rivadavia esq. aramburu, y no muy lehos ", de larrea esquina san martin)

// scala> val r (x,y) = "en corrientes y callao, cerca de "
// x: String = "en corrientes "
// y: String = "callao, cerca de "

// scala> val r (x,y) =  "rivadavia esq. aramburu, y no muy lehos "
// x: String = "rivadavia esq. aramburu, "
// y: String = "no muy lehos "

// scala> val r (x,y) =  "de larrea esquina san martin"
// x: String = "de larrea "
// y: String = san martin


// OK!

// separar y de esq|esquina|esq.
// 
// scala> val r = """((?:\w+(?:\W+|$)){1,3})(?:y)\W+((?:\w+(?:\W+|$)){1,3})""".r
// r: scala.util.matching.Regex = ((?:\w+(?:\W+|$)){1,3})(?:y)\W+((?:\w+(?:\W+|$)){1,3})

// scala> r.findAllIn("en corrientes y callao, cerca de rivadavia esq. aramburu, y no muy lehos de larrea esquina san martin").toList
// res93: List[String] = List("en corrientes y callao, cerca de ", "rivadavia esq. aramburu, y no muy lehos ")

// scala> val r (x,y) = "en corrientes y callao, cerca de "
// x: String = "en corrientes "
// y: String = "callao, cerca de "

// scala> val r (x,y) = "rivadavia esq. aramburu, y no muy lehos "
// x: String = "rivadavia esq. aramburu, "
// y: String = "no muy lehos "

// scala> val r = """((?:\w+(?:\W+|$)){1,3})(?:esq|esq\.|esquina)\W+((?:\w+(?:\W+|$)){1,3})""".r
// r: scala.util.matching.Regex = ((?:\w+(?:\W+|$)){1,3})(?:esq|esq\.|esquina)\W+((?:\w+(?:\W+|$)){1,3})

// scala> r.findAllIn("en corrientes y callao, cerca de rivadavia esq. aramburu, y no muy lehos de larrea esquina san martin").toList
// res94: List[String] = List("cerca de rivadavia esq. aramburu, y no ", lehos de larrea esquina san martin)

// scala> val r (x,y) = "cerca de rivadavia esq. aramburu, y no "
// x: String = "cerca de rivadavia "
// y: String = "aramburu, y no "

// scala> val r (x,y) = lehos de larrea esquina san martin
// <console>:10: error: not found: value lehos
//        val r (x,y) = lehos de larrea esquina san martin
//                      ^

// scala> val r (x,y) = "lehos de larrea esquina san martin"
// x: String = "lehos de larrea "
// y: String = san martin

case class Address(
  val original: String,
  val pos: Int, 
  val street1: String, 
  val street2: String, 
  val number: Option[Long]
)