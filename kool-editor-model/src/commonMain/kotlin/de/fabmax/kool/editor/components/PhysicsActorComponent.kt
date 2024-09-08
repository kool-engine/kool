package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.TransformComponentData
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.scene.set
import de.fabmax.kool.util.logW

abstract class PhysicsActorComponent<T: ComponentData>(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<T>
) :
    GameEntityDataComponent<T>(gameEntity, componentInfo),
    PhysicsComponent,
    TransformComponent.ListenerComponent
{
    protected val physicsWorldComponent: PhysicsWorldComponent? get() = getPhysicsWorldComponent(gameEntity.scene)
    protected val physicsWorld: PhysicsWorld? get() = physicsWorldComponent?.physicsWorld

    var isAttachedToSimulation = false
        internal set

    abstract val physicsActorTransform: TrsTransformF?
    val localActorTransform = TrsTransformF()

    private val tmpMat4 = MutableMat4d()
    protected val scale = MutableVec3d(Vec3d.ONES)

    protected var warnOnNonUniformScale = true

    override suspend fun applyComponent() {
        super.applyComponent()
        localActorTransform.set(gameEntity.transform.transform)
        gameEntity.transform.transform = localActorTransform
    }

    override fun onStart() {
        super.onStart()
        setPhysicsTransformFromDrawNode()
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        if (!isAttachedToSimulation) {
            return
        }

        val world = physicsWorldComponent ?: return
        val physicsTrs = physicsActorTransform ?: return
        val globalToParent = gameEntity.parent?.globalToLocalD ?: return

        tmpMat4
            .set(globalToParent)
            .mul(world.gameEntity.localToGlobalD)
            .mul(physicsTrs.matrixD)
        localActorTransform.setMatrix(tmpMat4).scale(scale)
    }

    override fun onTransformChanged(component: TransformComponent, transformData: TransformComponentData) {
        setPhysicsTransformFromDrawNode()
    }

    fun setPhysicsTransformFromDrawNode() {
        val world = physicsWorldComponent ?: return
        val t = MutableVec3d()
        val r = MutableQuatD()
        val s = MutableVec3d()
        val mat = MutableMat4d(world.gameEntity.globalToLocalD).mul(gameEntity.localToGlobalD)
        mat.decompose(t, r, s)

        if (warnOnNonUniformScale && !s.isFuzzyEqual(Vec3d.ONES, eps = 1e-3)) {
            logW { "${gameEntity.name} / ${this::class.simpleName}: transform contains a scaling component $s, which may lead to unexpected behavior." }
        }
        applyPose(t, r)
    }

    protected abstract fun applyPose(position: Vec3d, rotation: QuatD)
}