package de.fabmax.kool.physics2d

import box2dandroid.B2_Rot
import box2dandroid.b2Rot
import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.rad

internal actual object Box2dMath {
    private val rot = b2Rot()

    actual fun angleToRotation(angle: AngleF, result: MutableRotation) {
        B2_Rot.makeRot(angle.rad, rot)
        result.set(rot.s, rot.c)
    }

    actual fun rotationToAngle(rotation: Rotation): AngleF {
        rot.c = rotation.cos
        rot.s = rotation.sin
        return B2_Rot.getAngle(rot).rad
    }
}