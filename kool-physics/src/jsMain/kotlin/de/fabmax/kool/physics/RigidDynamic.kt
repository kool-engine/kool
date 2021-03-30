package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import physx.PxRigidDynamic

actual open class RigidDynamic actual constructor(mass: Float, pose: Mat4f) : RigidBody() {

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    protected val pxRigidDynamic: PxRigidDynamic
        get() = pxRigidActor as PxRigidDynamic

    init {
        MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            pxRigidActor = Physics.physics.createRigidDynamic(pxPose)
            this.mass = mass
        }
    }

    actual fun wakeUp() {
        pxRigidDynamic.wakeUp()
    }

    actual fun putToSleep() {
        pxRigidDynamic.putToSleep()
    }
}