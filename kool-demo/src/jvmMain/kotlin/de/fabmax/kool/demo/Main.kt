package de.fabmax.kool.demo

import de.fabmax.kool.createContext
import de.fabmax.kool.platform.Lwjgl3Context

/**
 * @author fabmax
 */
fun main() {
    // uncomment to load assets locally instead of from web
    //Demo.setProperty("assets.base", ".")

    // sub-directories for individual asset classes within asset base dir
    //Demo.setProperty("assets.hdri", "hdri")
    //Demo.setProperty("assets.materials", "materials")
    //Demo.setProperty("assets.models", "models")

    val ctx = createContext {
        renderBackend = Lwjgl3Context.Backend.OPEN_GL
        title = "Kool Demo @ ${renderBackend.displayName}"
        setWindowed(1600, 900)

        // Local asset path: Only used in case assets are not loaded from web (set "assets.base" demo property
        // from above to ".")
        localAssetPath = "./assets"
    }

    // launch demo
    demo(null, ctx)
}
