package plaid.antlr

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTreeWalker
import plaid.*
import plaid.ast.*

import scala.jdk.CollectionConverters.*

/** Support for converting Prelude source code into an abstract syntax tree. */
object Loader {

  /** Creates an ANTLR4 parser for Prelude source code. */
  def createParser(src: String): PreludeParser = {
    val input = new ANTLRInputStream(src)
    val lexer  = new PreludeLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new PreludeParser(tokens)
    parser.setBuildParseTree(true)
    parser
  }

  /** Convert an ANTLR TypeContext into AST. */
  def toType(ctx: PreludeParser.TypeContext): Type =
    TypeVisitor.visit(ctx)

  def toType(src: String): Type =
    toType(createParser(src).`type`())

  /** Converts an ANTLR4 context into an abstract syntax tree for an expression. */
  def toExpression(ctx: PreludeParser.ExprContext): Expr =
    ExpressionVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for an expression. */
  def toExpression(src: String): Expr =
    toExpression(createParser(src).expr())

  /** Converts an ANTLR context for Constraint Expr into an abstract syntax tree. */
  def toConstraintExpression(ctx: PreludeParser.ConstraintExprContext): Constraint =
    ConstraintVisitor.visit(ctx)

  /** Converts Constraint Expr source code into an abstract syntax tree. */
  def toConstraintExpression(src: String): Constraint =
    toConstraintExpression(createParser(src).constraintExpr())

  /** Converts an ANTLR4 context into an abstract syntax tree for a command. */
  def toCommand(ctx: PreludeParser.CommandContext): Cmd =
    CommandVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for a command. */
  def toCommand(src: String): Cmd =
    toCommand(createParser(src).command())

  /** Converts an ANTLR4 context into an abstract syntax tree for a Prelude program. */
  def toProgram(ctx: PreludeParser.ProgramContext): Program = {
    val listener = new FunctionListener()
    ParseTreeWalker.DEFAULT.walk(listener, ctx)

    val precondition =
      Option(ctx.precondsection())
        .map(sec => toConstraintExpression(sec.constraintExpr()))
        .orNull

    val postcondition =
      Option(ctx.postcondsection())
        .map(sec => toConstraintExpression(sec.constraintExpr()))
        .orNull

    Program(
      listener.getCommandFunctions,
      listener.getExprFunctions,
      listener.getConstraintFunctions,
      precondition,
      postcondition
    )
  }

  /** Converts Prelude source code into an abstract syntax tree for a Prelude program. */
  def toProgram(src: String): Program =
    toProgram(createParser(src).program())
}
