import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Face
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.Suit
import com.github.aglassman.cardengine.games.Blind
import com.github.aglassman.cardengine.games.Teams
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BlindTests {

  @Test
  fun setupBlind() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val blind = Blind(players, Teams())

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

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val blind = Blind(players, Teams())

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

    val andy = Player("Andy") // Dealer
    val brad = Player("Brad") // First Option
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val blind = Blind(players, Teams())

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

    val andy = Player("Andy") // Dealer
    val brad = Player("Brad") // First Option
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val blind = Blind(players, Teams())

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

    val andy = Player("Andy") // Dealer
    val brad = Player("Brad") // First Option
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val blind = Blind(players, Teams())

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

    val andy = Player("Andy") // Dealer
    val brad = Player("Brad") // First Option
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val blind = Blind(players, Teams())

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

    val andy = Player("Andy") // Dealer
    val brad = Player("Brad") // First Option
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val blind = Blind(players, Teams())

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