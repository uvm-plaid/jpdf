package plaid.ast
import java.lang

case class MessageExpr(e: PreludeExpression) extends MemoryExpr{
  override def prettyPrint(): String = "m[\"" + e.prettyPrint() + "\"]"
}
