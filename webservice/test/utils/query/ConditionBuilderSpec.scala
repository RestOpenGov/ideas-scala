// test with play "test-only test.ConditionBuilder.*"
package test.utils.query

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.Logger

class ConditionBuilderSpec extends Specification {
  import utils.query.ConditionOperator._
  import utils.sql.ColumnInfo
  import utils.sql.FieldType._
  import java.sql.Types._

  import utils.query.Condition
  import exceptions.InvalidQueryConditionException

  "ConditionBuilder.build" should {

    import utils.query.ConditionBuilder.build

    val columnsInfo = List(
      ColumnInfo("test", "test", "finteger",  "finteger", INTEGER,  "integer",  10),
      ColumnInfo("test", "test", "fnumeric",  "fnumeric", NUMERIC,  "numeric",  10),
      ColumnInfo("test", "test", "fstring",   "fstring",  VARCHAR,  "string",   50),
      ColumnInfo("test", "test", "fdate",     "fdate",    DATE,     "date",     10),
      ColumnInfo("test", "test", "fboolean",  "fboolean", BOOLEAN,  "boolean",  10)
    )

    "build the sql condition when dealing with numbers in" in {

      build("finteger=1", columnsInfo)    must equalTo("finteger = 1")
      build("finteger:1", columnsInfo)    must equalTo("finteger = 1")
      build("finteger>1", columnsInfo)    must equalTo("finteger > 1")
      build("finteger:>1", columnsInfo)   must equalTo("finteger > 1")
      build("finteger>=1", columnsInfo)   must equalTo("finteger >= 1")
      build("finteger:>=1", columnsInfo)  must equalTo("finteger >= 1")
      build("finteger<1", columnsInfo)    must equalTo("finteger < 1")
      build("finteger:<1", columnsInfo)   must equalTo("finteger < 1")
      build("finteger<=1", columnsInfo)   must equalTo("finteger <= 1")
      build("finteger:<=1", columnsInfo)  must equalTo("finteger <= 1")
      build("finteger<>1", columnsInfo)   must equalTo("finteger <> 1")
      build("finteger:<>1", columnsInfo)  must equalTo("finteger <> 1")
      build("finteger=1..2", columnsInfo) must equalTo("finteger between 1 and 2")
      build("finteger:1..2", columnsInfo) must equalTo("finteger between 1 and 2")

      build("finteger=*1*", columnsInfo
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must matching(""".*Operator '.*' is only allowed for string fields\..*""")
      }

      build("finteger:*1*", columnsInfo
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must matching(""".*Operator '.*' is only allowed for string fields\..*""")
      }

      build("finteger$1", columnsInfo
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must matching(""".*Operator '.*' is only allowed for string fields\..*""")
      }

      build("finteger:$1", columnsInfo
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must matching(""".*Operator '.*' is only allowed for string fields\..*""")
      }

      build("finteger=1*", columnsInfo
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must matching(""".*Operator '.*' is only allowed for string fields\..*""")
      }

      build("finteger:1*", columnsInfo
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must matching(""".*Operator '.*' is only allowed for string fields\..*""")
      }

      build("finteger=*1", columnsInfo
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must matching(""".*Operator '.*' is only allowed for string fields\..*""")
      }

      build("finteger:*1", columnsInfo
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must matching(""".*Operator '.*' is only allowed for string fields\..*""")
      }

    }

    "build the sql condition when dealing with string in" in {

      build("fstring=text", columnsInfo) must equalTo("lower(fstring) = 'text'")
      build("fstring:=text", columnsInfo) must equalTo("lower(fstring) = 'text'")
      build("fstring:text", columnsInfo) must equalTo("lower(fstring) like '%text%'")
    }

    "accept ':' char as separator" in {

      build("fstring<>value", columnsInfo) must equalTo(
      build("fstring:<>value", columnsInfo))

      build("fstring>=value", columnsInfo) must equalTo(
      build("fstring:>=value", columnsInfo))

      build("fstring>value", columnsInfo) must equalTo(
      build("fstring:>value", columnsInfo))

      build("fstring<=value", columnsInfo) must equalTo(
      build("fstring:<=value", columnsInfo))

      build("fstring<value", columnsInfo) must equalTo(
      build("fstring:<value", columnsInfo))

      build("fstring=value1..value2", columnsInfo) must equalTo(
      build("fstring:=value1..value2", columnsInfo))

      build("fstring=value1;value2;value3", columnsInfo) must equalTo(
      build("fstring:=value1;value2;value3", columnsInfo))

      build("fstring=value*", columnsInfo) must equalTo(
      build("fstring:=value*", columnsInfo))

      build("fstring=*value", columnsInfo) must equalTo(
      build("fstring:=*value", columnsInfo))

      build("fstring=*value*", columnsInfo) must equalTo(
      build("fstring:=*value*", columnsInfo))

      build("fstring$value", columnsInfo) must equalTo(
      build("fstring$value", columnsInfo))
    }

    "assume contains as operator when no operator is passed and field is a string" in {

      build("fstring=text", columnsInfo) must equalTo("lower(fstring) = 'text'")
      build("fstring:text", columnsInfo) must equalTo("lower(fstring) like '%text%'")

      build("fstring!=text", columnsInfo) must equalTo("(not lower(fstring) = 'text')")
      build("fstring!text", columnsInfo)  must equalTo("lower(fstring) not like '%text%'")
      build("fstring:!text", columnsInfo) must equalTo("lower(fstring) not like '%text%'")
    }

    "assume Equal as operator when no operator is passed and field is not a string" in {
      build("finteger=10", columnsInfo) must equalTo("finteger = 10")
      build("finteger:10", columnsInfo) must equalTo("finteger = 10")

      build("finteger!=10", columnsInfo)  must equalTo("(not finteger = 10)")
      build("finteger!10", columnsInfo)   must equalTo("(not finteger = 10)")
      build("finteger:!10", columnsInfo)  must equalTo("(not finteger = 10)")
    }

  }

  "ConditionBuilder.buildSingleCondition" should {

    import utils.query.ConditionBuilder.buildSingleCondition

    "build the sql condition" in {
      buildSingleCondition(Condition("table.field=10", "table", "field", false, Equal, List("10")), Numeric
      ) must equalTo("table.field = 10")
    }

    "build the negated sql condition" in {
      buildSingleCondition(Condition("table.field!=10", "table", "field", true, Equal, List("10")), Numeric
      ) must equalTo("(not table.field = 10)")
    }

    "build the sql condition when dealing with string operations" in {
      buildSingleCondition(Condition("table.field=TexT", "table", "field", false, Equal, List("TexT")), String
      ) must equalTo("lower(table.field) = 'text'")

      buildSingleCondition(Condition("table.field=TexT*", "table", "field", false, StartsWith, List("TexT")), String
      ) must equalTo("lower(table.field) like 'text%'")

      buildSingleCondition(Condition("table.field=*TexT", "table", "field", false, EndsWith, List("TexT")), String
      ) must equalTo("lower(table.field) like '%text'")

      buildSingleCondition(Condition("table.field=*TexT", "table", "field", false, Contains, List("TexT")), String
      ) must equalTo("lower(table.field) like '%text%'")

      buildSingleCondition(Condition("table.field=*TexT*", "table", "field", false, Contains, List("TexT")), String
      ) must equalTo("lower(table.field) like '%text%'")

      buildSingleCondition(Condition("table.field$TexT", "table", "field", false, Contains, List("TexT")), String
      ) must equalTo("lower(table.field) like '%text%'")
    }

    "build the sql condition when dealing with string operations, with case unsensitive" in {
      buildSingleCondition(Condition("table.field$Hi Fellows!", "table", "field", false, Contains, List("Hi Fellows!")), String
      ) must equalTo("lower(table.field) like '%hi fellows!%'")
    }

    "build the sql negated condition when dealing with string operations" in {
      buildSingleCondition(Condition("table.field=10", "table", "field", true, Equal, List("10")), String
      ) must equalTo("(not lower(table.field) = '10')")
    }

    "build the sql condition escaping single quotes when dealing with string operations" in {
      buildSingleCondition(Condition("table.field=Paul's home", "table", "field", false, Equal, List("Paul's home")), String
      ) must equalTo("lower(table.field) = 'paul''s home'")
    }

    "build the sql condition leaving double quotes and slashes untouched, case unsensitive" in {
      buildSingleCondition(Condition("""table.field=Paul"s home""", "table", "field", false, Equal, List("""Paul"s home""")), String
      ) must equalTo("""lower(table.field) = 'paul"s home'""")

      buildSingleCondition(Condition("""table.field=Paul\s home""", "table", "field", false, Equal, List("""Paul\s home""")), String
      ) must equalTo("""lower(table.field) = 'paul\s home'""")
    }

    "build the sql condition when dealing with numeric operations" in {
      buildSingleCondition(Condition("table.field=10", "table", "field", false, Equal, List("10")), Numeric
      ) must equalTo("table.field = 10")

      buildSingleCondition(Condition("table.field:10", "table", "field", false, Equal, List("10")), Numeric
      ) must equalTo("table.field = 10")

      buildSingleCondition(Condition("table.field>10", "table", "field", false, Greater, List("10")), Numeric
      ) must equalTo("table.field > 10")
    }

    "build the sql condition when dealing with boolean operations" in {
      List("1", "yes", "on", "true", "TrUE", "anything").foreach { value =>
        buildSingleCondition(Condition("table.field=" + value, "table", "field", false, Equal, List(value)), Boolean
        ) must equalTo("table.field = 1")
      }

      List("0", "off", "OFF", "false", "FaLse", "null").foreach { value =>
        buildSingleCondition(Condition("table.field=" + value, "table", "field", false, Equal, List(value)), Boolean
        ) must equalTo("table.field = 0")
      }

      buildSingleCondition(Condition("table.field=false", "table", "field", false, Equal, List("false")), Boolean
      ) must equalTo("table.field = 0")
    }

    "build the sql condition when dealing with between operator" in {
      buildSingleCondition(Condition("table.field=12..15", "table", "field", false, Between, List("12", "15")), Numeric
      ) must equalTo("table.field between 12 and 15")

      buildSingleCondition(Condition("table.field=12..15", "table", "field", false, Between, List("12", "15")), String
      ) must equalTo("lower(table.field) between '12' and '15'")
    }

    "build the sql negated condition when dealing with between operator" in {
      buildSingleCondition(Condition("table.field=12..15", "table", "field", true, Between, List("12", "15")), Numeric
      ) must equalTo("table.field not between 12 and 15")

      buildSingleCondition(Condition("table.field=TextA..TextB", "table", "field", true, Between, List("TextA", "TextB")), String
      ) must equalTo("lower(table.field) not between 'texta' and 'textb'")
    }

    "build the sql condition when dealing with in operator" in {
      buildSingleCondition(Condition("table.field=12;13;14", "table", "field", false, In, List("12", "13", "14")), Numeric
      ) must equalTo("table.field in (12, 13, 14)")

      buildSingleCondition(Condition("table.field=on,true,false", "table", "field", false, In, List("on", "true", "false")), Boolean
      ) must equalTo("table.field in (1, 1, 0)")

      buildSingleCondition(Condition("table.field=TexTA;TexTB;teXTc", "table", "field", false, In, List("TexTA", "TexTB", "teXTc")), String
      ) must equalTo("( lower(table.field) like '%texta%' or lower(table.field) like '%textb%' or lower(table.field) like '%textc%' )")

    }

    "raise an error when it's expecting a number and no number is passed" in {
      buildSingleCondition(Condition("table.field=10+", "table", "field", false, Equal, List("10+")), Numeric
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must contain("Value '10+' is not a valid number.")
      }

      buildSingleCondition(Condition("table.field=12..1a5", "table", "field", true, Between, List("12", "1a5")), Numeric
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must contain("Value '1a5' is not a valid number.")
      }

      buildSingleCondition(Condition("table.field=12 a 23..15", "table", "field", true, Between, List("12 a 23", "15")), Numeric
      ) must throwA[InvalidQueryConditionException].like {
        case e: Throwable => e.getMessage must contain("Value '12 a 23' is not a valid number.")
      }
    }

    "build the sql condition with table mapping" in {

      val columnsInfo = List(
        ColumnInfo("test", "test",      "field",   "field",  VARCHAR,  "string",   50),
        ColumnInfo("test", "mapped1",   "field",   "field",  VARCHAR,  "string",   50),
        ColumnInfo("test", "mapped2",   "field",   "field",  VARCHAR,  "string",   50),
        ColumnInfo("test", "default",   "field",   "field",  VARCHAR,  "string",   50)
      )

      val mappings1 = Map( "" -> "default", "table1" -> "mapped1")
      val mappings2 = Map( "table1" -> "mapped1", "table2" -> "mapped2")

      buildSingleCondition(Condition("table1.field=TexTA;TexTB;teXTc", "table1", "field", false, In, List("TexTA", "TexTB", "teXTc")), columnsInfo, mappings1
      ) must equalTo("( lower(mapped1.field) like '%texta%' or lower(mapped1.field) like '%textb%' or lower(mapped1.field) like '%textc%' )")

      buildSingleCondition(Condition("field=TexTA;TexTB;teXTc", "", "field", false, In, List("TexTA", "TexTB", "teXTc")), columnsInfo, mappings1
      ) must equalTo("( lower(default.field) like '%texta%' or lower(default.field) like '%textb%' or lower(default.field) like '%textc%' )")

      buildSingleCondition(Condition("mapped2.field=TexTA;TexTB;teXTc", "mapped2", "field", false, In, List("TexTA", "TexTB", "teXTc")), columnsInfo, mappings1
      ) must equalTo("( lower(mapped2.field) like '%texta%' or lower(mapped2.field) like '%textb%' or lower(mapped2.field) like '%textc%' )")

      buildSingleCondition(Condition("field=TexTA;TexTB;teXTc", "", "field", false, In, List("TexTA", "TexTB", "teXTc")), columnsInfo, mappings2
      ) must equalTo("( lower(field) like '%texta%' or lower(field) like '%textb%' or lower(field) like '%textc%' )")

      buildSingleCondition(Condition("table2.field=TexTA;TexTB;teXTc", "table2", "field", false, In, List("TexTA", "TexTB", "teXTc")), columnsInfo, mappings2
      ) must equalTo("( lower(mapped2.field) like '%texta%' or lower(mapped2.field) like '%textb%' or lower(mapped2.field) like '%textc%' )")

    }

  }

}
