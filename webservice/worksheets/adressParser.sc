object adressParser {
val r = """((?:\w+\W+){1,4})(?:al\W+)?(\d+)""".r  //> r  : scala.util.matching.Regex = ((?:\w+\W+){1,4})(?:al\W+)?(\d+)

val f = r.findAllIn(
"Lic. maria jose ignacio zavaleta 23, " +
"esta idea esta en rivadavia al 5345, " +
"al lado de José María Moreno 4356, " +
"a la vuelta de conte y Juan B. Alberdi").toList  //> f  : List[String] = List(maria jose ignacio zavaleta 23, idea esta en rivada
                                                  //| via al 5345, José María Moreno 4356)


val r(x,y) = "maria jose ignacio zavaleta 23"     //> x  : String = "maria jose ignacio zavaleta "
                                                  //| y  : String = 23

f.foreach { address =>
	val r(calle, altura) = address
	println(calle, altura)
}                                                 //> (maria jose ignacio zavaleta ,23)
                                                  //| (idea esta en rivadavia ,5345)
                                                  //| (José María Moreno ,4356)

}