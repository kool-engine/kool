package de.fabmax.kool.demo

import de.fabmax.kool.createContext
import de.fabmax.kool.platform.Lwjgl3Context

/**
 * @author fabmax
 */
fun main() {
    Demo.setProperty("assetsBaseDir", "./docs/assets")

    val ctx = createContext {
        renderBackend = Lwjgl3Context.Backend.OPEN_GL
        title = "Kool Demo @ ${renderBackend.displayName}"
    }

    // launch demo
    demo("aoDemo", ctx)
}
