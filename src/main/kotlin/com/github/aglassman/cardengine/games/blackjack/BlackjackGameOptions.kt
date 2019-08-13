package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.*

enum class DealerStrategy(
    val description: String
) {
  casino(
      "The dealer is a NPC, and does not change."),
  switchPerHand(
      "The deal rotates to the left after each hand."),
  cutForLow(
      """
      Players cut a deck for the lowest card to determine the original dealer.
      Dealer retains the privilege , but has the option of selling it to the
      higest bidder before or after any hand.  However, if they deal a natural
      to another player and are unable to match it, the dealing privilege goes to
      that player beginning with the next hand.  With more than one claimant, the
      player nearest the dealer's left takes precedence.
      """.trimIndent()),


}

class BlackjackGameOptions(
    val dealerStrategy: DealerStrategy = DealerStrategy.switchPerHand,
    val availableSeats: Int = 5,
    val dealerHitsSoft17: Boolean = true,
    val allowSplit: Boolean = false,
    val allowDoubleDown: Boolean = false
): GameOptionProvider {

  private val minSeats = 1
  private val maxSeats = 6

  init {
    when {
      availableSeats < minSeats -> throw GameException("Must have at least $minSeats availableSeats.")
      availableSeats > maxSeats -> throw GameException("Cannot have more than $maxSeats availableSeats.")
    }
  }

  constructor(optionMap: Map<String, String>): this(
      dealerStrategy = optionMap["dealerStrategy"]?.let { DealerStrategy.valueOf(it) } ?: DealerStrategy.switchPerHand,
      availableSeats = optionMap["availableSeats"]?.let { it.toInt() } ?: 5,
      dealerHitsSoft17 = optionMap["dealerHitsSoft17"]?.let { it.toBoolean() } ?: true,
      allowSplit = optionMap["allowSplit"]?.let { it.toBoolean() } ?: true,
      allowDoubleDown = optionMap["allowDoubleDown"]?.let { it.toBoolean() } ?: true
  )

  override fun availableOptions() = listOf(
      GameOptionDescriptor(
          "Dealer Strategy",
          "dealerStrategy",
          "Specify the strategy on how a dealer is determined.",
          DealerStrategy.values().map { it.toString() }),
      GameOptionDescriptor(
          "Available Seats",
          "availableSeats",
          "The number of seats available at this table.",
          (minSeats..maxSeats).map { it.toString() }),
      GameOptionDescriptor(
          "Dealer Hits on Soft 17",
          "dealerHitsSoft17",
          "Dealer must hit when showing a soft 17.",
          listOf("true", "false")),
      GameOptionDescriptor(
          "Allow Splits",
          "allowSplit",
          "Player can split after the deal if showing a pair of 9, 10, J, Q, K, or Ace.",
          listOf("true", "false")),
      GameOptionDescriptor(
          "Allow Double Down",
          "allowDoubleDown",
          "Player can double their bet after the deal if showing 9, 10, J, Q, K, or Ace. " +
            "The dealer will then deal them only one more card.",
          listOf("true", "false"))
  )

  override fun options() = listOf(
        GameOption("dealerStrategy", dealerStrategy),
        GameOption("availableSeats", availableSeats),
        GameOption("dealerHitsSoft17", dealerHitsSoft17),
        GameOption("allowSplit", allowSplit),
        GameOption("allowDoubleDown", allowDoubleDown))
      .map { it.key to it }
      .toMap()

  fun validate(players: List<Player>) {
    when {
      players.map { it.name }.contains("dealer") ->
        throw GameException("Player cannot be named 'dealer'.")
      players.size < minSeats || players.size > availableSeats ->
        throw GameException("Must have between $minSeats and $availableSeats players. Currently ${players.size} players.")
    }
  }

}