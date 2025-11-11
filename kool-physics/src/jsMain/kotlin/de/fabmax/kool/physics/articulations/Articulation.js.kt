@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.SupportFunctions
import de.fabmax.kool.physics.memStack
import de.fabmax.kool.physics.toPxTransform
import physx.PxArticulationFlagEnum
import physx.PxArticulationLink
import physx.PxArticulationReducedCoordinate

actual fun Articulation(isFixedBase: Boolean): Articulation = ArticulationImpl(isFixedBase)

class ArticulationImpl(val isFixedBase: Boolean) : Articulation() {
    internal val pxArticulation: PxArticulationReducedCoordinate

    override var minPositionIterations: Int
        get() = SupportFunctions.PxArticulationReducedCoordinate_getMinSolverPositionIterations(pxArticulation)
        set(value) {
            pxArticulation.setSolverIterationCounts(value, minVelocityIterations)
        }

    override var minVelocityIterations: Int
        get() = SupportFunctions.PxArticulationReducedCoordinate_getMinSolverVelocityIterations(pxArticulation)
        set(value) {
            pxArticulation.setSolverIterationCounts(minPositionIterations, value)
        }

    init {
        PhysicsImpl.checkIsLoaded()
        pxArticulation = PhysicsImpl.physics.createArticulationReducedCoordinate()

        if (isFixedBase) {
            pxArticulation.setArticulationFlag(PxArticulationFlagEnum.eFIX_BASE, true)
        }
    }

    override fun createLink(parent: ArticulationLink?, pose: PoseF): ArticulationLink {
        parent as ArticulationLinkImpl?
        return memStack {
            val parentLink = parent?.holder?.px as PxArticulationLink?
            val pxPose = pose.toPxTransform(createPxTransform())
            val pxLink = pxArticulation.createLink(parentLink, pxPose)
            val link = ArticulationLinkImpl(pxLink, parent)
            _links += link
            link
        }
    }

    override fun wakeUp() {
        pxArticulation.wakeUp()
    }

    override fun putToSleep() {
        pxArticulation.putToSleep()
    }

    override fun doRelease() {
        pxArticulation.release()
    }
}