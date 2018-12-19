package com.github.aglassman.cardengine.games

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import java.util.*


class TrickTracker(
    playerOrder: List<Player>
) {

  var trickPlayerOrder = playerOrder.toMutableList()

  private val tricks: MutableList<Trick> = mutableListOf()

  fun playHasBegun() = tricks.size > 0

  fun playIsComplete() = tricks.filter { it.trickTaken() }.size == trickPlayerOrder.size

  fun currentTrick(): Trick {
    if(playIsComplete()) {
      throw GameException("No current trick as play has completed.")
    }

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
      tricks.add(Trick(trickPlayerOrder.size))
    }
  }

  private fun newTrick(): Trick {
    val lastTrick = lastTrick()

    if(lastTrick != null) {
      // rotate order so trick winner is first for next trick
      Collections.rotate(trickPlayerOrder, -1 * trickPlayerOrder.indexOf(lastTrick.trickWinner()))
    }

    val newTrick = Trick(trickPlayerOrder.size)
    tricks.add(newTrick)
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

}

typealias TrickDetails = List<Triple<Player, Card,Boolean>>
