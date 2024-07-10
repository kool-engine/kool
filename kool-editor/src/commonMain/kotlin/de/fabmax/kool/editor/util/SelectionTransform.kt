package de.fabmax.kool.editor.util

import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.globalToLocalD
import de.fabmax.kool.editor.components.localToGlobalD
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableQuatD
import de.fabmax.kool.math.MutableVec3d

class SelectionTransform(nodeModels: List<GameEntity>) {
    val selection: List<EntityTransformData>
    val primaryTransformNode: GameEntity?
        get() = selection.getOrNull(0)?.gameEntity

    init {
        val nodeModelsSet = nodeModels.toSet()
        selection = mutableListOf()
        nodeModelsSet
            .filter { it.hasNoParentIn(nodeModelsSet) }
            .forEach { selection += EntityTransformData(it) }
    }

    fun startTransform() {
        selection.forEach { it.captureStart() }
    }

    fun updateTransform() {
        selection.forEach { it.captureCurrent() }
    }

    fun applyTransform(isUndoable: Boolean) {
        val transformEntities = mutableListOf<GameEntity>()
        val undoTransforms = mutableListOf<TransformData>()
        val applyTransforms = mutableListOf<TransformData>()

        selection.forEach {
            transformEntities += it.gameEntity
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
        val action = transformEntities.mapIndexed { i, gameEntity ->
            val data = gameEntity.transform.data
            SetTransformAction(gameEntity.transform, data.copy(transform = undoTransforms[i]), data.copy(applyTransforms[i]))
        }.fused()

        if (isUndoable) {
            action.apply()
        } else {
            action.doAction()
        }
    }

    fun restoreInitialTransform() {
        selection.forEach { it.restoreInitial() }
    }

    private fun GameEntity.hasNoParentIn(nodeModels: Set<GameEntity>): Boolean {
        var parent = parent
        while (parent != null && parent.isSceneChild) {
            if (parent in nodeModels) {
                return false
            }
            parent = parent.parent
        }
        return true
    }

    inner class EntityTransformData(val gameEntity: GameEntity) {
        private val poseInPrimaryFrame = MutableMat4d()

        val startPosition = MutableVec3d()
        val startRotation = MutableQuatD()
        val startScale = MutableVec3d()

        val currentPosition = MutableVec3d()
        val currentRotation = MutableQuatD()
        val currentScale = MutableVec3d()

        fun captureStart() {
            gameEntity.transform.transform.decompose(startPosition, startRotation, startScale)

            poseInPrimaryFrame.setIdentity()
            primaryTransformNode?.let { prim ->
                poseInPrimaryFrame.set(prim.globalToLocalD).mul(gameEntity.localToGlobalD)
            }
        }

        fun captureCurrent() {
            if (gameEntity === primaryTransformNode) {
                gameEntity.transform.transform.decompose(currentPosition, currentRotation, currentScale)
            } else {
                primaryTransformNode?.let { prim ->
                    val poseInGlobalFrame = MutableMat4d(prim.localToGlobalD).mul(poseInPrimaryFrame)
                    val poseInParentFrame = gameEntity.parent?.globalToLocalD?.let { globalToParent ->
                        MutableMat4d(globalToParent).mul(poseInGlobalFrame)
                    } ?: poseInGlobalFrame
                    poseInParentFrame.decompose(currentPosition, currentRotation, currentScale)
                }
            }
        }

        fun restoreInitial() {
            val restoreData = gameEntity.transform.data.copy(
                transform = TransformData(
                    Vec3Data(startPosition),
                    Vec4Data(startRotation),
                    Vec3Data(startScale),
                )
            )
            gameEntity.transform.setPersistent(restoreData)
        }
    }
}