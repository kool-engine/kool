package de.fabmax.kool.physics.joints

import com.bulletphysics.dynamics.constraintsolver.TypedConstraint

actual interface Joint {

    val btConstraint: TypedConstraint

}