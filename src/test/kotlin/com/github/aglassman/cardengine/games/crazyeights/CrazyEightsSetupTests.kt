package com.github.aglassman.cardengine.games.crazyeights

import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.cardString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith


class CrazyEightsSetupTests {

  @Test
  fun testCreateGame_testInvalidNumberOfPlayers() {

    assertFailsWith(
        GameException::class,
        { CrazyEights(players = List(0,{ Player("$it")})) }
    )

    assertFailsWith(
        GameException::class,
        { CrazyEights(players = List(1,{ Player("$it")})) }
    )

    assertFailsWith(
        GameException::class,
        { CrazyEights(players = List(8,{ Player("$it")})) }
    )

  }

  @Test
  fun testCreateGame_testValidNumberOfPlayers() {
    CrazyEights(players = List(2,{ Player("$it")}))
    CrazyEights(players = List(7,{ Player("$it")}))
  }

  @Test
  fun testDeal_twoPlayers() {
    val players = List(2,{ Player("player:$it")})

    val game = CrazyEights(players = players)

    game.availableActions(players[0]).contains("deal")

    assertEquals(52, game.state("cardsRemaining"))

    game.performAction<Any?>(players[0], "deal")

    players
        .forEach { println("${it.name}(${cardString(game.state("hand", it))})") }

    players
        .forEach {
          assertEquals(5, game.state<List<*>>("hand", it).size)
        }

    assertEquals(41, game.state("cardsRemaining"))


  }

  @Test
  fun testDeal_sevenPlayers() {
    val players = List(7,{ Player("player:$it")})

    val game = CrazyEights(players = players)

    game.availableActions(players[0]).contains("deal")

    assertEquals(52, game.state("cardsRemaining"))

    game.performAction<Any?>(players[0], "deal")

    players.forEach { println("${it.name}(${cardString(game.state("hand", it))})") }

    players
        .forEach {
          assertEquals(5, game.state<List<*>>("hand", it).size)
        }

    assertEquals(16, game.state("cardsRemaining"))

  }

}