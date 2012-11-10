object sql_field_list_parser {
  
  
  val r1 = """(?:(\w+)\.)?((?:\w|\s)+)""".r       //> r1  : scala.util.matching.Regex = (?:(\w+)\.)?((?:\w|\s)+)
  
  val fields = "nombre desc, xx.apellido".split("""\W*,\W*""")
                                                  //> fields  : Array[java.lang.String] = Array(nombre desc, xx.apellido)
  /*
  fields.map { prefixedfield =>
  	val r1(table, field) = prefixedfield
  	(if (table==null) "" else table, field)
  }
  */
  
  val m = r1.findAllIn("nombre").toList           //> m  : List[String] = List(nombre)
  
  val r1(table, nombre) = "nombre desc"           //> table  : String = null
                                                  //| nombre  : String = nombre desc
	val r1(table2, nombre2) = "tabla.nombre asc"
                                                  //> table2  : String = tabla
                                                  //| nombre2  : String = nombre asc
  
}