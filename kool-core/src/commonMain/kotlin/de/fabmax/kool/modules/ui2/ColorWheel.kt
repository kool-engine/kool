package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Color.Hsv
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.set
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.*

interface ColorWheelScope : UiScope {
    override val modifier: ColorWheelModifier
}

open class ColorWheelModifier(surface: UiSurface) : UiModifier(surface) {
    var hue by property(0f)
    var saturation by property(1f)
    var value by property(1f)

    var hueRingWidth by property { it.sizes.gap }
    var hueIndicatorColor by property { if (it.colors.isLight) Color.GRAY else Color.WHITE }

    var onChange: ((Float, Float, Float) -> Unit)? by property(null)
}

fun <T: ColorWheelModifier> T.hue(hue: Float): T { this.hue = hue; return this }
fun <T: ColorWheelModifier> T.saturation(saturation: Float): T { this.saturation = saturation; return this }
fun <T: ColorWheelModifier> T.value(value: Float): T { this.value = value; return this }
fun <T: ColorWheelModifier> T.onChange(block: (Float, Float, Float) -> Unit): T { onChange = block; return this }

inline fun UiScope.ColorWheel(
    hue: Float = 0f,
    saturation: Float = 1f,
    value: Float = 1f,
    scopeName: String? = null,
    block: ColorWheelScope.() -> Unit
): ColorWheelScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val colorChooser = uiNode.createChild(scopeName, ColorWheelNode::class, ColorWheelNode.factory)
    colorChooser.modifier
        .hue(hue).saturation(saturation).value(value)
        .dragListener(colorChooser)
    colorChooser.block()
    return colorChooser
}

open class ColorWheelNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ColorWheelScope, Draggable {
    override val modifier = ColorWheelModifier(surface)

    private var centerX = 0f
    private var centerY = 0f

    private var ro = 0f
    private var ri = 0f
    private var rt = 0f

    private var isHueDrag = false
    private var isSatValDrag = false

    private val geomCache = CachedGeometry(this, IndexedVertexList(UiVertexLayout))

    override fun onDragStart(ev: PointerEvent) {
        val dx = ev.position.x - centerX
        val dy = ev.position.y - centerY
        val l = sqrt(dx * dx + dy * dy)
        if (l in ri..ro) {
            isHueDrag = true
            isSatValDrag = false
        } else if (l < ri) {
            isHueDrag = false
            isSatValDrag = true
        } else {
            ev.reject()
        }
    }

    override fun onDrag(ev: PointerEvent) {
        val dx = ev.position.x - centerX
        val dy = ev.position.y - centerY
        if (isHueDrag) {
            hueDrag(dx, dy)
        } else if (isSatValDrag) {
            satValDrag(dx, dy)
        }
    }

    private fun hueDrag(dx: Float, dy: Float) {
        var hue = atan2(dy, dx).toDeg()
        if (hue < 0f) {
            hue += 360f
        }
        modifier.onChange?.invoke(hue, modifier.saturation, modifier.value)
    }

    private fun satValDrag(dx: Float, dy: Float) {
        val p = MutableVec2f(dx, dy).rotate(-modifier.hue.deg)
        val x = p.x
        val y = p.y

        val x1 = rt
        val y1 = 0f
        val x2 = -0.5f * rt
        val y2 = -0.866f * rt
        val x3 = -0.5f * rt
        val y3 = 0.866f * rt

        val denom = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3)
        val u = (((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / denom).clamp()
        val v = (((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / denom).clamp()

        val value = (u + v).clamp()
        val sat = if (value > 0.001f) (value - v) / value else 0f
        modifier.onChange?.invoke(modifier.hue, sat, value)
    }

    private fun getPointForSatValue(): Vec2f {
        val xCol = rt
        val yCol = 0f
        val xWht = -0.5f * rt
        val yWht = -0.866f * rt
        val xBlk = -0.5f * rt
        val yBlk = 0.866f * rt

        val xBri = (xWht + xCol) * 0.5f
        val yBri = (yWht + yCol) * 0.5f

        val p = MutableVec2f((xBri - xBlk) * modifier.value + xBlk, (yBri - yBlk) * modifier.value + yBlk)
        p.x += (xCol - xWht) * modifier.value * (modifier.saturation - 0.5f)
        p.y += (yCol - yWht) * modifier.value * (modifier.saturation - 0.5f)

        p.rotate(modifier.hue.deg)
        p.x += centerX
        p.y += centerY
        return p
    }

    override fun measureContentSize(ctx: KoolContext) {
        val w = modifier.hueRingWidth.px * 20f
        val h = modifier.hueRingWidth.px * 20f
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else w + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else h + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        super.setBounds(minX, minY, maxX, maxY)

        ro = min(innerHeightPx, innerWidthPx) * 0.5f - sizes.smallGap.px * 0.5f
        ri = ro - modifier.hueRingWidth.px
        rt = ri * 0.9f
        centerX = widthPx * 0.5f
        centerY = heightPx * 0.5f
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        val svPt = getPointForSatValue()
        val hsvColor = Hsv(modifier.hue, modifier.saturation, modifier.value).toSrgb()
        val draw = getUiPrimitives(UiSurface.LAYER_FLOATING)
        draw.localCircle(svPt.x, svPt.y, sizes.smallGap.px * 1.5f, hsvColor)
        draw.localCircleBorder(svPt.x, svPt.y, sizes.smallGap.px * 1.5f, 1.dp.px, Color.WHITE)

        getPlainBuilder().configured {
            if (geomCache.hasSizeChanged()) {
                rebuildGeometry()
            }
            geomCache.updateCache()
            geomCache.appendTo(geometry)

            translate(centerX, centerY, 0f)
            rotate(modifier.hue.deg, Vec3f.Z_AXIS)

            // inner triangle (saturation / value chooser)
            var i1 = vertex {
                it.position.set(rt, 0f, 0f)
                it.color.set(Hsv(modifier.hue, 1f, 1f).toSrgb(a = 1f))
            }
            var i2 = vertex {
                it.position.set(-0.5f * rt, 0.866f * rt, 0f)
                it.color.set(Color.BLACK)
            }
            var i3 = vertex {
                it.position.set(-0.5f * rt, -0.866f * rt, 0f)
                it.color.set(Color.WHITE)
            }
            addTriIndices(i1, i2, i3)

            color = modifier.hueIndicatorColor
            i1 = vertex { it.position.set(ri + sizes.smallGap.px * 0.75f, 0f, 0f) }
            i2 = vertex { it.position.set(ri - sizes.smallGap.px * 0.5f, sizes.smallGap.px * 0.8f, 0f) }
            i3 = vertex { it.position.set(ri - sizes.smallGap.px * 0.5f, sizes.smallGap.px * -0.8f, 0f) }
            addTriIndices(i1, i2, i3)
            i1 = vertex { it.position.set(ro - sizes.smallGap.px * 0.75f, 0f, 0f) }
            i2 = vertex { it.position.set(ro + sizes.smallGap.px * 0.5f, sizes.smallGap.px * 0.8f, 0f) }
            i3 = vertex { it.position.set(ro + sizes.smallGap.px * 0.5f, sizes.smallGap.px * -0.8f, 0f) }
            addTriIndices(i3, i2, i1)

        }
    }

    private fun rebuildGeometry() {
        geomCache.rebuildCache {
            translate(centerX, centerY, 0f)

            // outer ring (hue chooser)
            val n = 60
            var vi1 = 0
            var vi2 = 0
            for (i in 0..n) {
                val a = 2f * PI.toFloat() * i / n

                color = hueGradient.getColor(i.toFloat() / n)
                val vi3 = vertex {
                    it.position.set(cos(a) * ro, sin(a) * ro, 0f)
                }
                val vi4 = vertex {
                    it.position.set(cos(a) * ri, sin(a) * ri, 0f)
                }
                if (i > 0) {
                    addTriIndices(vi1, vi2, vi4)
                    addTriIndices(vi1, vi4, vi3)
                }
                vi1 = vi3
                vi2 = vi4
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ColorWheelNode = { parent, surface -> ColorWheelNode(parent, surface) }

        private val hueGradient = ColorGradient(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED)
    }
}
