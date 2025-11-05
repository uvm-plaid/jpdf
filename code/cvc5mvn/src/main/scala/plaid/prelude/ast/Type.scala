package plaid.prelude.ast

import plaid.prelude.logic.Unique

sealed trait Type extends Node {

  def freshValue(): Expr = this match
    case PartyIndexType() => Unique.party()
    case StringType() => Unique.string()
    case RecordType(elements) => FieldExpr(elements.view.mapValues(_.freshValue()).toMap)
}

case class PartyIndexType() extends Type
case class RecordType(elements: Map[Identifier, Type]) extends Type
case class StringType() extends Type
