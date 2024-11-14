package plaid.ast
import java.lang

case class Identifier(getName: String) extends PreludeExpression{
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]
  
  override def prettyPrint(): String = getName
}
