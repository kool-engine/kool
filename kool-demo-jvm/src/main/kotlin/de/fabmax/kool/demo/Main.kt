package de.fabmax.kool.demo

import de.fabmax.kool.createContext

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    // create KoolContext
    val ctx = createContext()

    // this assumes that the working directory is root project dir. demo assets are located in ./docs/assets
    ctx.assetMgr.assetsBaseDir = "./docs/assets"

    // launch demo
    Demo(ctx, "boxDemo")
}
