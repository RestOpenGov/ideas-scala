package utils

import utils.sql.FieldType

object Sql {

  def sanitize(value: String): String = {
    value.replaceAll("'", "''")
  }

  def formatValue(value: String)(implicit fieldType: FieldType.Value): String = {

    import utils.sql.FieldType._

    fieldType match {
      case Numeric  => value
      case String   => "'" + sanitize(value) + "'"
      // #TODO
      case Date     => value
      case Boolean  => {
        value.toLowerCase match {
          case "0" | "off" | "false" | "no" | "null" => "0"
          case _ => "1"
        }
      }
      case Unknown  => value
    }
  }

  def replaceFields(sql: String, fields: String): String = {
    val parseSql = """(?imx)                #insensitive case, multiline, whitespaces and comments
      (^ select \s+ #(?:top \s+ \d+ \s+)?)  #m1: select clause and optional clauses
        (?:top \s+ \d+ \s+)?                #  top x clause (ignored match)
        (?:(?:distinct|all) \s+)?           #  distinct | all clause (ignored match)
      )
      (.+?)                                 #m2: the field clause I'm looking for, non greedy to leave spaces to match3
      (\s+ from \s+ .* $)                   #m3: the rest of the sql sentence, greedy spaces
    """.r
    val replace = "$1%s$3".format(fields)   // replace match2 with new fields
    parseSql.replaceFirstIn(sql, replace)
  }
}