package plaid.prelude.cvc

import io.github.cvc5.{CVC5ApiException, Sort, TermManager}
import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.{AssignCmd, Cmd}

class VerifierTmpTest {

  private val termManager = new TermManager()
  private val sort: Sort = termManager.mkFiniteFieldSort("7", 10)
  private val termFactory = new TermFactory(termManager, sort)
  private val verifier = new VerifierTmp(termFactory)

  private def satisfiable(src: String): Boolean =
    satisfiable(Loader.command(src))

  private def satisfiable(command: Cmd): Boolean =
    val e = termFactory.toTerm(command)
    verifier.satisfies(e)

  /** satisfies a simple protocol */
  @Test
  def satisfiesCorrectProtocol(): Unit = {
    assertTrue(satisfiable("out@1 := 1@1; out@2 := 2@2"))
  }

  /** Fails to satisfy a simple contradictory protocol */
  @Test
  def failIncorrectProtocol(): Unit = {
    assertFalse(satisfiable("out@1 := 1@1; out@1 := 2@1"))
  }

  /** satisfies a protocol that has an assertion */
  @Test
  def assertionProtocol(): Unit = {
    assertTrue(satisfiable("out@1 := 1@1; assert (out = 1)@1"))
  }

  /** Fails to satisfy a protocol that has a contradiction caused by an assertion */
  @Test
  def failAssertionProtocol(): Unit = {
    assertFalse(satisfiable("out@1 := 1@1; assert (out = 2)@1"))
  }

  /** two-party addition */
  @Test
  def twoPartyAddition(): Unit = {
    val protocol =
      """m["s"]@2 := (s["x"] + -r["1"])@1;
        |m["s"]@1 := (s["y"] + -r["2"])@2;
        |p["1"] := (m["s"] + r["1"])@1;
        |p["2"] := (m["s"] + r["2"])@2;
        |out@1 := (p["1"] + p["2"])@1;
        |out@2 := (p["1"] + p["2"])@2""".stripMargin

    val proposition =
      """(out@1 == s["x"]@1 + s["y"]@2) AND (out@2 == s["x"]@1 + s["y"]@2)""".stripMargin

    assertTrue(satisfiable(protocol))
    assertTrue(verifier.entails(
      termFactory.toTerm(Loader.command(protocol)),
      termFactory.constraintToTerm(Loader.constraint(proposition))
    ))
  }

  /** an overture protocol entails a correct proposition */
  @Test
  def entailsCorrectProposition(): Unit = {
    val protocol =
      """m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1;
        |m["x"]@3 := r["x"]@1""".stripMargin

    val proposition = """r["x"]@1 == m["x"]@3"""

    assertTrue(verifier.entails(
      termFactory.toTerm(Loader.command(protocol)),
      termFactory.constraintToTerm(Loader.constraint(proposition))
    ))
  }

  /** three-party addition */
  @Test
  def entailsThreePartyAddition(): Unit = {
    val protocol =
      """m["s1"]@2 := ((s["1"] + -r["local"]) + -r["x"])@1;
        |m["s1"]@3 := r["x"]@1;
        |m["s2"]@1 := ((s["2"] + -r["local"]) + -r["x"])@2;
        |m["s2"]@3 := r["x"]@2;
        |m["s3"]@1 := ((s["3"] + -r["local"]) + -r["x"])@3;
        |m["s3"]@2 := r["x"]@3;
        |p["1"] := (r["local"] + m["s2"] + m["s3"])@1;
        |p["2"] := (m["s1"] + r["local"] + m["s3"])@2;
        |p["3"] := (m["s1"] + m["s2"] + r["local"])@3;
        |out@1 := (p["1"] + p["2"] + p["3"])@1;
        |out@2 := (p["1"] + p["2"] + p["3"])@2;
        |out@3 := (p["1"] + p["2"] + p["3"])@3""".stripMargin

    val propositions = Seq(
      """out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3""",
      """out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3""",
      """out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3""",
      """out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
        |out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
        |out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3""".stripMargin
    )

    propositions.foreach { p =>
      assertTrue(verifier.entails(
        termFactory.toTerm(Loader.command(protocol)),
        termFactory.constraintToTerm(Loader.constraint(p))
      ))
    }

    val falseProps = Seq(
      """out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
        |out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
        |(NOT (out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3))""".stripMargin,
      """out@2 == s["1"]@1 + s["2"]@2 + r["x"]@3"""
    )

    falseProps.foreach { p =>
      assertFalse(verifier.entails(
        termFactory.toTerm(Loader.command(protocol)),
        termFactory.constraintToTerm(Loader.constraint(p))
      ))
    }
  }

  /** a protocol does not verify a contradictory proposition */
  @Test
  def entailsIncorrectProposition(): Unit = {
    val protocol =
      """m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1;
        |m["x"]@3 := r["x"]@1""".stripMargin

    val falseProps = Seq(
      """r["x"]@1 == m["x"]@2""",
      """r["loc"]@1 == m["x"]@3"""
    )

    falseProps.foreach { p =>
      assertFalse(verifier.entails(
        termFactory.toTerm(Loader.command(protocol)),
        termFactory.constraintToTerm(Loader.constraint(p))
      ))
    }
  }

  /** The truth value when some predicate entails nothing is always true */
  @Test
  @throws[CVC5ApiException]
  def somethingEntailsNothing(): Unit = {
    val trueTerm = termFactory.toTerm(Loader.command("out@1 := 1@1"))
    assertTrue(verifier.entails(trueTerm, null))
    val falseTerm = termFactory.toTerm(Loader.command("out@1 := 1@1; out@1 := 2@1"))
    assertTrue(verifier.entails(falseTerm, null))
  }

  /** Overture protocols with only one command in them can be satisfied */
  @Test
  def oneCommandProtocolSatisfaction(): Unit = {
    val command = Loader.command("out@1 := 1@1")
    assertTrue(command.isInstanceOf[AssignCmd])
    assertTrue(satisfiable(command))
  }

}
