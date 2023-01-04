package de.fabmax.kool.physics.articulations

import de.fabmax.kool.physics.RigidBody
import physx.PxArticulationLink
import physx.PxRigidActor
import physx.inboundJoint

actual class ArticulationLink(val pxLink: PxArticulationLink, val parent: ArticulationLink?) : RigidBody() {

    private val mutChildren = mutableListOf<ArticulationLink>()
    actual val children: List<ArticulationLink>
        get() = mutChildren

    actual val inboundJoint: ArticulationJoint? = if (parent != null) {
        pxLink.inboundJoint?.let { ArticulationJoint(it) }
    } else {
        null
    }

    override val pxRigidActor: PxRigidActor = pxLink

    init {
        parent?.mutChildren?.add(this)
    }

}