package plaid.ast
import java.lang

case class RandomExpr(e: PreludeExpression) extends MemoryExpr{
  override def prettyPrint(): String = "r[\"" + e.prettyPrint() + "\"]"
}
