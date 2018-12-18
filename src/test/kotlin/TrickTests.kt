import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Face
import com.github.aglassman.cardengine.Player
import com.github.aglassman.cardengine.Suit
import com.github.aglassman.cardengine.games.Trick
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrickTests {

  @Test
  fun testGetTrickWinner_higherTrumpPlayed() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val trick = Trick(5)

    trick.playCard(andy, Card(Suit.DIAMOND, Face.TEN))
    trick.playCard(brad, Card(Suit.HEART, Face.NINE))
    trick.playCard(carl, Card(Suit.CLUB, Face.JACK))
    trick.playCard(deryl, Card(Suit.SPADE, Face.SEVEN))
    trick.playCard(earl, Card(Suit.HEART, Face.ACE))

    assertEquals(carl, trick.trickWinner(),"Carl should be the trick winner.")

  }

  @Test
  fun testGetTrickWinner_failSuitTrumped() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val trick = Trick(5)

    trick.playCard(andy, Card(Suit.SPADE, Face.TEN))
    trick.playCard(brad, Card(Suit.SPADE, Face.ACE))
    trick.playCard(carl, Card(Suit.CLUB, Face.SEVEN))
    trick.playCard(deryl, Card(Suit.SPADE, Face.SEVEN))
    trick.playCard(earl, Card(Suit.DIAMOND, Face.ACE))

    assertEquals(earl, trick.trickWinner(),"Earl should be the trick winner.")

  }

  @Test
  fun testGetTrickWinner_noTrumpPlayed() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val trick = Trick(5)

    trick.playCard(andy, Card(Suit.SPADE, Face.TEN))
    trick.playCard(brad, Card(Suit.SPADE, Face.ACE))
    trick.playCard(carl, Card(Suit.CLUB, Face.SEVEN))
    trick.playCard(deryl, Card(Suit.SPADE, Face.SEVEN))
    trick.playCard(earl, Card(Suit.HEART, Face.ACE))

    assertEquals(brad, trick.trickWinner(),"Brad should be the trick winner.")

  }

  @Test
  fun testGetTrickWinner_leadWins() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val trick = Trick(5)

    trick.playCard(andy, Card(Suit.DIAMOND, Face.SEVEN))
    trick.playCard(brad, Card(Suit.SPADE, Face.ACE))
    trick.playCard(carl, Card(Suit.CLUB, Face.EIGHT))
    trick.playCard(deryl, Card(Suit.SPADE, Face.EIGHT))
    trick.playCard(earl, Card(Suit.HEART, Face.ACE))

    assertEquals(andy, trick.trickWinner(),"Andy should be the trick winner.")

  }

}