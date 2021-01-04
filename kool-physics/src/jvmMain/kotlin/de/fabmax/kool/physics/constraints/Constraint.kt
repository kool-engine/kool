package de.fabmax.kool.physics.constraints

import com.bulletphysics.dynamics.constraintsolver.TypedConstraint

actual interface Constraint {

    val btConstraint: TypedConstraint

}