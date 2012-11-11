package utils

object StringHelper {

  // trim whitespaces from the end of the string
  def trimTail(text: String): String = {
    text.replaceAll("""(?m)\s+$""", "")
  }

  // trim whitespaces from the end of the string
  def trimHead(text: String): String = {
    text.replaceAll("""(?m)^\s+""", "")
  }

  def trim(text: String): String = {
    trimHead(trimTail(text))
  }

  def normalizeSpaces(text: String): String = {
    text.replaceAll("""\s{2,}""", " ")
  }

  def stripHtmlTags(text: String): String = {
    text.replaceAll("""(?m) <[^>]*>""", "")
  }

  def replaceTildes(text: String): String = {
    val replacements = List(
      ("á", "a"), ("Á", "A"),
      ("é", "e"), ("É", "E"),
      ("í", "i"), ("Í", "I"),
      ("ó", "o"), ("Ó", "O"),
      ("ú", "u"), ("Ú", "U")
    )

    replacements.foldRight(text){ (replacement, text) =>
      text.replaceAll(replacement._1, replacement._2)
    }
  }

  // check http://stackoverflow.com/a/1757107/47633
  // test with: splitCSV("""a,b,"c,d,e",f,,h,i""").foreach( println )
  def splitCSV(text: String): Array[String] = {
    val notQuote = """[^\"]"""                            // any char except a quote
    val quotedString = """\" %s* \"""".format(notQuote)   // a string surrounded by quoted

    val r = """(?mx)                  # multilne, enable comments, ignore white space
      ,                               # match a comma
      (?=                             # start positive look ahead
        (                             # start group 1
          %s*                         #   match 'otherThanQuote' zero or more times
          %s                          #   match 'quotedString'
        )*                            # end group 1 and repeat it zero or more times
        %s*                           # match 'otherThanQuote'
        $                             # match the end of the string
      )                               # stop positive look ahead
    """.format(notQuote, quotedString, notQuote);

    text.split(r).map { _.stripPrefix("\"").stripSuffix("\"") }
  }

}
