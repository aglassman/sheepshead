package com.github.aglassman.cardengine.games

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player


class Trick(
    private val numberOfPlayers: Int
) {

  internal val playedCards: MutableList<Pair<Player, Card>> = mutableListOf()

  fun currentSeatIndex() = playedCards.size

  fun trickTaken() = playedCards.size == numberOfPlayers

  fun playCard(player: Player, card: Card) {
    if(suitLead() != null
        &&card.sheepsheadSuit() != suitLead()
        && player.hasSuitInHand(suitLead()!!))  {
      throw GameException("$player cannot play ${card.toUnicodeString()} as ${suitLead()} was lead, and $player has ${suitLead()} remaining.")
    }
    playedCards.add(player to card)
  }

  fun suitLead(): SheepsheadSuit? = playedCards.firstOrNull().let { it?.second?.sheepsheadSuit() }

  fun trickWinner(): Player? {
    return if(suitLead() == SheepsheadSuit.Trump) {
      playedCards
          .filter { it.second.sheepsheadSuit() == SheepsheadSuit.Trump }
          .maxBy { it.second.power() }
          ?.first
    } else {
      val maxLeadSuit = playedCards
          .filter { it.second.sheepsheadSuit() == suitLead() }
          .maxBy { it.second.power() }

      val maxTrump = playedCards
          .filter { it.second.sheepsheadSuit() == SheepsheadSuit.Trump }
          .maxBy { it.second.power() }

      if(maxTrump != null) maxTrump.first else maxLeadSuit?.first
    }
  }
}