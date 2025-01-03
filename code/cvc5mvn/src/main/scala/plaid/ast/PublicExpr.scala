package plaid.ast
import java.lang

case class PublicExpr(e: PreludeExpression) extends MemoryExpr{
  override def prettyPrint(): String = "p[\"" + e.prettyPrint() + "\"]"
}
