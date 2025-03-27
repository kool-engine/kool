package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxD
import de.fabmax.kool.scene.OrbitInputTransform
import kotlin.math.max

class EditorCamTransform(val editor: KoolEditor) : OrbitInputTransform("Editor cam transform") {

    private var panTarget: Vec3d? = null

    init {
        minZoom = 1.0
        maxZoom = 10000.0
        zoom = 50.0
        smoothingDecay = 20.0
        setRotation(20f, -30f)
        InputStack.defaultInputHandler.pointerListeners += this

        middleDragMethod = DragMethod.ROTATE
        isInfiniteDragCursor = true

        onUpdate {
            if (isVisible) {
                panTarget?.let { animatePan(it) }

                editor.activeScene.value?.let { scene ->
                    val isFloatingOrigin = scene.sceneComponent.isFloatingOrigin
                    if (isFloatingOrigin) {
                        isApplyTranslation = false
                        scene.sceneOrigin.translation.set(globalTranslation).mul(-1.0)
                        scene.sceneOrigin.markDirty()
                    } else {
                        isApplyTranslation = true
                        scene.sceneOrigin.translation.set(Vec3d.ZERO)
                        scene.sceneOrigin.markDirty()
                    }
                }
            }
        }
    }

    private fun animatePan(target: Vec3d) {
        val diff = MutableVec3d(target).subtract(translation)
        if (diff.length() < 0.001) {
            // target reached
            setTranslation(target.x, target.y, target.z)
            panTarget = null

        } else {
            translation.expDecay(target, smoothingDecay)
        }
    }

    fun focusSelectedObject() = focusObjects(editor.selectionOverlay.getSelectedSceneEntities())

    fun focusObject(gameEntity: GameEntity) = focusObjects(listOf(gameEntity))

    fun focusObjects(gameEntities: List<GameEntity>) {
        val flattenedEntities = mutableListOf<GameEntity>()
        gameEntities.forEach { it.collectChildren(flattenedEntities) }

        val localToGlobal = MutableMat4d()
        val bounds = BoundingBoxD()
        flattenedEntities.forEach { gameEntity ->
            val c: Vec3d
            val r: Double
            val sceneNode = gameEntity.getComponent<MeshComponent>()?.sceneNode
            computeLocalToGlobal(gameEntity, localToGlobal.setIdentity())
            if (sceneNode == null) {
                c = localToGlobal.getTranslation()
                r = 1.0
            } else {
                c = localToGlobal.transform(sceneNode.bounds.center.toVec3d(), 1.0, MutableVec3d())
                r = localToGlobal.transform(sceneNode.bounds.size.toVec3d(), 0.0, MutableVec3d()).length() * 0.5f
            }
            bounds.add(c, max(r, 1.0))
        }

        if (bounds.isNotEmpty) {
            val target = MutableVec3d(bounds.center)
            parent?.toLocalCoords(target)
            panTarget = target
            zoom = bounds.size.length() * 0.7
        }
    }

    private fun computeLocalToGlobal(entity: GameEntity, result: MutableMat4d) {
        entity.parent?.let { computeLocalToGlobal(it, result) }
        result.mul(entity.transform.transform.matrixD)
    }

    private fun GameEntity.collectChildren(result: MutableList<GameEntity>) {
        result += this
        children.forEach { it.collectChildren(result) }
    }

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (pointerState.primaryPointer.isAnyButtonDown) {
            // stop any ongoing animated pan
            panTarget = null
        }
        super.handlePointer(pointerState, ctx)
    }
}