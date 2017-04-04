package de.fabmax.kool.js

import de.fabmax.kool.demo.pointDemo
import de.fabmax.kool.demo.simpleShapesDemo
import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.util.Color

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    val ctx = PlatformImpl.initContext()

    //simpleShapesDemo(ctx)
    //modelDemo(ctx)
    //uiDemo(ctx)
    pointDemo(ctx)
    //multiSceneDemo(ctx)
}
