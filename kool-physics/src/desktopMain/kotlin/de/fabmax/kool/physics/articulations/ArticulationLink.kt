package de.fabmax.kool.physics.articulations

import de.fabmax.kool.physics.RigidBodyImpl
import physx.physics.PxArticulationLink

class ArticulationLinkImpl(
    override val holder: PxArticulationLink,
    parent: ArticulationLinkImpl?
) : RigidBodyImpl(), ArticulationLink {

    private val _children = mutableListOf<ArticulationLink>()
    override val children: List<ArticulationLink>
        get() = _children

    override val inboundJoint: ArticulationJoint? = if (parent != null) {
        holder.inboundJoint?.let { ArticulationJointImpl(it) }
    } else {
        null
    }

    init {
        parent?._children?.add(this)
    }
}