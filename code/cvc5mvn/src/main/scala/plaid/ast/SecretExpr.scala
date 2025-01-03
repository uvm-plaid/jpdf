package plaid.ast
import java.lang

case class SecretExpr(e: PreludeExpression) extends MemoryExpr{
  override def prettyPrint(): String = "s[\"" + e.prettyPrint() + "\"]"
}