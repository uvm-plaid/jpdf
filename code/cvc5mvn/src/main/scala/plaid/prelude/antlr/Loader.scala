package plaid.prelude.antlr

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTreeWalker
import plaid.prelude.*
import plaid.prelude.ast.*

/** Support for converting Prelude source code into an abstract syntax tree. */
object Loader {

  /** Creates an ANTLR4 parser for Prelude source code. */
  def parser(src: String): PreludeParser = {
    val input = new ANTLRInputStream(src)
    val lexer = new PreludeLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new PreludeParser(tokens)
    parser.setBuildParseTree(true)
    parser
  }

  /** Converts an ANTLR4 context into an abstract syntax tree for a type. */
  def typeMarker(ctx: PreludeParser.TypeContext): Type =
    TypeVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for an type. */
  def typeMarker(src: String): Type =
    typeMarker(parser(src).`type`())

  /** Converts an ANTLR4 context into an abstract syntax tree for an expression. */
  def expression(ctx: PreludeParser.ExprContext): Expr =
    ExpressionVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for an expression. */
  def expression(src: String): Expr =
    expression(parser(src).expr())

  /** Converts an ANTLR context into an abstract syntax tree for a constraint. */
  def constraint(ctx: PreludeParser.ConstraintExprContext): Constraint =
    ConstraintVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for a constraint. */
  def constraint(src: String): Constraint =
    constraint(parser(src).constraintExpr())

  /** Converts an ANTLR4 context into an abstract syntax tree for a command. */
  def command(ctx: PreludeParser.CommandContext): Cmd =
    CommandVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for a command. */
  def command(src: String): Cmd =
    command(parser(src).command())

  /** Converts an ANTLR4 context into an abstract syntax tree for a Prelude program. */
  def program(ctx: PreludeParser.ProgramContext): Program = {
    val listener = new FunctionListener()
    ParseTreeWalker.DEFAULT.walk(listener, ctx)

    Program(
      listener.commandFunctions.toList,
      listener.exprFunctions.toList,
      listener.constraintFunctions.toList)
  }

  /** Converts Prelude source code into an abstract syntax tree for a Prelude program. */
  def program(src: String): Program =
    program(parser(src).program())
}
