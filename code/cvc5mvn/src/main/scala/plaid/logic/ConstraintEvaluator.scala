package plaid.logic

import plaid.ast.*

import scala.jdk.CollectionConverters.*

case class ConstraintEvaluator(
  program: Program,
  bindings: Map[Identifier, Expr] = Map()) {

  private def concat(e1: Expr, e2: Expr) = (e1, e2) match
    case (Str(s1), Str(s2)) => Str(s1 + s2)
    case (ConcatExpr(e1, Str(s1)), Str(s2)) => ConcatExpr(e1, Str(s1 + s2))
    case (Str(s1), ConcatExpr(Str(s2), e2)) => ConcatExpr(Str(s1 + s2), e2)
    case _ => ConcatExpr(e1, e2)

  def toOverture(e: Expr): Expr = e match
    case str: Str => str
    case num: Num => num
    case id: Identifier => bindings.getOrElse(id, id)
    case RandomExpr(e) => RandomExpr(toOverture(e))
    case SecretExpr(e) => SecretExpr(toOverture(e))
    case MessageExpr(e) => MessageExpr(toOverture(e))
    case PublicExpr(e) => PublicExpr(toOverture(e))
    case oe: OutputExpr => oe
    case TimesExpr(e1, e2) => TimesExpr(toOverture(e1), toOverture(e2))
    case PlusExpr(e1, e2) => PlusExpr(toOverture(e1), toOverture(e2))
    case ConcatExpr(e1, e2) => concat(toOverture(e1), toOverture(e2))
    case MinusExpr(e)  => MinusExpr(toOverture(e))
    case LetExpr(y, e1, e2) =>
      val evaluator = copy(bindings = bindings.updated(y, toOverture(e1)))
      evaluator.toOverture(e2)
    case FunctionCall(fn, parms) =>
      val f = program.resolveExprFunction(fn)
      val formalParms = f.y
      val actualParms = parms.map(toOverture)
      val evaluator = copy(bindings = Map.from(formalParms.zip(actualParms)))
      evaluator.toOverture(f.e)
    case FieldExpr(elements) =>
      val field = scala.collection.immutable.TreeMap(elements.asScala.toSeq.map((k,v) => k -> toOverture(v)):_*)
      FieldExpr(java.util.TreeMap(field.asJava))

    case FieldSelectExpr(e, l) =>
      val field = toOverture(e).asInstanceOf[FieldExpr]
      field.elements.get(l)

    case AtExpr(e1, e2) => AtExpr(toOverture(e1), toOverture(e2))

    case OTExpr(e1, i1, e2, e3) =>
      OTExpr(toOverture(e1), toOverture(i1), toOverture(e2), toOverture(e3))

    case OTFourExpr(s1, s2, i1, e1, e2, e3, e4) =>
      OTFourExpr(
        toOverture(s1), toOverture(s2), toOverture(i1),
        toOverture(e1), toOverture(e2), toOverture(e3), toOverture(e4))

    case _ =>
      throw new IllegalArgumentException("Bad Expression")

  def evalInstruction(instr: Cmd): Cmd = instr match
    case AssignCmd(e1, e2) => AssignCmd(toOverture(e1), toOverture(e2))
    case CallCmd(fn, parms) =>
      val f = program.resolveCommandFunction(fn)
      val formalParms = f.typedVariables.map(_.y)
      val actualParms = parms.map(toOverture)
      val evaluator = copy(bindings = Map.from(formalParms.zip(actualParms)))
      evaluator.evalInstruction(f.c)
    case LetCmd(y, e, c) =>
      val evaluator = copy(bindings = bindings.updated(y, toOverture(e)))
      evaluator.evalInstruction(c)
    case ListCmd(c1, c2) => ListCmd(evalInstruction(c1), evalInstruction(c2))
    case AssertCmd(e1, e2, e3) => AssertCmd(toOverture(e1), toOverture(e2), toOverture(e3))
    case _ => throw new IllegalArgumentException("Bad instruction")

  def evalConstraint(e: Constraint): Constraint = e match
    case AndConstraint(e1, e2) => AndConstraint(evalConstraint(e1), evalConstraint(e2))
    case NotConstraint(e) => NotConstraint(evalConstraint(e))
    case EqualConstraint(e1, e2) => EqualConstraint(toOverture(e1), toOverture(e2))
    case FunctionCall(fn, parms) =>
      val f = program.resolveConstraintFunction(fn)
      val formalParams = f.params
      val actualParams = parms.map(toOverture)
      val evaluator = copy(bindings = Map.from(formalParams.zip(actualParams)))
      evaluator.evalConstraint(f.body)
    case x: TrueConstraint => x
    case _ => throw new IllegalArgumentException("Bad constraint")
}
