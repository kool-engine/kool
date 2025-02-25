package de.fabmax.kool.editor.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.EditorEditMode
import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.Key
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.overlays.GizmoClientEntity
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.SPEED_MOD_ACCURATE
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.SPEED_MOD_NORMAL
import de.fabmax.kool.editor.overlays.applySpeedAndTickRate
import de.fabmax.kool.editor.overlays.updateLabel
import de.fabmax.kool.editor.ui.SceneView
import de.fabmax.kool.input.*
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.gizmo.*
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.toString

class ImmediateTransformEditMode(val editor: KoolEditor) : InputStack.PointerListener {
    private val mode: MutableStateValue<EditorEditMode.Mode> get() = editor.editMode.mode

    private val gizmoGroup = Node("immediate-transform-gizmo")
    private val gizmo = GizmoNode()
    private val translationOverlay = TranslationOverlay(gizmo)
    private val rotationOverlay = RotationOverlay(gizmo)
    private val scaleOverlay = ScaleOverlay(gizmo)
    private val gizmoLabel = SceneView.Label()

    private val globalRay = RayD()
    private val localRay = RayD()
    private val virtualPointerPos = MutableVec2f()

    private val globalToDragLocal = MutableMat4d()
    private val dragLocalToGlobal = MutableMat4d()
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

        addKeyListener(Key.TickIncrement) {
            val ov = (overwriteStrValue?.toDoubleOrNull() ?: 0.0) + getTick()
            overwriteStrValue = ov.toString(1)
        }
        addKeyListener(Key.MinorTickIncrement) {
            val ov = (overwriteStrValue?.toDoubleOrNull() ?: 0.0) + getMinorTick()
            overwriteStrValue = ov.toString(1)
        }
        addKeyListener(Key.TickDecrement) {
            val ov = (overwriteStrValue?.toDoubleOrNull() ?: 0.0) - getTick()
            overwriteStrValue = ov.toString(1)
        }
        addKeyListener(Key.MinorTickDecrement) {
            val ov = (overwriteStrValue?.toDoubleOrNull() ?: 0.0) - getMinorTick()
            overwriteStrValue = ov.toString(1)
        }

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
        gizmoGroup.apply {
            addNode(gizmo)
            addNode(translationOverlay)
            addNode(rotationOverlay)
            addNode(scaleOverlay)
        }
        editor.overlayScene += gizmoGroup
        gizmo.gizmoListeners += translationOverlay
        gizmo.gizmoListeners += rotationOverlay
        gizmo.gizmoListeners += scaleOverlay
    }

    private fun getTick(): Double {
        return when (mode.value) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> TransformGizmoOverlay.TICK_TRANSLATION_MAJOR
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> TransformGizmoOverlay.TICK_ROTATION_MAJOR
            EditorEditMode.Mode.SCALE_IMMEDIATE -> TransformGizmoOverlay.TICK_SCALE_MAJOR
            else -> 0.0
        }
    }

    private fun getMinorTick(): Double {
        return when (mode.value) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> TransformGizmoOverlay.TICK_TRANSLATION_MINOR
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> TransformGizmoOverlay.TICK_ROTATION_MINOR
            EditorEditMode.Mode.SCALE_IMMEDIATE -> TransformGizmoOverlay.TICK_SCALE_MINOR
            else -> 0.0
        }
    }

    fun start(mode: EditorEditMode.Mode) {
        if (isActive) {
            return
        }

        activeOp = when (mode) {
            EditorEditMode.Mode.MOVE_IMMEDIATE -> opCamPlaneTranslate
            EditorEditMode.Mode.ROTATE_IMMEDIATE -> opCamPlaneRotate
            EditorEditMode.Mode.SCALE_IMMEDIATE -> opUniformScale
            else -> opCamPlaneTranslate
        }

        selectionTransform = SelectionTransform(editor.selectionOverlay.getSelectedSceneEntities())
        selectionTransform?.primaryTransformNode?.let { updateGizmoFromClient(GizmoClientEntity(it)) }
        selectionTransform?.startTransform()
        overwriteStrValue = null

        inputHandler.push()
        if (mode != EditorEditMode.Mode.ROTATE_IMMEDIATE) {
            PointerInput.cursorMode = CursorMode.LOCKED
        }
        editor.ui.sceneView.addLabel(gizmoLabel)
    }

    fun finish(isCanceled: Boolean) {
        editor.ui.sceneView.removeLabel(gizmoLabel)

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

    private fun updateGizmoFromClient(client: GizmoClient) {
        val translation = client.localToGlobal.transform(MutableVec3d(), 1.0)
        val rotation = MutableQuatD(QuatD.IDENTITY)

        clientGlobalToParent.set(client.globalToParent)
        clientTransformOffset.setIdentity()

        when (editor.gizmoOverlay.transformFrame.value) {
            GizmoFrame.LOCAL -> {
                val localScale = MutableVec3d()
                client.localToGlobal.decompose(rotation = rotation, scale = localScale)
                gizmo.gizmoTransform.setCompositionOf(translation, rotation)
                clientTransformOffset.scale(localScale)
            }
            GizmoFrame.PARENT -> {
                client.parentToGlobal.decompose(rotation = rotation)
                gizmo.gizmoTransform.setCompositionOf(translation, rotation)
                val localRotation = MutableQuatD()
                val localScale = MutableVec3d()
                client.clientTransform.decompose(rotation = localRotation)
                client.localToGlobal.decompose(scale = localScale)
                clientTransformOffset.rotate(localRotation).scale(localScale)
            }
            GizmoFrame.GLOBAL -> {
                gizmo.gizmoTransform.setCompositionOf(translation)
                val localRotation = MutableQuatD()
                val localScale = MutableVec3d()
                client.localToGlobal.decompose(rotation = localRotation, scale = localScale)
                clientTransformOffset.rotate(localRotation).scale(localScale)
            }
        }
        dragLocalToGlobal.set(gizmo.gizmoTransform.matrixD)
        globalToDragLocal.set(gizmo.gizmoTransform.invMatrixD)
    }

    private fun updateFromGizmo(transform: TrsTransformD) {
        val client = selectionTransform?.primaryTransformNode ?: return

        val localTransform = MutableMat4d().set(Mat4d.IDENTITY)
            .mul(clientGlobalToParent)
            .mul(transform.matrixD)
            .mul(clientTransformOffset)
        client.transform.transform.setMatrix(localTransform)
        gizmoLabel.updateLabel(translationOverlay, rotationOverlay, scaleOverlay)
    }

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (selectionTransform?.primaryTransformNode == null) {
            // selection is empty
            finish(false)
            mode.set(EditorEditMode.Mode.NONE)
            return
        }

        val ptr = pointerState.primaryPointer
        if (!gizmo.isManipulating || mode.value == EditorEditMode.Mode.ROTATE_IMMEDIATE) {
            virtualPointerPos.set(ptr.pos)
        } else {
            val speedMod = if (KeyboardInput.isShiftDown) SPEED_MOD_ACCURATE else SPEED_MOD_NORMAL
            virtualPointerPos.x += ptr.delta.x * speedMod
            virtualPointerPos.y += ptr.delta.y * speedMod
        }
        gizmo.applySpeedAndTickRate()

        val scene = editor.overlayScene
        val ptrX = virtualPointerPos.x
        val ptrY = virtualPointerPos.y
        if (!scene.camera.computePickRay(globalRay, ptrX, ptrY, scene.mainRenderPass.viewport)) {
            return
        }

        globalRay.transformBy(globalToDragLocal, localRay)
        val dragCtx = DragContext(gizmo, virtualPointerPos, globalRay, localRay, globalToDragLocal, dragLocalToGlobal, scene.camera)

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
        dragCtxStart?.cancelManipulation()
        selectionTransform?.let { st ->
            st.restoreInitialTransform()
            st.primaryTransformNode?.let { updateGizmoFromClient(GizmoClientEntity(it)) }
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