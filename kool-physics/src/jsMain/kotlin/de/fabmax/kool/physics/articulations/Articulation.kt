package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.MemoryStack
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.SupportFunctions
import de.fabmax.kool.physics.toPxTransform
import physx.PxArticulationFlagEnum
import physx.PxArticulationLink
import physx.PxArticulationReducedCoordinate

actual fun Articulation(isFixedBase: Boolean): Articulation = ArticulationImpl(isFixedBase)

class ArticulationImpl(val isFixedBase: Boolean) : Articulation {
    val pxArticulation: PxArticulationReducedCoordinate

    private val _links = mutableListOf<ArticulationLink>()
    override val links: List<ArticulationLink>
        get() = _links

    override val onFixedUpdate = mutableListOf<(Float) -> Unit>()

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

    override fun createLink(parent: ArticulationLink?, pose: Mat4f): ArticulationLink {
        parent as ArticulationLinkImpl?
        return MemoryStack.stackPush().use { mem ->
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val parentLink = parent?.holder?.px as PxArticulationLink?
            val pxPose = pose.toPxTransform(mem.createPxTransform())
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

    override fun release() {
        pxArticulation.release()
    }
}