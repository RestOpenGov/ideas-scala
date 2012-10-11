package utils.query

import play.Logger

import exceptions.InvalidQueryConditionException

case class Condition(
  original : String, 
  prefix   : String, 
  field    : String, 
  negated  : Boolean, 
  operator : ConditionOperator.Value, 
  values   : List[String]) {

  def description: String = {

    import utils.query.ConditionOperator._

    val neg = if (negated) " not" else ""
    val value = this.values(0)
    val fieldName = if (prefix == "") field else "%s.%s".format(prefix, field)
    
    operator match {
      case Equal           => "%s should%s be equal to %s".format(fieldName, neg, value)
      case NotEqual        => "%s should%s be not equal to %s".format(fieldName, neg, value)
      case GreaterOrEqual  => "%s should%s be greater than or equal to %s".format(fieldName, neg, value)
      case Greater         => "%s should%s be greater than %s".format(fieldName, neg, value)
      case LessOrEqual     => "%s should%s be less than or equal to %s".format(fieldName, neg, value)
      case Less            => "%s should%s be less than %s".format(fieldName, neg, value)
      case Between         => "%s should%s be between %s and %s".format(fieldName, neg, this.values(0), this.values(1))
      case In              => "%s should%s be one of %s".format(fieldName, neg, this.values.mkString(", "))
      case StartsWith      => "%s should%s start with %s".format(fieldName, neg, value)
      case EndsWith        => "%s should%s end with %s".format(fieldName, neg, value)
      case Contains        => "%s should%s contain %s".format(fieldName, neg, value)
      case Missing         => "%s should%s (missing operator!) %s".format(fieldName, neg, value)
      case Unknown         => "%s should%s have something to do with %s".format(fieldName, neg, value)
    }
  }

  def withMapping(mappings: Map[String, String]): Condition = {
    val newPrefix = if (mappings.contains(this.prefix)) mappings(this.prefix) else this.prefix
    this.copy(prefix = newPrefix)
  }

}

object Condition {
  def apply(original: String, prefix: String, field: String, negated: Boolean, 
    operator: ConditionOperator.Value, value: String): Condition = {
    Condition(original, prefix, field, negated, operator, List(value))
  }

  def apply(original: String, prefix: String, field: String, negated: Boolean, 
    operator: ConditionOperator.Value, value1: String, value2: String): Condition = {
    Condition(original, prefix, field, negated, operator, List(value1, value2))
  }

}

object ConditionParser {

  import ConditionOperator._

  def parse(conditions: String): List[Condition] = {
    conditions.split(",").map{ condition =>
      parseSingleCondition(condition)
    }.toList
  }

  def parseSingleCondition(condition: String): Condition = {

    if (condition == "") {
      return Condition("", "", "", false, Missing, List[String]())
      throw new InvalidQueryConditionException(
        "Error parsing query condition. Condition is empty.")
    }

    val conditionRegExp = """(?mx)    #multiline, whitespaces and comments
      ^(?:([\w|\.]*)\.)?              #prefix, optionally nested table1.table2., don't capture last dot
      ([\w-]*)                        #field, allow hyphens
      [:]?                            #optional colon separator
      (!?)                            #negated
      (=|:|\$|<=|>=|<>|<|>|){1}+      #operator, mandatory, only one
      (.*)$                           #value
    """.r

    if (!conditionRegExp.pattern.matcher(condition).matches) {
      throw new InvalidQueryConditionException(
        "Error parsing query condition '%s'.".format(condition))
    }

    val conditionRegExp(parsedPrefix, parsedField, parsedNegated, parsedOperator, parsedValue) = condition

    val prefix = if (parsedPrefix==null) "" else parsedPrefix

    if (parsedField == "") {
      throw new InvalidQueryConditionException(
        "Error parsing query condition '%s' No field specified.".format(condition))
    }

    if (parsedValue == "") {
      throw new InvalidQueryConditionException(
        "Error parsing query condition '%s' No value specified.".format(condition))
    }

    val field = parsedField
    val negated = (parsedNegated == "!")
    val operator = ConditionOperator.toConditionOperator(parsedOperator)

    operator match {
      case Equal | Missing | Unknown => {

        val betweenRegExp    = """^(\w*)\.\.(\w*)$""".r
        val inRegExp         = """^(.*;.*)$""".r
        val containsRegExp   = """^\*(.*)\*$""".r
        val startsWithRegExp = """^(.*)\*$""".r
        val endsWithRegExp   = """^\*(.*)$""".r

        parsedValue match {

          case betweenRegExp(from, to) => {
            if (from.isEmpty && to.isEmpty) {
              throw new InvalidQueryConditionException(
                "Error parsing query condition '%s' You have to specify value 'from' or 'to' when using between operator.".format(condition))
            }
            if (from.isEmpty) {
              Condition(condition, prefix, field, negated, LessOrEqual, to)
            } else if (to.isEmpty) {
              Condition(condition, prefix, field, negated, GreaterOrEqual, from)
            } else {
              Condition(condition, prefix, field, negated, Between, from, to)
            }
          }

          case inRegExp(value) => {
            Condition(condition, prefix, field, negated, In, value.split(";").toList)
          }

          case containsRegExp(value) => {
            Condition(condition, prefix, field, negated, Contains, value)
          }

          case startsWithRegExp(value) => {
            Condition(condition, prefix, field, negated, StartsWith, value)
          }

          case endsWithRegExp(value) => {
            Condition(condition, prefix, field, negated, EndsWith, value)
          }

          case _ => {
            Condition(condition, prefix, field, negated, operator, parsedValue)
          }
        }
      }
      
      case _ => {   // case Equal | Missing | Unknown
        Condition(condition, prefix, field, negated, operator, parsedValue)
      }
    }

  }
}

object ConditionOperator extends Enumeration {

  val Equal           = Value("equal")
  val NotEqual        = Value("notEqual")
  val GreaterOrEqual  = Value("greaterOrEqual")
  val Greater         = Value("greater")
  val LessOrEqual     = Value("lessOrEqual")
  val Less            = Value("less")
  val Between         = Value("between")
  val In              = Value("in")
  val StartsWith      = Value("startsWith")
  val EndsWith        = Value("endsWith")
  val Contains        = Value("contains")
  val Missing         = Value("missing")
  val Unknown         = Value("unknown")

  def toSqlOperator(value: ConditionOperator.Value): String = {
    value match {
      case ConditionOperator.Equal            => "="
      case ConditionOperator.NotEqual         => "<>"
      case ConditionOperator.GreaterOrEqual   => ">="
      case ConditionOperator.Greater          => ">"
      case ConditionOperator.LessOrEqual      => "<="
      case ConditionOperator.Less             => "<"
      case ConditionOperator.Between          => "between %s and %s"
      case ConditionOperator.In               => "in (%s)"
      case ConditionOperator.StartsWith       => "like '%s*'"
      case ConditionOperator.EndsWith         => "like '*%s'"
      case ConditionOperator.Contains         => "like '*%s*'"
      case ConditionOperator.Missing          => ""
      case ConditionOperator.Unknown          => ""
    }
  }

  def toConditionOperator(value: String): ConditionOperator.Value = {
    if (value==null) {
      ConditionOperator.Unknown
    } else {
      value.toLowerCase match {
        case "equal" | "=" | ":"             => ConditionOperator.Equal
        case "notequal" | "!=" | "!:" | "<>" => ConditionOperator.NotEqual
        case "greaterorequal" | ">="         => ConditionOperator.GreaterOrEqual
        case "greater" | ">"                 => ConditionOperator.Greater
        case "lessorequal" | "<="            => ConditionOperator.LessOrEqual
        case "less" | "<"                    => ConditionOperator.Less
        case "between"                       => ConditionOperator.Between
        case "in"                            => ConditionOperator.In
        case "startswith"                    => ConditionOperator.StartsWith
        case "endswith"                      => ConditionOperator.EndsWith
        case "contains" | "$"                => ConditionOperator.Contains
        case "missing" | ""                  => ConditionOperator.Missing
        case "unknown" | _                   => ConditionOperator.Unknown
      }
    }
  }

}