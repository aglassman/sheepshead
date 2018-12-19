package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Player


internal fun Player.hasSuitInHand(sheepsheadSuit: SheepsheadSuit): Boolean {
  return this.hand().firstOrNull { it.sheepsheadSuit() == sheepsheadSuit } != null
}