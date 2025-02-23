package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.util.BufferedList

class GizmoNode(name: String = "gizmo") : Node(name), InputStack.PointerListener {

    val gizmoTransform = TrsTransformD()

    val gizmoListeners = BufferedList<GizmoListener>()

    private val nodeTransform = TrsTransformD()
    val handleTransform = TrsTransformD()

    private val startTransform = TrsTransformD()
    private val startScale = MutableVec3d()

    private val _handles = mutableListOf<GizmoHandle>()
    val handles: List<GizmoHandle> get() = _handles
    private val handleGroup = Node().apply {
        transform = handleTransform
    }

    private val rayTest = RayTest()
    private val pickRay = RayD()
    private val virtualPointerPos = MutableVec2f()
    private var dragMode = DragMode.NO_DRAG
    private var isDrag = false
    private var hoverHandle: GizmoHandle? = null

    private val globalToDragLocal = MutableMat4d()
    private val gizmoRotation = MutableMat3d()

    var isDistanceIndependentSize = true
    var gizmoSize = 1f
        set(value) {
            field = value
            handleTransform.scale(value)
        }

    val latestManipulatorValue = mutableStateOf<ManipulatorValue?>(null)
    val overwriteManipulatorValue = mutableStateOf<Double?>(null)

    val dragSpeedModifier = mutableStateOf(1.0f)
    val translationTick = mutableStateOf(0.0)
    val rotationTick = mutableStateOf(0.0)
    val scaleTick = mutableStateOf(0.0)

    private var parentCam: Camera? = null
    private val camUpdateListener: (RenderPass.UpdateEvent) -> Unit = { ev ->
        gizmoTransform.decompose(nodeTransform.translation, nodeTransform.rotation)
        nodeTransform.markDirty()

        if (isDistanceIndependentSize) {
            val cam = ev.camera
            val handleOrigin = handleGroup.modelMatF.transform(MutableVec3f(), 1f)
            val distance = (handleOrigin - cam.globalPos) dot cam.globalLookDir
            handleTransform.setIdentity().scale(distance / 10f * gizmoSize)
        } else {
            handleTransform.scale(gizmoSize)
        }
        updateModelMatRecursive()
        if (isManipulating) {
            gizmoListeners.forEach { it.onGizmoUpdate(gizmoTransform) }
        }
    }

    var isManipulating = false
        private set
    var activeOp: GizmoOperation? = null
        private set

    init {
        transform = nodeTransform
        drawGroupId = DEFAULT_GIZMO_DRAW_GROUP
        addNode(handleGroup)

        onUpdate { ev ->
            gizmoListeners.update()
            if (parentCam != ev.camera) {
                parentCam?.let { it.onCameraUpdated -= camUpdateListener }
                parentCam = ev.camera
                ev.camera.onCameraUpdated += camUpdateListener
            }
        }
    }

    override fun release() {
        super.release()
        parentCam?.let { it.onCameraUpdated -= camUpdateListener }
    }

    fun addHandle(handle: GizmoHandle) {
        _handles += handle
        handleGroup.addNode(handle.drawNode)
    }

    fun removeHandle(handle: GizmoHandle) {
        _handles -= handle
        handleGroup.removeNode(handle.drawNode)
    }

    fun clearHandles() {
        _handles.clear()
        handleGroup.children.forEach { it.release() }
        handleGroup.clearChildren()
    }

    fun startManipulation(operation: GizmoOperation?) {
        startTransform.set(gizmoTransform)
        isManipulating = true
        activeOp = operation
        gizmoListeners.updated().forEach { it.onManipulationStart(startTransform) }
    }

    fun finishManipulation() {
        check(isManipulating) { "finishManipulation is only allowed after calling startManipulation()" }

        isManipulating = false
        activeOp = null
        gizmoListeners.updated().forEach { it.onManipulationFinished(startTransform, gizmoTransform) }
    }

    fun cancelManipulation() {
        check(isManipulating) { "cancelManipulation is only allowed after calling startManipulation()" }

        gizmoTransform.set(startTransform)
        isManipulating = false
        activeOp = null
        gizmoListeners.updated().forEach { it.onManipulationCanceled(startTransform) }
    }

    fun manipulateAxisTranslation(axis: GizmoHandle.Axis, distance: Double) {
        check(isManipulating) { "manipulateAxisTranslation is only allowed between calling startManipulation() and finishManipulation()" }

        // gizmoTransform is TRS, i.e. translation is applied before rotation. Rotate given translation to current
        // gizmo orientation
        gizmoRotation.setIdentity().rotate(gizmoTransform.rotation)
        val rotatedAxis = gizmoRotation.transform(axis.axis, MutableVec3d())

        gizmoTransform.set(startTransform)
        gizmoTransform.translate(rotatedAxis * distance)
        latestManipulatorValue.set(ManipulatorValue.ManipulatorValue1d(distance))
    }

    fun manipulateTranslation(translationOffset: Vec3d) {
        check(isManipulating) { "manipulateAxisTranslation is only allowed between calling startManipulation() and finishManipulation()" }

        // gizmoTransform is TRS, i.e. translation is applied before rotation. Rotate given translation to current
        // gizmo orientation
        gizmoRotation.setIdentity().rotate(gizmoTransform.rotation)
        val rotatedTranslation = gizmoRotation.transform(translationOffset, MutableVec3d())

        gizmoTransform.set(startTransform)
        gizmoTransform.translate(rotatedTranslation)
        latestManipulatorValue.set(ManipulatorValue.ManipulatorValue3d(translationOffset))
    }

    fun manipulateAxisRotation(axis: Vec3d, angle: AngleD) {
        check(isManipulating) { "manipulateAxisRotation is only allowed between calling startManipulation() and finishManipulation()" }

        gizmoTransform.set(startTransform)
        gizmoTransform.rotate(angle, axis)
        latestManipulatorValue.set(ManipulatorValue.ManipulatorValue1d(angle.deg))
    }

    fun manipulateRotation(rotation: QuatD) {
        check(isManipulating) { "manipulateAxisRotation is only allowed between calling startManipulation() and finishManipulation()" }

        gizmoTransform.set(startTransform)
        gizmoTransform.rotate(rotation)
        latestManipulatorValue.set(ManipulatorValue.ManipulatorValue4d(rotation.toVec4d()))
    }

    fun manipulateScale(scale: Vec3d) {
        check(isManipulating) { "manipulateAxisTranslation is only allowed between calling startManipulation() and finishManipulation()" }

        gizmoTransform.set(startTransform)
        gizmoTransform.scale(scale)
        latestManipulatorValue.set(ManipulatorValue.ManipulatorValue3d(scale))
    }

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (!isVisibleRecursive()) {
            return
        }

        val ptr = pointerState.primaryPointer
        val scene = findParentOfType<Scene>()
        if (scene == null || !scene.computePickRay(ptr, pickRay)) {
            return
        }

        rayTest.clear(camera = scene.camera)
        rayTest.ray.set(pickRay)
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
            virtualPointerPos.set(ptr.pos)
        }

        if (dragMode == DragMode.NO_DRAG && ptr.isLeftButtonDown) {
            dragMode = if (hoverHandle != null) DragMode.DRAG_MANIPULATE else DragMode.DRAG_IGNORE
        } else if (!ptr.isLeftButtonDown) {
            dragMode = DragMode.NO_DRAG
        }

        hoverHandle?.let { hover ->
            hover.moveVirtualPointer(virtualPointerPos, ptr, dragSpeedModifier.value)
            scene.camera.computePickRay(pickRay, virtualPointerPos.x.toFloat(), virtualPointerPos.y.toFloat(), scene.mainRenderPass.viewport)

            if (ptr.isLeftButtonDown && !isDrag) {
                globalToDragLocal.set(invModelMatD)
            }
            val dragCtx = DragContext(
                gizmo = this,
                virtualPointerPos = virtualPointerPos,
                globalRay = pickRay,
                localRay = pickRay.transformBy(globalToDragLocal, RayD()),
                globalToLocal = globalToDragLocal,
                localToGlobal = modelMatD,
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

    private fun isVisibleRecursive(): Boolean {
        var it: Node? = this
        while (it != null) {
            if (!it.isVisible) {
                return false
            }
            it = it.parent
        }
        return true
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
