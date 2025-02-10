package plaid.ast

import java.util.stream.Collectors
import scala.jdk.CollectionConverters.ListHasAsScala

case class CommandList(commands: java.util.List[PreludeCommand]) extends PreludeCommand
