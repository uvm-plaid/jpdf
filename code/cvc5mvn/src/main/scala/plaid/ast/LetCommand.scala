package plaid.ast
import java.lang

case class LetCommand(y: Identifier, e: PreludeExpression, c: PreludeCommand) extends PreludeCommand {
  override def prettyPrint(): String =
    throw new UnsupportedOperationException()
}
