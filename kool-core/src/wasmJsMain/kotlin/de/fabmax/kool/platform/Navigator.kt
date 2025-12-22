package de.fabmax.kool.platform

import de.fabmax.kool.Gamepad
import org.w3c.dom.events.Event

external val navigator: Navigator

external class Navigator {
    val language: String
    fun getGamepads(): JsArray<Gamepad?>?
}

external class GamepadEvent : Event, JsAny {
    val gamepad: Gamepad
}
