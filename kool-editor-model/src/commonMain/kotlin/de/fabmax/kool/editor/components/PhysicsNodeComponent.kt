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

    override suspend fun createComponent() {
        super.createComponent()
        nodeModel.transform.onTransformEdited += { setPhysicsTransformFromDrawNode() }

        onUpdate {
            if (isStarted) {
                updatePhysics()
            }
        }
    }

    protected open fun updatePhysics() {
        actorTransform?.let {
            nodeModel.parent.drawNode.invModelMatD.mul(it.matrixD, tmpMat4)
            nodeModel.drawNode.transform.setMatrix(tmpMat4)
        }
    }

    override fun onStart() {
        super.onStart()
        setPhysicsTransformFromDrawNode()
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
            logW { "CharacterControllerComponent transform contains a scaling component, which may lead to unexpected behavior." }
        }
        applyPose(t, r)
    }

    protected abstract fun applyPose(position: Vec3d, rotation: QuatD)
}