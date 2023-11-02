package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.physics.PxArticulationFlagEnum
import physx.physics.PxArticulationReducedCoordinate
import physx.support.SupportFunctions

actual class Articulation actual constructor(isFixedBase: Boolean) : CommonArticulation(isFixedBase) {

    val pxArticulation: PxArticulationReducedCoordinate

    actual var minPositionIterations: Int
        get() = SupportFunctions.PxArticulationReducedCoordinate_getMinSolverPositionIterations(pxArticulation)
        set(value) {
            pxArticulation.setSolverIterationCounts(value, minVelocityIterations)
        }

    actual var minVelocityIterations: Int
        get() = SupportFunctions.PxArticulationReducedCoordinate_getMinSolverVelocityIterations(pxArticulation)
        set(value) {
            pxArticulation.setSolverIterationCounts(minPositionIterations, value)
        }

    init {
        Physics.checkIsLoaded()
        pxArticulation = Physics.physics.createArticulationReducedCoordinate()

        if (isFixedBase) {
            pxArticulation.setArticulationFlag(PxArticulationFlagEnum.eFIX_BASE, true)
        }
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

    actual override fun release() {
        pxArticulation.release()
    }
}