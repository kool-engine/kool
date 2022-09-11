package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.logW
import kotlin.math.max
import kotlin.math.min

interface ScrollPaneScope : UiScope {
    override val modifier: ScrollPaneModifier
}

open class ScrollPaneModifier : UiModifier() {
    var scrollPosX = Dp.ZERO
    var scrollPosY = Dp.ZERO
    var onScrollPosChanged: ((Float, Float) -> Unit)? = null

    var allowOverscrollX = false
    var allowOverscrollY = false

    override fun resetDefaults() {
        super.resetDefaults()
        scrollPosX = Dp.ZERO
        scrollPosY = Dp.ZERO
        onScrollPosChanged = null
        allowOverscrollX = false
        allowOverscrollY = false
    }
}

fun <T: ScrollPaneModifier> T.scrollPos(x: Dp = scrollPosX, y: Dp = scrollPosY): T {
    scrollPosX = x
    scrollPosY = y
    return this
}

fun <T: ScrollPaneModifier> T.allowOverScroll(x: Boolean, y: Boolean): T {
    allowOverscrollX = x
    allowOverscrollY = y
    return this
}

fun <T: ScrollPaneModifier> T.onScrollPosChanged(block: (Float, Float) -> Unit): T {
    onScrollPosChanged = block
    return this
}

inline fun UiScope.ScrollPane(block: ScrollPaneScope.() -> Unit): ScrollPaneScope {
    val scrollPane = uiNode.createChild(ScrollPaneNode::class, ScrollPaneNode.factory)
    scrollPane.block()
    return scrollPane
}

open class ScrollPaneNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ScrollPaneScope {
    override val modifier = ScrollPaneModifier()

    private var scrollPosX = 0f
    private var scrollPosY = 0f

    private var complainedAboutSize = false

    override fun measureContentSize(ctx: KoolContext) {
        if ((modifier.width !== WrapContent || modifier.height !== WrapContent) && !complainedAboutSize) {
            complainedAboutSize = true
            logW { "ScrollPane width / height should be set to WrapContent for scrolling to work as expected" }
        }
        super.measureContentSize(ctx)
    }

    override fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        scrollPosX = modifier.scrollPosX.value
        scrollPosY = modifier.scrollPosY.value

        if (parent != null) {
            val contentWidthDp = contentWidth / surface.measuredScale
            val contentHeightDp = contentHeight / surface.measuredScale
            val lt = max(parent.clippedMinX, minX) / surface.measuredScale
            val up = max(parent.clippedMinY, minY) / surface.measuredScale
            val rt = min(parent.clippedMaxX, maxX) / surface.measuredScale
            val dn = min(parent.clippedMaxY, maxY) / surface.measuredScale
            val viewWidthDp = rt - lt
            val viewHeightDp = dn - up

            if (!modifier.allowOverscrollX) {
                if (scrollPosX + viewWidthDp > contentWidthDp) {
                    scrollPosX = contentWidthDp - viewWidthDp
                }
                scrollPosX = max(0f, scrollPosX)
            }
            if (!modifier.allowOverscrollY) {
                if (scrollPosY + viewHeightDp > contentHeightDp) {
                    scrollPosY = contentHeightDp - viewWidthDp
                }
                scrollPosY = max(0f, scrollPosY)
            }
        }

        if (scrollPosX != modifier.scrollPosX.value || scrollPosY != modifier.scrollPosY.value) {
            modifier.onScrollPosChanged?.invoke(scrollPosX, scrollPosY)
        }

        val scrollX = scrollPosX * surface.measuredScale
        val scrollY = scrollPosY * surface.measuredScale
        super.setBounds(minX - scrollX, minY - scrollY, maxX - scrollX, maxY - scrollY)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ScrollPaneNode = { parent, surface -> ScrollPaneNode(parent, surface) }
    }
}