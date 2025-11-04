package plaid.prelude.logic

import io.github.cvc5.{Sort, TermManager}
import plaid.prelude.ast.*
import plaid.prelude.cvc.{TermFactory, Verifier}
import plaid.prelude.eval.ConstraintChecker

import java.util
import java.util.TreeMap
import scala.jdk.CollectionConverters.*

class GenEntailVerifier(val program: Program, order: String) {

  private var counter: Int = 0
  private val termManager: TermManager = new TermManager()
  private val sort: Sort = termManager.mkFiniteFieldSort(order, 10)

  private def genFreshString(): Str = {
    counter += 1
    Str("$" + counter)
  }

  private def genFreshCID(): Num = {
    counter += 1
    Num(-counter)
  }

  def genFreshValue(`type`: Type): Expr = `type` match
    case _: StringType      => genFreshString()
    case _: PartyIndexType  => genFreshCID()
    case t: RecordType =>
      val map: util.TreeMap[Identifier, Expr] = new util.TreeMap()
      t.elements.asScala.foreach { case (k, v) =>
        map.put(k, genFreshValue(v))
      }
      FieldExpr(map)

  /**
   * Generalized rule for entailment: verify if precondition entails postcondition for all inputs
   *
   * @param typings list of typed identifiers
   * @param e1      abstract precondition
   * @param e2      abstract postcondition
   * @return true/false
   */
  def genEntails(typings: List[TypedIdentifier], e1: Constraint, e2: Constraint): Boolean = {
    // evaluate abstract constraints into ground constraints with fresh variables
    val bindings = typings.map(x => x.y -> genFreshValue(x.t)).toMap
    val evaluator = Evaluator(program, bindings)
    val groundE1: Constraint = evaluator.constraint(e1)
    val groundE2: Constraint = evaluator.constraint(e2)

    // check if ground constraints are fully instantiated
    if ConstraintChecker.checkConstraint(groundE1) && ConstraintChecker.checkConstraint(groundE2) then
      val termFactory = new TermFactory(termManager, sort)
      val groundE1Term = termFactory.constraintToTerm(groundE1)
      val groundE2Term = termFactory.constraintToTerm(groundE2)
      val verifier = new Verifier(termFactory)
      verifier.entails(groundE1Term, groundE2Term)
    else
      throw new RuntimeException("constraints with fresh values are not ground!")
  }
}
