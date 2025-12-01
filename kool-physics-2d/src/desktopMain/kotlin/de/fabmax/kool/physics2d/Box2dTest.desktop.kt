package de.fabmax.kool.physics2d

import box2d.B2_Base
import de.fabmax.kool.util.logI

actual suspend fun loadBox2d() {
    val version = B2_Base.getVersion()
    logI("Box2D") { "Box2D loaded: ${version.major}.${version.minor}.${version.revision}" }
}