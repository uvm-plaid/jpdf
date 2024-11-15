package plaid.ast
import java.lang

case class ExprFunction(getFname: Identifier, getY: java.util.List[Identifier], getE: PreludeExpression) extends PreludeCommand {
  override def prettyPrint(): String = throw new UnsupportedOperationException
}