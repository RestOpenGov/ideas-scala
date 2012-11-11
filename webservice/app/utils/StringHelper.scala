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

}
