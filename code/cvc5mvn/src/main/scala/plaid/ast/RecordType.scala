package plaid.ast

case class RecordType(elements: java.util.TreeMap[Identifier, Type]) extends Type
