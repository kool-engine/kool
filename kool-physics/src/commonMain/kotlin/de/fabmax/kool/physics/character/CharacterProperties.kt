package de.fabmax.kool.physics.character

import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics

class CharacterProperties {

    var position = Vec3d(0.0, 1.0, 0.0)
    var upDirection = Vec3f.Y_AXIS

    var material = Physics.defaultMaterial

    /**
     * Capsule shape radius.
     */
    var radius = 0.3f

    /**
     * Capsule shape base height. Full shape height will be height + 2 * radius.
     */
    var height = 1.2f
}