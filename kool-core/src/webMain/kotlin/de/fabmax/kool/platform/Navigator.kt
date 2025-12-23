package de.fabmax.kool.platform

import de.fabmax.kool.Gamepad
import de.fabmax.kool.pipeline.backend.webgpu.GPU
import kotlin.js.JsArray

external val navigator: Navigator

external class Navigator {
    val language: String
    val gpu: GPU
    fun getGamepads(): JsArray<Gamepad?>?
}
