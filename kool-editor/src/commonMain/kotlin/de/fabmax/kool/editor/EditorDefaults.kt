package de.fabmax.kool.editor

import de.fabmax.kool.math.QuatD
import de.fabmax.kool.math.Vec3d

object EditorDefaults {

    val DEFAULT_LIGHT_POSITION: Vec3d = Vec3d(5.0, 5.0, 5.0)
    val DEFAULT_LIGHT_ROTATION: QuatD = QuatD(0.224144, 0.129410, -0.836516, 0.482963).normed()

}