package com.github.aglassman.cardengine

import java.io.Serializable


class CaptureEmitter : EventEmitter, Serializable {

  val gameEvents = mutableListOf<GameEvent>()

  override fun emit(gameEvent: GameEvent) {
    gameEvents.add(gameEvent)
  }


}