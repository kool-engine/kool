package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedEntityComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SceneComponentData
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Transform
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.SyncedMatrixFd

class TransformComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<TransformComponentData> = ComponentInfo(TransformComponentData())
) : GameEntityDataComponent<TransformComponentData>(gameEntity, componentInfo) {

    private val changeListeners by cachedEntityComponents<ListenerComponent>()
    val onUpdateTransformEarly = BufferedList<() -> Unit>()
    val onUpdateTransformLate = BufferedList<() -> Unit>()

    val globalTransform = SyncedMatrixFd()
    val viewTransform = SyncedMatrixFd()
    var transform: Transform = createTransform()
        set(value) {
            field = value
            updateTransform()
            fireTransformChanged(data)
        }

    init {
        componentOrder = COMPONENT_ORDER_EARLY
        data.transform.toTransform(transform)
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        updateTransform()
    }

    fun updateDataFromTransform() {
        dataState.set(data.copy(transform = TransformData(transform)))
    }

    override fun onDataChanged(oldData: TransformComponentData, newData: TransformComponentData) {
        super.onDataChanged(oldData, newData)
        newData.transform.toTransform(transform)
        updateTransform()
        fireTransformChanged(newData)
    }

    override fun onUpdate(ev: RenderPass.UpdateEvent) {
        updateTransform()
    }

    fun updateTransform() {
        onUpdateTransformEarly.update()
        for (i in onUpdateTransformEarly.indices) {
            onUpdateTransformEarly[i].invoke()
        }

        val parentModelMat = gameEntity.parent?.localToGlobalD ?: Mat4d.IDENTITY
        globalTransform.setMatD { parentModelMat.mul(transform.matrixD, it) }
        viewTransform.setMatD { gameEntity.scene.sceneOrigin.matrixD.mul(globalTransform.matD, it) }

        onUpdateTransformLate.update()
        for (i in onUpdateTransformLate.indices) {
            onUpdateTransformLate[i].invoke()
        }
    }

    fun updateTransformRecursive() {
        updateTransform()
        for (i in gameEntity.children.indices) {
            gameEntity.children[i].transform.updateTransformRecursive()
        }
    }

    private fun fireTransformChanged(data: TransformComponentData) {
        changeListeners.let { listeners ->
            for (i in listeners.indices) {
                listeners[i].onTransformChanged(this, data)
            }
        }
    }

    private fun createTransform(): Transform {
        val isDoublePrecision = if (gameEntity.isSceneChild) {
            sceneComponent.isDoublePrecision
        } else {
            gameEntity.entityData.components
                .map { it.data }.filterIsInstance<SceneComponentData>().firstOrNull()?.isFloatingOrigin == true
        }
        return if (isDoublePrecision) TrsTransformD() else TrsTransformF()
    }

    fun interface ListenerComponent {
        fun onTransformChanged(component: TransformComponent, transformData: TransformComponentData)
    }
}

val GameEntity.localToGlobalF: Mat4f get() = transform.globalTransform.matF
val GameEntity.localToGlobalD: Mat4d get() = transform.globalTransform.matD
val GameEntity.globalToLocalF: Mat4f get() = transform.globalTransform.invF
val GameEntity.globalToLocalD: Mat4d get() = transform.globalTransform.invD

fun GameEntity.toGlobalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f = localToGlobalF.transform(vec, w)
fun GameEntity.toGlobalCoords(vec: MutableVec3d, w: Double = 1.0): MutableVec3d = localToGlobalD.transform(vec, w)
fun GameEntity.toLocalCoords(vec: MutableVec3f, w: Float = 1f): MutableVec3f = globalToLocalF.transform(vec, w)
fun GameEntity.toLocalCoords(vec: MutableVec3d, w: Double = 1.0): MutableVec3d = globalToLocalD.transform(vec, w)

val GameEntity.localToViewF: Mat4f get() = transform.viewTransform.matF
val GameEntity.localToViewD: Mat4d get() = transform.viewTransform.matD
val GameEntity.viewToLocalF: Mat4f get() = transform.viewTransform.invF
val GameEntity.viewToLocalD: Mat4d get() = transform.viewTransform.invD
