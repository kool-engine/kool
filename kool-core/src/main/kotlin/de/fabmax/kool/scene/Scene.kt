package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.RayTest

/**
 * @author fabmax
 */
class Scene {

    var camera: Camera = PerspectiveCamera()
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
        val ptr = ctx.inputMgr.primaryPointer

        if (isPickingEnabled && camera.initRayTes(rayTest, ptr, ctx)) {
            root?.rayTest(rayTest)
            if (rayTest.isHit) {
                rayTest.computeHitPosition()
                hovered = rayTest.hitNode
            }
        }

        if (prevHovered != hovered) {
            if (prevHovered != null) {
                for (i in prevHovered.onHoverExit.indices) {
                    prevHovered.onHoverExit[i](prevHovered, ptr, rayTest, ctx)
                }
            }
            if (hovered != null) {
                for (i in hovered.onHoverEnter.indices) {
                    hovered.onHoverEnter[i](hovered, ptr, rayTest, ctx)
                }
            }
            hoverNode = hovered
        }
        if (hovered != null && prevHovered == hovered) {
            for (i in hovered.onHover.indices) {
                hovered.onHover[i](hovered, ptr, rayTest, ctx)
            }
        }

        ctx.inputMgr.handleDrag()
    }

}

