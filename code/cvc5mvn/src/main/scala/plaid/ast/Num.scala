package plaid.ast
import java.lang

case class  Num(getNum: Int) extends PreludeExpression{

  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]()
  
  override def prettyPrint() : String = getNum.toString
}
