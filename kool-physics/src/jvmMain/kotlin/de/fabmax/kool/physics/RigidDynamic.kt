package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import org.lwjgl.system.MemoryStack
import physx.physics.PxRigidDynamic

actual open class RigidDynamic actual constructor(mass: Float, pose: Mat4f) : RigidBody() {

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