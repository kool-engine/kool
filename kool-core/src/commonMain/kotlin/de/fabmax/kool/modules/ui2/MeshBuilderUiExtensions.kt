package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.toRad
import de.fabmax.kool.scene.geometry.MeshBuilder
import kotlin.math.cos
import kotlin.math.sin

fun MeshBuilder.arrow(centerX: Float, centerY: Float, size: Float, rotation: Float) {
    val si = size * 0.3f
    val so = size * 0.5f

    val m11 = cos((rotation - 45).toRad())
    val m12 = -sin((rotation - 45).toRad())
    val m21 = sin((rotation - 45).toRad())
    val m22 = m11

    val i1 = vertex {
        set(centerX + m11 * -so + m12 * so, centerY + m21 * -so + m22 * so, 0f)
    }
    val i2 = vertex {
        set(centerX + m11 * so + m12 * so, centerY + m21 * so + m22 * so, 0f)
    }
    val i3 = vertex {
        set(centerX + m11 * so + m12 * -so, centerY + m21 * so + m22 * -so, 0f)
    }
    val i4 = vertex {
        set(centerX + m11 * si + m12 * -so, centerY + m21 * si + m22 * -so, 0f)
    }
    val i5 = vertex {
        set(centerX + m11 * si + m12 * si, centerY + m21 * si + m22 * si, 0f)
    }
    val i6 = vertex {
        set(centerX + m11 * -so + m12 * si, centerY + m21 * -so + m22 * si, 0f)
    }

    addTriIndices(i1, i2, i6)
    addTriIndices(i6, i2, i5)
    addTriIndices(i5, i2, i3)
    addTriIndices(i5, i3, i4)
}
