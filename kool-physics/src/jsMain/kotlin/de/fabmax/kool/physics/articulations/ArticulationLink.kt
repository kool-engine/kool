package de.fabmax.kool.physics.articulations

import de.fabmax.kool.physics.RigidActorHolder
import de.fabmax.kool.physics.RigidBodyImpl
import physx.PxArticulationLink
import physx.inboundJoint

class ArticulationLinkImpl(
    link: PxArticulationLink,
    parent: ArticulationLinkImpl?
) : RigidBodyImpl(), ArticulationLink {

    private val _children = mutableListOf<ArticulationLink>()
    override val children: List<ArticulationLink>
        get() = _children

    override val inboundJoint: ArticulationJoint? = if (parent != null) {
        link.inboundJoint?.let { ArticulationJointImpl(it) }
    } else {
        null
    }

    override val holder = RigidActorHolder(link)

    init {
        parent?._children?.add(this)
    }

}