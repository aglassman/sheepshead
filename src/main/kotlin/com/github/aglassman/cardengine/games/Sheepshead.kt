package com.github.aglassman.cardengine.games

import com.github.aglassman.cardengine.*
import com.github.aglassman.cardengine.games.Sheepshead.Action.*
import com.github.aglassman.cardengine.games.Sheepshead.Scoring.normal
import java.util.Collections.emptyList
import java.util.Collections.rotate

class Sheepshead(
    private val players: List<Player>,
    private val deck: Deck = SheepsheadDeck(),
    gameNumber: Int = 1,
    val partnerStyle: PartnerStyle = PartnerStyle.jackOfDiamonds
) : Game {

  init {
    if(gameNumber < 1) {
      throw GameException("gameNumber cannot be < 1")
    }
  }

  override fun gameType() = "sheepshead"

  enum class PartnerStyle { goAlone, calledAce, jackOfDiamonds }

  enum class Action { deal, peek, pick, pass, bury, callLeaster, callDoubler, playCard, goAlone, declareUndercard, callAce }

  enum class Scoring { normal, leaster, doubler }

  private val scoring: Scoring = normal

  private val playerOrder = players.toMutableList().apply { rotate(this, -1 * (gameNumber)) }

  private val trickTracker = TrickTracker(playerOrder)

  override fun currentPlayer(): Player {
    return if(!trickTracker.playHasBegun()) {
      dealer()
    } else {
      trickTracker.waitingOnPlayer()
    }
  }

  override fun dealer() = playerOrder.last()

  var teams: Teams? = null

  var blind = Blind(
      playerOrder
  )

  var burriedCards = BurriedCards()

  var cardsDealt = false

  val tricks: List<Trick> = mutableListOf()

  fun gameComplete() = tricks.filter { it.trickTaken() }.size == players.size

  override fun deal() {
    if (players.size == 5) {
      FiveHandDeal(deck, playerOrder, blind).deal()
      cardsDealt = true
    } else {
      throw GameException("Unsupported number of players.")
    }
  }

  private fun playCard(player: Player, cardIndex: Int) {
    val card = player.requestCard(cardIndex)
    trickTracker.currentTrick().playCard(player, card)
    println("$player played ${card.toUnicodeString()}")
    trickTracker.currentTrick() // Will trigger creation of next trick if applicable.
  }

  override fun availableActions(player: Player) = listAvailableActions(player).map { it.name }

  private fun listAvailableActions(player: Player): List<Action> {

    val isDealer = player.isPlayer(dealer())

    return when {
      // deal
      isDealer && !cardsDealt -> listOf(deal)

      // blind
      !blind.blindRoundComplete() -> blindActions(player)
      !trickTracker.playHasBegun() && player == teams?.picker -> listOf(bury)
      player.isPlayer(trickTracker.waitingOnPlayer()) -> listOf(playCard)

      // play
      else -> emptyList()
    }

  }

  private fun blindActions(player: Player): List<Action> {
    return when {
      blind.isAvailable() && blind.playerHasOption(player) && blind.hasLastOption(player) -> listOf(peek, pick, callLeaster, callDoubler)
      blind.isAvailable() && blind.playerHasOption(player) -> listOf(pick, pass, peek)
      else -> emptyList()
    }
  }

  override fun <T> performAction(player: Player, action: String, parameters: Any?): T? {

    val actionToPerform = try {
      Action.valueOf(action)
    } catch (e: Exception) {
      throw GameException("($action) is not a valid action.")
    }

    if (!listAvailableActions(player).contains(actionToPerform)) {
      throw GameException("Player ${player.name} cannot perform $action at this time.")
    }

    return when (actionToPerform) {
      deal -> {
        deal()
        log(player, actionToPerform)
      }
      pass -> {
        blind.pass(player)
        log(player, actionToPerform)
      }
      pick -> {
        blind.pick(player)
        teams = Teams(partnerStyle, player)
        log(player, actionToPerform)
      }
      peek -> {
        blind.peek(player) as T
      }
      bury -> {
        burriedCards.bury(player, parameters as List<Int>)
        trickTracker.beginPlay()
        log(player, actionToPerform)
      }
      playCard -> {
        playCard(player, parameters as Int)
        log(player, actionToPerform)
      }
      else -> throw GameException("No mapping for action (${actionToPerform.name}).")
    }

  }

  private fun <T> log(player: Player, action: Action) = "${player.name} performed ${action.name}" as T

  override fun availableStates(): List<String> {
    return listOf("blind")
  }

  override fun <T> state(key: String): T {
    return when (key) {
      "blind" -> blind.peek() as T
      "lastTrickDetails" -> trickTracker.lastTrickDetails() as T
      else -> throw GameStateException("No game state found for key: ($key)")
    }
  }
}



