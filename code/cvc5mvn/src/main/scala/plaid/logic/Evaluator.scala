package plaid.logic

import plaid.ast.*

import scala.jdk.CollectionConverters.*

/** Support for reducing an AST using variable substitution, expanding function calls, etc. */
case class Evaluator(
  program: Program,
  bindings: Map[Identifier, Expr] = Map()) {

  private def concat(e1: Expr, e2: Expr) = (e1, e2) match
    case (Str(s1), Str(s2)) => Str(s1 + s2)
    case (ConcatExpr(e1, Str(s1)), Str(s2)) => ConcatExpr(e1, Str(s1 + s2))
    case (Str(s1), ConcatExpr(Str(s2), e2)) => ConcatExpr(Str(s1 + s2), e2)
    case _ => ConcatExpr(e1, e2)

  /** Reduces an expression as much as possible in the context of this evaluator's Program and bindings. */
  def expression(e: Expr): Expr = e match
    case str: Str => str
    case num: Num => num
    case id: Identifier => bindings.getOrElse(id, id)
    case RandomExpr(e) => RandomExpr(expression(e))
    case SecretExpr(e) => SecretExpr(expression(e))
    case MessageExpr(e) => MessageExpr(expression(e))
    case PublicExpr(e) => PublicExpr(expression(e))
    case oe: OutputExpr => oe
    case TimesExpr(e1, e2) => TimesExpr(expression(e1), expression(e2))
    case PlusExpr(e1, e2) => PlusExpr(expression(e1), expression(e2))
    case ConcatExpr(e1, e2) => concat(expression(e1), expression(e2))
    case MinusExpr(e)  => MinusExpr(expression(e))
    case LetExpr(y, e1, e2) =>
      val evaluator = copy(bindings = bindings.updated(y, expression(e1)))
      evaluator.expression(e2)
    case FunctionCall(fn, parms) =>
      val f = program.resolveExprFunction(fn)
      val formalParms = f.y
      val actualParms = parms.map(expression)
      val evaluator = copy(bindings = Map.from(formalParms.zip(actualParms)))
      evaluator.expression(f.e)
    case FieldExpr(elements) =>
      val field = scala.collection.immutable.TreeMap(elements.asScala.toSeq.map((k,v) => k -> expression(v)):_*)
      FieldExpr(java.util.TreeMap(field.asJava))

    case FieldSelectExpr(e, l) =>
      val field = expression(e).asInstanceOf[FieldExpr]
      field.elements.get(l)

    case AtExpr(e1, e2) => AtExpr(expression(e1), expression(e2))

    case OTExpr(e1, i1, e2, e3) =>
      OTExpr(expression(e1), expression(i1), expression(e2), expression(e3))

    case OTFourExpr(s1, s2, i1, e1, e2, e3, e4) =>
      OTFourExpr(
        expression(s1), expression(s2), expression(i1),
        expression(e1), expression(e2), expression(e3), expression(e4))

  /** Reduces a command as much as possible in the context of this evaluator's Program and bindings. */
  def command(cmd: Cmd): Cmd = cmd match
    case AssignCmd(e1, e2) => AssignCmd(expression(e1), expression(e2))
    case CallCmd(fn, parms) =>
      val f = program.resolveCommandFunction(fn)
      val formalParms = f.typedVariables.map(_.y)
      val actualParms = parms.map(expression)
      val evaluator = copy(bindings = Map.from(formalParms.zip(actualParms)))
      evaluator.command(f.c)
    case LetCmd(y, e, c) =>
      val evaluator = copy(bindings = bindings.updated(y, expression(e)))
      evaluator.command(c)
    case ListCmd(c1, c2) => ListCmd(command(c1), command(c2))
    case AssertCmd(e1, e2, e3) => AssertCmd(expression(e1), expression(e2), expression(e3))
    case _ => throw new IllegalArgumentException("Bad instruction")

  /** Reduces a constraint as much as possible in the context of this evaluator's Program and bindings. */
  def constraint(c: Constraint): Constraint = c match
    case AndConstraint(e1, e2) => AndConstraint(constraint(e1), constraint(e2))
    case NotConstraint(e) => NotConstraint(constraint(e))
    case EqualConstraint(e1, e2) => EqualConstraint(expression(e1), expression(e2))
    case FunctionCall(fn, parms) =>
      val f = program.resolveConstraintFunction(fn)
      val formalParams = f.params
      val actualParams = parms.map(expression)
      val evaluator = copy(bindings = Map.from(formalParams.zip(actualParams)))
      evaluator.constraint(f.body)
    case x: TrueConstraint => x
    case _ => throw new IllegalArgumentException("Bad constraint")
}
