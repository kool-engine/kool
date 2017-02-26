package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext

/**
 * @author fabmax
 */

class LayoutSpec {

    var width = un(0f)
    var height = un(0f)
    var depth = un(0f)

    var x = un(0f)
    var y = un(0f)
    var z = un(0f)

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

enum class Unit {
    UN,
    DP,
    MM,
    PC
}

fun un(value: Float) = SizeSpec(value, Unit.UN)
fun dp(value: Float) = SizeSpec(value, Unit.DP)
fun mm(value: Float) = SizeSpec(value, Unit.MM)
fun pc(value: Float) = SizeSpec(value, Unit.PC)

fun pc(pc: Float, size: Float) = size * pc / 100f
fun dp(dp: Float, ctx: RenderContext) = dp * ctx.screenDpi / 96f
fun dp(dp: Float, dpi: Float) = dp * dpi / 96f
fun mm(mm: Float, ctx: RenderContext) = mm * ctx.screenDpi / 25.4f
fun mm(mm: Float, dpi: Float) = mm * dpi / 25.4f

fun pcR(pc: Float, size: Float) = Math.round(size * pc / 100f).toFloat()
fun dpR(dp: Float, ctx: RenderContext) = Math.round(dp * ctx.screenDpi / 96f).toFloat()
fun dpR(dp: Float, dpi: Float) = Math.round(dp * dpi / 96f).toFloat()
fun mmR(mm: Float, ctx: RenderContext) = Math.round(mm * ctx.screenDpi / 25.4f).toFloat()
fun mmR(mm: Float, dpi: Float) = Math.round(mm * dpi / 25.4f).toFloat()

data class SizeSpec(val value: Float, val unit: Unit) {
    fun toUnits(size: Float, ctx: RenderContext): Float {
        return toUnits(size, ctx.screenDpi)
    }

    fun toUnits(size: Float, dpi: Float): Float {
        return when(unit) {
            Unit.UN -> value
            Unit.DP -> dp(value, dpi)
            Unit.MM -> mm(value, dpi)
            Unit.PC -> pc(value, size)
        }
    }
}
