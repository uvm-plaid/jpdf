package plaid.cvc

import io.github.cvc5.{CVC5ApiException, Kind, Term, TermManager}
import org.junit.Test
import org.junit.Assert.{assertEquals, assertNotNull}
import plaid.antlr.Loader
import plaid.ast._

import scala.jdk.CollectionConverters._

class TermFactoryTest {

  private def mockModel(factory: TermFactory, nameBasedModel: Map[String, Int]): Map[Term, Int] = {
    nameBasedModel.map { case (name, value) =>
      val term = factory.getMemories.asScala
        .find(_.name == name)
        .get
        .term
      term -> value
    }
  }

  /** Party indexes must not be ambiguous */
  @Test(expected = classOf[IllegalStateException])
  @throws[CVC5ApiException]
  def partyIndexesDoNotStack(): Unit = {
    val expr = Loader.toExpression("(s[\"y\"] + s[\"x\"]@1)@2")
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    factory.toTerm(expr)
  }

  /** Party index required for memory expression */
  @Test(expected = classOf[IllegalStateException])
  @throws[CVC5ApiException]
  def memoryPartyIndexRequired(): Unit = {
    val expr = Loader.toExpression("m[\"y\"]")
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    factory.toTerm(expr)
  }

  /** Party index required for secret expression */
  @Test(expected = classOf[IllegalStateException])
  @throws[CVC5ApiException]
  def secretPartyIndexRequired(): Unit = {
    val expr = Loader.toExpression("s[\"y\"]")
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    factory.toTerm(expr)
  }

  /** Party index required for random expression */
  @Test(expected = classOf[IllegalStateException])
  @throws[CVC5ApiException]
  def randomPartyIndexRequired(): Unit = {
    val expr = Loader.toExpression("r[\"y\"]")
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    factory.toTerm(expr)
  }

  /** Party index required for output expression */
  @Test(expected = classOf[IllegalStateException])
  @throws[CVC5ApiException]
  def outputPartyIndexRequired(): Unit = {
    val expr = Loader.toExpression("out")
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    factory.toTerm(expr)
  }

  /** Creates terms for all commands in a list */
  @Test
  @throws[CVC5ApiException]
  def commandListTerms(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val terms = factory.toTerm(ListCmd(
      AssertCmd(new OutputExpr(), new Num(1), new Num(3)),
      AssignCmd(new AtExpr(new OutputExpr(), new Num(1)), new Num(3))
    ))
    // assertEquals(2, terms.size()) // optional
  }

  /** Converts numbers to finite field terms */
  @Test
  @throws[CVC5ApiException]
  def numericTerms(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val term = factory.toTerm(new Num(1))
    assertNotNull(term.getFiniteFieldValue)
  }

  /** Registers memory nodes when they are children of other nodes */
  @Test
  @throws[CVC5ApiException]
  def registerChildren(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val mem = new AtExpr(new MessageExpr(new Str("x")), new Num(3))
    val expr = new PlusExpr(new Num(6), mem)
    factory.toTerm(expr)
    val memories = factory.getMemories.asScala
    assertEquals(1, memories.size)
  }

  /** Registers memory nodes when no other memories are registered */
  @Test
  @throws[CVC5ApiException]
  def registerFirstMemory(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val expr = new AtExpr(new MessageExpr(new Str("x")), new Num(3))
    factory.toTerm(expr)
    val memories = factory.getMemories.asScala
    assertEquals(1, memories.size)
    val mem = memories.head
    assertEquals("m_x_3", mem.name)
    assertEquals(Integer.valueOf(3), mem.partyIndex)
  }

  /** Registers memory nodes when other memories exist */
  @Test
  @throws[CVC5ApiException]
  def registerDistinctMemories(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val expr1 = new AtExpr(new MessageExpr(new Str("x")), new Num(3))
    factory.toTerm(expr1)
    val expr2 = new AtExpr(new RandomExpr(new Str("y")), new Num(5))
    factory.toTerm(expr2)
    val memories = factory.getMemories.asScala
    assertEquals(2, memories.size)
  }

  /** Creates term for Equal constraint */
  @Test
  @throws[CVC5ApiException]
  def equalConstraintTerm(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val ast = Loader.toConstraintExpression("m[\"foo\"]@3 == 5")
    val term = factory.constraintToTerm(ast)
    val verifier = new Verifier(factory)
    val actual = verifier.findModelSatisfying(term)
    val expected = Map("m_foo_3" -> 5)
    assertEquals(mockModel(factory, expected), actual)
  }

  /** Creates term for And constraint expression */
  @Test
  @throws[CVC5ApiException]
  def andConstraintTerm(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val ast = new AndConstraint(
      new EqualConstraint(new AtExpr(new MessageExpr(new Str("x")), new Num(3)), new AtExpr(new RandomExpr(new Str("y")), new Num(5))),
      new EqualConstraint(new AtExpr(new RandomExpr(new Str("y")), new Num(5)), new Num(1))
    )
    val term = factory.constraintToTerm(ast)
    val verifier = new Verifier(factory)
    val actual = verifier.findModelSatisfying(term)
    val expected = Map("m_x_3" -> 1, "r_y_5" -> 1)
    assertEquals(mockModel(factory, expected), actual)
  }

  /** Creates term for Not constraint */
  @Test
  @throws[CVC5ApiException]
  def notConstraintTerm(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("2", 10)
    val factory = new TermFactory(termManager, sort)
    val term = factory.constraintToTerm(
      new NotConstraint(
        new EqualConstraint(
          new AtExpr(new MessageExpr(new Str("foo")), new Num(3)),
          new Num(0)
        )
      )
    )
    val verifier = new Verifier(factory)
    val actual = verifier.findModelSatisfying(term)
    val expected = Map("m_foo_3" -> 1)
    assertEquals(mockModel(factory, expected), actual)
  }

  /** Creates term for True constraint */
  @Test
  @throws[CVC5ApiException]
  def trueConstraintTerm(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("2", 10)
    val factory = new TermFactory(termManager, sort)
    val term = factory.constraintToTerm(new TrueConstraint())
    assertEquals(termManager.mkTrue(), term)
  }

  /** Creates complex constraint expressions */
  @Test
  @throws[CVC5ApiException]
  def complexConstraintTerms(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val ast = Loader.toConstraintExpression("out@1 == out@2 + 1 AND out@2 == out@3 + 2 AND out@3 == 4")
    val term = factory.constraintToTerm(ast)
    val verifier = new Verifier(factory)
    val actual = verifier.findModelSatisfying(term)
    val expected = Map("o_3" -> 4, "o_2" -> 6, "o_1" -> 0)
    assertEquals(mockModel(factory, expected), actual)
  }

  /** Does not register a new cvc5 term if one already exists for the memory */
  @Test
  @throws[CVC5ApiException]
  def reuseMemories(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    val expr = new AtExpr(new MessageExpr(new Str("x")), new Num(3))
    factory.toTerm(expr)
    val twin = new AtExpr(new MessageExpr(new Str("x")), new Num(3))
    factory.toTerm(twin)
    val memories = factory.getMemories.asScala
    assertEquals(1, memories.size)
  }

  /** Fails if encounters a command that is not part of overture */
  @Test(expected = classOf[IllegalArgumentException])
  @throws[CVC5ApiException]
  def nonOvertureCommand(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("7", 10)
    val factory = new TermFactory(termManager, sort)
    factory.toTerm(new CallCmd(new Identifier("f"), List()))
  }

  /** CVC5 interpretation for OT */
  @Test
  @throws[CVC5ApiException]
  def OTInterpretation(): Unit = {
    val termManager = new TermManager()
    val sort = termManager.mkFiniteFieldSort("2", 10)
    val factory = new TermFactory(termManager, sort)

    val expr1 = new AtExpr(
      new OTExpr(
        new MessageExpr(new Str("x")),
        new Num(3),
        new RandomExpr(new Str("y")),
        new MessageExpr(new Str("foo"))
      ),
      new Num(2)
    )

    val m_x_3 = termManager.mkConst(sort, "m_x_3")
    val r_y_2 = termManager.mkConst(sort, "r_y_2")
    val m_foo_2 = termManager.mkConst(sort, "m_foo_2")
    val one = termManager.mkFiniteFieldElem("1", sort, 10)

    val select_1 = termManager.mkTerm(Kind.FINITE_FIELD_MULT, m_x_3, m_foo_2)
    val not_m_x_3 = termManager.mkTerm(Kind.FINITE_FIELD_ADD, m_x_3, one)
    val select_2 = termManager.mkTerm(Kind.FINITE_FIELD_MULT, not_m_x_3, r_y_2)
    val ot = termManager.mkTerm(Kind.FINITE_FIELD_ADD, select_1, select_2)

    // TODO: memory equality
    // assertEquals(ot, factory.toTerm(expr1))
  }

}
