package plaid.prelude

import io.github.cvc5.TermManager
import picocli.CommandLine
import picocli.CommandLine.{Command, Option, Parameters}
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.ListExprFuncExt.expandAll
import plaid.prelude.ast.ListConstraintFuncExt.expandAll
import plaid.prelude.ast.ListCmdFuncExt.expandAll
import plaid.prelude.cvc.TermFactory
import plaid.prelude.logic.{contracts, verificationFailures}

import java.io.File
import java.nio.file.Files
import scala.compiletime.uninitialized

@Command(
  name = "prelude",
  version = Array("prelude-dev"),
  mixinStandardHelpOptions = true)
class App extends Runnable {

  @Option(names = Array("--field-size", "-s"), description = Array("Order of the finite field"))
  var fieldSize: String = "2"

  @Parameters(paramLabel = "<path>", description = Array("Path to a prelude source file"))
  var path: String = uninitialized

  override def run(): Unit =
    val src = Files.readString(new File(path).toPath)
    val ast = Loader.program(src)
    val exprFns = ast.exprFuncs.expandAll()
    val constraintFns = ast.constraintFuncs.expandAll(exprFns)
    val cmdFns = ast.cmdFuncs.expandAll(exprFns, constraintFns)
    val contracts = cmdFns.contracts()

    contracts.foreach(x =>
      val termManager = TermManager()
      val termFactory = TermFactory(termManager, fieldSize)
      val failures = x.verificationFailures(termFactory)
      val total = x.internals.size
      println(s"${x.f.id.name}: ${total - failures.size}/$total entailments verified"))
}

object App {
  def main(args: Array[String]): Unit =
    val exitCode = new CommandLine(new App()).execute(args*)
    System.exit(exitCode)
}
