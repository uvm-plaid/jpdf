package plaid.cvc

import io.github.cvc5.*
import plaid.ast.*

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object TermFactory {
  private val DEFAULT_FIELD_SIZE = 10

  /** Extract party index for assert commands */
  def getPartyIndex(command: Cmd): Integer =
    command match {
      case x: AssertCmd => CvcUtils.toInt(x.e3)
      case _            => null
    }
}

class TermFactory(val termManager: TermManager, val sort: Sort) {

  private val memories = new mutable.HashSet[Memory]()
  private val minusOne: Term = termManager.mkFiniteFieldElem("-1", sort, TermFactory.DEFAULT_FIELD_SIZE)
  private var partyIndex: Integer = _

  // initialize solver logic
  private val solver = new Solver(termManager)
  try solver.setLogic("ALL")
  catch {
    case e: CVC5ApiException => throw new RuntimeException(e)
  }

  def getMemories: Set[Memory] = memories.toSet

  private def not(term: Term): Term =
    termManager.mkTerm(Kind.FINITE_FIELD_ADD, term, termManager.mkFiniteFieldElem("1", sort, TermFactory.DEFAULT_FIELD_SIZE))

  def joinWithAnd(terms: java.util.Collection[Term]): Term =
    if (terms.size() == 1) terms.iterator().next()
    else termManager.mkTerm(Kind.AND, terms.asScala.toArray)

  def toTerm(command: Cmd): Term = command match {
    case x: ListCmd =>
      joinWithAnd(List(toTerm(x.c1), toTerm(x.c2)).asJava)
    case x: AssertCmd =>
      val idx = TermFactory.getPartyIndex(x)
      joinWithAnd(List(termManager.mkTerm(Kind.EQUAL, toTerm(x.e1, idx), toTerm(x.e2, idx))).asJava)
    case x: AssignCmd =>
      joinWithAnd(List(termManager.mkTerm(Kind.EQUAL, toTerm(x.e1), toTerm(x.e2))).asJava)
    case other =>
      throw new IllegalArgumentException(s"Not an overture command ${other.getClass.getName}")
  }

  def toTerm(expr: Expr, idx: Integer): Term = {
    partyIndex = idx
    val result = toTerm(expr)
    partyIndex = null
    result
  }

  def toTerm(expr: Expr): Term = expr match {
    case x: PublicExpr  => lookupOrCreate(x, null)
    case x: AtExpr =>
      if (partyIndex != null) throw new IllegalStateException(s"Party index $partyIndex already active")
      partyIndex = CvcUtils.toInt(x.e2)
      val result = toTerm(x.e1)
      partyIndex = null
      result
    case x: Num         => termManager.mkFiniteFieldElem(x.num.toString, sort, TermFactory.DEFAULT_FIELD_SIZE)
    case x: PlusExpr    => termManager.mkTerm(Kind.FINITE_FIELD_ADD, toTerm(x.e1), toTerm(x.e2))
    case x: TimesExpr   => termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(x.e1), toTerm(x.e2))
    case x: MinusExpr   => termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(x.e), minusOne)
    case x: OTExpr =>
      val e1 = toTerm(x.e1)
      termManager.mkTerm(
        Kind.FINITE_FIELD_ADD,
        termManager.mkTerm(Kind.FINITE_FIELD_MULT, e1, toTerm(x.e3)),
        termManager.mkTerm(Kind.FINITE_FIELD_MULT, not(e1), toTerm(x.e2))
      )
    case x: OTFourExpr =>
      val s1 = toTerm(x.s1)
      val s2 = toTerm(x.s2)
      val first  = termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_MULT, s1, s2), toTerm(x.e4))
      val second = termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_MULT, s1, not(s2)), toTerm(x.e3))
      val third  = termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_MULT, not(s1), s2), toTerm(x.e2))
      val fourth = termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_MULT, not(s1), not(s2)), toTerm(x.e1))
      termManager.mkTerm(Kind.FINITE_FIELD_ADD, first, termManager.mkTerm(Kind.FINITE_FIELD_ADD, second, termManager.mkTerm(Kind.FINITE_FIELD_ADD, third, fourth)))
    case x: Expr =>
      if (partyIndex == null) throw new IllegalStateException("Party index for memory cannot be null")
      lookupOrCreate(x, partyIndex)
  }

  def lookupOrCreate(expr: Expr, idx: Integer): Term = {
    val name = CvcUtils.getCvcName(expr, idx)
    val memory = memories.find(_.name == name).getOrElse {
      Memory(name, termManager.mkConst(sort, name), expr, idx)
    }
    memories.add(memory)
    memory.term
  }

  def constraintToTerm(expr: Constraint): Term = expr match
    case NotConstraint(e) => termManager.mkTerm(Kind.NOT, constraintToTerm(e))
    case AndConstraint(e1, e2) => termManager.mkTerm(Kind.AND, constraintToTerm(e1), constraintToTerm(e2))
    case EqualConstraint(e1, e2) => termManager.mkTerm(Kind.EQUAL, toTerm(e1), toTerm(e2))
    case TrueConstraint() => termManager.mkTrue()
    case other => throw new IllegalArgumentException(s"cannot convert ${other.getClass.getName} into CVC5 term")
}
