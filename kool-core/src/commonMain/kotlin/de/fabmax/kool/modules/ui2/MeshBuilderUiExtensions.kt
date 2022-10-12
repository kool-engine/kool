package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.toRad
import de.fabmax.kool.scene.geometry.MeshBuilder
import kotlin.math.cos
import kotlin.math.sin

fun MeshBuilder.arrow(centerX: Float, centerY: Float, size: Float, rotation: Float) {
    val si = size * 0.3f * 0.7f
    val so = size * 0.5f * 0.7f
    val off = size * 0.15f * 0.7f

    val m11 = cos((rotation - 45).toRad())
    val m12 = -sin((rotation - 45).toRad())
    val m21 = sin((rotation - 45).toRad())
    val m22 = m11

    val x1 = -so - off;   val y1 = so - off
    val x2 = so - off;    val y2 = so - off
    val x3 = so - off;    val y3 = -so - off
    val x4 = si - off;    val y4 = -so - off
    val x5 = si - off;    val y5 = si - off
    val x6 = -so - off;   val y6 = si - off

    val i1 = vertex {
        set(centerX + m11 * x1 + m12 * y1, centerY + m21 * x1 + m22 * y1, 0f)
    }
    val i2 = vertex {
        set(centerX + m11 * x2 + m12 * y2, centerY + m21 * x2 + m22 * y2, 0f)
    }
    val i3 = vertex {
        set(centerX + m11 * x3 + m12 * y3, centerY + m21 * x3 + m22 * y3, 0f)
    }
    val i4 = vertex {
        set(centerX + m11 * x4 + m12 * y4, centerY + m21 * x4 + m22 * y4, 0f)
    }
    val i5 = vertex {
        set(centerX + m11 * x5 + m12 * y5, centerY + m21 * x5 + m22 * y5, 0f)
    }
    val i6 = vertex {
        set(centerX + m11 * x6 + m12 * y6, centerY + m21 * x6 + m22 * y6, 0f)
    }

    addTriIndices(i1, i2, i6)
    addTriIndices(i6, i2, i5)
    addTriIndices(i5, i2, i3)
    addTriIndices(i5, i3, i4)
}
