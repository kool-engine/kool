package de.fabmax.kool.scene

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor

/**
 * @author fabmax
 */
class Light {

    val direction = MutableVec3f(1f, 1f, 1f)

    val color = MutableColor(Color.WHITE)

}
