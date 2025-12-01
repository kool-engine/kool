@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package de.fabmax.kool.physics2d

import box2d.Box2dWasmLoader
import box2d.prototypes.B2_Base
import de.fabmax.kool.util.logI

actual suspend fun loadBox2d() {
    Box2dWasmLoader.loadModule()

    val version = B2_Base.getVersion()
    logI("Box2D") { "Box2D loaded: ${version.major}.${version.minor}.${version.revision}" }
}