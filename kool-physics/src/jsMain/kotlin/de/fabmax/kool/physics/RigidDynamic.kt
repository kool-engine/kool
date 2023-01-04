package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import physx.PxRigidActor
import physx.PxRigidDynamic

actual open class RigidDynamic internal constructor(mass: Float, pose: Mat4f, pxActor: PxRigidActor?) : RigidBody() {

    actual constructor(mass: Float, pose: Mat4f) : this(mass, pose, null)

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    protected val pxRigidDynamic: PxRigidDynamic
        get() = pxRigidActor as PxRigidDynamic

    override val pxRigidActor: PxRigidActor

    init {
        if (pxActor == null) {
            MemoryStack.stackPush().use { mem ->
                val pxPose = pose.toPxTransform(mem.createPxTransform())
                pxRigidActor = Physics.physics.createRigidDynamic(pxPose)
                this.mass = mass
            }
        } else {
            pxRigidActor = pxActor
        }
        transform.set(pose)
    }

    actual fun wakeUp() {
        pxRigidDynamic.wakeUp()
    }

    actual fun putToSleep() {
        pxRigidDynamic.putToSleep()
    }
}