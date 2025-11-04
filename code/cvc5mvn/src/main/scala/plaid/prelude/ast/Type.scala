package plaid.prelude.ast

sealed trait Type extends Node
case class PartyIndexType() extends Type
case class RecordType(elements: Map[Identifier, Type]) extends Type
case class StringType() extends Type
