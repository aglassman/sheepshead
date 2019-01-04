package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Deck
import com.github.aglassman.cardengine.StandardPlayer


class FiveHandDeal(
    private val deck: Deck,
    private val players: List<StandardPlayer>,
    private val blind: Blind
) {
  fun deal() {
    while (deck.cardsLeft() > 0) {

      players.forEach {
        it.recieveCards(deck.deal(3))
      }

      if (deck.cardsLeft() == 0) break

      blind.setBlind(deck.deal(2))
    }
  }
}