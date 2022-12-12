package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import org.lwjgl.system.MemoryStack
import physx.physics.PxRigidActor
import physx.physics.PxRigidDynamic

actual open class RigidDynamic internal constructor(mass: Float, pose: Mat4f, pxActor: PxRigidActor?) : RigidBody() {

    actual constructor(mass: Float, pose: Mat4f) : this(mass, pose, null)

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