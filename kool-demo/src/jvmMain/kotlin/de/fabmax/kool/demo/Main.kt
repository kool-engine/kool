package de.fabmax.kool.demo

import de.fabmax.kool.createContext
import de.fabmax.kool.platform.Lwjgl3Context

/**
 * @author fabmax
 */
fun main() {
    // optional local directory to load assets from (by default they are loaded from web)
    //Demo.setProperty("assetsBaseDir", "./assets")

    // sub directories for individual asset classes within asset base dir
    //Demo.setProperty("pbrDemo.envMaps", "hdri")
    //Demo.setProperty("pbrDemo.materials", "materials")
    //Demo.setProperty("pbrDemo.models", "models")

    val ctx = createContext {
        renderBackend = Lwjgl3Context.Backend.OPEN_GL
        title = "Kool Demo @ ${renderBackend.displayName}"
        setWindowed(1600, 900)
    }

    // launch demo
    demo(null, ctx)
}
