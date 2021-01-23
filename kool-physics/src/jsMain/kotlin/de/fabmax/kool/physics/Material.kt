package de.fabmax.kool.physics

@Suppress("CanBeParameter")
actual class Material actual constructor(
    actual val staticFriction: Float,
    actual val dynamicFriction: Float,
    actual val restitution: Float) {

    val pxMaterial by lazy {
        Physics.checkIsLoaded()
        Physics.physics.createMaterial(staticFriction, dynamicFriction, restitution)
    }

}