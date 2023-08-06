package de.fabmax.kool.editor.overlays

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Gizmo
import kotlin.math.abs
import kotlin.math.sqrt

class TransformGizmoOverlay(private val editor: KoolEditor) : Node("Transform gizmo") {

    var transformMode = TransformMode.MOVE
        set(value) {
            field = value
            applyTransformMode()
        }

    private val selection = mutableListOf<NodeTransformData>()
    private var hasTransformAuthority = false

    private val gizmo = Gizmo()
    private var gizmoScale = 1f
    private val gizmoToGlobal = Mat4d()

    private val gizmoListener = object : Gizmo.GizmoListener {
        override fun onDragAxis(axis: Vec3f, distance: Float, targetTransform: Mat4d, ctx: KoolContext) {
            if (transformMode == TransformMode.MOVE) {
                targetTransform.translate(axis.x * distance, axis.y * distance, axis.z * distance)
                val t = gizmoToGlobal.transform(MutableVec3d().set(axis).scale(distance.toDouble()), 0.0)
                translateSelection(t)

            } else if (transformMode == TransformMode.SCALE) {
                applyGizmoScale(axis, distance)
            }
        }

        override fun onDragPlane(planeNormal: Vec3f, dragPosition: Vec3f, targetTransform: Mat4d, ctx: KoolContext) {
            targetTransform.translate(dragPosition)
            val t = gizmoToGlobal.transform(MutableVec3d().set(dragPosition), 0.0)
            translateSelection(t)
        }

        override fun onDragRotate(rotationAxis: Vec3f, angle: Float, targetTransform: Mat4d, ctx: KoolContext) {
            targetTransform.rotate(angle, rotationAxis)
            val ax = gizmoToGlobal.transform(MutableVec3d().set(rotationAxis), 0.0)
            rotateSelection(ax, angle.toDouble())
        }

        override fun onDragStart(ctx: KoolContext) {
            captureSelectionTransform()
            hasTransformAuthority = true
        }

        override fun onDragFinished(ctx: KoolContext) {
            gizmo.properties.setAxesLengths(1f)
            gizmo.updateMesh()
            hasTransformAuthority = false
            applySelectionTransform(true)
        }
    }

    init {
        addNode(gizmo)
        hideGizmo()

        gizmo.gizmoListener = gizmoListener
        gizmo.onUpdate {
            selection.getOrNull(0)?.nodeModel?.drawNode?.let {
                if (!hasTransformAuthority) {
                    gizmo.transform.set(it.modelMat)

                    gizmoScale = sqrt(it.globalRadius) + 0.5f
                    gizmo.transform.matrix.resetScale()
                    gizmo.setFixedScale(gizmoScale)
                    gizmoToGlobal.set(gizmo.transform.matrix)
                }
            }
        }
    }

    private fun applyGizmoScale(axis: Vec3f, distance: Float) {
        val axX = abs(axis.x).toDouble()
        val axY = abs(axis.y).toDouble()
        val axZ = abs(axis.z).toDouble()

        when {
            isFuzzyEqual(1.0, axX) -> {
                gizmo.properties.axisLenX = 1f + distance / gizmoScale
                gizmo.properties.axisLenNegX = 1f + distance / gizmoScale
            }
            isFuzzyEqual(1.0, axY) -> {
                gizmo.properties.axisLenY = 1f + distance / gizmoScale
                gizmo.properties.axisLenNegY = 1f + distance / gizmoScale
            }
            isFuzzyEqual(1.0, axZ) -> {
                gizmo.properties.axisLenZ = 1f + distance / gizmoScale
                gizmo.properties.axisLenNegZ = 1f + distance / gizmoScale
            }
        }
        gizmo.updateMesh()

        val ori = gizmoToGlobal.transform(MutableVec3d())
        val base = gizmoToGlobal.transform(MutableVec3d().set(axis))
        val scaled = gizmoToGlobal.transform(
            MutableVec3d().set(axis)
                .scale(distance / gizmoScale.toDouble())
                .add(MutableVec3d().set(axis))
        )
        val f = ori.distance(scaled) / ori.distance(base)
        val s = MutableVec3d(axX, axY, axZ)
            .scale(f)
            .add(Vec3d(1.0 - axX, 1.0 - axY, 1.0 - axZ))
        scaleSelection(s, f)
    }

    private fun applyTransformMode() {
        when (transformMode) {
            TransformMode.MOVE -> {
                gizmo.properties.axesHandleShape = Gizmo.AxisHandleShape.ARROW
                gizmo.properties.setAxisHandlesEnabled(true)
                gizmo.properties.setPlaneHandlesEnabled(true)
                gizmo.properties.setRotationHandlesEnabled(false)
            }
            TransformMode.ROTATE -> {
                gizmo.properties.setAxisHandlesEnabled(false)
                gizmo.properties.setPlaneHandlesEnabled(false)
                gizmo.properties.setRotationHandlesEnabled(true)
            }
            TransformMode.SCALE -> {
                gizmo.properties.axesHandleShape = Gizmo.AxisHandleShape.SPHERE
                gizmo.properties.setAxisHandlesEnabled(true)
                gizmo.properties.setPlaneHandlesEnabled(false)
                gizmo.properties.setRotationHandlesEnabled(false)
            }
        }
        gizmo.updateMesh()
    }

    private fun captureSelectionTransform() {
        selection.forEach {
            it.updateTransform()
        }
    }

    private fun applySelectionTransform(withUndo: Boolean) {
        selection.forEach {
            val action = SetTransformAction(
                editedNodeModel = it.nodeModel,
                oldTransform = TransformData(Vec3Data(it.startPosition), Vec4Data(it.startRotation), Vec3Data(it.startScale)),
                newTransform = TransformData(Vec3Data(it.dragPosition), Vec4Data(it.dragRotation), Vec3Data(it.dragScale)),
            )
            if (withUndo) {
                action.apply()
            } else {
                action.doAction()
            }
        }
    }

    private fun translateSelection(globalTranslation: Vec3d) {
        val t = MutableVec3d()
        selection.forEach { node ->
            val translationInParentFrame = node.globalToParent.transform(t.set(globalTranslation), 0.0)
            node.dragPosition.set(node.startPosition).add(translationInParentFrame)
        }
        applySelectionTransform(false)
    }

    private fun rotateSelection(globalAxis: Vec3d, angle: Double) {
        val m = Mat3d()
        val ax = MutableVec3d()
        selection.forEach { node ->
            val axisInParentFrame = node.globalToNode.transform(ax.set(globalAxis), 0.0)
            m.setRotate(node.startRotation)
                .rotate(angle, axisInParentFrame)
                .getRotation(node.dragRotation)
        }
        applySelectionTransform(false)
    }

    private fun scaleSelection(scale: Vec3d, singleScale: Double) {
        selection.forEach { node ->
            if (node.nodeModel.transform.isFixedScaleRatio.value) {
                node.dragScale.set(node.startScale).scale(singleScale)
            } else {
                node.dragScale.set(node.startScale).mul(scale)
            }
        }
        applySelectionTransform(false)
    }

    private fun hideGizmo() {
        gizmo.isVisible = false
        editor.editorInputContext.pointerListeners -= gizmo
    }

    private fun showGizmo() {
        gizmo.isVisible = true
        if (gizmo !in KoolEditor.instance.editorInputContext.pointerListeners) {
           editor.editorInputContext.pointerListeners += gizmo
        }
    }

    fun setTransformObject(nodeModel: SceneNodeModel?) {
        if (nodeModel != null) {
            setTransformObjects(listOf(nodeModel))
        } else {
            setTransformObjects(emptyList())
        }
    }

    fun setTransformObjects(nodeModels: List<SceneNodeModel>) {
        selection.clear()
        nodeModels.forEach {
            // todo: handle multi selection
            selection += NodeTransformData(it)
            gizmo.setFixedScale(sqrt(it.drawNode.globalRadius) + 0.5f)
        }

        if (selection.isNotEmpty()) {
            showGizmo()
        } else {
            hideGizmo()
        }
    }

    private class NodeTransformData(val nodeModel: SceneNodeModel) {
        val nodeToGlobal = Mat4d()
        val globalToParent = Mat4d()
        val globalToNode = Mat4d()

        val startPosition = MutableVec3d()
        val startRotation = MutableVec4d()
        val startScale = MutableVec3d()

        val dragPosition = MutableVec3d()
        val dragRotation = MutableVec4d()
        val dragScale = MutableVec3d()

        init {
            updateTransform()
        }

        fun updateTransform() {
            nodeToGlobal.set(nodeModel.drawNode.modelMat)
            nodeToGlobal.invert(globalToNode)
            globalToParent.setIdentity()
            nodeModel.drawNode.parent?.modelMat?.invert(globalToParent)

            nodeModel.transform.transformState.value.position.toVec3d(startPosition)
            nodeModel.transform.transformState.value.rotation.toVec4d(startRotation)
            nodeModel.transform.transformState.value.scale.toVec3d(startScale)

            dragPosition.set(startPosition)
            dragRotation.set(startRotation)
            dragScale.set(startScale)
        }
    }

    enum class TransformMode {
        MOVE,
        ROTATE,
        SCALE
    }
}