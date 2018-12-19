
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

  val andysExpectedHand = listOf(
      Card(CLUB, TEN),
      Card(DIAMOND, EIGHT),
      Card(DIAMOND, TEN),
      Card(HEART, NINE),
      Card(HEART, JACK),
      Card(DIAMOND, SEVEN))

  val bradsExpectedHand = listOf(
      Card(SPADE, JACK),
      Card(CLUB, JACK),
      Card(HEART, KING),
      Card(SPADE, KING),
      Card(HEART, SEVEN),
      Card(SPADE, NINE))

  val carlsExpectedHand = listOf(
      Card(CLUB, ACE),
      Card(SPADE, TEN),
      Card(SPADE, SEVEN),
      Card(HEART, TEN),
      Card(SPADE, EIGHT),
      Card(CLUB, SEVEN))

  val derylsExpectedHand = listOf(
      Card(CLUB, EIGHT),
      Card(CLUB, QUEEN),
      Card(DIAMOND, KING),
      Card(DIAMOND, JACK),
      Card(DIAMOND, ACE),
      Card(DIAMOND, QUEEN))

  val earlsExpectedHand = listOf(
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

    println(game.describeAction("deal"))
    println(game.describeAction("pass"))
    println(game.describeAction("peek"))

    with(game) {

      assertEquals("Earl", game.dealer().name, "Earl should be the dealer.")

      assertEquals("Earl", currentPlayer().name)

      assertFalse(availableActions(andy).contains("deal"), "Andy should not be able to deal.")
      assertFalse(availableActions(brad).contains("deal"), "Brad should not be able to deal.")
      assertFalse(availableActions(carl).contains("deal"), "Carl should not be able to deal.")
      assertFalse(availableActions(deryl).contains("deal"), "Deryl should not be able to deal.")
      assertTrue(availableActions(earl).contains("deal"), "Earl should  be able to deal.")

      assertEquals(0, availableActions(andy).size)

      players
          .filter { it != earl }
          .forEach {
            val actions = game.availableActions(it)
            println("Pre Deal: Actions for $it: $actions")
            assertTrue(actions.isEmpty(), "$it should not have any pre-deal available actions, but had $actions.")
          }

      performAction<Any?>(earl, "deal")

      players
          .filter { it != andy }
          .forEach {
            val actions = game.availableActions(it)
            println("Post Deal: Actions for $it: $actions")
            assertTrue(actions.isEmpty(), "$it should not have any post-deal available actions, but had $actions.")
          }

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
    game.performAction<Any?>(carl, "pass")

    val derylsExpectedHandAfterPicking = listOf(
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

    val derylsExpectedHandAfterBury = listOf(
        Card(CLUB, EIGHT),
        Card(CLUB, QUEEN),
        Card(DIAMOND, KING),
        Card(DIAMOND, JACK),
        Card(DIAMOND, ACE),
        Card(DIAMOND, QUEEN),
        Card(HEART, ACE),
        Card(DIAMOND, NINE))

    with(deryl) {
      assertEquals(6, hand().size)

      assertEquals(derylsExpectedHand, hand())

      val blindPeek = game.performAction<List<Card>>(this, "peek") ?: fail("peek failed")

      assertTrue(blindPeek.containsAll(expectedBlind))

      game.performAction<Any?>(this, "pick")

      assertEquals(8, hand().size)
      assertEquals(derylsExpectedHandAfterPicking, this.hand())

      assertEquals(Card(DIAMOND, ACE), hand()[4])
      assertEquals(Card(HEART, ACE), hand()[6])

      assertTrue(game.availableActions(this).contains("bury"), "Deryl should be able to bury.")

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
  fun startSheepshead_deal_passPickupAndPlayFirstCard() {

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

    players
        .forEach {
          println("Pre Deal: Actions for $it: ${game.availableActions(it)}")
        }

    game.performAction<Any?>(earl, "deal")

    players
        .forEach {
          println("Post Deal: Actions for $it: ${game.availableActions(it)}")
        }

    game.performAction<Any?>(andy, "pass")
    game.performAction<Any?>(brad, "pass")
    game.performAction<Any?>(carl, "pass")

    game.performAction<Any?>(deryl, "pick")
    game.performAction<Any?>(deryl, "bury", listOf(4, 6))

    // Verify it is Andy's turn
    assertTrue(game.availableActions(andy).contains("playCard"), "Andy should be able to play a card.")

    // Verify other players cannot play a card
    players
        .filter { it != andy }
        .forEach {
          println("Actions for $it: ${game.availableActions(it)}")
          assertFalse(game.availableActions(it).contains("playCard"), "$it should not be able to play a card.")
        }


    // Check Andy's had before a card is play
    assertEquals(
        listOf(
            Card(CLUB, TEN),
            Card(DIAMOND, EIGHT),
            Card(DIAMOND, TEN),
            Card(HEART, NINE),
            Card(HEART, JACK),
            Card(DIAMOND, SEVEN)),
        andy.hand())

    game.performAction<Any?>(andy, "playCard", 2)

    // Verify the correct card was removed from Andy's hand
    assertEquals(
        listOf(
            Card(CLUB, TEN),
            Card(DIAMOND, EIGHT),
            Card(HEART, NINE),
            Card(HEART, JACK),
            Card(DIAMOND, SEVEN)),
        andy.hand())

    // Verify it is now Brad's turn

    assertTrue(game.availableActions(brad).contains("playCard"), "It should be Brad's turn now.")

    players
        .filter { it != brad }
        .forEach {
          println("Actions for $it: ${game.availableActions(it)}")
          assertFalse(game.availableActions(it).contains("playCard"), "$it should not be able to play a card.")
        }

  }

  @Test
  fun startSheepshead_deal_playCard_enforceSuit() {

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
      performAction<Any?>(earl, "deal")
      performAction<Any?>(andy, "pass")
      performAction<Any?>(brad, "pass")
      performAction<Any?>(carl, "pass")
      performAction<Any?>(deryl, "pick")
      performAction<Any?>(deryl, "bury", listOf(4, 6))
      performAction<Any?>(andy, "playCard", 2)

      try {
        performAction<Any?>(brad, "playCard", 2)
        fail<Any?>("Brad should not be able to play the King of hearts as trump was lead, and he has trump.")
      } catch (e: Exception) {
        assertEquals("Brad cannot play K♡ as Trump was lead, and Brad has Trump remaining.", e.message)
      }
    }

  }

  @Test
  fun startSheepshead_deal_playWholeTrick_verifyWinner_verifyNextLead() {

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
      performAction<Any?>(earl, "deal")
      performAction<Any?>(andy, "pass")
      performAction<Any?>(brad, "pass")
      performAction<Any?>(carl, "pass")
      performAction<Any?>(deryl, "pick")
      performAction<Any?>(deryl, "bury", listOf(4, 6))

      players.forEach { player -> println("$player's hand: ${player.hand().joinToString { "${it.toUnicodeString()}" }}") }

      assertNull(state<Map<String,String>>("lastTrickDetails"))

      performAction<Any?>(andy,  "playCard", 3)
      performAction<Any?>(brad,  "playCard", 4)
      performAction<Any?>(carl,  "playCard", 3)
      performAction<Any?>(deryl, "playCard", 2)
      performAction<Any?>(earl,  "playCard", 2)

      val lastTrick = state<List<Triple<Player, Card, Boolean>>>("lastTrickDetails") ?: throw RuntimeException("lastTrick should not be null")

      val trickWinner = lastTrick
          .first { it.third }
          .let { it.first }

      assertEquals(deryl, trickWinner, "Trick winner should be Deryl.")

      assertEquals(deryl, game.currentPlayer(), "Current player should be Deryl.")

      assertTrue(game.availableActions(deryl).contains("playCard"), "It should be Deryl's turn now since he won the last trick.")

      players
          .filter { it != deryl }
          .forEach {
            println("Actions for $it: ${game.availableActions(it)}")
            assertFalse(game.availableActions(it).contains("playCard"), "$it should not be able to play a card.")
          }

    }

  }
}