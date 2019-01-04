package com.github.aglassman.cardengine.games.sheepshead
import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Face
import com.github.aglassman.cardengine.StandardPlayer
import com.github.aglassman.cardengine.Suit
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BlindTests {

  @Test
  fun setupBlind() {

    val brad = StandardPlayer("Brad") // First Option
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")
    val andy = StandardPlayer("Andy") // Dealer

    val players = listOf(brad, carl, deryl, earl, andy)

    val blind = Blind(players)

    assertFalse(blind.isAvailable())
    assertNull(blind.option())

    blind.setBlind(
        listOf(
            Card(Suit.HEART, Face.ACE),
            Card(Suit.CLUB, Face.QUEEN)
        ))

    assertTrue(blind.isAvailable())
  }

  @Test
  public fun setupBlind_testSettingTwice() {

    val brad = StandardPlayer("Brad") // First Option
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")
    val andy = StandardPlayer("Andy") // Dealer

    val players = listOf(brad, carl, deryl, earl, andy)

    val blind = Blind(players)

    assertFalse(blind.isAvailable())
    assertNull(blind.option())

    blind.setBlind(
        listOf(
            Card(Suit.HEART, Face.ACE),
            Card(Suit.CLUB, Face.QUEEN)
        ))

    assertTrue(blind.isAvailable())

    try {
      blind.setBlind(
          listOf(
              Card(Suit.SPADE, Face.TWO),
              Card(Suit.DIAMOND, Face.THREE)
          ))
      fail<Any?>("Should not be able to set blind twice.")
    } catch (e: Exception) {
      assertEquals("Blind has already been set.", e.message)
    }

  }

  @Test
  fun setupBlind_testFirstOption() {

    val brad = StandardPlayer("Brad") // First Option
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")
    val andy = StandardPlayer("Andy") // Dealer

    val players = listOf(brad, carl, deryl, earl, andy)

    val blind = Blind(players)

    blind.setBlind(
        listOf(
            Card(Suit.HEART, Face.ACE),
            Card(Suit.CLUB, Face.QUEEN)
        ))

    with(blind) {
      assertEquals(brad, option())
      assertFalse(hasLastOption(brad))

      assertNotEquals(carl, option())
      assertFalse(hasLastOption(carl))

      assertNotEquals(deryl, option())
      assertFalse(hasLastOption(deryl))

      assertNotEquals(earl, option())
      assertFalse(hasLastOption(earl))

      assertNotEquals(andy, option())
      assertTrue(hasLastOption(andy))
    }
  }

  @Test
  fun setupBlind_testPassing() {

    val brad = StandardPlayer("Brad") // First Option
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")
    val andy = StandardPlayer("Andy") // Dealer

    val players = listOf(brad, carl, deryl, earl, andy)

    val blind = Blind(players)

    blind.setBlind(
        listOf(
            Card(Suit.HEART, Face.ACE),
            Card(Suit.CLUB, Face.QUEEN)
        ))

    assertEquals(0, andy.hand().size)
    assertEquals(0, brad.hand().size)
    assertEquals(0, carl.hand().size)
    assertEquals(0, deryl.hand().size)
    assertEquals(0, earl.hand().size)

    with(blind) {
      assertEquals(brad, option())
      pass(brad)

      assertEquals(carl, option())
      pass(carl)

      assertEquals(deryl, option())
      assertEquals(
          listOf(
              Card(Suit.HEART, Face.ACE),
              Card(Suit.CLUB, Face.QUEEN)),
          peek(deryl))

      pick(deryl)
    }

    assertEquals(0, andy.hand().size)
    assertEquals(0, brad.hand().size)
    assertEquals(0, carl.hand().size)
    assertEquals(2, deryl.hand().size)
    assertEquals(0, earl.hand().size)

  }

  @Test
  fun setupBlind_testPickingOutOfOrder_expectException() {

    val brad = StandardPlayer("Brad") // First Option
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")
    val andy = StandardPlayer("Andy") // Dealer

    val players = listOf(brad, carl, deryl, earl, andy)

    val blind = Blind(players)

    blind.setBlind(
        listOf(
            Card(Suit.HEART, Face.ACE),
            Card(Suit.CLUB, Face.QUEEN)
        ))

    with(blind) {
      assertEquals(brad, option())
      pass(brad)

      assertEquals(carl, option())
      pass(carl)

      assertEquals(deryl, option())

      try {
        pick(earl)
        fail<Any?>("Earl should not be able to pick as deryl currently has the option.")
      } catch (e: Exception) {
        assertEquals("Earl cannot pick as Deryl currently has the option.", e.message)
      }

      try {
        pick(earl)
        fail<Any?>("Earl should not be able to peek as deryl currently has the option.")
      } catch (e: Exception) {
        assertEquals("Earl cannot pick as Deryl currently has the option.", e.message)
      }

      try {
        pick(earl)
        fail<Any?>("Earl should not be able to pass as deryl currently has the option.")
      } catch (e: Exception) {
        assertEquals("Earl cannot pick as Deryl currently has the option.", e.message)
      }
    }

  }

  @Test
  fun setupBlind_testBlindStateAfterPick() {

    val brad = StandardPlayer("Brad") // First Option
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")
    val andy = StandardPlayer("Andy") // Dealer

    val players = listOf(brad, carl, deryl, earl, andy)

    val blind = Blind(players)

    blind.setBlind(
        listOf(
            Card(Suit.HEART, Face.ACE),
            Card(Suit.CLUB, Face.QUEEN)
        ))

    with(blind) {
      assertEquals(brad, option())
      pass(brad)

      assertEquals(carl, option())
      pass(carl)

      assertEquals(deryl, option())

      assertNull(picker())
      pick(deryl)
      assertNotNull(picker())
      assertEquals(deryl, picker())

      assertNull(option())

      try {
        pick(deryl)
        fail<Any?>("Should not be able to pick as blind has been picked already.")
      } catch (e: Exception) {
        assertEquals("Cannot pick as blind has already been picked.", e.message)
      }

      try {
        pick(deryl)
        fail<Any?>("Should not be able to pass as blind has been picked already.")
      } catch (e: Exception) {
        assertEquals("Cannot pick as blind has already been picked.", e.message)
      }

      try {
        pick(deryl)
        fail<Any?>("Should not be able to peek as blind has been picked already.")
      } catch (e: Exception) {
        assertEquals("Cannot pick as blind has already been picked.", e.message)
      }
    }

  }

  @Test
  fun setupBlind_testGoToLastOption() {

    val brad = StandardPlayer("Brad") // First Option
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")
    val andy = StandardPlayer("Andy") // Dealer

    val players = listOf(brad, carl, deryl, earl, andy)

    val blind = Blind(players)

    blind.setBlind(
        listOf(
            Card(Suit.HEART, Face.ACE),
            Card(Suit.CLUB, Face.QUEEN)
        ))

    with(blind) {
      assertFalse(blindRoundComplete())
      pass(brad)

      assertFalse(blindRoundComplete())
      pass(carl)

      assertFalse(blindRoundComplete())
      pass(deryl)

      assertFalse(blindRoundComplete())
      pass(earl)

      assertFalse(blindRoundComplete())
      pass(andy)

      assertTrue(isAvailable())
      assertTrue(blindRoundComplete())

      assertNull(option())
      assertNull(picker())
    }

  }

}