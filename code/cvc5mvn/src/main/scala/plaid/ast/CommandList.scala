package plaid.ast

import scala.jdk.CollectionConverters.ListHasAsScala

case class CommandList(getCommands: java.util.List[PreludeCommand]) extends PreludeCommand{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
