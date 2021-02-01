package de.fabmax.kool.physics

import physx.physics.PxMaterial

actual class Material actual constructor(
    actual val staticFriction: Float,
    actual val dynamicFriction: Float,
    actual val restitution: Float) {

    val pxMaterial: PxMaterial by lazy {
        Physics.physics.createMaterial(staticFriction, dynamicFriction, restitution)
    }

    actual fun release() {
        pxMaterial.release()
    }
}