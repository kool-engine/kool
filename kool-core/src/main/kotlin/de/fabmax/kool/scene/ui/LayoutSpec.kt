package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */

class LayoutSpec {

    var width = uns(0f)
    var height = uns(0f)
    var depth = uns(0f)

    var x = uns(0f)
    var y = uns(0f)
    var z = uns(0f)

    fun setOrigin(x: SizeSpec, y: SizeSpec, z: SizeSpec) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun setSize(width: SizeSpec, height: SizeSpec, depth: SizeSpec) {
        this.width = width
        this.height = height
        this.depth = depth
    }

}

enum class SizeUnit {
    UN,
    DP,
    MM,
    PC
}

fun zero() = SizeSpec(0f, SizeUnit.UN)
fun uns(value: Float, roundToUnit: Boolean = false) = SizeSpec(value, SizeUnit.UN, roundToUnit)
fun dps(value: Float, roundToUnit: Boolean = false) = SizeSpec(value, SizeUnit.DP, roundToUnit)
fun mms(value: Float, roundToUnit: Boolean = false) = SizeSpec(value, SizeUnit.MM, roundToUnit)
fun pcs(value: Float, roundToUnit: Boolean = false) = SizeSpec(value, SizeUnit.PC, roundToUnit)

fun pc(pc: Float, size: Float) = size * pc / 100f
fun dp(dp: Float, dpi: Float) = dp * dpi / 96f
fun mm(mm: Float, dpi: Float) = mm * dpi / 25.4f

fun UiComponent.pcW(pc: Float) = pc(pc, this.width)
fun UiComponent.pcH(pc: Float) = pc(pc, this.height)
fun UiComponent.dp(pc: Float) = dp(pc, this.dpi)
fun UiComponent.mm(pc: Float) = mm(pc, this.dpi)

fun pcR(pc: Float, size: Float) = Math.round(size * pc / 100f).toFloat()
fun dpR(dp: Float, dpi: Float) = Math.round(dp * dpi / 96f).toFloat()
fun mmR(mm: Float, dpi: Float) = Math.round(mm * dpi / 25.4f).toFloat()

fun UiComponent.pcWR(pc: Float) = pcR(pc, this.width)
fun UiComponent.pcHR(pc: Float) = pcR(pc, this.height)
fun UiComponent.dpR(pc: Float) = dpR(pc, this.dpi)
fun UiComponent.mmR(pc: Float) = mmR(pc, this.dpi)

data class SizeSpec(val value: Float, val unit: SizeUnit, val roundToUnit: Boolean = false) {
    fun toUnits(size: Float, dpi: Float): Float {
        if (roundToUnit) {
            return when (unit) {
                SizeUnit.UN -> Math.round(value).toFloat()
                SizeUnit.DP -> dpR(value, dpi)
                SizeUnit.MM -> mmR(value, dpi)
                SizeUnit.PC -> pcR(value, size)
            }
        } else {
            return when (unit) {
                SizeUnit.UN -> value
                SizeUnit.DP -> dp(value, dpi)
                SizeUnit.MM -> mm(value, dpi)
                SizeUnit.PC -> pc(value, size)
            }
        }
    }
}

data class Margin(var top: SizeSpec, var bottom: SizeSpec, var left: SizeSpec, var right: SizeSpec)

data class Gravity(val xAlignment: Alignment, val yAlignment: Alignment)

enum class Alignment {
    START,
    CENTER,
    END
}
