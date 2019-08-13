package com.github.aglassman.cardengine.emitter

import com.github.aglassman.cardengine.EventEmitter
import com.github.aglassman.cardengine.GameEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable


class ComposedEmitter (
    val emitterList: List<EventEmitter> = listOf(LogEmitter())
): EventEmitter, Serializable {

  companion object {
    val LOGGER: Logger = LoggerFactory.getLogger(ComposedEmitter.javaClass)
  }

  override fun emit(gameEvent: GameEvent) {
    emitterList.forEach { emitter ->
      try {
        emitter.emit(gameEvent)
      } catch (e: Exception) {
        LOGGER.error("Failed to emit event to: ${emitter.javaClass.simpleName}", e)
      }
    }
  }

}