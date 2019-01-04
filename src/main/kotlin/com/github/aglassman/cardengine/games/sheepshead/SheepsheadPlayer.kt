package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.StandardPlayer


internal fun StandardPlayer.hasSuitInHand(sheepsheadSuit: SheepsheadSuit): Boolean {
  return this.hand().firstOrNull { it.sheepsheadSuit() == sheepsheadSuit } != null
}