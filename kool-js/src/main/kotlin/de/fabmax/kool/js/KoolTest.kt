package de.fabmax.kool.js

import de.fabmax.kool.demo.multiSceneDemo
import de.fabmax.kool.demo.pointDemo
import de.fabmax.kool.demo.uiDemo
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.platform.js.JsContext
import de.fabmax.kool.util.MutableVec3f

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    val ctx = PlatformImpl.initContext()

    //simpleShapesDemo(ctx)
    //modelDemo(ctx)
    //uiDemo(ctx)
    //pointDemo(ctx)
    multiSceneDemo(ctx)
}
