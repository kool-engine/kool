package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.logW

abstract class PhysicsNodeComponent<T: ComponentData>(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<T>
) : GameEntityDataComponent<T>(gameEntity, componentInfo) {

    val physicsWorldComponent: PhysicsWorldComponent?
        get() = gameEntity.scene.sceneEntity.getComponent<PhysicsWorldComponent>()
    val physicsWorld: PhysicsWorld?
        get() = physicsWorldComponent?.physicsWorld

    abstract val actorTransform: TrsTransformF?

    private val tmpMat4 = MutableMat4d()
    protected val scale = MutableVec3d(Vec3d.ONES)

    override suspend fun applyComponent() {
        super.applyComponent()
        gameEntity.transform.onTransformEdited += { setPhysicsTransformFromDrawNode() }
    }

    protected open fun updatePhysics(dt: Float) {
        actorTransform?.let {
            gameEntity.parent!!.drawNode.invModelMatD.mul(it.matrixD, tmpMat4)
            tmpMat4.scale(scale)
            gameEntity.drawNode.transform.setMatrix(tmpMat4)
        }
    }

    override fun onStart() {
        super.onStart()
        setPhysicsTransformFromDrawNode()
        physicsWorld?.let { world ->
            world.onPhysicsUpdate += this::updatePhysics
        }
    }

    suspend fun getOrCreatePhysicsWorldComponent(): PhysicsWorldComponent {
        val sceneEntity = gameEntity.scene.sceneEntity
        var physicsWorldComponent = sceneEntity.getComponent<PhysicsWorldComponent>()
        if (physicsWorldComponent == null) {
            logW { "Failed to find a PhysicsWorldComponent in parent scene, creating default one" }
            physicsWorldComponent = PhysicsWorldComponent(sceneEntity)

            // add component and explicitly create it, so that the physics world is immediately available
            sceneEntity.addComponent(physicsWorldComponent, autoCreateComponent = false)
            if (isApplied) {
                physicsWorldComponent.applyComponent()
            }
        }
        return physicsWorldComponent
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