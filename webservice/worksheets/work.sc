object work {

case class Token(val token: String, val alias: String)

val tokens = List( Token( "corrientes", "cor1, cor2"), Token( "uruguay", "uru1, uru2") )
                                                  //> tokens  : List[work.Token] = List(Token(corrientes,cor1, cor2), Token(urugua
                                                  //| y,uru1, uru2))

tokens.map { token =>
  val l = (token.token +: token.alias.split(", ")).toList
  l.map { t => (t, token) }
}                                                 //> res0: List[List[(java.lang.String, work.Token)]] = List(List((corrientes,Tok
                                                  //| en(corrientes,cor1, cor2)), (cor1,Token(corrientes,cor1, cor2)), (cor2,Token
                                                  //| (corrientes,cor1, cor2))), List((uruguay,Token(uruguay,uru1, uru2)), (uru1,T
                                                  //| oken(uruguay,uru1, uru2)), (uru2,Token(uruguay,uru1, uru2))))
val m = tokens.flatMap { token =>
  val l = (token.token +: token.alias.split(", ")).toList
  l.map { t => (t -> token) }
}.toMap                                           //> m  : scala.collection.immutable.Map[java.lang.String,work.Token] = Map(urugu
                                                  //| ay -> Token(uruguay,uru1, uru2), corrientes -> Token(corrientes,cor1, cor2),
                                                  //|  uru2 -> Token(uruguay,uru1, uru2), uru1 -> Token(uruguay,uru1, uru2), cor1 
                                                  //| -> Token(corrientes,cor1, cor2), cor2 -> Token(corrientes,cor1, cor2))

m("cor1")                                         //> res1: work.Token = Token(corrientes,cor1, cor2)

m("cor2")                                         //> res2: work.Token = Token(corrientes,cor1, cor2)

}