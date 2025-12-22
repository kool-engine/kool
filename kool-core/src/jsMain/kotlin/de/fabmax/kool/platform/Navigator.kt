package de.fabmax.kool.platform

import de.fabmax.kool.Gamepad
import de.fabmax.kool.pipeline.backend.webgpu.GPU
import org.w3c.dom.events.Event

external val navigator: Navigator

external class Navigator {
    val language: String
    val gpu: GPU
    fun getGamepads(): JsArray<Gamepad?>?
}

external class GamepadEvent : Event, JsAny {
    val gamepad: Gamepad
}
