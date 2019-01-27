package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.gl.GL_COLOR_BUFFER_BIT
import de.fabmax.kool.gl.GL_DEPTH_BUFFER_BIT
import de.fabmax.kool.gl.glClear
import de.fabmax.kool.lock
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.util.Disposable

/**
 * @author fabmax
 */

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

open class Scene(name: String? = null) : Group(name) {

    val onRenderScene: MutableList<Scene.(KoolContext) -> Unit> = mutableListOf()

    override var isFrustumChecked: Boolean
        // frustum check is force disabled for Scenes
        get() = false
        set(@Suppress("UNUSED_PARAMETER") value) {}

    var camera: Camera = PerspectiveCamera()
    var lighting = Lighting(this)
    var viewport = KoolContext.Viewport(0, 0, 0, 0)
        protected set

    var clearMask = GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT
    var isPickingEnabled = true
    private val rayTest = RayTest()
    private var hoverNode: Node? = null

    private val dragPtrs: MutableList<InputManager.Pointer> = mutableListOf()
    private val dragHandlers: MutableList<DragHandler> = mutableListOf()

    private val disposables = mutableListOf<Disposable>()

    init {
        scene = this
    }

    fun renderScene(ctx: KoolContext) {
        for (i in onRenderScene.indices) {
            onRenderScene[i](ctx)
        }
        viewport = ctx.viewport

        camera.updateCamera(ctx)
        preRender(ctx)
        //handleInput(ctx)

        if (clearMask != 0) {
            glClear(clearMask)
        }
        render(ctx)
        postRender(ctx)
    }

    fun processInput(ctx: KoolContext) {
        handleInput(ctx)
    }

    fun onRenderingHintsChanged(ctx: KoolContext) {
        lighting.onRenderingHintsChanged(ctx)
    }

    override fun preRender(ctx: KoolContext) {
        lock(disposables) {
            for (i in disposables.indices) {
                disposables[i].dispose(ctx)
            }
            disposables.clear()
        }
        super.preRender(ctx)
    }

    fun dispose(disposable: Disposable) {
        lock(disposables) {
            disposables += disposable
        }
    }

    override fun dispose(ctx: KoolContext) {
        lock(disposables) {
            disposables.forEach { it.dispose(ctx) }
            disposables.clear()
        }
        super.dispose(ctx)
    }

    fun registerDragHandler(handler: DragHandler) {
        if (handler !in dragHandlers) {
            dragHandlers += handler
        }
    }

    fun removeDragHandler(handler: DragHandler) {
        dragHandlers -= handler
    }

    fun computeRay(pointer: InputManager.Pointer, ctx: KoolContext, result: Ray): Boolean {
        return camera.computePickRay(result, pointer, viewport, ctx)
    }

    private fun handleInput(ctx: KoolContext) {
        var hovered: Node? = null
        val prevHovered = hoverNode
        val ptr = ctx.inputMgr.pointerState.primaryPointer

        if (!isPickingEnabled || !ptr.isValid || ptr.isConsumed()) {
            return
        }

        if (ptr.isInViewport(viewport, ctx) && camera.initRayTes(rayTest, ptr, viewport, ctx)) {
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

        handleDrag(ctx)
    }

    private fun handleDrag(ctx: KoolContext) {
        dragPtrs.clear()
        for (i in ctx.inputMgr.pointerState.pointers.indices) {
            val ptr = ctx.inputMgr.pointerState.pointers[i]
            if (ptr.isValid && ptr.isInViewport(viewport, ctx) &&
                    (ptr.buttonMask != 0 || ptr.buttonEventMask != 0 || ptr.deltaScroll != 0f)) {
                dragPtrs.add(ptr)
            }
        }

        for (i in dragHandlers.indices.reversed()) {
            dragHandlers[i].handleDrag(dragPtrs, this, ctx)
        }
    }

    interface DragHandler {
        fun handleDrag(dragPtrs: List<InputManager.Pointer>, scene: Scene, ctx: KoolContext)

    }
}
