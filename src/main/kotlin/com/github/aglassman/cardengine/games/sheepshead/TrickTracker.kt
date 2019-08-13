package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.StandardPlayer
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*

/**
 * TrickTracker tracks all the tricks for one hand.
 */
class TrickTracker(
    playerOrder: List<StandardPlayer>
): Serializable {

  companion object {
    val LOGGER = LoggerFactory.getLogger(TrickTracker::class.java)
  }

  private val cardsPerHand: Int = when(playerOrder.size){
    5 -> 6
    else -> throw GameException("Unsupported number of players ${playerOrder.size}")
  }

  var trickPlayerOrder = playerOrder.toList()

  private var tricks = emptyList<Trick>()

  /**
   * Returns true of all tricks for the hand have been taken
   */
  fun playIsComplete() = tricks.filter { it.trickTaken() }.size == cardsPerHand

  /**
   * Returns a list of all tricks that have been played thus far.
   */
  fun tricks() = tricks

  /**
   * Returns the most current trick that has not been taken.
   */
  fun currentTrick(): Trick? {
    return tricks.firstOrNull { !it.trickTaken() }
  }

  /**
   * Returns the player who should play the next card.
   */
  fun waitingOnPlayer(): Player? {
    val currentTrick = currentTrick()

    if(currentTrick != null) {
      return trickPlayerOrder[currentTrick.currentSeatIndex()]
    }

    val lastTrick = lastTrick()

    if(lastTrick != null) {
      return lastTrick.trickWinner()
    }

    return null
  }

  /**
   * Create a new trick if possible.  There should be no current trick, and last trick must
   * be complete.
   */
  fun newTrick(): Trick {
    val currentTrick = currentTrick()

    if(currentTrick != null) {
      return currentTrick
    }

    val lastTrick = lastTrick()

    if(lastTrick != null && !playIsComplete()) {

      LOGGER.debug("Trick: ${tricks.size} winner is ${lastTrick.trickWinner()}")
      // rotate order so trick winner is first for next trick
      Collections.rotate(trickPlayerOrder, -1 * trickPlayerOrder.indexOf(lastTrick.trickWinner()))

      LOGGER.debug("New play order: ${trickPlayerOrder}")
    }

    val newTrick = Trick(trickPlayerOrder.size)
    tricks = tricks.plus(newTrick)
    LOGGER.debug("Trick: ${tricks.size} created.")
    return newTrick
  }

  /**
   * Returns the last taken trick.
   */
  fun lastTrick(): Trick? {
    return tricks
        .filter { it.trickTaken() }
        .lastOrNull()
  }

  /**
   * Returns a list of TrickTurns representing the order of the last completed trick, or null if no
   * tricks have been completed yet.
   */
  fun lastTrickDetails(): List<TrickTurn>? {
    val lastCompleteTrick = tricks
        .filter { it.trickTaken() }
        .lastOrNull()

    return lastCompleteTrick?.let {
      val trickWinner = it.trickWinner()!!
      it.playedCards().map { TrickTurn(it.first, it.second, it.first == trickWinner) }
    }
  }

  internal fun calculateCurrentPoints(teams: Teams): Map<Team, Int> =
      teams.teams()
          .map { it to tallyTeamPoints(it.members, tricks) }
          .toMap()

  private fun tallyTeamPoints(teamPlayers: List<Player>, tricks: List<Trick>): Int =
      tricks
          .filter { teamPlayers.contains(it.trickWinner()) }
          .map { it.trickPoints() }
          .sum()

}
