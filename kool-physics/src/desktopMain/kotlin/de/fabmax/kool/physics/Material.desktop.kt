package de.fabmax.kool.physics

import de.fabmax.kool.util.BaseReleasable
import physx.physics.PxMaterial

actual fun Material(staticFriction: Float, dynamicFriction: Float, restitution: Float): Material {
    return MaterialImpl(staticFriction, dynamicFriction, restitution)
}

val Material.pxMaterial: PxMaterial get() = (this as MaterialImpl).pxMaterial

class MaterialImpl(
    staticFriction: Float,
    dynamicFriction: Float,
    restitution: Float
) : BaseReleasable(), Material {

    val pxMaterial: PxMaterial by lazy {
        PhysicsImpl.checkIsLoaded()
        PhysicsImpl.physics.createMaterial(staticFriction, dynamicFriction, restitution)
    }

    override var staticFriction
        get() = pxMaterial.staticFriction
        set(value) { pxMaterial.staticFriction = value }

    override var dynamicFriction
        get() = pxMaterial.dynamicFriction
        set(value) { pxMaterial.dynamicFriction = value }

    override var restitution
        get() = pxMaterial.restitution
        set(value) { pxMaterial.restitution = value }

    override fun release() {
        pxMaterial.release()
        super.release()
    }
}