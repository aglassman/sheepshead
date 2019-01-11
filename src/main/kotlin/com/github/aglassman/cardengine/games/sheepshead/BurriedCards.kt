package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.StandardPlayer
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*


class BurriedCards(
    numberOfPlayers: Int
): Serializable {

  private val burySize = when(numberOfPlayers) {
    5 -> 2
    4 -> 4
    else -> throw GameException("Number of players $numberOfPlayers is unsupported for BurriedCards.")
  }

  companion object {
    val LOGGER = LoggerFactory.getLogger(BurriedCards::class.java)
  }

  private var burriedCards: List<Card> = Collections.emptyList()

  fun cardsBurried() = burriedCards.size != 0

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