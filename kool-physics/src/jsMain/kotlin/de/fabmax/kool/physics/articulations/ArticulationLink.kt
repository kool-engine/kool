package de.fabmax.kool.physics.articulations

import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBody
import physx.PxArticulationLink
import physx.inboundJoint

actual class ArticulationLink(val pxLink: PxArticulationLink, val parent: ArticulationLink?) : RigidBody() {

    private val mutChildren = mutableListOf<ArticulationLink>()
    actual val children: List<ArticulationLink>
        get() = mutChildren

    actual val inboundJoint: ArticulationJoint? = if (parent != null) {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        ArticulationJoint(Physics.TypeHelpers.articulationBaseJointToJoint(pxLink.inboundJoint!!))
    } else {
        null
    }

    init {
        pxRigidActor = pxLink
        parent?.mutChildren?.add(this)
    }
}