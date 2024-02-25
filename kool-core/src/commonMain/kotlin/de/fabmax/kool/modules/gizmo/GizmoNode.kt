package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.*
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

    private val globalToDragLocal = MutableMat4d()

    private val escListener = InputStack.SimpleKeyListener(KeyboardInput.KEY_ESC, "Cancel drag") {
        cancelManipulation()
    }

    var isManipulating = false
        private set

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

    fun startManipulation(cancelOnEscape: Boolean = true) {
        startTransform.set(gizmoTransform)
        isManipulating = true

        if (cancelOnEscape) {
            InputStack.defaultInputHandler.addKeyListener(escListener)
        }
    }

    fun finishManipulation() {
        check(isManipulating) { "finishManipulation is only allowed after calling startManipulation()" }

        isManipulating = false
        InputStack.defaultInputHandler.removeKeyListener(escListener)
    }

    fun cancelManipulation() {
        check(isManipulating) { "cancelManipulation is only allowed after calling startManipulation()" }

        gizmoTransform.set(startTransform)
        isManipulating = false
        InputStack.defaultInputHandler.removeKeyListener(escListener)
    }

    fun manipulateAxisTranslation(axis: GizmoHandle.Axis, distance: Double) {
        check(isManipulating) { "manipulateAxisTranslation is only allowed between calling startManipulation() and finishManipulation()" }

        gizmoTransform.set(startTransform)
        gizmoTransform.translate(axis.axis * distance)
    }

    fun manipulateTranslation(translationOffset: Vec3d) {
        check(isManipulating) { "manipulateAxisTranslation is only allowed between calling startManipulation() and finishManipulation()" }

        gizmoTransform.set(startTransform)
        gizmoTransform.translate(translationOffset)
    }

    fun manipulateAxisRotation(axis: Vec3d, angle: AngleD) {
        check(isManipulating) { "manipulateAxisRotation is only allowed between calling startManipulation() and finishManipulation()" }

        gizmoTransform.set(startTransform)
        gizmoTransform.rotate(angle, axis)
    }

    fun manipulateRotation(rotation: QuatD) {
        check(isManipulating) { "manipulateAxisRotation is only allowed between calling startManipulation() and finishManipulation()" }

        gizmoTransform.set(startTransform)
        gizmoTransform.rotate(rotation)
    }

    fun manipulateScale(scale: Vec3d) {
        check(isManipulating) { "manipulateAxisTranslation is only allowed between calling startManipulation() and finishManipulation()" }

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
            if (ptr.isLeftButtonDown && !isDrag) {
                globalToDragLocal.set(invModelMatD)
            }
            val dragCtx = DragContext(
                gizmo = this,
                pointer = ptr,
                globalRay = pickRay,
                localRay = pickRay.transformBy(globalToDragLocal, RayD()),
                globalToLocal = globalToDragLocal,
                camera = scene.camera
            )

            if (ptr.isLeftButtonDown) {
                ptr.consume()
                if (!isDrag) {
                    hover.onDragStart(dragCtx)
                    isDrag = true
                } else {
                    hover.onDrag(dragCtx)
                }
            } else {
                if (isDrag) {
                    hover.onDragEnd(dragCtx)
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