package plaid

import plaid.ast.Node

object ScalaFunctions {
  def prettyPrint(n: Node): String = plaid.prettyPrint(n)
}
