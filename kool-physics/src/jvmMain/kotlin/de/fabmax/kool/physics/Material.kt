package de.fabmax.kool.physics

import physx.physics.PxMaterial

actual fun Material(staticFriction: Float, dynamicFriction: Float, restitution: Float): Material {
    return MaterialImpl(staticFriction, dynamicFriction, restitution)
}

val Material.pxMaterial: PxMaterial get() = (this as MaterialImpl).pxMaterial

class MaterialImpl(
    override val staticFriction: Float,
    override val dynamicFriction: Float,
    override val restitution: Float
) : Material {

    val pxMaterial: PxMaterial by lazy {
        PhysicsImpl.checkIsLoaded()
        PhysicsImpl.physics.createMaterial(staticFriction, dynamicFriction, restitution)
    }

    override fun release() {
        pxMaterial.release()
    }
}