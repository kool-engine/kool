package de.fabmax.kool.physics.articulations

import de.fabmax.kool.physics.RigidBody

expect class ArticulationLink : RigidBody {

    val children: List<ArticulationLink>

    val inboundJoint: ArticulationJoint?

}