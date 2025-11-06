package plaid.prelude.antlr

import org.antlr.v4.runtime.*
import plaid.prelude.*
import plaid.prelude.PreludeParser.*
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

/** Support for converting Prelude source code into an abstract syntax tree. */
object Loader {

  /** Creates an ANTLR4 parser for Prelude source code. */
  def parser(src: String): PreludeParser =
    val input = new ANTLRInputStream(src)
    val lexer = new PreludeLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new PreludeParser(tokens)
    parser.setBuildParseTree(true)
    parser

  /** Converts an ANTLR4 context into an abstract syntax tree for a type. */
  def typeMarker(ctx: TypeContext): Type =
    TypeVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for a type. */
  def typeMarker(src: String): Type =
    typeMarker(parser(src).`type`())

  /** Converts an ANTLR4 context into an abstract syntax tree for an expression. */
  def expression(ctx: ExprContext): Expr =
    ExpressionVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for an expression. */
  def expression(src: String): Expr =
    expression(parser(src).expr())

  /** Converts an ANTLR context into an abstract syntax tree for a constraint. */
  def constraint(ctx: ConstraintContext): Constraint =
    ConstraintVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for a constraint. */
  def constraint(src: String): Constraint =
    constraint(parser(src).constraint())

  /** Converts an ANTLR4 context into an abstract syntax tree for a command. */
  def command(ctx: CmdContext): Cmd =
    CommandVisitor.visit(ctx)

  /** Converts Prelude source code into an abstract syntax tree for a command. */
  def command(src: String): Cmd =
    command(parser(src).cmd())

  /** Converts an ANTLR4 context into an abstract syntax tree for an expression function. */
  def exprFunc(ctx: ExprFuncContext) = ExprFunc(
    id = Identifier(ctx.ident(0).getText),
    parms = toIdentifiers(ctx.ident()),
    body = expression(ctx.expr()))

  /** Converts Prelude source code into an abstract syntax tree for an expression function. */
  def exprFunc(src: String): ExprFunc =
    exprFunc(parser(src).exprFunc())

  /** Converts an ANTLR context into an abstract syntax tree for a constraint function. */
  def constraintFunc(ctx: ConstraintFuncContext) = ConstraintFunc(
    id = Identifier(ctx.ident(0).getText),
    parms = toIdentifiers(ctx.ident()),
    body = constraint(ctx.constraint()))

  /** Converts Prelude source code into an abstract syntax tree for a constraint function. */
  def constraintFunc(src: String): ConstraintFunc =
    constraintFunc(parser(src).constraintFunc())

  /** Converts an ANTLR4 context into an abstract syntax tree for a command function. */
  def cmdFunc(ctx: CmdFuncContext) = CmdFunc(
    id = Identifier(ctx.ident().getText),
    typedVariables = toTypedIdentifiers(ctx.typedIdent()),
    body = ctx.cmd().asScala.map(command).toList,
    precond = Option(ctx.pre()).map(x => constraint(x.constraint())),
    postcond = Option(ctx.post()).map(x => constraint(x.constraint())))

  /** Converts Prelude source code into an abstract syntax tree for a command function. */
  def cmdFunc(src: String): CmdFunc =
    cmdFunc(parser(src).cmdFunc())

  /** Converts an ANTLR4 context into an abstract syntax tree for a Prelude program. */
  def program(ctx: ProgramContext) = Program(
      cmdFuncs = ctx.cmdFunc().asScala.map(cmdFunc).toList,
      exprFuncs = ctx.exprFunc().asScala.map(exprFunc).toList,
      constraintFuncs = ctx.constraintFunc().asScala.map(constraintFunc).toList)

  /** Converts Prelude source code into an abstract syntax tree for a Prelude program. */
  def program(src: String): Program =
    program(parser(src).program())

  private def toIdentifiers(contexts: java.util.List[IdentContext]): List[Identifier] =
    contexts.asScala.drop(1).map(ctx => Identifier(ctx.getText)).toList

  private def toTypedIdentifiers(contexts: java.util.List[TypedIdentContext]): List[TypedIdentifier] =
    contexts.asScala.map { ctx =>
      TypedIdentifier(Identifier(ctx.ident().getText), Loader.typeMarker(ctx.`type`()))
    }.toList
}
