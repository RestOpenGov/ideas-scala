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

}
