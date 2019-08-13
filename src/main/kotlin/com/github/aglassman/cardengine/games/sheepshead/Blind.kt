package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.StandardPlayer
import com.github.aglassman.cardengine.games.sheepshead.Blind.Option.*
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 * The BLIND is the set of cards that are not dealt to any player initially.  The size of the
 * BLIND changes depending on how many players are playing.  Each player left of the dealer has
 * the option to PICK the BLIND, then BURY the same number of cards.  This gives the picker
 * a slight advantage as they can BURY a short fail suit, or points.  Any points the picker
 * buries count towards their team's total points.
 *
 * If the player to the left of the dealer PASSES, the next player to the left has the option.
 * If all players, including the dealer PASS, then the game will move to a new scoring state.
 * (see doubler / leaster)
 */
class Blind(
    playerOrder: List<StandardPlayer>
): Serializable {

  companion object {
    val LOGGER = LoggerFactory.getLogger(Blind::class.java)
  }

  private enum class Option(description: String) {
    WAIT("The player is waiting to exercise their option to pick or pass on the blind."),
    PASS("The player has passed on the blind."),
    PICK("The player has picked the blind."),
    SKIP("The player was skipped, and did not get an option to pick or pass.")
  }

  // Internal tracker if a player has interacted with the blind
  private data class PickOption(
      val player: StandardPlayer,
      var pickOption: Option = WAIT
  ): Serializable

  // Creates a list of all players pickOptions.
  private val pickOptions: List<PickOption> = playerOrder.map { PickOption(it) }

  // The blind
  private var blind: List<Card> = listOf()

  /**
   * Returns true if the blind round has been completed.
   */
  fun blindRoundComplete() = pickOptions.none { it.pickOption == WAIT }

  /**
   * Returns true if the blind is still available.
   */
  fun isAvailable() = (blind.size > 0) && (pickOptions.none { it.pickOption == PICK })

  /**
   * Returns the player who currently has the option on the blind.
   */
  fun option(): StandardPlayer? = pickOptions.firstOrNull { isAvailable() && it.pickOption == WAIT }?.player

  /**
   * Returns true if player currently has the option on the blind.
   */
  fun playerHasOption(player: StandardPlayer) = isAvailable() && player == pickOptions.first { it.pickOption == WAIT }.player

  /**
   * Returns the player with the last option on the blind.  This can be used to determine if
   * the player can call leaster or doubler.
   */
  fun hasLastOption(player: StandardPlayer) = player == pickOptions.last().player

  fun setBlind(blind: List<Card>) {
    if (this.blind.size == 0) {
      this.blind = blind.toList()
      LOGGER.debug("Blind Set: ${blind.map { it.toUnicodeString() } }")
    } else {
      throw GameException("Blind has already been set.")
    }
  }

  // Returns the picker, if one exists
  fun picker() = pickOptions
      .firstOrNull { it.pickOption == PICK }
      ?.player

  // Peek at the blind
  fun peek() = blind

  // A player declares they are passing.
  fun pass(player: StandardPlayer) {

    option() ?: throw GameException("Cannot pass as blind has already been picked.")

    if (playerHasOption(player)) {
      setOption(player, PASS)
    } else {
      throw GameException("${player.name} cannot pick as ${option()?.name} currently has the option.")
    }
  }

  // A player declares they are picking
  fun pick(player: StandardPlayer) {

    option() ?: throw GameException("Cannot pick as blind has already been picked.")

    if (playerHasOption(player)) {

      // Set player to PICK
      setOption(player, PICK)

      // Set all other players to SKIP
      pickOptions
          .filter { it.pickOption == WAIT }
          .forEach { it.pickOption = SKIP }

      // Put blind cards to the player's hand.
      player.recieveCards(blind)

    } else {
      throw GameException("${player.name} cannot pick as ${option()?.name} currently has the option.")
    }
  }

  private fun setOption(player: Player, option: Option) {
    pickOptions
        .first { it.player == player }
        .pickOption = option
  }

}