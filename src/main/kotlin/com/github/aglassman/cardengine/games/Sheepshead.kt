package com.github.aglassman.cardengine.games

import com.github.aglassman.cardengine.*
import com.github.aglassman.cardengine.games.Blind.Option.*
import com.github.aglassman.cardengine.games.Sheepshead.Action.*
import com.github.aglassman.cardengine.games.Sheepshead.Scoring.normal

class Sheepshead(
    private val players: List<Player>,
    private val deck: Deck = SheepsheadDeck(),
    gameNumber: Int = 1
) : Game {

  override fun gameType() = "sheepshead"

  enum class PartnerStyle { calledAce, jackOfDiamonds }

  enum class Action { deal, peek, pick, pass, bury, callLeaster, callDoubler, playCard, goAlone, declareUndercard, callAce }

  enum class Scoring { normal, leaster, doubler }

  private val scoring: Scoring = normal

  private val playerOrder = players.subList((gameNumber - 1) % players.size, players.size).plus(players.subList(0, (gameNumber - 1) % players.size))

  private val trickTracker = TrickTracker(playerOrder)

  override fun currentPlayer() = trickTracker.waitingOnPlayer() ?: dealer

  private val dealer = playerOrder.first()

  override fun dealer() = dealer

  val teams = Teams()

  var blind = Blind(
      playerOrder,
      teams
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
  }

  override fun availableActions(player: Player) = listAvailableActions(player).map { it.name }

  private fun listAvailableActions(player: Player): List<Action> {

    val isDealer = player.isPlayer(dealer)

    return when {
      // deal
      isDealer && !cardsDealt -> listOf(deal)

      // blind
      !blind.blindRoundComplete() -> blindActions(player)
      !trickTracker.playHasBegun() && player == teams.picker -> listOf(bury)
      player.isPlayer(trickTracker.waitingOnPlayer()) -> listOf(playCard)

      // play
      else -> emptyList()
    }

  }

  private fun blindActions(player: Player): List<Action> {
    return when {
      blind.isAvailable() && blind.hasLastOption(player) -> listOf(peek, pick, callLeaster, callDoubler)
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
      else -> throw GameStateException("No game state found for key: ($key)")
    }
  }
}

class SheepsheadDeck : Deck(
    deck = Suit.values()
        .map { it to Face.values() }
        .flatMap { pair ->
          pair.second
              .filter {
                !listOf(
                    Face.TWO,
                    Face.THREE,
                    Face.FOUR,
                    Face.FIVE,
                    Face.SIX)
                    .contains(it)
              }
              .map {
                Card(pair.first, it)
              }
        }
)

val pointMap = mapOf(
    Face.SEVEN to 0,
    Face.EIGHT to 0,
    Face.NINE to 0,
    Face.KING to 4,
    Face.TEN to 10,
    Face.ACE to 11,
    Face.JACK to 2,
    Face.QUEEN to 3
)

class FiveHandDeal(
    private val deck: Deck,
    private val playerOrder: List<Player>,
    private val blind: Blind
) {
  fun deal() {
    while (deck.cardsLeft() > 0) {

      playerOrder.forEach {
        it.recieveCards(deck.deal(3))
      }

      if (deck.cardsLeft() == 0) break

      blind.setBlind(deck.deal(2))
    }
  }
}

class Blind(
    playerOrder: List<Player>,
    val teams: Teams
) {

  enum class Option { owait, opass, opick, oskip }

  data class PickOption(
      val player: Player,
      var pickOption: Option = owait
  )

  private val pickOption: List<PickOption> =
      (playerOrder.subList(1, playerOrder.size).plus(playerOrder.first()))
          .map { PickOption(it) }

  private val blind: MutableList<Card> = mutableListOf()

  fun blindRoundComplete() = pickOption.filter { it.pickOption == owait }.isEmpty()

  fun isAvailable() = (blind.size > 0) && (pickOption.filter { it.pickOption == opick }.isEmpty())

  fun option(): Player? = pickOption.firstOrNull { isAvailable() && it.pickOption == owait }?.player

  fun playerHasOption(player: Player) = isAvailable() && player == pickOption.first { it.pickOption == owait }.player

  fun hasLastOption(player: Player) = player == pickOption.last().player

  fun setBlind(blind: List<Card>) {
    if (this.blind.size == 0) {
      println("Blind recieved ${blind.joinToString { "${it.toUnicodeString()}" }}")
      this.blind.addAll(blind)
    } else {
      throw GameException("Blind has already been set.")
    }
  }

  fun picker() = pickOption.firstOrNull { it.pickOption == opick }?.player

  fun peek() = blind.toList()

  fun peek(player: Player): List<Card> {
    if(option() == null) {
      throw GameException("Cannot peek as blind has already been picked.")
    }

    return if(playerHasOption(player)) {
      blind.toList()
    }  else {
      throw GameException("${player.name} cannot pick as ${option()?.name} currently has the option.")
    }
  }

  fun pass(player: Player) {
    if (option() == null) {
      throw GameException("Cannot pass as blind has already been picked.")
    }

    if (playerHasOption(player)) {
      setOption(player, opass)
    } else {
      throw GameException("${player.name} cannot pick as ${option()?.name} currently has the option.")
    }
  }

  fun pick(player: Player) {
    if (option() == null) {
      throw GameException("Cannot pick as blind has already been picked.")
    }

    if (playerHasOption(player)) {
      setOption(player, opick)
      pickOption.filter { it.pickOption == owait }.forEach { it.pickOption = oskip }
      player.recieveCards(blind)
      teams.picker = player
    } else {
      throw GameException("${player.name} cannot pick as ${option()?.name} currently has the option.")
    }
  }

  private fun setOption(player: Player, option: Option) {
    pickOption
        .first { it.player == player }
        .pickOption = option
  }

}

class BurriedCards {

  private var burriedCards: List<Card> = emptyList()

  fun cardsBurried() = burriedCards.size != 0

  fun bury(player: Player, toBury: List<Int>) {
    if (cardsBurried()) {
      throw GameException("Cards have already been burried.")
    }

    burriedCards = player.requestCards(toBury)

  }
}

class TrickTracker(
    val playerOrder: List<Player>
) {

  private val tricks: MutableList<Trick> = mutableListOf()

  fun playHasBegun() = tricks.size > 0

  fun playIsComplete() = tricks.filter { it.trickTaken() }.size == playerOrder.size

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

  fun waitingOnPlayer() = currentTrick()?.let { playerOrder[it.currentSeatIndex()] }

  fun beginPlay() {
    if(playHasBegun()) {
      throw GameException("Play has already begun.")
    } else {
      tricks.add(Trick(playerOrder.size))
    }
  }

  private fun newTrick(): Trick {
    val newTrick = Trick(playerOrder.size)
    tricks.add(newTrick)
    return newTrick
  }

}

class Trick(
    private val numberOfPlayers: Int
) {

  private val playedCards: List<Pair<Player, Card>> = mutableListOf()

  fun currentSeatIndex() = playedCards.size

  fun trickTaken() = playedCards.size == numberOfPlayers

  fun playCard(player: Player, card: Card) {
    playedCards.plus(player to card)
  }

}

class Teams() {
  var picker: Player?  = null
  var partner: Player? = null
}