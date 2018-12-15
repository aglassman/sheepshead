import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Deck
import com.github.aglassman.cardengine.Face.*
import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.Suit.*
import com.github.aglassman.cardengine.games.Sheepshead
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SheepsheadTurnTest {


  val testSheepsDeck = Deck(
      listOf(
          Card(CLUB, TEN),
          Card(DIAMOND, EIGHT),
          Card(DIAMOND, TEN),

          Card(SPADE, JACK),
          Card(CLUB, JACK),
          Card(HEART, KING),

          Card(CLUB, ACE),
          Card(SPADE, TEN),
          Card(SPADE, SEVEN),

          Card(CLUB, EIGHT),
          Card(CLUB, QUEEN),
          Card(DIAMOND, KING),

          Card(SPADE, ACE),
          Card(CLUB, NINE),
          Card(HEART, EIGHT),

          Card(HEART, ACE),
          Card(DIAMOND, NINE),

          Card(HEART, NINE),
          Card(HEART, JACK),
          Card(DIAMOND, SEVEN),

          Card(SPADE, KING),
          Card(HEART, SEVEN),
          Card(SPADE, NINE),

          Card(HEART, TEN),
          Card(SPADE, EIGHT),
          Card(CLUB, SEVEN),

          Card(DIAMOND, JACK),
          Card(DIAMOND, ACE),
          Card(DIAMOND, QUEEN),

          Card(SPADE, QUEEN),
          Card(CLUB, KING),
          Card(HEART, QUEEN)
      )
  )

  // For reference

  val earlsExpectedHand = listOf(
      Card(CLUB, TEN),
      Card(DIAMOND, EIGHT),
      Card(DIAMOND, TEN),
      Card(HEART, NINE),
      Card(HEART, JACK),
      Card(DIAMOND, SEVEN))

  val andysExpectedHand = listOf(
      Card(SPADE, JACK),
      Card(CLUB, JACK),
      Card(HEART, KING),
      Card(SPADE, KING),
      Card(HEART, SEVEN),
      Card(SPADE, NINE))

  val bradsExpectedHand = listOf(
      Card(CLUB, ACE),
      Card(SPADE, TEN),
      Card(SPADE, SEVEN),
      Card(HEART, TEN),
      Card(SPADE, EIGHT),
      Card(CLUB, SEVEN))

  val carlsExpectedHand = listOf(
      Card(CLUB, EIGHT),
      Card(CLUB, QUEEN),
      Card(DIAMOND, KING),
      Card(DIAMOND, JACK),
      Card(DIAMOND, ACE),
      Card(DIAMOND, QUEEN))

  val derylsExpectedHand = listOf(
      Card(SPADE, ACE),
      Card(CLUB, NINE),
      Card(HEART, EIGHT),
      Card(SPADE, QUEEN),
      Card(CLUB, KING),
      Card(HEART, QUEEN))

  val expectedBlind = listOf(
      Card(HEART, ACE),
      Card(DIAMOND, NINE))

  @Test
  fun startSheepshead_deal_passAndPickupBlind() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    // Game 2, so Brad should be dealer
    val game = Sheepshead(
        players = players,
        deck = testSheepsDeck,
        gameNumber = 5
    )

    with(game) {

      assertEquals("Earl", game.dealer().name, "Earl should be the dealer.")

      assertEquals("Earl", currentPlayer().name)

      assertFalse(availableActions(andy).contains("deal"), "Andy should not be able to deal.")
      assertFalse(availableActions(brad).contains("deal"), "Brad should not be able to deal.")
      assertFalse(availableActions(carl).contains("deal"), "Carl should not be able to deal.")
      assertFalse(availableActions(deryl).contains("deal"), "Deryl should not be able to deal.")
      assertTrue(availableActions(earl).contains("deal"), "Earl should  be able to deal.")

      assertEquals(0, availableActions(andy).size)

      performAction<Any?>(earl, "deal")

    }

    try {
      game.performAction<Any?>(brad, "pass")
      fail<Any?>("Brad should not be able to pass, as it was Earl's deal.  Andy has first option.")
    } catch (e: GameException) {
      assertEquals("Player Brad cannot perform pass at this time.", e.message)
    }

    try {
      game.performAction<Any?>(andy, "random-thing")
      fail<Any?>("User should not be able to perform a non-existent action.")
    } catch (e: GameException) {
      assertEquals("(random-thing) is not a valid action.", e.message)
    }

    game.performAction<Any?>(andy, "pass")
    game.performAction<Any?>(brad, "pass")

    val carlsExpectedHandAfterPicking = listOf(
        // dealt hand
        Card(CLUB, EIGHT),
        Card(CLUB, QUEEN),
        Card(DIAMOND, KING),
        Card(DIAMOND, JACK),
        Card(DIAMOND, ACE), // bury
        Card(DIAMOND, QUEEN),
        // blind
        Card(HEART, ACE), // bury
        Card(DIAMOND, NINE))

    val carlsExpectedHandAfterBury = listOf(
        Card(CLUB, EIGHT),
        Card(CLUB, QUEEN),
        Card(DIAMOND, KING),
        Card(DIAMOND, JACK),
        Card(DIAMOND, ACE),
        Card(DIAMOND, QUEEN),
        Card(HEART, ACE),
        Card(DIAMOND, NINE))

    with(carl) {
      assertEquals(6, hand().size)

      assertEquals(carlsExpectedHand, hand())

      val blindPeek = game.performAction<List<Card>>(this, "peek") ?: fail("peek failed")

      assertTrue(blindPeek.containsAll(expectedBlind))

      game.performAction<Any?>(this, "pick")

      assertEquals(8, hand().size)
      assertTrue(hand().containsAll(carlsExpectedHandAfterPicking))

      assertEquals(Card(DIAMOND, ACE), hand()[4])
      assertEquals(Card(HEART, ACE), hand()[6])

      try {
        game.performAction<Any?>(this, "bury", listOf(2, 44))
        fail<Any?>("Should not have been allowed to bury a card with index 44.")
      } catch (e: Exception) {
        assertEquals("Requested card at index 44, but hand only has 8 cards.", e.message)
      }

      // Bury the Ace of Hearts, and Ace of Diamonds
      game.performAction<Any?>(this, "bury", listOf(4, 6))

      assertTrue(
          hand().containsAll(
              listOf(
                  Card(CLUB, EIGHT),
                  Card(CLUB, QUEEN),
                  Card(DIAMOND, KING),
                  Card(DIAMOND, JACK),
                  Card(DIAMOND, QUEEN),
                  Card(DIAMOND, NINE)
              )))
    }

  }

  @Test
  fun startSheepshead_deal_passPickupAndCompleteFirstTrick() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    // Game 2, so Brad should be dealer
    val game = Sheepshead(
        players = players,
        deck = testSheepsDeck,
        gameNumber = 5
    )

    game.performAction<Any?>(earl, "deal")

    game.performAction<Any?>(andy, "pass")
    game.performAction<Any?>(brad, "pass")

    game.performAction<Any?>(carl, "pick")
    game.performAction<Any?>(carl, "bury", listOf(4, 6))

    // Verify it is Andy's turn
    players
        .filter { it != andy }
        .forEach {
          println("Actions for $it: ${game.availableActions(it)}")
          assertFalse(game.availableActions(it).contains("playCard"), "$it should not be able to play a card.")
        }

    assertTrue(game.availableActions(andy).contains("playCard"), "Andy should be able to play a card.")

    // Check Hand
    assertEquals(
        listOf(
            Card(SPADE, JACK),
            Card(CLUB, JACK),
            Card(HEART, KING),
            Card(SPADE, KING),
            Card(HEART, SEVEN),
            Card(SPADE, NINE)),
        andy.hand())

    game.performAction<Any?>(andy, "playCard", 2)

    // Verify card was removed from hand
    assertEquals(
        listOf(
            Card(SPADE, JACK),
            Card(CLUB, JACK),
            Card(SPADE, KING),
            Card(HEART, SEVEN),
            Card(SPADE, NINE)),
        andy.hand())

    // Verify it is now Brad's turn

    players
        .filter { it != brad }
        .forEach {
          println("Actions for $it: ${game.availableActions(it)}")
          assertFalse(game.availableActions(it).contains("playCard"), "$it should not be able to play a card.")
        }

    assertTrue(game.availableActions(brad).contains("playCard"))
  }
}