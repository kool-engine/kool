package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableQuatD
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.modules.gizmo.GizmoListener
import de.fabmax.kool.modules.gizmo.GizmoMode
import de.fabmax.kool.modules.gizmo.SimpleGizmo
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD

class TransformGizmoOverlay : Node("Transform gizmo") {

    private val gizmo = SimpleGizmo()
    private val selection = mutableListOf<NodeTransformData>()
    private val primaryTransformNode: SceneNodeModel?
        get() = selection.getOrNull(0)?.nodeModel

    private var hasTransformAuthority = false
    val isTransformDrag: Boolean get() = hasTransformAuthority


    val transformFrame = mutableStateOf(gizmo.transformFrame).onChange {
        gizmo.transformFrame = it
    }

    var transformMode: GizmoMode by gizmo::mode

    private val gizmoListener = object : GizmoListener {
        override fun onManipulationStart(startTransform: TrsTransformD) {
            hasTransformAuthority = true
            selection.forEach { it.captureStart() }
        }

        override fun onManipulationFinished(startTransform: TrsTransformD, endTransform: TrsTransformD) {
            hasTransformAuthority = false
            applySelectionTransform(true)
        }

        override fun onManipulationCanceled(startTransform: TrsTransformD) {
            println("canceled")
            hasTransformAuthority = false
        }

        override fun onGizmoUpdate(transform: TrsTransformD) {
            selection.forEach { it.captureDrag() }
            applySelectionTransform(false)
        }
    }

    init {
        gizmo.gizmoNode.gizmoListeners += gizmoListener
        gizmo.isVisible = false
        addNode(gizmo)

        onUpdate {
            if (!hasTransformAuthority && primaryTransformNode != null) {
                gizmo.updateGizmoFromClient()
            }
        }
    }

    fun cancelTransformOperation() {
        gizmo.gizmoNode.cancelManipulation()
    }

    private fun applySelectionTransform(withUndo: Boolean) {
        val transformNodes = mutableListOf<SceneNodeModel>()
        val undoTransforms = mutableListOf<TransformData>()
        val applyTransforms = mutableListOf<TransformData>()

        selection.forEach {
            transformNodes += it.nodeModel
            undoTransforms += TransformData(
                Vec3Data(it.startPosition),
                Vec4Data(it.startRotation),
                Vec3Data(it.startScale)
            )
            applyTransforms += TransformData(
                Vec3Data(it.dragPosition),
                Vec4Data(it.dragRotation),
                Vec3Data(it.dragScale)
            )
        }
        val action = SetTransformAction(transformNodes, undoTransforms, applyTransforms)
        if (withUndo) {
            action.apply()
        } else {
            action.doAction()
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

        val nodeModelsSet = nodeModels.toSet()
        val nonHierarchical = nodeModelsSet.filter { it.noParentIn(nodeModelsSet) }
        nonHierarchical.forEach { selection += NodeTransformData(it) }

        val prim = primaryTransformNode
        if (prim != null) {
            gizmo.transformNode = prim.drawNode
            gizmo.isVisible = true
        } else {
            gizmo.isVisible = false
        }
    }

    private fun SceneNodeModel.noParentIn(nodeModels: Set<SceneNodeModel>): Boolean {
        var parent = parent as? SceneNodeModel
        while (parent != null) {
            if (parent in nodeModels) {
                return false
            }
            parent = parent.parent as? SceneNodeModel
        }
        return true
    }

    private inner class NodeTransformData(val nodeModel: SceneNodeModel) {
        val poseInPrimaryFrame = MutableMat4d()

        val startPosition = MutableVec3d()
        val startRotation = MutableQuatD()
        val startScale = MutableVec3d()

        val dragPosition = MutableVec3d()
        val dragRotation = MutableQuatD()
        val dragScale = MutableVec3d()

        fun captureStart() {
            nodeModel.drawNode.transform.decompose(startPosition, startRotation, startScale)

            poseInPrimaryFrame.setIdentity()
            primaryTransformNode?.let { prim ->
                poseInPrimaryFrame.set(prim.drawNode.invModelMatD).mul(nodeModel.drawNode.modelMatD)
            }
        }

        fun captureDrag() {
            if (nodeModel === primaryTransformNode) {
                nodeModel.drawNode.transform.decompose(dragPosition, dragRotation, dragScale)
            } else {
                primaryTransformNode?.let { prim ->
                    val poseInGlobalFrame = MutableMat4d(prim.drawNode.modelMatD).mul(poseInPrimaryFrame)
                    val poseInParentFrame = nodeModel.drawNode.parent?.invModelMatD?.let { globalToParent ->
                        MutableMat4d(globalToParent).mul(poseInGlobalFrame)
                    } ?: poseInGlobalFrame
                    poseInParentFrame.decompose(dragPosition, dragRotation, dragScale)
                }
            }
        }
    }
}