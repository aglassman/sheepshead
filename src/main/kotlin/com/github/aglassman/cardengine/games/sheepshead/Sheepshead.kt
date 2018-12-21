package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*
import com.github.aglassman.cardengine.games.sheepshead.Action.*
import com.github.aglassman.cardengine.games.sheepshead.Scoring.normal
import org.slf4j.LoggerFactory
import java.util.Collections.emptyList
import java.util.Collections.rotate

class Sheepshead(
    private val players: List<Player>,
    private val deck: Deck = SheepsheadDeck(),
    gameNumber: Int = 1,
    val partnerStyle: PartnerStyle = PartnerStyle.jackOfDiamonds
) : Game {

  companion object {
    val LOGGER = LoggerFactory.getLogger(Sheepshead::class.java)
  }

  init {
    if(gameNumber < 1) {
      throw GameException("gameNumber cannot be < 1")
    }
  }

  override fun gameType() = "sheepshead"

  private val scoring: Scoring = normal

  private val playerOrder = players.toMutableList().apply { rotate(this, -1 * (gameNumber)) }

  private val trickTracker = TrickTracker(
      playerOrder = playerOrder,
      cardsPerHand = when(players.size){
        5 -> 6
        else -> 5
      })

  override fun currentPlayer(): Player {
    return if(!cardsDealt) {
      dealer()
    } else if(blind.option() != null) {
      blind.option()!!
    } else if(!burriedCards.cardsBurried()) {
      teams?.picker!!
    } else if(teams?.needToCallPartner() ?: false) {
      teams?.picker!!
    } else {
      trickTracker.waitingOnPlayer()
    }
  }

  override fun dealer() = playerOrder.last()

  var teams: Teams? = null

  var blind = Blind(
      playerOrder
  )

  var burriedCards = BurriedCards( numberOfPlayers = players.size )

  var cardsDealt = false

  override fun isComplete() = trickTracker.playIsComplete()

  override fun deal() {
    if (players.size == 5) {
      FiveHandDeal(deck, playerOrder, blind).deal()
      cardsDealt = true
    } else {
      throw GameException("Unsupported number of players.")
    }
  }

  private fun playCard(player: Player, cardIndex: Int) {
    trickTracker.currentTrick().playCard(player, cardIndex)
    trickTracker.currentTrick() // Will trigger creation of next trick if applicable.
  }

  override fun availableActions(player: Player) = listAvailableActions(player).map { it.name }

  override fun describeAction(action: String): String {
    return try {
      Action.valueOf(action).let { "${it.describe(this)}" }
    } catch (e: Exception) {
      throw GameException("($action) is not a valid action.")
    }
  }

  private fun listAvailableActions(player: Player): List<Action> {

    val isDealer = player.isPlayer(dealer())

    return when {
      // deal
      isDealer && !cardsDealt -> listOf(deal)

      // blind
      !blind.blindRoundComplete() -> blindActions(player)
      !burriedCards.cardsBurried() && player == teams?.picker -> listOf(Action.bury)
      teams?.needToCallPartner() ?: false -> partnerActions()
      player.isPlayer(trickTracker.waitingOnPlayer()) -> listOf(playCard)

      // play
      else -> emptyList()
    }

  }

  private fun blindActions(player: Player): List<Action> {
    return when {
      blind.isAvailable() && blind.playerHasOption(player) && blind.hasLastOption(player) -> listOf(Action.pick, Action.callLeaster, Action.callDoubler)
      blind.isAvailable() && blind.playerHasOption(player) -> listOf(Action.pick, Action.pass)
      else -> emptyList()
    }
  }

  private fun partnerActions(): List<Action> {
    return when(partnerStyle) {
      PartnerStyle.calledAce -> listOf(callAce)
      else -> listOf(goAlone, startPlay)
    }
  }

  override fun <T> performAction(player: Player, action: String, parameters: Any?): T {

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
        LOGGER.info("$player dealt")
        log(player, actionToPerform)
      }
      pass -> {
        blind.pass(player)
        LOGGER.info("$player passed")
        log(player, actionToPerform)
      }
      pick -> {
        blind.pick(player)
        LOGGER.info("$player picked")
        teams = Teams(partnerStyle, player, players)
        log(player, actionToPerform)
      }
      bury -> {
        burriedCards.bury(player, parameters as List<Int>)
        if(partnerStyle == PartnerStyle.jackOfDiamonds) {
          teams?.callPartner()
        }
        LOGGER.info("$player burried")
        log(player, actionToPerform)
      }
      callAce -> {
        teams?.callPartner(Suit.valueOf(parameters as String))
        log(player, actionToPerform)
      }
      goAlone -> {
        teams?.goAlone()
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
    return mutableListOf<String>()
        .plus("blind")
        .plus("lastTrickDetails")
        .plus("lastTrickWinner")
        .plus("teams")
        .plus("points")
        .plus("gameWinner")
        .plus("score")
        .plus("partnerStyle")
        .plus("partnerKnown")
        .let {
          if(trickTracker.lastTrick() != null)
            it
                .plus("lastTrickDetails")
                .plus("lastTrickWinner")
          else
            it }
  }

  override fun <T> state(key: String): T {
    return when (key) {
      "blind" -> blind.peek() as T
      "lastTrickDetails" -> trickTracker.lastTrickDetails() as T
      "lastTrickWinner" -> trickTracker.lastTrick()?.trickWinner() as T
      "teams" -> teams?.teamList() as T
      "points" -> points().determinePoints() as T
      "gameWinner" -> points().determineWinner() as T
      "score" -> points().determineScore() as T
      "partnerStyle" -> partnerStyle as T
      "partnerKnown" -> teams?.partnerKnown() as T
      else -> throw GameStateException("No game state found for key: ($key)")
    }
  }

  private fun points() = Points(
      scoring,
      trickTracker,
      burriedCards,
      teams)

}



