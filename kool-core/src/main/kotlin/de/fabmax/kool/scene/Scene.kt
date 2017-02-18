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
    private var hoverNode: Node? = null

    fun onRender(ctx: RenderContext) {
        camera.updateCamera(ctx)

        handleInput(ctx)

        root?.render(ctx)
    }

    private fun handleInput(ctx: RenderContext) {
        var hovered: Node? = null
        val prevHovered = hoverNode

        if (isPickingEnabled && camera.initRayTes(rayTest, ctx)) {
            root?.rayTest(rayTest)
            if (rayTest.isHit) {
                rayTest.computeHitPosition()
                hovered = rayTest.hitNode
            }
        }

        if (prevHovered != hovered) {
            if (prevHovered != null) {
                prevHovered.onHoverExit?.invoke(prevHovered, ctx)
            }
            if (hovered != null) {
                hovered.onHoverEnter?.invoke(hovered, ctx)
            }
            hoverNode = hovered
        }
    }

}

