package de.fabmax.kool.editor.components

import de.fabmax.kool.physics.PhysicsWorld

interface PhysicsComponent {
    context(GameEntityComponent)
    val physicsWorldComponent: PhysicsWorldComponent?
        get() = gameEntity.scene.sceneEntity.getComponent<PhysicsWorldComponent>()

    context(GameEntityComponent)
    val physicsWorld: PhysicsWorld?
        get() = physicsWorldComponent?.physicsWorld

    context(GameEntityComponent)
    suspend fun getOrCreatePhysicsWorldComponent(): PhysicsWorldComponent {
        val sceneEntity = gameEntity.scene.sceneEntity
        val physicsWorldComponent = sceneEntity.getOrPutComponentLifecycleAware<PhysicsWorldComponent> {
            PhysicsWorldComponent(sceneEntity)
        }
        return physicsWorldComponent
    }
}