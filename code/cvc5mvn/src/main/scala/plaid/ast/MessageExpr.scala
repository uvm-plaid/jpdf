package plaid.ast
import java.lang

case class MessageExpr(getE: PreludeExpression) extends MemoryExpr{
  override def prettyPrint(): String = "m[\"" + getE.prettyPrint() + "\"]"
}
