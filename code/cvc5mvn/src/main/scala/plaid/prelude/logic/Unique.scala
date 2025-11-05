package plaid.prelude.logic

import plaid.prelude.ast.{Identifier, Num, Str}

import java.util.concurrent.atomic.AtomicInteger

/**
 * Support for generating unique instances of AST objects.
 */
object Unique {
  private val partyCounter = new AtomicInteger(0)
  private val stringCounter = new AtomicInteger(0)

  /**
   * Generate a unique party index using a negative number to avoid collisions.
   */
  def party() = Num(-partyCounter.getAndIncrement())

  /**
   * Generate a unique string that includes an illegal symbol to avoid
   * collisions.
   */
  def string() = Str(s"$$${stringCounter.getAndIncrement()}")
}
