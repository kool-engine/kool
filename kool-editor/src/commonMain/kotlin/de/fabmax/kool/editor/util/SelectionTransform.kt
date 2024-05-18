package de.fabmax.kool.editor.util

import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableQuatD
import de.fabmax.kool.math.MutableVec3d

class SelectionTransform(nodeModels: List<SceneNodeModel>) {
    val selection: List<NodeTransformData>
    val primaryTransformNode: SceneNodeModel?
        get() = selection.getOrNull(0)?.nodeModel

    init {
        val nodeModelsSet = nodeModels.toSet()
        selection = mutableListOf()
        nodeModelsSet
            .filter { it.hasNoParentIn(nodeModelsSet) }
            .forEach { selection += NodeTransformData(it) }
    }

    fun startTransform() {
        selection.forEach { it.captureStart() }
    }

    fun updateTransform() {
        selection.forEach { it.captureCurrent() }
    }

    fun applyTransform(isUndoable: Boolean) {
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
                Vec3Data(it.currentPosition),
                Vec4Data(it.currentRotation),
                Vec3Data(it.currentScale)
            )
        }
        val action = SetTransformAction(transformNodes, undoTransforms, applyTransforms)
        if (isUndoable) {
            action.apply()
        } else {
            action.doAction()
        }
    }

    fun restoreInitialTransform() {
        selection.forEach { it.restoreInitial() }
    }

    private fun SceneNodeModel.hasNoParentIn(nodeModels: Set<SceneNodeModel>): Boolean {
        var parent = parent as? SceneNodeModel
        while (parent != null) {
            if (parent in nodeModels) {
                return false
            }
            parent = parent.parent as? SceneNodeModel
        }
        return true
    }

    inner class NodeTransformData(val nodeModel: SceneNodeModel) {
        private val poseInPrimaryFrame = MutableMat4d()

        val startPosition = MutableVec3d()
        val startRotation = MutableQuatD()
        val startScale = MutableVec3d()

        val currentPosition = MutableVec3d()
        val currentRotation = MutableQuatD()
        val currentScale = MutableVec3d()

        fun captureStart() {
            nodeModel.drawNode.transform.decompose(startPosition, startRotation, startScale)

            poseInPrimaryFrame.setIdentity()
            primaryTransformNode?.let { prim ->
                poseInPrimaryFrame.set(prim.drawNode.invModelMatD).mul(nodeModel.drawNode.modelMatD)
            }
        }

        fun captureCurrent() {
            if (nodeModel === primaryTransformNode) {
                nodeModel.drawNode.transform.decompose(currentPosition, currentRotation, currentScale)
            } else {
                primaryTransformNode?.let { prim ->
                    val poseInGlobalFrame = MutableMat4d(prim.drawNode.modelMatD).mul(poseInPrimaryFrame)
                    val poseInParentFrame = nodeModel.drawNode.parent?.invModelMatD?.let { globalToParent ->
                        MutableMat4d(globalToParent).mul(poseInGlobalFrame)
                    } ?: poseInGlobalFrame
                    poseInParentFrame.decompose(currentPosition, currentRotation, currentScale)
                }
            }
        }

        fun restoreInitial() {
            nodeModel.transform.transformState.set(
                TransformData(
                    Vec3Data(startPosition),
                    Vec4Data(startRotation),
                    Vec3Data(startScale),
                )
            )
        }
    }
}