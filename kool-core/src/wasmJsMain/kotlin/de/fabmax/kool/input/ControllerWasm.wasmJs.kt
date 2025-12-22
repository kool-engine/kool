package de.fabmax.kool.input

import de.fabmax.kool.Gamepad
import de.fabmax.kool.platform.navigator

actual fun getGamepadState(index: Int): Gamepad? = navigator.getGamepads()?.get(index)