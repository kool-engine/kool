package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.Color
import kotlin.reflect.KProperty

open class UiModifier(val surface: UiSurface) {
    private val properties = mutableListOf<PropertyHolder<*>>()

    var width: Dimension by property(FitContent)
    var height: Dimension by property(FitContent)
    var layout: Layout by property(CellLayout)
    var background: UiRenderer<UiNode>? by property(null)
    var border: UiRenderer<UiNode>? by property(null)
    var zLayer: Int by property(0)
    var isBlocking: Boolean by property(true)

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

    var onMeasured: ((UiNode) -> Unit)? by property(null)
    var onPositioned: ((UiNode) -> Unit)? by property(null)

    val onPointer: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onClick: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onWheelX: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onWheelY: MutableList<(PointerEvent) -> Unit> by listProperty()

    val onEnter: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onExit: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onHover: MutableList<(PointerEvent) -> Unit> by listProperty()

    val onDragStart: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onDrag: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onDragEnd: MutableList<(PointerEvent) -> Unit> by listProperty()

    protected fun <T> property(defaultVal: T): PropertyHolder<T> {
        val holder = PropertyHolder { defaultVal }
        properties += holder
        return holder
    }

    protected fun <T> property(defaultVal: (UiSurface) -> T): PropertyHolder<T> {
        val holder = PropertyHolder(defaultVal)
        properties += holder
        return holder
    }

    protected fun <T> listProperty(): ListPropertyHolder<T> {
        val holder = ListPropertyHolder<T>()
        properties += holder
        return holder
    }

    open fun resetDefaults() {
        for (i in properties.indices) {
            properties[i].resetDefault()
        }
    }

    val hasAnyPointerCallback: Boolean
        get() = onPointer.isNotEmpty() ||
                onClick.isNotEmpty() ||
                onWheelX.isNotEmpty() ||
                onWheelY.isNotEmpty() ||
                onEnter.isNotEmpty() ||
                onExit.isNotEmpty() ||
                onHover.isNotEmpty() ||
                onDragStart.isNotEmpty() ||
                onDrag.isNotEmpty() ||
                onDragEnd.isNotEmpty()

    val hasAnyHoverCallback: Boolean
        get() = onEnter.isNotEmpty() ||
                onExit.isNotEmpty() ||
                onHover.isNotEmpty()

    val hasAnyDragCallback: Boolean
        get() = onDragStart.isNotEmpty() ||
                onDrag.isNotEmpty() ||
                onDragEnd.isNotEmpty()

    protected open inner class PropertyHolder<T>(private val defaultVal: (UiSurface) -> T) {
        var field = defaultVal(surface)

        open fun resetDefault() { field = defaultVal(surface) }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = field
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { field = value }
    }

    protected inner class ListPropertyHolder<T> : PropertyHolder<MutableList<T>>({ mutableListOf() }) {
        override fun resetDefault() {
            field.clear()
        }
    }
}

fun <T: UiModifier> T.width(width: Dimension): T { this.width = width; return this }
fun <T: UiModifier> T.height(height: Dimension): T { this.height = height; return this }
fun <T: UiModifier> T.size(width: Dimension, height: Dimension): T {
    this.width = width
    this.height = height
    return this
}
fun <T: UiModifier> T.layout(layout: Layout): T { this.layout = layout; return this }
fun <T: UiModifier> T.border(border: UiRenderer<UiNode>?): T { this.border = border; return this }
fun <T: UiModifier> T.zLayer(zLayer: Int): T { this.zLayer = zLayer; return this }
fun <T: UiModifier> T.isBlocking(isBlocking: Boolean): T { this.isBlocking = isBlocking; return this }
fun <T: UiModifier> T.background(background: UiRenderer<UiNode>?): T { this.background = background; return this }
fun <T: UiModifier> T.backgroundColor(color: Color?): T {
    background = if (color != null) RectBackground(color) else null
    return this
}

fun <T: UiModifier> T.padding(all: Dp): T {
    paddingStart = all
    paddingEnd = all
    paddingTop = all
    paddingBottom = all
    return this
}

fun <T: UiModifier> T.padding(vertical: Dp? = null, horizontal: Dp? = null): T {
    vertical?.let {
        paddingTop = it
        paddingBottom = it
    }
    horizontal?.let {
        paddingStart = it
        paddingEnd = it
    }
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

fun <T: UiModifier> T.margin(vertical: Dp? = null, horizontal: Dp? = null): T {
    vertical?.let {
        marginTop = it
        marginBottom = it
    }
    horizontal?.let {
        marginStart = it
        marginEnd = it
    }
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


fun <T: UiModifier> T.clearHoverCallbacks(): T {
    onEnter.clear()
    onExit.clear()
    onHover.clear()
    return this
}

fun <T: UiModifier> T.clearDragCallbacks(): T {
    onDragStart.clear()
    onDrag.clear()
    onDragEnd.clear()
    return this
}

fun <T: UiModifier> T.clearWheelCallbacks(): T {
    onWheelX.clear()
    onWheelY.clear()
    return this
}

fun <T: UiModifier> T.clearPointerCallbacks(): T {
    onPointer.clear()
    onClick.clear()
    onWheelX.clear()
    onWheelY.clear()
    onEnter.clear()
    onExit.clear()
    onHover.clear()
    onDragStart.clear()
    onDrag.clear()
    onDragEnd.clear()
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
fun <T: UiModifier> T.align(xAlignment: AlignmentX = alignX, yAlignment: AlignmentY = alignY): T {
    alignX = xAlignment
    alignY = yAlignment
    return this
}

fun <T: UiModifier> T.onMeasured(block: (UiNode) -> Unit): T { onMeasured = block; return this }
fun <T: UiModifier> T.onPositioned(block: (UiNode) -> Unit): T { onPositioned = block; return this }

class PointerEvent(val pointer: Pointer, val ctx: KoolContext) {
    /**
     * Pointer position in UiNode local coordinates: (0, 0) = upper left node corner.
     */
    val position = MutableVec2f()

    /**
     * Pointer position in screen coordinates: (0, 0) = upper left screen corner.
     */
    val screenPosition: Vec2f get() = pointer.pos

    /**
     * Can be set to false by listeners to reject the event. Rejected events will be passed on to potential other
     * consumers.
     * @see [reject]
     */
    var isConsumed = true

    /**
     * Can be called by listeners to reject the event. Rejected events will be passed on to potential other
     * consumers.
     * @see [reject]
     */
    fun reject() {
        isConsumed = false
    }
}

val PointerEvent.isLeftClick: Boolean get() = pointer.isLeftButtonClicked && pointer.leftButtonRepeatedClickCount == 1
val PointerEvent.isLeftDoubleClick: Boolean get() = pointer.isLeftButtonClicked && pointer.leftButtonRepeatedClickCount == 2
val PointerEvent.isRightClick: Boolean get() = pointer.isRightButtonClicked && pointer.rightButtonRepeatedClickCount == 1

fun <T: UiModifier> T.onClick(block: (PointerEvent) -> Unit): T { onClick += block; return this }
fun <T: UiModifier> T.onWheelX(block: (PointerEvent) -> Unit): T { onWheelX += block; return this }
fun <T: UiModifier> T.onWheelY(block: (PointerEvent) -> Unit): T { onWheelY += block; return this }
fun <T: UiModifier> T.onPointer(block: (PointerEvent) -> Unit): T { onPointer += block; return this }

fun <T: UiModifier> T.onEnter(block: (PointerEvent) -> Unit): T { onEnter += block; return this }
fun <T: UiModifier> T.onExit(block: (PointerEvent) -> Unit): T { onExit += block; return this }
fun <T: UiModifier> T.onHover(block: (PointerEvent) -> Unit): T { onHover += block; return this }

fun <T: UiModifier> T.onDragStart(block: (PointerEvent) -> Unit): T { onDragStart += block; return this }
fun <T: UiModifier> T.onDrag(block: (PointerEvent) -> Unit): T { onDrag += block; return this }
fun <T: UiModifier> T.onDragEnd(block: (PointerEvent) -> Unit): T { onDragEnd += block; return this }

fun <T: UiModifier> T.onClick(clickable: Clickable): T { onClick += clickable::onClick; return this }

fun <T: UiModifier> T.hoverListener(hoverable: Hoverable): T {
    onEnter(hoverable::onEnter)
    onHover(hoverable::onHover)
    onExit(hoverable::onExit)
    return this
}

fun <T: UiModifier> T.dragListener(draggable: Draggable): T {
    onDragStart(draggable::onDragStart)
    onDrag(draggable::onDrag)
    onDragEnd(draggable::onDragEnd)
    return this
}