package utils

object I18n {

  // val recognizer: LangRecognizer = CategorizerLangRecognizer
  val recognizer: LangRecognizer = DetectorLangRecognizer

  def isSpanish(value: String): Boolean = recognizer.isSpanish(value)
  def isEnglish(value: String): Boolean = recognizer.isEnglish(value)
}

trait LangRecognizer {
  def isSpanish(value: String): Boolean
  def isEnglish(value: String): Boolean
}

object CategorizerLangRecognizer extends LangRecognizer {

  import org.knallgrau.utils.textcat.TextCategorizer

  private lazy val guesser = new TextCategorizer

  def isSpanish(value: String): Boolean = {
    isLanguage(value, "spanish")
  }

  def isEnglish(value: String): Boolean = {
    isLanguage(value, "english")
  }

  // checks if value is a sentence from the specified language
  // maxPostion is the tolerance, by default is 3
  private def isLanguage(value: String, language: String, maxPosition: Int = 3) = {
    import collection.JavaConversions._
    val categories = guesser.getCategoryDistances(value).toSeq.sortBy(_._2).take(maxPosition).map(_._1)

    categories.find(_ == language).isDefined
  }
}

object DetectorLangRecognizer extends LangRecognizer {

  import com.cybozu.labs.langdetect.{DetectorFactory, Detector}

  val profileDirectory = "app/utils/i18n/profiles"

  private lazy val init: Boolean = {
    DetectorFactory.loadProfile(profileDirectory)
    true
  }

  def createDetector: Detector = {
    init
    DetectorFactory.create
  }

  def isSpanish(value: String): Boolean = {
    isLanguage(value, "es")
  }

  def isEnglish(value: String): Boolean = {
    isLanguage(value, "en")
  }

  private def isLanguage(value: String, language: String): Boolean = {
    val detector: Detector = createDetector
    detector.append(value)
    detector.detect == language
  }
}

