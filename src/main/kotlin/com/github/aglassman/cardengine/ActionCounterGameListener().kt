package com.github.aglassman.cardengine

import java.io.Serializable


class ActionCounterEmitter : EventEmitter, Serializable {

  val gameEvents = mutableListOf<GameEvent>()

  override fun emit(gameEvent: GameEvent) {
    gameEvents.add(gameEvent)
  }


}