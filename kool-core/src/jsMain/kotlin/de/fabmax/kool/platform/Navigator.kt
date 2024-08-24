package de.fabmax.kool.platform

import de.fabmax.kool.input.Gamepad
import de.fabmax.kool.pipeline.backend.webgpu.GPU

external val navigator: Navigator

external class Navigator {
    val gpu: GPU
    fun getGamepads(): Array<Gamepad?>?
}
