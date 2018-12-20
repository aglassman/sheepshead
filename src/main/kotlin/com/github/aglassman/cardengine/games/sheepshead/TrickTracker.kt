package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import org.slf4j.LoggerFactory
import java.util.*


typealias TrickDetails = List<Triple<Player, Card, Boolean>>

class TrickTracker(
    playerOrder: List<Player>,
    private val cardsPerHand: Int
) {

  companion object {
    val LOGGER = LoggerFactory.getLogger(TrickTracker::class.java)
  }

  var trickPlayerOrder = playerOrder.toMutableList()

  private val tricks: MutableList<Trick> = mutableListOf()

  fun playHasBegun() = tricks.size > 0

  fun playIsComplete() = tricks.filter { it.trickTaken() }.size == cardsPerHand // fix this

  fun tricks() = tricks.toList()

  fun currentTrick(): Trick {

    val currentTrick = tricks.firstOrNull { !it.trickTaken() }

    return if(currentTrick != null) {
      currentTrick
    } else {
      newTrick()
    }

  }

  fun waitingOnPlayer() = currentTrick()?.let { trickPlayerOrder[it.currentSeatIndex()] }

  fun beginPlay() {
    if(playHasBegun()) {
      throw GameException("Play has already begun.")
    } else {
      newTrick()
      LOGGER.debug("Play has now begun. Initial play order: ${trickPlayerOrder}")
    }
  }

  private fun newTrick(): Trick {
    val lastTrick = lastTrick()

    if(lastTrick != null && !playIsComplete()) {

      LOGGER.debug("Trick: ${tricks.size} winner is ${lastTrick.trickWinner()}")
      // rotate order so trick winner is first for next trick
      Collections.rotate(trickPlayerOrder, -1 * trickPlayerOrder.indexOf(lastTrick.trickWinner()))

      LOGGER.debug("New play order: ${trickPlayerOrder}")
    }

    val newTrick = Trick(trickPlayerOrder.size)
    tricks.add(newTrick)
    LOGGER.debug("Trick: ${tricks.size} created.")
    return newTrick
  }

  fun lastTrick(): Trick? {
    return tricks
        .filter { it.trickTaken() }
        .lastOrNull()
  }

  /**
   * Returns a list of triples representing the order of the last completed trick, or null if no
   * tricks have been completed yet.
   * first: player who played the card
   * second: the card played
   * third: true if the card won the trick
   */
  fun lastTrickDetails(): TrickDetails? {
    val lastCompleteTrick = tricks
        .filter { it.trickTaken() }
        .lastOrNull()

    return lastCompleteTrick?.let {
      val trickWinner = it.trickWinner()!!
      it.playedCards.map { Triple(it.first, it.second, it.first == trickWinner) }
    }
  }

  internal fun calculateCurrentPoints(teams: Teams): Map<Team, Int> =
      teams.teamList()
          .map { it to tallyTeamPoints(it.second, tricks) }
          .toMap()

  private fun tallyTeamPoints(teamPlayers: List<Player>, tricks: List<Trick>): Int =
      tricks
          .filter { teamPlayers.contains(it.trickWinner()) }
          .map { it.trickPoints() }
          .sum()

}
