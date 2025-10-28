package plaid.logic

import plaid.ast.*

import java.util.{HashMap, LinkedList, Map as JMap}
import scala.jdk.CollectionConverters.*

class ConstraintEvaluator(val program: Program) {

  val binding_list: LinkedList[JMap[Identifier, Expr]] = new LinkedList
  binding_list.add(new HashMap)

  /** Evaluate Prelude Expression to Overture */
  def toOverture(e: Expr): Expr = e match
    // base cases
    case w: Str       => Str(w.str)
    case i: Num       => Num(i.num)
    case id: Identifier =>
      val lastBindings = binding_list.getLast
      Option(lastBindings.get(id)).getOrElse(id)

    case re: RandomExpr  => RandomExpr(toOverture(re.e))
    case se: SecretExpr  => SecretExpr(toOverture(se.e))
    case me: MessageExpr => MessageExpr(toOverture(me.e))
    case pe: PublicExpr  => PublicExpr(toOverture(pe.e))
    case oe: OutputExpr  => oe
    case te: TimesExpr   => TimesExpr(toOverture(te.e1), toOverture(te.e2))
    case pe: PlusExpr    => PlusExpr(toOverture(pe.e1), toOverture(pe.e2))
    case ce: ConcatExpr  =>
      val e1 = toOverture(ce.e1)
      val e2 = toOverture(ce.e2)
      (e1, e2) match
        case (s1: Str, s2: Str) => Str(s1.str + s2.str)
        case (c1: ConcatExpr, s2: Str) if c1.e2.isInstanceOf[Str] =>
          ConcatExpr(c1.e1, Str(c1.e2.asInstanceOf[Str].str + s2.str))
        case (s1: Str, c2: ConcatExpr) if c2.e1.isInstanceOf[Str] =>
          ConcatExpr(Str(s1.str + c2.e1.asInstanceOf[Str].str), c2.e2)
        case _ => ConcatExpr(e1, e2)
    case me: MinusExpr  => MinusExpr(toOverture(me.e))

    case le: LetExpr =>
      val e1 = toOverture(le.e1)
      val bindings = new HashMap(binding_list.getLast)
      bindings.put(le.y, e1)
      binding_list.addLast(bindings)
      val result = toOverture(le.e2)
      binding_list.removeLast()
      result

    case fe: FunctionCall =>
      val actualParams = fe.parameters.map(toOverture).toList
      val bindings = new HashMap[Identifier, Expr]
      val fn = program.resolveExprFunction(fe.fname)
      fn.y.zip(actualParams).foreach((id, value) => bindings.put(id, value))
      binding_list.add(bindings)
      val result = toOverture(fn.e)
      binding_list.removeLast()
      result

    case fe: FieldExpr =>
      val field = scala.collection.immutable.TreeMap(fe.elements.asScala.toSeq.map((k,v) => k -> toOverture(v)):_*)
      FieldExpr(java.util.TreeMap(field.asJava))

    case fse: FieldSelectExpr =>
      val field = toOverture(fse.e).asInstanceOf[FieldExpr]
      field.elements.get(fse.l)

    case ae: AtExpr =>
      AtExpr(toOverture(ae.e1), toOverture(ae.e2))

    case oe: OTExpr =>
      OTExpr(toOverture(oe.e1), toOverture(oe.i1), toOverture(oe.e2), toOverture(oe.e3))

    case ofe: OTFourExpr =>
      OTFourExpr(
        toOverture(ofe.s1), toOverture(ofe.s2), toOverture(ofe.i1),
        toOverture(ofe.e1), toOverture(ofe.e2), toOverture(ofe.e3), toOverture(ofe.e4)
      )

    case _ =>
      throw new IllegalArgumentException("Bad Expression")

  /** Evaluate Prelude Command */
  def evalInstruction(instr: Cmd): Cmd = instr match
    case assignCmd: AssignCmd =>
      AssignCmd(toOverture(assignCmd.e1), toOverture(assignCmd.e2))

    case callCmd: CallCmd =>
      val actualParameters = callCmd.parameters.map(toOverture).toList
      val fn = program.resolveCommandFunction(callCmd.fname)
      val bindings = new HashMap[Identifier, Expr]
      fn.typedVariables.zip(actualParameters).foreach((typedId, value) => bindings.put(typedId.y, value))
      binding_list.add(bindings)
      val result = evalInstruction(fn.c)
      binding_list.removeLast()
      result

    case letCmd: LetCmd =>
      val v = toOverture(letCmd.e)
      val binding = new HashMap(binding_list.getLast)
      binding.put(letCmd.y, v)
      binding_list.addLast(binding)
      val result = evalInstruction(letCmd.c)
      binding_list.removeLast()
      result

    case listCmd: ListCmd =>
      ListCmd(evalInstruction(listCmd.c1), evalInstruction(listCmd.c2))

    case assertCmd: AssertCmd =>
      AssertCmd(toOverture(assertCmd.e1), toOverture(assertCmd.e2), toOverture(assertCmd.e3))

    case _ =>
      throw new IllegalArgumentException("Bad instruction")

  /** Evaluate Constraint Expression */
  def evalConstraint(e: Constraint): Constraint = e match
    case x: AndConstraint   => AndConstraint(evalConstraint(x.e1), evalConstraint(x.e2))
    case x: NotConstraint   => NotConstraint(evalConstraint(x.e))
    case x: EqualConstraint => EqualConstraint(toOverture(x.e1), toOverture(x.e2))
    case x: FunctionCall =>
      val fn = program.resolveConstraintFunction(x.fname)
      val formalParams = fn.params
      val actualParams = x.parameters.map(toOverture)
      val bindings = new HashMap[Identifier, Expr]
      formalParams.zip(actualParams).foreach((id, value) => bindings.put(id, value))
      binding_list.add(bindings)
      val result = evalConstraint(fn.body)
      binding_list.removeLast()
      result
    case x: TrueConstraint => x
    case _ => throw new IllegalArgumentException("Bad constraint")
}
