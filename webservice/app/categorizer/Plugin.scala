package categorizer

trait Plugin {

  def categorize(freeText: String): Seq[Token]

}
