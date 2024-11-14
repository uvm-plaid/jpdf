package plaid.constraints.ast
import java.lang

trait Node {
  def children() : lang.Iterable[Node]

  def prettyPrint(): String
}
