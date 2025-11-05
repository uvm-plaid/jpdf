package plaid.prelude.logic

import plaid.prelude.ast.{Identifier, Num, Str}

import java.util.concurrent.atomic.AtomicInteger

// TODO Don't need most of this probably?

/**
 * Support for generating unique instances of AST objects.
 */
object Unique {
  private val identifierCounter = new AtomicInteger(0)
  private val partyCounter = new AtomicInteger(0)
  private val stringCounter = new AtomicInteger(0)

  /**
   * Generate a unique identifier prefixed with the name of another identifier
   * and including an illegal symbol to avoid collisions.
   */
  def identifier(prefix: Identifier) =
    Identifier(s"${prefix.name}$$${-identifierCounter.getAndIncrement()}")

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
