package plaid.ast
import java.{lang, util}

case class CommandList(getCommands: java.util.List[PreludeCommand]) extends PreludeCommand{
  override def children(): lang.Iterable[Node] = new util.ArrayList[Node]()

  override def prettyPrint(): String = throw new UnsupportedOperationException()
  
}
