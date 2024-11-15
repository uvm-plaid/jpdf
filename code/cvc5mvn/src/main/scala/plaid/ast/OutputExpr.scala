package plaid.ast
import java.lang

case class OutputExpr() extends MemoryExpr{
  override def prettyPrint(): String = "out"
}
