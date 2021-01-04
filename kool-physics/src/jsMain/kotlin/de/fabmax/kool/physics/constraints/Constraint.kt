package de.fabmax.kool.physics.constraints

import ammo.btTypedConstraint

actual interface Constraint {

    val btConstraint: btTypedConstraint

}