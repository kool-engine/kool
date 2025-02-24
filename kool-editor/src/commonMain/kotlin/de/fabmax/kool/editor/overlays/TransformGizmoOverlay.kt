package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.localToViewD
import de.fabmax.kool.editor.components.viewToLocalD
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.SPEED_MOD_ACCURATE
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.SPEED_MOD_NORMAL
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_NO_TICK
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_ROTATION_MAJOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_ROTATION_MINOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_SCALE_MAJOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_SCALE_MINOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_TRANSLATION_MAJOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_TRANSLATION_MINOR
import de.fabmax.kool.editor.ui.SceneView
import de.fabmax.kool.editor.ui.precisionForValue
import de.fabmax.kool.editor.util.SelectionTransform
import de.fabmax.kool.input.CursorMode
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.gizmo.*
import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Transform
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.toString

class TransformGizmoOverlay(val overlay: OverlayScene) : Node("Transform gizmo"), EditorOverlay {
    private val editor: KoolEditor get() = overlay.editor

    private val gizmo = SimpleGizmo()
    private val gizmoLabel = SceneView.Label()
    private var selectionTransform: SelectionTransform? = null

    private var hasTransformAuthority = false

    val transformFrame = mutableStateOf(gizmo.transformFrame).onChange { _, new -> gizmo.transformFrame = new }
    var transformMode: GizmoMode by gizmo::mode

    private val cancelListener = EditorKeyListener.cancelListener("Object transform") {
        gizmo.gizmoNode.cancelManipulation()
    }

    private val gizmoListener = object : GizmoListener {
        override fun onManipulationStart(startTransform: TrsTransformD) {
            hasTransformAuthority = true
            selectionTransform?.startTransform()
            cancelListener.push()
            if (transformMode != GizmoMode.ROTATE) {
                PointerInput.cursorMode = CursorMode.LOCKED
            }

            gizmoLabel.isVisible.set(false)
            editor.ui.sceneView.addLabel(gizmoLabel)
        }

        override fun onManipulationFinished(startTransform: TrsTransformD, endTransform: TrsTransformD) {
            hasTransformAuthority = false
            selectionTransform?.applyTransform(true)
            cancelListener.pop()
            PointerInput.cursorMode = CursorMode.NORMAL

            editor.ui.sceneView.removeLabel(gizmoLabel)
        }

        override fun onManipulationCanceled(startTransform: TrsTransformD) {
            hasTransformAuthority = false
            selectionTransform?.restoreInitialTransform()
            cancelListener.pop()
            PointerInput.cursorMode = CursorMode.NORMAL

            editor.ui.sceneView.removeLabel(gizmoLabel)
        }

        override fun onGizmoUpdate(transform: TrsTransformD) {
            gizmo.gizmoNode.applySpeedAndTickRate()
            selectionTransform?.updateTransform()
            selectionTransform?.applyTransform(false)

            gizmoLabel.updateLabel(gizmo.translationOverlay, gizmo.rotationOverlay, gizmo.scaleOverlay)
        }
    }

    init {
        gizmo.gizmoNode.gizmoListeners += gizmoListener
        gizmo.isVisible = false
        addNode(gizmo)
        onUpdate {
            if (!hasTransformAuthority && selectionTransform?.primaryTransformNode != null) {
                gizmo.updateGizmoFromClient()
            }
        }
    }

    fun setTransformObject(nodeModel: GameEntity?) {
        if (nodeModel != null) {
            setTransformObjects(listOf(nodeModel))
        } else {
            setTransformObjects(emptyList())
        }
    }

    fun setTransformObjects(nodeModels: List<GameEntity>) {
        selectionTransform = SelectionTransform(nodeModels)

        val prim = selectionTransform?.primaryTransformNode
        if (prim != null) {
            gizmo.setTransformEntity(prim)
            gizmo.isVisible = true
        } else {
            gizmo.isVisible = false
        }
    }

    companion object {
        const val SPEED_MOD_NORMAL = 1.0f
        const val SPEED_MOD_ACCURATE = 0.1f

        const val TICK_NO_TICK = 0.0

        const val TICK_TRANSLATION_MAJOR = 1.0
        const val TICK_ROTATION_MAJOR = 5.0
        const val TICK_SCALE_MAJOR = 0.1

        const val TICK_TRANSLATION_MINOR = 0.1
        const val TICK_ROTATION_MINOR = 1.0
        const val TICK_SCALE_MINOR = 0.01
    }
}

class GizmoClientEntity(val gameEntity: GameEntity) : GizmoClient {
    override val clientTransform: Transform get() = gameEntity.transform.transform
    override val localToGlobal: Mat4d get() = gameEntity.localToViewD
    override val parentToGlobal: Mat4d get() = gameEntity.parent?.localToViewD ?: Mat4d.IDENTITY
    override val globalToParent: Mat4d get() = gameEntity.parent?.viewToLocalD ?: Mat4d.IDENTITY

    override fun updateMatrices() {
        gameEntity.transform.updateTransformRecursive()
    }
}

fun SimpleGizmo.setTransformEntity(gameEntity: GameEntity) {
    transformClient = GizmoClientEntity(gameEntity)
}

fun GizmoNode.applySpeedAndTickRate() {
    dragSpeedModifier.set(if (KeyboardInput.isShiftDown) SPEED_MOD_ACCURATE else SPEED_MOD_NORMAL)
    if (KeyboardInput.isCtrlDown) {
        translationTick.set(if (KeyboardInput.isShiftDown) TICK_TRANSLATION_MINOR else TICK_TRANSLATION_MAJOR)
        rotationTick.set(if (KeyboardInput.isShiftDown) TICK_ROTATION_MINOR else TICK_ROTATION_MAJOR)
        scaleTick.set(if (KeyboardInput.isShiftDown) TICK_SCALE_MINOR else TICK_SCALE_MAJOR)
    } else {
        translationTick.set(TICK_NO_TICK)
        rotationTick.set(TICK_NO_TICK)
        scaleTick.set(TICK_NO_TICK)
    }
}

fun SceneView.Label.updateLabel(translationOverlay: TranslationOverlay, rotationOverlay: RotationOverlay, scaleOverlay: ScaleOverlay) {
    val lblPos: Vec2f
    val isValid: Boolean
    val value: Double
    when {
        translationOverlay.isLabelValid -> {
            lblPos = translationOverlay.labelPosition
            value = translationOverlay.labelValue
            isValid = true
        }
        rotationOverlay.isLabelValid -> {
            lblPos = rotationOverlay.labelPosition
            value = rotationOverlay.labelValue
            isValid = true
        }
        scaleOverlay.isLabelValid -> {
            lblPos = scaleOverlay.labelPosition
            value = scaleOverlay.labelValue
            isValid = true
        }
        else -> {
            lblPos = Vec2f.ZERO
            isValid = false
            value = 0.0
        }
    }
    val precision = precisionForValue(value)
    text.set(value.toString(precision))
    isVisible.set(isValid)
    x.set(Dp.fromPx(lblPos.x))
    y.set(Dp.fromPx(lblPos.y))
}