package de.fabmax.kool.js

import de.fabmax.kool.demo.textDemo
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.platform.js.JsContext

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    PlatformImpl.init()
    val ctx = Platform.createContext(JsContext.InitProps())

    //simpleShapesDemo(ctx)
    //textureDemo(ctx)
    textDemo(ctx)
}