package categorizer.plugins.wordlist

import categorizer.SimpleTokenFormatter._
import categorizer.SimpleToken

case class WordlistTokenFile(
  file: String              = "unknown file",
  tags: List[String]        = List(),
  tokens: List[SimpleToken] = List()
)

object WordlistTokenFile {

  def fromCSV(file: String): WordlistTokenFile = {
    WordlistTokenFile()
  }

}