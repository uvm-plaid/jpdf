package plaid.prelude.ast

sealed trait Cmd extends Node {

  /**
   * Expand all the expressions contained inside this command. The ctx is
   * for looking up expression functions, and bindings are for any identifiers
   * that should be replaced by expressions.
   */
  def expand(ctx: List[ExprFunc], bindings: Map[Identifier, Expr] = Map()): Cmd = this match
    case AssertCmd(e1, e2, e3) => AssertCmd(e1.expand(ctx, bindings), e2.expand(ctx, bindings), e3.expand(ctx, bindings))
    case AssignCmd(e1, e2) => AssignCmd(e1.expand(ctx, bindings), e2.expand(ctx, bindings))
    case CallCmd(id, parms) => CallCmd(id, parms.map(_.expand(ctx, bindings)))
    case LetCmd(y, e, c) =>
      val b = bindings.updated(y, e.expand(ctx, bindings))
      LetCmd(y, e, c.map(_.expand(ctx, b)))
}

case class AssertCmd(e1: Expr, e2: Expr, e3: Expr) extends Cmd
case class AssignCmd(e1: Expr, e2: Expr) extends Cmd
case class CallCmd(id: Identifier, parms: List[Expr]) extends Cmd
case class LetCmd(y: Identifier, e: Expr, c: List[Cmd]) extends Cmd
