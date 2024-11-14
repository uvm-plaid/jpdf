package plaid.ast

import java.lang

case class MinusExpr(getE: PreludeExpression) extends PreludeExpression{
  override def children() : lang.Iterable[Node] = new java.util.ArrayList[Node]()
  
  override def prettyPrint(): String = "(" + "-" + getE.prettyPrint() + ")" 
}
