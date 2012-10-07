package test.utils

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.Logger

class SqlSpec extends Specification {

  import utils.Sql._

  "Sql.replaceFields" should {

    "handle the basic case and replace a '*' fields clause" in {
      (replaceFields("select * from x", "count(*)")
        must equalTo("select count(*) from x"))
    }

    "handle top x clause" in {
      (replaceFields("select top 24 f1, f2 from x", "f3, f4, f5")
        must equalTo("select top 24 f3, f4, f5 from x"))
    }

    "handle distinct clause" in {
      (replaceFields("select distinct f1, f2 from x", "f3, f4, f5")
        must equalTo("select distinct f3, f4, f5 from x"))
    }

    "handle all clause" in {
      (replaceFields("select all f1, f2 from x", "f3, f4, f5")
        must equalTo("select all f3, f4, f5 from x"))
    }

    "handle top and distinct clause combined" in {
      (replaceFields("select top 23 distinct f1, f2 from x", "f3, f4, f5")
        must equalTo("select top 23 distinct f3, f4, f5 from x"))
    }

    "handle spaces" in {
      (replaceFields("select  f1  from x", " f2,  f3 ")
        must equalTo("select   f2,  f3   from x"))
    }

    "handle multiple lines" in {
      (replaceFields(
        """select 
             top 24 
             f1, f2 
          from x""", "f3, f4, f5")
        must equalTo(
        """select 
             top 24 
             f3, f4, f5 
          from x"""))
    }

    "handle complex case" in {

      // taken from http://mckoi.com/database/SQLSyntax.html#15
      (replaceFields("""
        |SELECT number, quantity, CONCAT('$', ROUND(price, 2))
        |  FROM Order
        |WHERE quantity > 5
        |ORDER BY number DESC
        """.stripMargin, "f2, f3")
        must equalTo("""
        |SELECT f2, f3
        |  FROM Order
        |WHERE quantity > 5
        |ORDER BY number DESC
        """.stripMargin)
      )
    }

  }

}