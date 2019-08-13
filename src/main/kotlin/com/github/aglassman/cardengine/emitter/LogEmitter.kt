package com.github.aglassman.cardengine.emitter

import com.github.aglassman.cardengine.EventEmitter
import com.github.aglassman.cardengine.GameEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable


class LogEmitter: EventEmitter, Serializable {

  companion object {
    val LOGGER: Logger = LoggerFactory.getLogger(LogEmitter::class.java)
  }

  override fun emit(gameEvent: GameEvent) {
    LOGGER.info(gameEvent.toString())
  }

}