package de.fabmax.kool.scene.ui

import kotlin.math.round

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

// _R variants of size functions round to units (can prevent blurry text in UIs)
fun pcR(pc: Float, size: Float) = round(size * pc / 100f)
fun dpR(dp: Float, dpi: Float) = round(dp * dpi / 96f)
fun mmR(mm: Float, dpi: Float) = round(mm * dpi / 25.4f)

fun UiComponent.pcWR(pc: Float) = pcR(pc, this.width)
fun UiComponent.pcHR(pc: Float) = pcR(pc, this.height)
fun UiComponent.dpR(pc: Float) = dpR(pc, this.dpi)
fun UiComponent.mmR(pc: Float) = mmR(pc, this.dpi)

open class SizeSpec(val value: Float, val unit: SizeUnit, val roundToUnit: Boolean = false) {
    open fun toUnits(size: Float, dpi: Float): Float {
        return if (roundToUnit) {
            when (unit) {
                SizeUnit.UN -> round(value)
                SizeUnit.DP -> dpR(value, dpi)
                SizeUnit.MM -> mmR(value, dpi)
                SizeUnit.PC -> pcR(value, size)
            }
        } else {
            when (unit) {
                SizeUnit.UN -> value
                SizeUnit.DP -> dp(value, dpi)
                SizeUnit.MM -> mm(value, dpi)
                SizeUnit.PC -> pc(value, size)
            }
        }
    }

    operator fun plus(size: SizeSpec): SizeSpec {
        return CombSizeSpec(this, size, true)
    }

    operator fun minus(size: SizeSpec): SizeSpec {
        return CombSizeSpec(this, size, false)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SizeSpec) return false

        if (value != other.value) return false
        if (unit != other.unit) return false
        if (roundToUnit != other.roundToUnit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + unit.hashCode()
        result = 31 * result + roundToUnit.hashCode()
        return result
    }
}

private class CombSizeSpec(val left: SizeSpec, val right: SizeSpec, val add: Boolean) : SizeSpec(0f, SizeUnit.UN) {
    override fun toUnits(size: Float, dpi: Float): Float {
        val leftUns = left.toUnits(size, dpi)
        val rightUns = right.toUnits(size, dpi)
        if (add) {
            return leftUns + rightUns
        } else {
            return leftUns - rightUns
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
