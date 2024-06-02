package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.logW

abstract class PhysicsNodeComponent(nodeModel: SceneNodeModel) : SceneNodeComponent(nodeModel) {

    val physicsWorldComponent: PhysicsWorldComponent?
        get() = nodeModel.sceneModel.getComponent<PhysicsWorldComponent>()
    val physicsWorld: PhysicsWorld?
        get() = physicsWorldComponent?.physicsWorld

    abstract val actorTransform: TrsTransformF?

    private val tmpMat4 = MutableMat4d()
    protected val scale = MutableVec3d(Vec3d.ONES)

    override suspend fun createComponent() {
        super.createComponent()
        nodeModel.transform.onTransformEdited += { setPhysicsTransformFromDrawNode() }
    }

    protected open fun updatePhysics(dt: Float) {
        actorTransform?.let {
            nodeModel.parent.drawNode.invModelMatD.mul(it.matrixD, tmpMat4)
            tmpMat4.scale(scale)
            nodeModel.drawNode.transform.setMatrix(tmpMat4)
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
        var physicsWorldComponent = nodeModel.sceneModel.getComponent<PhysicsWorldComponent>()
        if (physicsWorldComponent == null) {
            logW { "Failed to find a PhysicsWorldComponent in parent scene, creating default one" }
            physicsWorldComponent = PhysicsWorldComponent(nodeModel.sceneModel)

            // add component and explicitly create it, so that the physics world is immediately available
            nodeModel.sceneModel.addComponent(physicsWorldComponent, autoCreateComponent = false)
            if (isCreated) {
                physicsWorldComponent.createComponent()
            }
        }
        return physicsWorldComponent
    }

    fun setPhysicsTransformFromDrawNode() {
        val t = MutableVec3d()
        val r = MutableQuatD()
        val s = MutableVec3d()
        nodeModel.drawNode.modelMatD.decompose(t, r, s)

        if (!s.isFuzzyEqual(Vec3d.ONES, eps = 1e-3)) {
            logW { "${nodeModel.name} / ${this::class.simpleName}: transform contains a scaling component $s, which may lead to unexpected behavior." }
        }
        applyPose(t, r)
    }

    protected abstract fun applyPose(position: Vec3d, rotation: QuatD)
}