package de.fabmax.kool.physics

import physx.PxMaterial

actual class Material actual constructor (
    actual val staticFriction: Float,
    actual val dynamicFriction: Float,
    actual val restitution: Float) : Releasable {

    val pxMaterial: PxMaterial by lazy {
        Physics.checkIsLoaded()
        Physics.physics.createMaterial(staticFriction, dynamicFriction, restitution)
    }

    actual override fun release() {
        pxMaterial.release()
    }
}