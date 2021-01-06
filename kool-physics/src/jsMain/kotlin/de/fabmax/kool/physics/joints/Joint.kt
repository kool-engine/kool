package de.fabmax.kool.physics.joints

import ammo.btTypedConstraint

actual interface Joint {

    val btConstraint: btTypedConstraint

}