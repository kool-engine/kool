package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.gl.GL_COLOR_BUFFER_BIT
import de.fabmax.kool.gl.GL_DEPTH_BUFFER_BIT
import de.fabmax.kool.gl.glClear
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.util.Disposable
import de.fabmax.kool.util.ShadowMap

/**
 * @author fabmax
 */

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

open class Scene(name: String? = null) : Group(name) {

    override var isFrustumChecked: Boolean
        get() = false
        set(value) {}

    var camera: Camera = PerspectiveCamera()
    var light = Light()
    var defaultShadowMap: ShadowMap? = null
        set(value) {
            if (field != null) {
                synchronized(disposables) {
                    disposables += field!!
                }
            }
            field = value
        }

    var clearMask = GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT
    var isPickingEnabled = true
    private val rayTest = RayTest()
    private var hoverNode: Node? = null

    private val dragPtrs: MutableList<InputManager.Pointer> = mutableListOf()
    private val dragHandlers: MutableList<InputManager.DragHandler> = mutableListOf()

    private val disposables = mutableListOf<Disposable>()

    init {
        scene = this

        onPreRender += { ctx ->
            defaultShadowMap?.renderShadowMap(this, ctx)
        }
        onDispose += { ctx ->
            defaultShadowMap?.dispose(ctx)
        }
    }

    override fun preRender(ctx: KoolContext) {
        synchronized(disposables) {
            for (i in disposables.indices) {
                disposables[i].dispose(ctx)
            }
            disposables.clear()
        }
        super.preRender(ctx)
    }

    fun renderScene(ctx: KoolContext) {
        if (!isVisible) {
            return
        }

        preRender(ctx)

        camera.updateCamera(ctx)
        handleInput(ctx)

        if (clearMask != 0) {
            glClear(clearMask)
        }
        render(ctx)

        postRender(ctx)
    }

    fun dispose(disposable: Disposable) {
        synchronized(disposables) {
            disposables += disposable
        }
    }

    override fun dispose(ctx: KoolContext) {
        synchronized(disposables) {
            disposables.forEach { it.dispose(ctx) }
            disposables.clear()
        }
        super.dispose(ctx)
    }

    fun registerDragHandler(handler: InputManager.DragHandler) {
        if (handler !in dragHandlers) {
            dragHandlers += handler
        }
    }

    fun removeDragHandler(handler: InputManager.DragHandler) {
        dragHandlers -= handler
    }

    private fun handleInput(ctx: KoolContext) {
        var hovered: Node? = null
        val prevHovered = hoverNode
        val ptr = ctx.inputMgr.primaryPointer

        if (isPickingEnabled && ptr.isInViewport(ctx) && camera.initRayTes(rayTest, ptr, ctx)) {
            rayTest(rayTest)
            if (rayTest.isHit) {
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

    private fun handleDrag(ctx: KoolContext) {
        dragPtrs.clear()
        for (i in ctx.inputMgr.pointers.indices) {
            val ptr = ctx.inputMgr.pointers[i]
            if (ptr.isInViewport(ctx) &&
                    (ptr.buttonMask != 0 || ptr.buttonEventMask != 0 || ptr.deltaScroll != 0f)) {
                dragPtrs.add(ctx.inputMgr.pointers[i])
            }
        }

        for (i in dragHandlers.lastIndex downTo 0) {
            val result = dragHandlers[i].handleDrag(dragPtrs, ctx)
            if (result and InputManager.DragHandler.REMOVE_HANDLER != 0) {
                dragHandlers.removeAt(i)
            }
            if (result and InputManager.DragHandler.HANDLED != 0) {
                break
            }
        }
    }

}

