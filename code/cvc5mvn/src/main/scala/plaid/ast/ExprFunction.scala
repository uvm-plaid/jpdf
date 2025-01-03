package plaid.ast
import java.lang

case class ExprFunction(fname: Identifier, y: java.util.List[Identifier], e: PreludeExpression) extends PreludeCommand {
  override def prettyPrint(): String = throw new UnsupportedOperationException
}