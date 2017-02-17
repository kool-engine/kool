package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.RayTest

/**
 * @author fabmax
 */
class Scene {

    var camera = Camera()
    var light = Light()
    var root: Node? = null

    var isPickingEnabled = true
    private val rayTest = RayTest()

    fun onRender(ctx: RenderContext) {
        camera.updateCamera(ctx)
        root?.render(ctx)

        if (isPickingEnabled && camera.initRayTes(rayTest, ctx)) {
            root?.rayTest(rayTest)
            if (rayTest.isHit) {
                rayTest.computeHitPosition()
                println("hit: ${rayTest.hitNode?.name}, d=${Math.sqrt(rayTest.hitDistanceSqr.toDouble())}, p=${rayTest.hitPosition}")
            }
        }
    }

}

