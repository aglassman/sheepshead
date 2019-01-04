package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.StandardPlayer
import org.slf4j.LoggerFactory


class Trick(
    private val numberOfPlayers: Int
) {

  companion object {
    val LOGGER = LoggerFactory.getLogger(Trick::class.java)
  }

  internal val playedCards: MutableList<Pair<Player, Card>> = mutableListOf()

  fun currentSeatIndex() = playedCards.size

  fun trickTaken() = playedCards.size == numberOfPlayers

  fun playCard(player: StandardPlayer, cardIndex: Int) {
    val proposedCard = player.peekCard(cardIndex)
    LOGGER.info("$player played ${proposedCard.toUnicodeString()}")
    if(suitLead() != null
        && proposedCard.sheepsheadSuit() != suitLead()
        && player.hasSuitInHand(suitLead()!!))  {
      throw GameException("$player cannot play ${proposedCard.toUnicodeString()} as ${suitLead()} was lead, and $player has ${suitLead()} remaining.")
    }

    val cardToPlay = player.requestCard(cardIndex)

    if(cardToPlay == proposedCard) {
      playedCards.add(player to cardToPlay)
    } else {
      LOGGER.error("Card to play (${cardToPlay.toUnicodeString()})  did not match proposed card (${proposedCard.toUnicodeString()}).")
    }

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

  fun trickPoints() = playedCards
      .map { it.second.points() }
      .sum()
}