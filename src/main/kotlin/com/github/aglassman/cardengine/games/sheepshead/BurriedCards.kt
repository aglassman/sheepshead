package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import org.slf4j.LoggerFactory
import java.util.*


class BurriedCards(
    numberOfPlayers: Int
) {

  private val maxBurySize = when(numberOfPlayers) {
    5 -> 2
    4 -> 4
    else -> throw GameException("Number of players $numberOfPlayers is unsupported for BurriedCards.")
  }

  companion object {
    val LOGGER = LoggerFactory.getLogger(BurriedCards::class.java)
  }

  private var burriedCards: List<Card> = Collections.emptyList()

  fun cardsBurried() = burriedCards.size != 0

  fun bury(player: Player, toBury: List<Int>) {
    if (cardsBurried()) {
      throw GameException("Cards have already been burried.")
    }

    if(toBury.size > maxBurySize) {
      throw GameException("Cannot bury more than $maxBurySize cards.")
    }

    burriedCards = player.requestCards(toBury)
    LOGGER.debug("$player burried ${burriedCards.map { it.toUnicodeString() } }")

  }

  internal fun points(): Int =
      burriedCards
          .map { it.points() }
          .sum()
}