import com.github.aglassman.cardengine.Deck

fun generateTestSheepsheadDeckInKotlin(deck: Deck) {

  val cards = deck.deal(deck.cardsLeft())

  val cardString = cards.joinToString(separator = ",\n", transform = { "Card(Suit.${it.suit.name}, Face.${it.face.name})" })

  print("""
      val testDeck = Deck(
        listOf(
          $cardString
        )
      )
    """.trimIndent())
}