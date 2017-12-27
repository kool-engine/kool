package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.RenderContext
import de.fabmax.kool.gl.GL_COLOR_BUFFER_BIT
import de.fabmax.kool.gl.GL_DEPTH_BUFFER_BIT
import de.fabmax.kool.gl.glClear
import de.fabmax.kool.util.RayTest

/**
 * @author fabmax
 */

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

open class Scene(name: String? = null) : Group(name) {

    val preRender: MutableList<Node.(RenderContext) -> Unit> = mutableListOf()
    val postRender: MutableList<Node.(RenderContext) -> Unit> = mutableListOf()

    var camera: Camera = PerspectiveCamera()
    var light = Light()

    var clearMask = GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT
    var isPickingEnabled = true
    private val rayTest = RayTest()
    private var hoverNode: Node? = null

    private val dragPtrs: MutableList<InputManager.Pointer> = mutableListOf()
    private val dragHandlers: MutableList<InputManager.DragHandler> = mutableListOf()

    init {
        scene = this
    }

    override fun render(ctx: RenderContext) {
        if (!isVisible) {
            return
        }

        for (i in preRender.indices) {
            preRender[i](ctx)
        }

        camera.updateCamera(ctx)

        handleInput(ctx)

        if (clearMask != 0) {
            glClear(clearMask)
        }
        super.render(ctx)

        for (i in postRender.indices) {
            postRender[i](ctx)
        }
    }

    fun registerDragHandler(handler: InputManager.DragHandler) {
        if (handler !in dragHandlers) {
            dragHandlers += handler
        }
    }

    fun removeDragHandler(handler: InputManager.DragHandler) {
        dragHandlers -= handler
    }

    private fun handleInput(ctx: RenderContext) {
        var hovered: Node? = null
        val prevHovered = hoverNode
        val ptr = ctx.inputMgr.primaryPointer

        if (isPickingEnabled && ptr.isInViewport(ctx) && camera.initRayTes(rayTest, ptr, ctx)) {
            rayTest(rayTest)
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

        if (isPickingEnabled) {
            handleDrag(ctx)
        }
    }

    private fun handleDrag(ctx: RenderContext) {
        dragPtrs.clear()
        for (i in ctx.inputMgr.pointers.indices) {
            val ptr = ctx.inputMgr.pointers[i]
            if (ptr.isInViewport(ctx) &&
                    (ptr.buttonMask != 0 || ptr.buttonEventMask != 0 || ptr.deltaScroll != 0f)) {
                dragPtrs.add(ctx.inputMgr.pointers[i])
            }
        }

        var handlerIdx = dragHandlers.lastIndex
        while (handlerIdx >= 0) {
            val result = dragHandlers[handlerIdx].handleDrag(dragPtrs, ctx)
            if (result and InputManager.DragHandler.REMOVE_HANDLER != 0) {
                dragHandlers.removeAt(handlerIdx)
            }
            if (result and InputManager.DragHandler.HANDLED != 0) {
                break
            }
            handlerIdx--
        }
    }

}

