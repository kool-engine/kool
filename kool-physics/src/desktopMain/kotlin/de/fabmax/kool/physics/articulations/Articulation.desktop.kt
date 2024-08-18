package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.physics.PxArticulationFlagEnum
import physx.physics.PxArticulationReducedCoordinate
import physx.support.SupportFunctions

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
        return MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            val pxLink = pxArticulation.createLink(parent?.holder, pxPose)
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

    override fun release() {
        super.release()
        pxArticulation.release()
    }
}