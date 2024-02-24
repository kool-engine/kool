package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.toRayF
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.TrsTransformD

class GizmoNode(name: String = "gizmo") : Node(name), InputStack.PointerListener {

    private val gizmoTransform = TrsTransformD()
    private val startTransform = TrsTransformD()

    private val handles = mutableListOf<GizmoHandle>()

    private val rayTest = RayTest()
    private val pickRay = RayD()
    private var dragMode = DragMode.NO_DRAG
    private var isDrag = false
    private var hoverHandle: GizmoHandle? = null

    private var isManipulating = false

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

    fun startManipulation() {
        startTransform.set(gizmoTransform)
        isManipulating = true
    }

    fun finishManipulation() {
        isManipulating = false
    }

    fun manipulateAxisTranslation(axis: GizmoHandle.Axis, distance: Double) {
        check(isManipulating) { "manipulateAxisTranslation is only allowed between calling startManipulation() and finishManipulation()" }

        gizmoTransform.set(startTransform)
        gizmoTransform.translate(axis.axis * distance)
    }

    fun manipulateAxisScale(axis: GizmoHandle.Axis, factor: Double) {
        check(isManipulating) { "manipulateAxisTranslation is only allowed between calling startManipulation() and finishManipulation()" }

        val scale = Vec3d(
            if (axis.axis.x == 0.0) 1.0 else axis.axis.x * factor,
            if (axis.axis.y == 0.0) 1.0 else axis.axis.y * factor,
            if (axis.axis.z == 0.0) 1.0 else axis.axis.z * factor,
        )

        gizmoTransform.set(startTransform)
        gizmoTransform.scale(scale)
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
                hoverHandle?.onHoverExit(this)
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
                if (!isDrag) {
                    hover.onDragStart(ptr, pickRay, this)
                    isDrag = true
                } else {
                    hover.onDrag(ptr, pickRay, this)
                }
            } else {
                if (isDrag) {
                    hover.onDragEnd(ptr, pickRay, this)
                    isDrag = false
                }
                hover.onHover(ptr, pickRay, this)
            }
        }

        if (!ptr.isLeftButtonDown) {
            isDrag = false
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