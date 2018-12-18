package com.github.aglassman.cardengine.games

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import java.util.*


class BurriedCards {

  private var burriedCards: List<Card> = Collections.emptyList()

  fun cardsBurried() = burriedCards.size != 0

  fun bury(player: Player, toBury: List<Int>) {
    if (cardsBurried()) {
      throw GameException("Cards have already been burried.")
    }

    burriedCards = player.requestCards(toBury)

  }
}