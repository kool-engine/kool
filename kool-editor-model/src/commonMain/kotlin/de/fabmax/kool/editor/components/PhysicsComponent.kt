package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.logW

abstract class PhysicsComponent<T: ComponentData>(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<T>
) :
    GameEntityDataComponent<T>(gameEntity, componentInfo),
    TransformComponent.ListenerComponent
{

    val physicsWorldComponent: PhysicsWorldComponent?
        get() = gameEntity.scene.sceneEntity.getComponent<PhysicsWorldComponent>()
    val physicsWorld: PhysicsWorld?
        get() = physicsWorldComponent?.physicsWorld

    abstract val actorTransform: TrsTransformF?

    private var needsTransformUpdate = true
    private val tmpMat4 = MutableMat4d()
    protected val scale = MutableVec3d(Vec3d.ONES)

    override fun onStart() {
        super.onStart()
        setPhysicsTransformFromDrawNode()

        if (gameEntity.parent == sceneEntity) {
            needsTransformUpdate = false
            actorTransform?.let { gameEntity.transform.transform = it }
        }
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        if (needsTransformUpdate) {
            actorTransform?.let {
                gameEntity.parent!!.drawNode.invModelMatD.mul(it.matrixD, tmpMat4)
                tmpMat4.scale(scale)
                gameEntity.drawNode.transform.setMatrix(tmpMat4)
            }
        }
    }

    suspend fun getOrCreatePhysicsWorldComponent(): PhysicsWorldComponent {
        val sceneEntity = gameEntity.scene.sceneEntity
        val physicsWorldComponent = sceneEntity.getOrPutComponentLifecycleAware<PhysicsWorldComponent> {
            PhysicsWorldComponent(sceneEntity)
        }
        return physicsWorldComponent
    }

    override fun onTransformChanged(component: TransformComponent, transformData: TransformComponentData) {
        setPhysicsTransformFromDrawNode()
    }

    fun setPhysicsTransformFromDrawNode() {
        val t = MutableVec3d()
        val r = MutableQuatD()
        val s = MutableVec3d()
        gameEntity.drawNode.modelMatD.decompose(t, r, s)

        if (!s.isFuzzyEqual(Vec3d.ONES, eps = 1e-3)) {
            logW { "${gameEntity.name} / ${this::class.simpleName}: transform contains a scaling component $s, which may lead to unexpected behavior." }
        }
        applyPose(t, r)
    }

    protected abstract fun applyPose(position: Vec3d, rotation: QuatD)
}