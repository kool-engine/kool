package de.fabmax.kool.platform

import de.fabmax.kool.input.Gamepad

external val navigator: Navigator

external class Navigator {
    fun getGamepads(): Array<Gamepad?>?
}
