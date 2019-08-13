package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.StandardPlayer
import org.slf4j.LoggerFactory
import java.io.Serializable


class BurriedCards(
    numberOfPlayers: Int
): Serializable {

  companion object {
    val LOGGER = LoggerFactory.getLogger(BurriedCards::class.java)
  }

  /**
   * The size of the bury is determined by the number of players.
   */
  private val burySize = when(numberOfPlayers) {
    5 -> 2
    4 -> 4
    else -> throw GameException("Number of players $numberOfPlayers is unsupported for BurriedCards.")
  }

  private var burriedCards = emptyList<Card>()

  fun cardsBurried() = burriedCards.size != 0

  /**
   * Player specifies which cards they'd like to bury from their hand.
   */
  fun bury(player: StandardPlayer, toBury: List<Int>) {

    if (cardsBurried()) {
      throw GameException("Cards have already been burried.")
    }

    if(toBury.size != burySize) {
      throw GameException("Must bury $burySize cards. Tried to bury ${toBury.size}.")
    }

    burriedCards = player.requestCards(toBury)

    LOGGER.debug("$player burried ${burriedCards.map { it.toUnicodeString() } }")

  }

  internal fun points(): Int =
      burriedCards
          .map { it.points() }
          .sum()
}