package categorizer

object MockCategorizer extends Plugin {

  def categorize(freeText: String): Seq[Token] = {

    var tags: Seq[Token] = Seq[Token]()

    //"corientes y uruguay", "corrientes y uruguay", "corientes esquina uruguay"...
    val corrientes_y_uruguay = """(?is).*\b(cor(?:r?)ientes\s+(?:y|esquina|esq)\s+uruguay)\b.*""".r
    if (corrientes_y_uruguay.pattern.matcher(freeText).matches) {
      val corrientes_y_uruguay(original) = freeText
      tags = tags :+ Token( 
        "dirección", original, "Corrientes y Uruguay", Some(10000), Some(20000), 
        Seq("esquina", "dirección", "geo")
      )
    }

    val pueyrredon_al_xxx = """(?is).*\b((pueyrred[o|ó]n)\s+(?:al )?\s*(\d{0,5}))\b.*""".r
    if (pueyrredon_al_xxx.pattern.matcher(freeText).matches) {
      val pueyrredon_al_xxx(original, street, number) = freeText
      tags = tags :+ Token( 
        "dirección", original, "Av. Pueyrredón al %s".format(number), Some(11000), Some(21000), 
        Seq("esquina", "dirección", "geo")
      )
      tags = tags :+ Token( 
        "dirección", original, "Av. Honorio Pueyrredón al %s".format(number), Some(13000), Some(23000), 
        Seq("esquina", "dirección", "geo")
      )
    }

    val lorange = """(?is).*\b(lorange)\b.*""".r
    if (lorange.pattern.matcher(freeText).matches) {
      val lorange(original) = freeText
      tags = tags :+ Token( 
        "teatro", original, "teatro Lorange", Some(14000), Some(24000), 
        Seq("teatro", "cultura", "geo")
      )
    }

    val guerrin = """(?is).*\b(guer[r]?[i|í]n)\b.*""".r
    if (guerrin.pattern.matcher(freeText).matches) {
      val guerrin(original) = freeText
      tags = tags :+ Token( 
        "comida", original, "pizzería Guerrín", None, None, 
        Seq("comida", "pizza")
      )
    }
    play.Logger.info(tags.toString)

    tags
  }

}

/*
/api/message_parser?message=en la esquina de corientes y uruguay, justito al lado del teatro lorange, esta guerrin, la mejor pizzeria de buenos aires!

{
  original: "en la esquina de corientes y uruguay, justito al lado del teatro lorange, está guerrin, la mejor pizzeria de buenos aires!",
  tokens: [

    {
      category: 'address',
      original: 'corientes y uruguay',
      text: 'Corrientes y Uruguay',
      lat: xxx,
      long: xxx
    },

    {
      category: 'theater',
      original: 'teatro lorange',
      text: 'teatro Lorange',
      lat: xxx,
      long: xxx,
      tags: ["cultura", "teatro", "esparcimiento"]
    },

    {
      category: 'food',
      original: 'guerrin',
      text: 'Guerrín',
      lat: xxx,
      long: xxx,
      tags: ["comida"]
    }

  ]

}
 */