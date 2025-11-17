package plaid.prelude.cvc

import io.github.cvc5.*
import plaid.prelude.ast.*

import scala.collection.mutable

class TermFactory(order: String) {
  // TODO We manually specify field size, this should be renamed?
  private val DEFAULT_FIELD_SIZE = 10

  /** Extract party index for assert commands */
  private def getPartyIndex(command: Cmd): Integer = command match
    case x: AssertCmd => CvcUtils.toInt(x.e3)
    case _ => null

  val termManager = TermManager()
  val sort: Sort = termManager.mkFiniteFieldSort(order, DEFAULT_FIELD_SIZE)
  private val memories = new mutable.HashSet[Memory]()
  private val minusOne: Term = termManager.mkFiniteFieldElem("-1", sort, DEFAULT_FIELD_SIZE)

  private val solver = new Solver(termManager)
  solver.setLogic("ALL")

  def getMemories: Set[Memory] = memories.toSet

  def toTerm(expr: Expr, idx: Option[Int] = None): Term = expr match
    case x: PublicExpr  => lookupOrCreate(x, null)
    case x: AtExpr =>
      if (idx.nonEmpty) throw Exception(s"Party index $idx already active")
      toTerm(x.e1, Some(CvcUtils.toInt(x.e2)))
    case Num(n) => termManager.mkFiniteFieldElem(n.toString, sort, DEFAULT_FIELD_SIZE)
    case PlusExpr(e1, e2) => termManager.mkTerm(Kind.FINITE_FIELD_ADD, toTerm(e1, idx), toTerm(e2, idx))
    case TimesExpr(e1, e2) => termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(e1, idx), toTerm(e2, idx))
    case MinusExpr(e) => termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(e, idx), minusOne)
    case x: Expr =>
      if (idx.isEmpty) throw Exception("A party index must be active")
      lookupOrCreate(x, idx)

  private def lookupOrCreate(expr: Expr, idx: Option[Int]): Term =
    val name = CvcUtils.getCvcName(expr, idx)
    val memory = memories.find(_.name == name).getOrElse {
      Memory(name, termManager.mkConst(sort, name), expr, idx)
    }
    memories.add(memory)
    memory.term

  def toTerm(constraint: Constraint): Term = constraint match
    case NotConstraint(e) => termManager.mkTerm(Kind.NOT, toTerm(e))
    case AndConstraint(e1, e2) => termManager.mkTerm(Kind.AND, toTerm(e1), toTerm(e2))
    case EqualConstraint(e1, e2) => termManager.mkTerm(Kind.EQUAL, toTerm(e1), toTerm(e2))
    case TrueConstraint() => termManager.mkTrue()
    case other => throw new IllegalArgumentException(s"Unsupported constraint $other")
}
