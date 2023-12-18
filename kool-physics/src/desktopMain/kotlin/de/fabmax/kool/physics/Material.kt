package de.fabmax.kool.physics

import de.fabmax.kool.util.BaseReleasable
import physx.physics.PxMaterial

actual fun Material(staticFriction: Float, dynamicFriction: Float, restitution: Float): Material {
    return MaterialImpl(staticFriction, dynamicFriction, restitution)
}

val Material.pxMaterial: PxMaterial get() = (this as MaterialImpl).pxMaterial

class MaterialImpl(
    override val staticFriction: Float,
    override val dynamicFriction: Float,
    override val restitution: Float
) : BaseReleasable(), Material {

    val pxMaterial: PxMaterial by lazy {
        PhysicsImpl.checkIsLoaded()
        PhysicsImpl.physics.createMaterial(staticFriction, dynamicFriction, restitution)
    }

    override fun release() {
        pxMaterial.release()
        super.release()
    }
}