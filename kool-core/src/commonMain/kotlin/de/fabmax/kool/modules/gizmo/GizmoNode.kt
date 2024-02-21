package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.MutableVec2d
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.toRayF
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.TrsTransformF

class GizmoNode(name: String = "gizmo") : Node(name), InputStack.PointerListener {

    private val gizmoTransform = TrsTransformF()

    private val handles = mutableListOf<GizmoHandle>()

    private val rayTest = RayTest()
    private val pickRay = RayD()
    private var dragMode = DragMode.NO_DRAG
    private var hoverHandle: GizmoHandle? = null
    private val dragStartPointerPos = MutableVec2d()

    init {
        transform = gizmoTransform
        drawGroupId = DEFAULT_GIZMO_DRAW_GROUP
    }

    fun addHandle(handle: GizmoHandle) {
        handles += handle
        addNode(handle.drawNode)
    }

    fun removeHandle(handle: GizmoHandle) {
        handles -= handle
        removeNode(handle.drawNode)
    }

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (!isVisible) {
            return
        }

        val ptr = pointerState.primaryPointer
        val scene = findParentOfType<Scene>()
        if (scene == null || !scene.computePickRay(ptr, pickRay)) {
            return
        }

        rayTest.clear()
        pickRay.toRayF(rayTest.ray)
        rayTest(rayTest)

        if (dragMode == DragMode.NO_DRAG) {
            val newHandle = if (rayTest.isHit) {
                rayTest.hitNode?.findParentOfType<GizmoHandle>()
            } else {
                null
            }
            if (newHandle != hoverHandle) {
                hoverHandle?.onHoverExit()
            }
            hoverHandle = newHandle
        }

        if (dragMode == DragMode.NO_DRAG && ptr.isLeftButtonDown) {
            dragMode = if (hoverHandle != null) DragMode.DRAG_MANIPULATE else DragMode.DRAG_IGNORE
        } else if (!ptr.isLeftButtonDown) {
            dragMode = DragMode.NO_DRAG
        }

        hoverHandle?.let { hover ->
            if (ptr.isLeftButtonDown) {
                ptr.consume()
                hover.onDrag(ptr, dragStartPointerPos, pickRay)
            } else {
                hover.onHover(ptr, pickRay)
            }
        }
    }

    companion object {
        const val DEFAULT_GIZMO_DRAW_GROUP = 1000
    }

    private enum class DragMode {
        NO_DRAG,
        DRAG_MANIPULATE,
        DRAG_IGNORE
    }
}