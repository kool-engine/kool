package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Color
import kotlin.reflect.KProperty

open class UiModifier {
    private val properties = mutableListOf<PropertyHolder<*>>()

    var width: Dimension by property(WrapContent)
    var height: Dimension by property(WrapContent)
    var layout: Layout by property(CellLayout)
    var background: Color? by property(null)

    var paddingStart: Dp by property(Dp.ZERO)
    var paddingEnd: Dp by property(Dp.ZERO)
    var paddingTop: Dp by property(Dp.ZERO)
    var paddingBottom: Dp by property(Dp.ZERO)

    var marginStart: Dp by property(Dp.ZERO)
    var marginEnd: Dp by property(Dp.ZERO)
    var marginTop: Dp by property(Dp.ZERO)
    var marginBottom: Dp by property(Dp.ZERO)

    var alignX: AlignmentX by property(AlignmentX.Start)
    var alignY: AlignmentY by property(AlignmentY.Top)

    var onRawPointer: ((PointerEvent) -> Unit)? by property(null)
    var onClick: ((PointerEvent) -> Unit)? by property(null)
    var onWheelX: ((PointerEvent) -> Unit)? by property(null)
    var onWheelY: ((PointerEvent) -> Unit)? by property(null)

    var onEnter: ((PointerEvent) -> Unit)? by property(null)
    var onExit: ((PointerEvent) -> Unit)? by property(null)
    var onHover: ((PointerEvent) -> Unit)? by property(null)

    var onDragStart: ((PointerEvent) -> Unit)? by property(null)
    var onDrag: ((PointerEvent) -> Unit)? by property(null)
    var onDragEnd: ((PointerEvent) -> Unit)? by property(null)

    protected fun <T> property(defaultVal: T): PropertyHolder<T> {
        val holder = PropertyHolder(defaultVal)
        properties += holder
        return holder
    }

    open fun resetDefaults() {
        for (i in properties.indices) {
            properties[i].resetDefault()
        }
    }

    val hasAnyCallback: Boolean
        get() = onRawPointer != null ||
                onClick != null ||
                onWheelX != null ||
                onWheelY != null ||
                onEnter != null ||
                onExit != null ||
                onHover != null ||
                onDragStart != null ||
                onDrag != null ||
                onDragEnd != null

    val hasAnyHoverCallback: Boolean
        get() = onEnter != null ||
                onExit != null ||
                onHover != null

    val hasAnyDragCallback: Boolean
        get() = onDragStart != null ||
                onDrag != null ||
                onDragEnd != null

    protected inner class PropertyHolder<T>(val defaultVal: T) {
        var field = defaultVal

        fun resetDefault() { field = defaultVal }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = field
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { field = value }
    }
}

fun <T: UiModifier> T.width(width: Dimension): T { this.width = width; return this }
fun <T: UiModifier> T.height(height: Dimension): T { this.height = height; return this }
fun <T: UiModifier> T.layout(layout: Layout): T { this.layout = layout; return this }
fun <T: UiModifier> T.background(color: Color?): T { background = color; return this }

fun <T: UiModifier> T.padding(all: Dp): T {
    paddingStart = all
    paddingEnd = all
    paddingTop = all
    paddingBottom = all
    return this
}

fun <T: UiModifier> T.verticalPadding(padding: Dp): T {
    paddingTop = padding
    paddingBottom = padding
    return this
}

fun <T: UiModifier> T.horizontalPadding(padding: Dp): T {
    paddingStart = padding
    paddingEnd = padding
    return this
}

fun <T: UiModifier> T.padding(
    start: Dp = paddingStart,
    end: Dp = paddingEnd,
    top: Dp = paddingTop,
    bottom: Dp = paddingBottom
): T {
    paddingStart = start
    paddingEnd = end
    paddingTop = top
    paddingBottom = bottom
    return this
}

fun <T: UiModifier> T.margin(all: Dp): T {
    marginStart = all
    marginEnd = all
    marginTop = all
    marginBottom = all
    return this
}

fun <T: UiModifier> T.verticalMargin(margin: Dp): T {
    marginTop = margin
    marginBottom = margin
    return this
}

fun <T: UiModifier> T.horizontalMargin(margin: Dp): T {
    marginStart = margin
    marginEnd = margin
    return this
}

fun <T: UiModifier> T.margin(
    start: Dp = marginStart,
    end: Dp = marginEnd,
    top: Dp = marginTop,
    bottom: Dp = marginBottom
): T {
    marginStart = start
    marginEnd = end
    marginTop = top
    marginBottom = bottom
    return this
}

enum class AlignmentX {
    Start,
    Center,
    End
}

enum class AlignmentY {
    Top,
    Center,
    Bottom
}

fun <T: UiModifier> T.alignX(alignment: AlignmentX): T { alignX = alignment; return this }
fun <T: UiModifier> T.alignY(alignment: AlignmentY): T { alignY = alignment; return this }

class PointerEvent(val pointer: InputManager.Pointer, val ctx: KoolContext) {
    var isConsumed = true

    fun reject() {
        isConsumed = false
    }
}

fun <T: UiModifier> T.onRawPointer(block: (PointerEvent) -> Unit): T { onRawPointer = block; return this }
fun <T: UiModifier> T.onClick(block: (PointerEvent) -> Unit): T { onClick = block; return this }
fun <T: UiModifier> T.onWheelX(block: (PointerEvent) -> Unit): T { onWheelX = block; return this }
fun <T: UiModifier> T.onWheelY(block: (PointerEvent) -> Unit): T { onWheelY = block; return this }

fun <T: UiModifier> T.onEnter(block: (PointerEvent) -> Unit): T { onEnter = block; return this }
fun <T: UiModifier> T.onExit(block: (PointerEvent) -> Unit): T { onExit = block; return this }
fun <T: UiModifier> T.onHover(block: (PointerEvent) -> Unit): T { onHover = block; return this }

fun <T: UiModifier> T.onDragStart(block: (PointerEvent) -> Unit): T { onDragStart = block; return this }
fun <T: UiModifier> T.onDrag(block: (PointerEvent) -> Unit): T { onDrag = block; return this }
fun <T: UiModifier> T.onDragEnd(block: (PointerEvent) -> Unit): T { onDragEnd = block; return this }

