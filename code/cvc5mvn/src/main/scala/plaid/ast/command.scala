package plaid.ast

trait Cmd extends Node
case class AssertCmd(e1: Expr, e2: Expr, e3: Expr) extends Cmd
case class AssignCmd(e1: Expr, e2: Expr) extends Cmd
case class ListCmd(c1: Cmd, c2: Cmd) extends Cmd
case class CallCmd(fname: Identifier, parameters: List[Expr]) extends Cmd
case class LetCmd(y: Identifier, e: Expr, c: Cmd) extends Cmd
