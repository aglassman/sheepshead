package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.StandardPlayer
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 * Tracks which card a player played, and if they won the trick.
 */
data class TrickTurn(
    val player: Player,
    val card: Card,
    val winner: Boolean
): Serializable

/**
 * Keeps track of the current round.
 */
class Trick(
    private val numberOfPlayers: Int
): Serializable {

  companion object {
    val LOGGER = LoggerFactory.getLogger(Trick::class.java)
  }

  private var playedCards: List<Pair<Player, Card>> = emptyList()

  /**
   * Return all the cards that have been played for this trick.
   */
  fun playedCards() = playedCards

  /**
   * Return the index of the seat who last payed a card.
   */
  fun currentSeatIndex() = playedCards.size

  /**
   * Return if the trick has been taken yet.
   */
  fun trickTaken() = playedCards.size == numberOfPlayers

  /**
   * Play a card out of a players had into the trick
   */
  fun playCard(player: StandardPlayer, cardIndex: Int) {

    if(trickTaken()) {
      throw GameException("Cannot play card, this trick is already taken.")
    }

    val proposedCard = player.peekCard(cardIndex)

    LOGGER.info("$player played ${proposedCard.toUnicodeString()}")

    if(suitLead() != null
        && proposedCard.sheepsheadSuit() != suitLead()
        && player.hasSuitInHand(suitLead()!!))  {
      throw GameException("$player cannot play ${proposedCard.toUnicodeString()} as ${suitLead()} was lead, and $player has ${suitLead()} remaining.")
    }

    val cardToPlay = player.requestCard(cardIndex)

    if(cardToPlay == proposedCard) {
      playedCards = playedCards.plus(player to cardToPlay)
    } else {
      LOGGER.error("Card to play (${cardToPlay.toUnicodeString()})  did not match proposed card (${proposedCard.toUnicodeString()}).")
    }

  }

  /**
   * Returns the lead suit for this trick.
   */
  fun suitLead(): SheepsheadSuit? = playedCards.firstOrNull().let { it?.second?.sheepsheadSuit() }

  /**
   * Returns the trick winner.
   */
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

  /**
   * Returns the number of points in the trick.
   */
  fun trickPoints() = playedCards
      .map { it.second.points() }
      .sum()

  fun state(): Map<String, Any?> = mapOf(
      "playedCards" to playedCards,
      "trickTaken" to trickTaken(),
      "trickPoints" to trickPoints(),
      "trickWinner" to trickWinner()
  )
}