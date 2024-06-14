package de.fabmax.kool.editor.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.EditorEditMode
import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.Key
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.SPEED_MOD_ACCURATE
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.SPEED_MOD_NORMAL
import de.fabmax.kool.editor.overlays.applySpeedAndTickRate
import de.fabmax.kool.input.*
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.gizmo.*
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.TrsTransformD

class ImmediateTransformEditMode(val editor: KoolEditor) : InputStack.PointerListener {
    private val mode: MutableStateValue<EditorEditMode.Mode> get() = editor.editMode.mode

    private val gizmo = GizmoNode()
    private val globalRay = RayD()
    private val localRay = RayD()
    private val virtualPointerPos = MutableVec2d()

    private val globalToDragLocal = MutableMat4d()
    private val clientGlobalToParent = MutableMat4d()
    private val clientTransformOffset = MutableMat4d()

    private var selectionTransform: SelectionTransform? = null
    private var dragCtxStart: DragContext? = null
    private var overwriteStrValue: String? = null

    private var activeOp: GizmoOperation? = null

    private val opCamPlaneTranslate = CamPlaneTranslation()
    private val opXAxisTranslate = AxisTranslation(GizmoHandle.Axis.POS_X)
    private val opYAxisTranslate = AxisTranslation(GizmoHandle.Axis.POS_Y)
    private val opZAxisTranslate = AxisTranslation(GizmoHandle.Axis.POS_Z)
    private val opXPlaneTranslate = PlaneTranslation(Vec3d.X_AXIS)
    private val opYPlaneTranslate = PlaneTranslation(Vec3d.Y_AXIS)
    private val opZPlaneTranslate = PlaneTranslation(Vec3d.Z_AXIS)

    private val opCamPlaneRotate = CamPlaneRotation()
    private val opXAxisRotate = AxisRotation(GizmoHandle.Axis.POS_X)
    private val opYAxisRotate = AxisRotation(GizmoHandle.Axis.POS_Y)
    private val opZAxisRotate = AxisRotation(GizmoHandle.Axis.POS_Z)

    private val opUniformScale = UniformScale()
    private val opXAxisScale = AxisScale(GizmoHandle.Axis.POS_X)
    private val opYAxisScale = AxisScale(GizmoHandle.Axis.POS_Y)
    private val opZAxisScale = AxisScale(GizmoHandle.Axis.POS_Z)
    private val opXPlaneScale = PlaneScale(GizmoHandle.Axis.POS_X)
    private val opYPlaneScale = PlaneScale(GizmoHandle.Axis.POS_Y)
    private val opZPlaneScale = PlaneScale(GizmoHandle.Axis.POS_Z)

    val isActive: Boolean get() = gizmo.isManipulating

    private val inputHandler = EditorKeyListener("Immediate transform mode").apply {
        pointerListeners += this@ImmediateTransformEditMode

        addKeyListener(Key.LimitToXAxis) { setXAxisOp() }
        addKeyListener(Key.LimitToYAxis) { setYAxisOp() }
        addKeyListener(Key.LimitToZAxis) { setZAxisOp() }

        addKeyListener(Key.LimitToXPlane) { setXPlaneOp() }
        addKeyListener(Key.LimitToYPlane) { setYPlaneOp() }
        addKeyListener(Key.LimitToZPlane) { setZPlaneOp() }

        addKeyListener(Key.ToggleImmediateMoveMode) {
            setOp(opCamPlaneTranslate)
            editor.editMode.toggleMode(EditorEditMode.Mode.MOVE_IMMEDIATE)
        }
        addKeyListener(Key.ToggleImmediateRotateMode) {
            setOp(opCamPlaneRotate)
            editor.editMode.toggleMode(EditorEditMode.Mode.ROTATE_IMMEDIATE)
        }
        addKeyListener(Key.ToggleImmediateScaleMode) {
            setOp(opUniformScale)
            editor.editMode.toggleMode(EditorEditMode.Mode.SCALE_IMMEDIATE)
        }
        addKeyListener(Key.Enter) {
            finish(isCanceled = false)
            mode.set(EditorEditMode.Mode.NONE)
        }
        addKeyListener(Key.Cancel) {
            finish(isCanceled = true)
            mode.set(EditorEditMode.Mode.NONE)
        }

        keyboardListeners += InputStack.KeyboardListener { events, _ ->
            events.forEach { evt ->
                val prevStr = overwriteStrValue ?: ""
                if (evt.isCharTyped) {
                    when {
                        evt.typedChar.isDigit() -> overwriteStrValue = "$prevStr${evt.typedChar}"
                        evt.typedChar == '.' && '.' !in prevStr -> overwriteStrValue = "$prevStr."
                        evt.typedChar == ',' && '.' !in prevStr -> overwriteStrValue = "$prevStr."
                        evt.typedChar == '+' -> overwriteStrValue = prevStr.removePrefix("-")
                        evt.typedChar == '-' -> overwriteStrValue = "-" + prevStr.removePrefix("-")
                    }
                } else if (evt.keyCode == KeyboardInput.KEY_BACKSPACE && evt.isPressed) {
                    overwriteStrValue = if (prevStr.isNotEmpty()) prevStr.substring(0 until prevStr.lastIndex) else null
                }
            }
        }
    }

    init {
        gizmo.addTranslationHandles()
    }

    fun start(mode: EditorEditMode.Mode): Boolean {
        if (isActive) {
            return true
        }

        activeOp = when (mode) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> opCamPlaneTranslate
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> opCamPlaneRotate
            EditorEditMode.Mode.SCALE_IMMEDIATE -> opUniformScale
            else -> opCamPlaneTranslate
        }

        selectionTransform = SelectionTransform(editor.selectionOverlay.getSelectedSceneNodes())
        val primNode = selectionTransform?.primaryTransformNode
        primNode?.let { updateGizmoFromClient(it.drawNode) }
        selectionTransform?.startTransform()
        overwriteStrValue = null

        inputHandler.push()
        if (mode != EditorEditMode.Mode.ROTATE_IMMEDIATE) {
            PointerInput.cursorMode = CursorMode.LOCKED
        }
        return true
    }

    fun finish(isCanceled: Boolean) {
        if (gizmo.isManipulating) {
            if (isCanceled) {
                gizmo.cancelManipulation()
                selectionTransform?.restoreInitialTransform()

            } else {
                gizmo.finishManipulation()
                selectionTransform?.applyTransform(true)
            }
        }
        selectionTransform = null
        overwriteStrValue = null
        PointerInput.cursorMode = CursorMode.NORMAL
        inputHandler.pop()
    }

    private fun updateGizmoFromClient(client: Node) {
        clientGlobalToParent.set(client.parent?.invModelMatD ?: Mat4d.IDENTITY)
        clientTransformOffset.setIdentity()

        val translation = client.modelMatD.transform(MutableVec3d(), 1.0)
        val rotation = MutableQuatD(QuatD.IDENTITY)

        when (editor.gizmoOverlay.transformFrame.value) {
            GizmoFrame.LOCAL -> {
                client.modelMatD.decompose(rotation = rotation)
                gizmo.gizmoTransform.setCompositionOf(translation, rotation)
                val localScale = MutableVec3d()
                client.modelMatD.decompose(scale = localScale)
                clientTransformOffset.scale(localScale)
            }
            GizmoFrame.PARENT -> {
                client.parent?.modelMatD?.decompose(rotation = rotation)
                gizmo.gizmoTransform.setCompositionOf(translation, rotation)
                val localRotation = MutableQuatD()
                val localScale = MutableVec3d()
                client.transform.decompose(rotation = localRotation)
                client.modelMatD.decompose(scale = localScale)
                clientTransformOffset.rotate(localRotation).scale(localScale)
            }
            GizmoFrame.GLOBAL -> {
                gizmo.gizmoTransform.setCompositionOf(translation)
                val localRotation = MutableQuatD()
                val localScale = MutableVec3d()
                client.modelMatD.decompose(rotation = localRotation, scale = localScale)
                clientTransformOffset.rotate(localRotation).scale(localScale)
            }
        }
        globalToDragLocal.set(gizmo.gizmoTransform.invMatrixD)
    }

    private fun updateFromGizmo(transform: TrsTransformD) {
        val client = selectionTransform?.primaryTransformNode?.drawNode ?: return

        val localTransform = MutableMat4d().set(Mat4d.IDENTITY)
            .mul(clientGlobalToParent)
            .mul(transform.matrixD)
            .mul(clientTransformOffset)
        client.transform.setMatrix(localTransform)
    }

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (selectionTransform?.primaryTransformNode == null) {
            // selection is empty
            finish(false)
            mode.set(EditorEditMode.Mode.NONE)
            return
        }

        val ptr = pointerState.primaryPointer
        if (!gizmo.isManipulating) {
            virtualPointerPos.set(ptr.x, ptr.y)
        } else {
            val speedMod = if (KeyboardInput.isShiftDown) SPEED_MOD_ACCURATE else SPEED_MOD_NORMAL
            virtualPointerPos.x += ptr.deltaX * speedMod
            virtualPointerPos.y += ptr.deltaY * speedMod
            gizmo.applySpeedAndTickRate()
        }

        val scene = editor.editorContent.findParentOfType<Scene>()
        val ptrX = virtualPointerPos.x.toFloat()
        val ptrY = virtualPointerPos.y.toFloat()
        if (scene == null || !scene.camera.computePickRay(globalRay, ptrX, ptrY, scene.mainRenderPass.viewport)) {
            return
        }

        globalRay.transformBy(globalToDragLocal, localRay)
        val dragCtx = DragContext(gizmo, virtualPointerPos, globalRay, localRay, globalToDragLocal, scene.camera)

        gizmo.overwriteManipulatorValue.set(overwriteStrValue?.toDoubleOrNull())

        if (!gizmo.isManipulating) {
            dragCtxStart = dragCtx
            activeOp?.onDragStart(dragCtx)
        } else {
            activeOp?.onDrag(dragCtx)
            updateFromGizmo(gizmo.gizmoTransform)
            selectionTransform?.updateTransform()
            selectionTransform?.applyTransform(false)
        }

        if (ptr.isLeftButtonClicked) {
            ptr.consume()
            finish(false)
            mode.set(EditorEditMode.Mode.NONE)

        } else if (ptr.isRightButtonClicked) {
            ptr.consume()
            finish(true)
            mode.set(EditorEditMode.Mode.NONE)
        }
    }

    private fun setXAxisOp() {
        when (mode.value) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> setOp(opXAxisTranslate)
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> setOp(opXAxisRotate)
            EditorEditMode.Mode.SCALE_IMMEDIATE -> setOp(opXAxisScale)
            else -> { }
        }
    }

    private fun setYAxisOp() {
        when (mode.value) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> setOp(opYAxisTranslate)
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> setOp(opYAxisRotate)
            EditorEditMode.Mode.SCALE_IMMEDIATE -> setOp(opYAxisScale)
            else -> { }
        }
    }

    private fun setZAxisOp() {
        when (mode.value) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> setOp(opZAxisTranslate)
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> setOp(opZAxisRotate)
            EditorEditMode.Mode.SCALE_IMMEDIATE -> setOp(opZAxisScale)
            else -> { }
        }
    }

    private fun setXPlaneOp() {
        when (mode.value) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> setOp(opXPlaneTranslate)
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> setOp(opXAxisRotate)
            EditorEditMode.Mode.SCALE_IMMEDIATE -> setOp(opXPlaneScale)
            else -> { }
        }
    }

    private fun setYPlaneOp() {
        when (mode.value) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> setOp(opYPlaneTranslate)
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> setOp(opYAxisRotate)
            EditorEditMode.Mode.SCALE_IMMEDIATE -> setOp(opYPlaneScale)
            else -> { }
        }
    }

    private fun setZPlaneOp() {
        when (mode.value) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> setOp(opZPlaneTranslate)
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> setOp(opZAxisRotate)
            EditorEditMode.Mode.SCALE_IMMEDIATE -> setOp(opZPlaneScale)
            else -> { }
        }
    }

    private fun setOp(op: GizmoOperation) {
        activeOp = op
        if (isActive) {
            dragCtxStart?.let { activeOp?.onDragStart(it) }
        }
    }

    companion object {
        private val FILTER_NO_SHIFT: (KeyEvent) -> Boolean = { it.isPressed && !it.isShiftDown }
        private val FILTER_SHIFT: (KeyEvent) -> Boolean = { it.isPressed && it.isShiftDown }

        private val numericChars = setOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '+', '-', '.', ','
        )
    }
}