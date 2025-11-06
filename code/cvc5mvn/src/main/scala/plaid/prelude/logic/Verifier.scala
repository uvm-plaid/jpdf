package plaid.prelude.logic

import plaid.prelude.cvc.{TermFactory, VerifierTmp}

extension (trg: Contract)
  /** Finds any entailments in a function contract that are false. */
  def verificationFailures(cvc: TermFactory): List[Entailment] =
    val bindings = trg.f.typedVariables.map(x => x.y -> x.t.freshValue()).toMap
    trg.internals.filter { x =>
      val a = x.a.expand(bindings = bindings)
      val b = x.b.expand(bindings = bindings)

      if !WellFormed.checkConstraint(a) || !WellFormed.checkConstraint(b) then
        println(s"***** a = $a")
        println(s"***** b = $b")
        throw Exception(s"Constraints must be ground")

      // TODO Reorganize the CVC5 stuff, this is weird
      val verifier = new VerifierTmp(cvc)
      !verifier.entails(cvc.constraintToTerm(a), cvc.constraintToTerm(b))
    }
