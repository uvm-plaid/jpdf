package plaid.logic

import io.github.cvc5.CVC5ApiException
import org.junit.Test
import org.junit.Assert._
import plaid.antlr.Loader
import plaid.ast._

import java.util.{Map, TreeMap}

class GenEntailVerifierTest {

  @Test
  @throws[ CVC5ApiException ]
  def genFreshStringTest(): Unit = {
    val program = new Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    assertEquals(new Str("$1"), genEntailVerifier.genFreshValue(new StringType()))
    assertEquals(new Str("$2"), genEntailVerifier.genFreshValue(new StringType()))
    assertEquals(new Str("$3"), genEntailVerifier.genFreshValue(new StringType()))
  }

  @Test
  @throws[ CVC5ApiException ]
  def genFreshCIDTest(): Unit = {
    val program = new Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    assertEquals(new Num(-1), genEntailVerifier.genFreshValue(new PartyIndexType()))
    assertEquals(new Num(-2), genEntailVerifier.genFreshValue(new PartyIndexType()))
    assertEquals(new Num(-3), genEntailVerifier.genFreshValue(new PartyIndexType()))
  }

  @Test
  @throws[ CVC5ApiException ]
  def genFreshRecordTest(): Unit = {
    val src = "{ s : string ; i : cid}"
    val program = new Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    val recordType = Loader.toType(src).asInstanceOf[RecordType]
    val map = new TreeMap[Identifier, Expr]()
    map.put(new Identifier("s"), new Str("$2"))
    map.put(new Identifier("i"), new Num(-1))
    val expected = new FieldExpr(map)

    assertEquals(expected, genEntailVerifier.genFreshValue(recordType))
  }

  @Test
  @throws[ CVC5ApiException ]
  def nestedRecordType(): Unit = {
    val src = "{t: {s:string; t2:{i:cid}}}"
    val program = new Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    val recordType = Loader.toType(src).asInstanceOf[RecordType]

    val innermap = new TreeMap[Identifier, Expr]()
    innermap.put(new Identifier("s"), new Str("$1"))
    innermap.put(new Identifier("t2"), new FieldExpr(new TreeMap(Map.of(new Identifier("i"), new Num(-2)))))

    val expected = new FieldExpr(new TreeMap(Map.of(new Identifier("t"), new FieldExpr(innermap))))
    assertEquals(expected, genEntailVerifier.genFreshValue(recordType))
  }

  @Test
  @throws[ CVC5ApiException ]
  def genEntailTest(): Unit = {
    val e1 =
      """m[z++"s"]@i1 == m[x++"s"]@i1 + m[y++"s"]@i1 AND
        |m[z++"s"]@i2 == m[x++"s"]@i2 + m[y++"s"]@i2""".stripMargin
    val e2 =
      """m[z++"s"]@i1 + m[z++"s"]@i2 ==
        |m[x++"s"]@i1 + m[y++"s"]@i1 + m[x++"s"]@i2 + m[y++"s"]@i2""".stripMargin

    val program = new Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    val typing = List(
      new TypedIdentifier(new Identifier("i2"), new PartyIndexType()),
      new TypedIdentifier(new Identifier("y"), new StringType()),
      new TypedIdentifier(new Identifier("x"), new StringType()),
      new TypedIdentifier(new Identifier("i1"), new PartyIndexType()),
      new TypedIdentifier(new Identifier("z"), new StringType())
    )

    val result1 = genEntailVerifier.genEntails(typing, Loader.toConstraintExpression(e1), Loader.toConstraintExpression(e2))
    assertTrue(result1)

    val result2 = genEntailVerifier.genEntails(typing, Loader.toConstraintExpression(e2), Loader.toConstraintExpression(e1))
    assertFalse(result2)
  }

}
