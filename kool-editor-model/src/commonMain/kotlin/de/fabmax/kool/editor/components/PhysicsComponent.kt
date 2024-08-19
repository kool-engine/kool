package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.EditorScene

interface PhysicsComponent {

    fun getPhysicsWorldComponent(scene: EditorScene): PhysicsWorldComponent? =
        scene.sceneEntity.getComponent<PhysicsWorldComponent>()

    fun getPhysicsWorld(scene: EditorScene) = getPhysicsWorldComponent(scene)?.physicsWorld

    suspend fun getOrCreatePhysicsWorldComponent(scene: EditorScene): PhysicsWorldComponent {
        val physicsWorldComponent = scene.sceneEntity.getOrPutComponentLifecycleAware<PhysicsWorldComponent> {
            PhysicsWorldComponent(scene.sceneEntity)
        }
        return physicsWorldComponent
    }
}