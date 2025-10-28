package plaid.ast

// TODO Do we need PartyIndexType?

sealed trait Type extends Node
case class PartyIndexType() extends Type
case class RecordType(elements: java.util.TreeMap[Identifier, Type]) extends Type
case class StringType() extends Type
