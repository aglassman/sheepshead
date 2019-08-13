package com.github.aglassman.cardengine.games.sheepshead
import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Deck
import com.github.aglassman.cardengine.Face.*
import com.github.aglassman.cardengine.Game
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.Suit.*
import com.github.aglassman.cardengine.emitter.CaptureEmitter
import com.github.aglassman.cardengine.emitter.ComposedEmitter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class SheepsheadGameTest {


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
  fun startSheepshead_deal_playWholeGame_verifyWinner_verifyPoints_verifyScore() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val captureEmitter = CaptureEmitter()

    // Game 2, so Brad should be dealer
    val game = Sheepshead(
        players = players,
        deck = testSheepsDeck,
        gameNumber = 5,
        emitter = ComposedEmitter(listOf(captureEmitter))
    )

    with(game) {
      performAction<Any?>(earl, "deal")
      performAction<Any?>(andy, "pass")
      performAction<Any?>(brad, "pass")
      performAction<Any?>(carl, "pass")
      performAction<Any?>(deryl, "pick")
      performAction<Any?>(deryl, "bury", listOf(4, 6))

      println()
      players.forEach { player -> println("$player's hand: ${game.state<List<Card>>("hand", player).joinToString { "${it.toUnicodeString()}" }}") }
      println()

      performAction<Any?>(earl,  "beginPlay")

      performAction<Any?>(andy,  "playCard", 3)
      performAction<Any?>(brad,  "playCard", 4)
      performAction<Any?>(carl,  "playCard", 3)
      performAction<Any?>(deryl, "playCard", 2)
      performAction<Any?>(earl,  "playCard", 2)

      println("\nTrick Winner: ${state<Player>("lastTrickWinner")}")
      assertEquals(deryl, state<Player>("lastTrickWinner"), "Trick winner should be Deryl.")
      assertEquals(deryl, game.currentPlayer(), "Current player should be Deryl.")



      println()
      players.forEach { player -> println("$player's hand: ${game.state<List<Card>>("hand", player).joinToString { "${it.toUnicodeString()}" }}") }
      println()

      performAction<Any?>(deryl, "playCard", 1)
      performAction<Any?>(earl,  "playCard", 4)
      performAction<Any?>(andy,  "playCard", 4)
      performAction<Any?>(brad,  "playCard", 0)
      performAction<Any?>(carl,  "playCard", 4)

      println("\nTrick Winner: ${state<Player>("lastTrickWinner")}")
      assertEquals(deryl, state<Player>("lastTrickWinner"), "Trick winner should be Deryl.")
      assertEquals(deryl, game.currentPlayer(), "Current player should be Deryl.")

      println()
      players.forEach { player -> println("$player's hand: ${game.state<List<Card>>("hand", player).joinToString { "${it.toUnicodeString()}" }}") }
      println()

      performAction<Any?>(deryl, "playCard", 3)
      performAction<Any?>(earl,  "playCard", 2)
      performAction<Any?>(andy,  "playCard", 2)
      performAction<Any?>(brad,  "playCard", 0)
      performAction<Any?>(carl,  "playCard", 0)

      println("\nTrick Winner: ${state<Player>("lastTrickWinner")}")
      assertEquals(earl, state<Player>("lastTrickWinner"), "Trick winner should be Earl.")
      assertEquals(earl, game.currentPlayer(), "Current player should be Earl.")

      println()
      players.forEach { player -> println("$player's hand: ${game.state<List<Card>>("hand", player).joinToString { "${it.toUnicodeString()}" }}") }
      println()

      performAction<Any?>(earl,  "playCard", 2)
      performAction<Any?>(andy,  "playCard", 0)
      performAction<Any?>(brad,  "playCard", 0)
      performAction<Any?>(carl,  "playCard", 0)
      performAction<Any?>(deryl, "playCard", 0)

      println()
      players.forEach { player -> println("$player's hand: ${game.state<List<Card>>("hand", player).joinToString { "${it.toUnicodeString()}" }}") }
      println()

      println("\nTrick Winner: ${state<Player>("lastTrickWinner")}")
      assertEquals(andy, state<Player>("lastTrickWinner"), "Trick winner should be Deryl.")
      assertEquals(andy, game.currentPlayer(), "Current player should be Andy.")

      performAction<Any?>(andy,  "playCard", 0)
      performAction<Any?>(brad,  "playCard", 1)
      performAction<Any?>(carl,  "playCard", 1)
      performAction<Any?>(deryl, "playCard", 0)
      performAction<Any?>(earl,  "playCard", 1)

      println()
      players.forEach { player -> println("$player's hand: ${game.state<List<Card>>("hand", player).joinToString { "${it.toUnicodeString()}" }}") }
      println()

      println("\nTrick Winner: ${state<Player>("lastTrickWinner")}")
      assertEquals(deryl, state<Player>("lastTrickWinner"), "Trick winner should be Deryl.")
      assertEquals(deryl, game.currentPlayer(), "Current player should be Deryl.")

      performAction<Any?>(deryl, "playCard", 0)
      performAction<Any?>(earl,  "playCard", 0)
      performAction<Any?>(andy,  "playCard", 0)
      performAction<Any?>(brad,  "playCard", 0)
      performAction<Any?>(carl,  "playCard", 0)

      println()
      println("Trick Winner: ${state<Player>("lastTrickWinner")}")
      println()

      assertTrue(game.isComplete())

      val teams = game.state<List<Team>>("teams")
      println(teams)

      with(teams) {
        assertEquals(size, 2)

        first { it.name == "pickers" }.also {
          assertTrue(it.members.contains(deryl), "Picking team should contain Deryl.")
        }

        first { it.name == "setters" }.also {
          assertTrue(it.members.contains(andy), "Picking team should contain Andy.")
          assertTrue(it.members.contains(brad), "Picking team should contain Brad.")
          assertTrue(it.members.contains(carl), "Picking team should contain Carl.")
          assertTrue(it.members.contains(earl), "Picking team should contain Earl.")
        }

      }

      val outcome = game.state<GameOutcome>("gameOutcome")

      val pickers = outcome.byTeamName("pickers")
      val setters = outcome.byTeamName("setters")

      val pickerPoints = pickers.points
      val setterPoints = setters.points

      assertEquals(120, pickerPoints + setterPoints)
      assertEquals(66, pickerPoints)
      assertEquals(54, setterPoints)

      with(game) {
        val gameWinner: Team = state("gameWinner")
            ?: throw RuntimeException("gameWinner should not be null.")

        assertEquals(Team("pickers", listOf(deryl)), gameWinner)

        val score = state<PlayerScores>("score")

        assertEquals(5, score.size, "There should be a score for each player.")

        val andysPoints = score.first { it.player == andy }.points
        val bradsPoints = score.first { it.player == brad }.points
        val carlsPoints = score.first { it.player == carl }.points
        val derylsPoints = score.first { it.player == deryl }.points
        val earlsPoints = score.first { it.player == earl }.points

        println(score)

        assertEquals(4, derylsPoints)
        assertEquals(-1, andysPoints)
        assertEquals(-1, bradsPoints)
        assertEquals(-1, carlsPoints)
        assertEquals(-1, earlsPoints)
      }


      // Check serialization
      val baos = ByteArrayOutputStream()
      val gzo = GZIPOutputStream(baos)
      ObjectOutputStream(gzo).writeObject(game)
      gzo.close()

      println("${baos.size()} bytes")

      with(ObjectInputStream(GZIPInputStream(ByteArrayInputStream(baos.toByteArray()))).readObject() as Game) {
        val gameWinner: Team = state("gameWinner")
            ?: throw RuntimeException("gameWinner should not be null.")

        assertEquals(Team("pickers", listOf(deryl)), gameWinner)

        val score = state<PlayerScores>("score")

        assertEquals(5, score.size, "There should be a score for each player.")

        val andysPoints = score.first { it.player == andy }.points
        val bradsPoints = score.first { it.player == brad }.points
        val carlsPoints = score.first { it.player == carl }.points
        val derylsPoints = score.first { it.player == deryl }.points
        val earlsPoints = score.first { it.player == earl }.points

        println(score)

        assertEquals(4, derylsPoints)
        assertEquals(-1, andysPoints)
        assertEquals(-1, bradsPoints)
        assertEquals(-1, carlsPoints)
        assertEquals(-1, earlsPoints)
      }

    }

    // check listener and actions
    with(captureEmitter) {
      assertEquals(37, gameEvents.size)
    }

  }

}