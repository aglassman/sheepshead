package com.github.aglassman.cardengine

import java.io.Serializable

data class GameEvent(
    val targetPlayer: Player? = null,
    val eventType: String,
    val eventMessage: String? = null,
    val properties: Map<String, String> = emptyMap()
): Serializable


interface EventEmitter {
  fun emit(gameEvent: GameEvent)
}

class StdOutEmitter: EventEmitter {
  override fun emit(gameEvent: GameEvent) {
    println(gameEvent)
  }
}

class NoOpEmitter: EventEmitter {
  override fun emit(gameEvent: GameEvent) {}

}