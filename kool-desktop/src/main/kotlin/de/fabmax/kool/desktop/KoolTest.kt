package de.fabmax.kool.desktop

import de.fabmax.kool.demo.uiDemo
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.platform.lwjgl3.Lwjgl3Context

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    PlatformImpl.init()
    val ctx = Platform.createContext(Lwjgl3Context.InitProps())

    //simpleShapesDemo(ctx)
    //modelDemo(ctx)
    uiDemo(ctx)
    //pointDemo(ctx)

}
