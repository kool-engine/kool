package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.EditorScene

interface PhysicsComponent {

    fun getPhysicsWorldComponent(scene: EditorScene): PhysicsWorldComponent? =
        scene.getAllComponents<PhysicsWorldComponent>().firstOrNull()

    fun getPhysicsWorld(scene: EditorScene) = getPhysicsWorldComponent(scene)?.physicsWorld

    suspend fun getOrCreatePhysicsWorldComponent(scene: EditorScene): PhysicsWorldComponent {
        var physicsWorldComponent = getPhysicsWorldComponent(scene)
        if (physicsWorldComponent == null) {
            physicsWorldComponent = PhysicsWorldComponent(scene.sceneEntity)
            scene.sceneEntity.addComponentLifecycleAware(physicsWorldComponent)
        }
        return physicsWorldComponent
    }
}