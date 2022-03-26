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
    var radius = 0.2f

    /**
     * Capsule shape base height. Full shape height will be height + 2 * [radius]. Effective collision height also
     * includes the [contactOffset]. Hence, a height of 1.2, radius of 0.2 and contactOffset of 0.1 results in an
     * effective height of 1.8
     */
    var height = 1.2f

    /**
     * Character contact offset. Don't change this value unless you know what you are doing.
     */
    var contactOffset = 0.1f
}