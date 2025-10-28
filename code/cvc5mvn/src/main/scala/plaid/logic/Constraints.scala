package plaid.logic

import plaid.ast.Constraint

case class Constraints(var precondition: Constraint, var postcondition: Constraint)
