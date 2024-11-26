package plaid.ast

import java.util.stream.Collectors
import scala.jdk.CollectionConverters.ListHasAsScala

case class CommandList(getCommands: java.util.List[PreludeCommand]) extends PreludeCommand{
  override def prettyPrint(): String = getCommands
    .stream()
    .map(x => x.prettyPrint())
    .collect(Collectors.joining(""))
}
