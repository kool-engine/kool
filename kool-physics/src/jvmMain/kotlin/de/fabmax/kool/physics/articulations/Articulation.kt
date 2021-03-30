package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.physics.PxArticulation

actual class Articulation : CommonArticulation() {

    val pxArticulation: PxArticulation

    init {
        Physics.checkIsLoaded()
        pxArticulation = Physics.physics.createArticulation()
    }

    actual fun createLink(parent: ArticulationLink?, pose: Mat4f): ArticulationLink {
        return MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            val pxLink = pxArticulation.createLink(parent?.pxLink, pxPose)
            val link = ArticulationLink(pxLink, parent)
            mutLinks += link
            link
        }
    }

    actual fun wakeUp() {
        pxArticulation.wakeUp()
    }

    actual fun putToSleep() {
        pxArticulation.putToSleep()
    }

    override fun release() {
        pxArticulation.release()
    }
}