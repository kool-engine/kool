package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.ScreenRenderPass
import de.fabmax.kool.util.Disposable

/**
 * @author fabmax
 */

inline fun scene(name: String? = null, block: Scene.() -> Unit): Scene {
    return Scene(name).apply(block)
}

class Scene(name: String? = null) : Group(name) {

    val mainRenderPass = ScreenRenderPass(this)
    val offscreenPasses = mutableListOf<OffscreenRenderPass>()

    val lighting = Lighting(this)
    var camera: Camera = PerspectiveCamera()

    val onRenderScene: MutableList<Scene.(KoolContext) -> Unit> = mutableListOf()

    override var isFrustumChecked: Boolean
        // frustum check is force disabled for Scenes
        get() = false
        set(_) {}

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

        update(ctx)

        for (i in offscreenPasses.indices.reversed()) {
            offscreenPasses[i].update(ctx)
            offscreenPasses[i].collectDrawCommands(ctx)
        }
        mainRenderPass.collectDrawCommands(ctx)
    }

    fun processInput(ctx: KoolContext) {
        handleInput(ctx)
    }

    override fun update(ctx: KoolContext) {
        for (i in disposables.indices) {
            disposables[i].dispose(ctx)
        }
        disposables.clear()
        super.update(ctx)
    }

    fun dispose(disposable: Disposable) {
        disposables += disposable
    }

    override fun dispose(ctx: KoolContext) {
        disposables.forEach { it.dispose(ctx) }
        disposables.clear()

        mainRenderPass.dispose(ctx)
        offscreenPasses.forEach { it.dispose(ctx) }

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
        return camera.computePickRay(result, pointer, mainRenderPass.viewport, ctx)
    }

    private fun handleInput(ctx: KoolContext) {
        var hovered: Node? = null
        val prevHovered = hoverNode
        val ptr = ctx.inputMgr.pointerState.primaryPointer

        if (!isPickingEnabled || !ptr.isValid || ptr.isConsumed()) {
            return
        }

        if (ptr.isInViewport(mainRenderPass.viewport, ctx) && camera.initRayTes(rayTest, ptr, mainRenderPass.viewport, ctx)) {
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
            if (ptr.isValid && ptr.isInViewport(mainRenderPass.viewport, ctx) &&
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
