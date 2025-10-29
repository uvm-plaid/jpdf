package plaid.prelude.logic

import plaid.prelude.ast.Constraint

case class Constraints(var precondition: Constraint, var postcondition: Constraint)
