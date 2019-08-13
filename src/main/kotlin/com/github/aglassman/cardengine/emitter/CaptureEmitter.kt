package com.github.aglassman.cardengine.emitter

import com.github.aglassman.cardengine.EventEmitter
import com.github.aglassman.cardengine.GameEvent
import java.io.Serializable

/**
 * A basic emitter that captures all game events in a list.
 */
class CaptureEmitter : EventEmitter, Serializable {

  var gameEvents = emptyList<GameEvent>()

  override fun emit(gameEvent: GameEvent) {
    gameEvents = gameEvents.plus(gameEvent)
  }


}