package plaid.ast

import java.{lang, util}

case class FunctionCallCommand(getFname: Identifier, getParameters: java.util.List[PreludeExpression]) extends PreludeCommand{
  override def children(): lang.Iterable[Node] = new util.ArrayList[Node]()

  override def prettyPrint(): String = throw new UnsupportedOperationException()
}

