package com.github.aglassman.cardengine.games.sheepshead

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SheepsheadDeckTests {

  @Test
  fun testSheepsheadDeck_creation() {
    with(SheepsheadDeck()) {
      assertEquals(32, cardsLeft())
    }
  }

}