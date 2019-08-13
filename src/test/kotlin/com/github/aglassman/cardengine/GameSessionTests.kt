package com.github.aglassman.cardengine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GameSessionTests {

  @Test
  fun createGameSession_sheepsheadGame_verifyType() {
    val players = listOf(
        Player("Andy"),
        Player("Brad"),
        Player("Carl"),
        Player("Deryl"),
        Player("Earl"))

    val gameSession = GameSession(
        gameType = "sheepshead",
        players = players
    )

    gameSession.startNewGame()

    val currentGame = gameSession.getCurrentGame()

    assertEquals("sheepshead", currentGame?.gameType())

  }

  @Test
  fun createGameSession_unknownGame_expectException() {
    val players = listOf(
        Player("Andy"),
        Player("Brad"),
        Player("Carl"),
        Player("Deryl"),
        Player("Earl"))

    try {
      val gameSession = GameSession(
          gameType = "obscure-cardgame",
          players = players
      )
      fail<Any?>("should have thrown an exception")
    } catch (e: GameException) {
      assertEquals("Unsupported gameType: (obscure-cardgame).", e.message)
    }
  }

  @Test
  fun gameStart_startAnotherGame_expectExceptionMessage() {

    val players = listOf(
        Player("Andy"),
        Player("Brad"),
        Player("Carl"),
        Player("Deryl"),
        Player("Earl"))

    val gameSession = GameSession(
        gameType = "sheepshead",
        players = players
    )

    assertNull(gameSession.getCurrentGame())

    gameSession.startNewGame()

    assertNotNull(gameSession.getCurrentGame())

    try {
      gameSession.startNewGame()
      Assertions.fail<Any?>("Should have thrown an exception here.")
    } catch (e: GameException) {
      assertEquals("Could not start new game while a current game is still active.", e.message)
    }

  }

  @Test
  fun startSheepshead_verifyDeal() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val gameSession = GameSession(
        gameType = "sheepshead",
        players = listOf(andy, brad, carl, deryl, earl)
    )

    assertNull(gameSession.getCurrentGame())

    gameSession.startNewGame()

    val game = gameSession.getCurrentGame() ?: throw RuntimeException("This should not be null.")

    try {
      game.state<Any?>("random-thing")
      fail<Any?>("Should have thrown an exception")
    } catch (e: GameException) {
      assertEquals("state (random-thing) is unavailable", e.message)
    }

    assertEquals(0, game.state<List<Card>>("blind").size)

    assertEquals(0, game.state<List<Card>>("hand", andy).size)
    assertEquals(0, game.state<List<Card>>("hand", brad).size)
    assertEquals(0, game.state<List<Card>>("hand", carl).size)
    assertEquals(0, game.state<List<Card>>("hand", deryl).size)
    assertEquals(0, game.state<List<Card>>("hand", earl).size)

    game.performAction<Any?>(andy, "deal")

    assertEquals(6, game.state<List<Card>>("hand", andy).size)
    assertEquals(6, game.state<List<Card>>("hand", brad).size)
    assertEquals(6, game.state<List<Card>>("hand", carl).size)
    assertEquals(6, game.state<List<Card>>("hand", deryl).size)
    assertEquals(6, game.state<List<Card>>("hand", earl).size)

    val blind: List<Card> = game.state("blind")

    assertEquals(2, blind.size)

  }


}