package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

abstract class UiNode(val parent: UiNode?, override val surface: UiSurface) : UiScope {
    override val uiNode: UiNode get() = this

    var nodeIndex = 0
        private set

    protected val oldChildren = mutableListOf<UiNode>()
    protected val mutChildren = mutableListOf<UiNode>()
    val children: List<UiNode> get() = mutChildren
    val weakMemory = WeakMemory()

    private var scopeName: String? = null

    var contentWidthPx = 0f
        private set
    var contentHeightPx = 0f
        private set

    var leftPx = 0f
        private set
    var topPx = 0f
        private set
    var rightPx = 0f
        private set
    var bottomPx = 0f
        private set
    val widthPx: Float get() = rightPx - leftPx
    val heightPx: Float get() = bottomPx - topPx
    val innerWidthPx: Float get() = widthPx - paddingStartPx - paddingEndPx
    val innerHeightPx: Float get() = heightPx - paddingTopPx - paddingBottomPx

    val clipBoundsPx = MutableVec4f()
    val clipLeftPx: Float get() = clipBoundsPx.x
    val clipTopPx: Float get() = clipBoundsPx.y
    val clipRightPx: Float get() = clipBoundsPx.z
    val clipBottomPx: Float get() = clipBoundsPx.w
    val isInClip: Boolean get() = clipRightPx - clipLeftPx > 0.5f && clipBottomPx - clipTopPx > 0.5f

    val paddingStartPx: Float get() = modifier.paddingStart.px
    val paddingEndPx: Float get() = modifier.paddingEnd.px
    val paddingTopPx: Float get() = modifier.paddingTop.px
    val paddingBottomPx: Float get() = modifier.paddingBottom.px

    val marginStartPx: Float get() = modifier.marginStart.px
    val marginEndPx: Float get() = modifier.marginEnd.px
    val marginTopPx: Float get() = modifier.marginTop.px
    val marginBottomPx: Float get() = modifier.marginBottom.px

    val setBoundsVertexMod: VertexView.() -> Unit = {
        getVec4fAttribute(Ui2Shader.ATTRIB_CLIP)?.set(clipLeftPx, clipTopPx, clipRightPx, clipBottomPx)
    }

    fun toLocal(screenX: Double, screenY: Double, result: MutableVec2f = MutableVec2f()): MutableVec2f =
        result.set(screenX.toFloat() - leftPx, screenY.toFloat() - topPx)
    fun toLocal(screenX: Float, screenY: Float, result: MutableVec2f = MutableVec2f()): MutableVec2f =
        result.set(screenX - leftPx, screenY - topPx)
    fun toLocal(screen: Vec2f, result: MutableVec2f = MutableVec2f()) = toLocal(screen.x, screen.y, result)

    fun toScreen(localX: Float, localY: Float, result: MutableVec2f = MutableVec2f()): MutableVec2f =
        result.set(localX + leftPx, localY + topPx)
    fun toScreen(local: Vec2f, result: MutableVec2f = MutableVec2f()) = toScreen(local.x, local.y, result)

    fun isInBounds(point: Vec2f): Boolean {
        return point.x in leftPx..rightPx && point.y in topPx..bottomPx
    }

    fun isInBoundsLocal(point: Vec2f): Boolean {
        return (point.x + leftPx) in leftPx..rightPx && (point.y + topPx) in topPx..bottomPx
    }

    fun isInClipBounds(point: Vec2f): Boolean {
        return point.x in clipLeftPx..clipRightPx && point.y in clipTopPx..clipBottomPx
    }

    fun isInClipBoundsLocal(point: Vec2f): Boolean {
        return (point.x + leftPx) in clipLeftPx..clipRightPx && (point.y + topPx) in clipTopPx..clipBottomPx
    }

    open fun setContentSize(width: Float, height: Float) {
        contentWidthPx = width
        contentHeightPx = height
        modifier.onMeasured?.let { it(this) }
    }

    open fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        this.leftPx = minX
        this.topPx = minY
        this.rightPx = maxX
        this.bottomPx = maxY

        if (parent != null) {
            clipBoundsPx.x = max(parent.clipLeftPx, minX)
            clipBoundsPx.y = max(parent.clipTopPx, minY)
            clipBoundsPx.z = min(parent.clipRightPx, maxX)
            clipBoundsPx.w = min(parent.clipBottomPx, maxY)
        } else {
            clipBoundsPx.x = minX
            clipBoundsPx.y = minY
            clipBoundsPx.z = maxX
            clipBoundsPx.w = maxY
        }
        modifier.onPositioned?.let { it(this) }
    }

    fun computeWidthFromDimension(scaledGrowSpace: Float): Float {
        return dimensionToPx(modifier.width, contentWidthPx, scaledGrowSpace, true)
    }

    fun computeHeightFromDimension(scaledGrowSpace: Float): Float {
        return dimensionToPx(modifier.height, contentHeightPx, scaledGrowSpace, true)
    }

    private fun dimensionToPx(dim: Dimension, contentPx: Float, scaledGrowSpace: Float, isGrowAllowed: Boolean): Float {
        return when (dim) {
            FitContent -> contentPx
            is Dp -> dim.px
            is Grow -> {
                if (isGrowAllowed) {
                    val min = dimensionToPx(dim.min, contentPx, 0f, false)
                    val max = dimensionToPx(dim.max, contentPx, 0f, false)
                    // max value has priority over min value
                    (scaledGrowSpace * dim.weight).coerceAtLeast(min).coerceAtMost(max)
                } else {
                    0f
                }
            }
        }
    }

    fun computeChildLocationX(child: UiNode, measuredChildWidth: Float): Float {
        return leftPx + when (child.modifier.alignX) {
            AlignmentX.Start -> if (paddingStartPx != 0f) max(paddingStartPx, child.marginStartPx) else child.marginStartPx
            AlignmentX.Center -> (widthPx - measuredChildWidth) * 0.5f
            AlignmentX.End -> {
                val marginPadding = if (paddingEndPx != 0f) max(paddingEndPx, child.marginEndPx) else child.marginEndPx
                widthPx - measuredChildWidth - marginPadding
            }
        }
    }

    fun computeChildLocationY(child: UiNode, measuredChildHeight: Float): Float {
        return topPx + when (child.modifier.alignY) {
            AlignmentY.Top -> if (paddingTopPx != 0f) max(paddingTopPx, child.marginTopPx) else child.marginTopPx
            AlignmentY.Center -> (heightPx - measuredChildHeight) * 0.5f
            AlignmentY.Bottom -> {
                val marginPadding = if (paddingBottomPx != 0f) max(paddingBottomPx, child.marginBottomPx) else child.marginBottomPx
                heightPx - measuredChildHeight - marginPadding
            }
        }
    }

    private fun setScopeName(scopeName: String?) {
        if (scopeName != this.scopeName) {
            this.scopeName = scopeName

            weakMemory.clear()
            oldChildren.clear()
        }
    }

    open fun render(ctx: KoolContext) {
        modifier.background?.renderUi(this)
        modifier.border?.renderUi(this)
    }

    open fun measureContentSize(ctx: KoolContext) {
        modifier.layout.measureContentSize(this, ctx)
    }

    open fun layoutChildren(ctx: KoolContext) {
        modifier.layout.layoutChildren(this, ctx)
    }

    open fun applyDefaults() {
        nodeIndex = surface.nodeIndex++
        if (children.isNotEmpty()) {
            oldChildren.clear()
            for (i in mutChildren.lastIndex downTo 0) {
                oldChildren += mutChildren[i]
            }
            mutChildren.clear()
        }
        modifier.resetDefaults()
        modifier.zLayer(parent?.modifier?.zLayer ?: UiSurface.LAYER_DEFAULT)
        weakMemory.rewind()
    }

    protected fun padCachedChildren(pad: Int) {
        if (abs(pad) < oldChildren.size) {
            var padI = pad
            while (padI < 0) {
                oldChildren.removeLast()
                padI++
            }
            while (padI > 0) {
                oldChildren.add(BoxNode.factory(this, surface))
                padI--
            }
        }
    }

    fun <T: UiNode> createChild(scopeName: String?, type: KClass<T>, factory: (UiNode, UiSurface) -> T): T {
        var child: T? = null
        if (oldChildren.isNotEmpty()) {
            val old = oldChildren.removeLast()
            if (old::class == type) {
                @Suppress("UNCHECKED_CAST")
                child = old as T
            }
        }
        if (child == null) {
            child = factory(this, surface)
        }
        child.applyDefaults()
        child.setScopeName(scopeName)
        mutChildren += child
        return child
    }

    inline fun MeshBuilder.configured(color: Color? = null, block: MeshBuilder.() -> Unit) {
        val prevMod = vertexModFun
        vertexModFun = setBoundsVertexMod
        val prevColor = this.color
        color?.let { this.color = it }

        withTransform {
            translate(leftPx, topPx, 0f)
            this.block()
        }

        this.vertexModFun = prevMod
        this.color = prevColor
    }

    fun getUiPrimitives(layerOffset: Int = 0): UiPrimitiveMesh {
        return surface.getMeshLayer(modifier.zLayer + layerOffset).uiPrimitives
    }

    fun getPlainBuilder(layerOffset: Int = 0): MeshBuilder {
        return surface.getMeshLayer(modifier.zLayer + layerOffset).plainBuilder
    }

    fun getTextBuilder(fontProps: Font, layerOffset: Int = 0): MeshBuilder {
        return surface.getMeshLayer(modifier.zLayer + layerOffset).getTextBuilder(fontProps)
    }

    fun UiPrimitiveMesh.localRect(x: Float, y: Float, width: Float, height: Float, color: Color) {
        rect(leftPx + x, topPx + y, width, height, clipBoundsPx, color)
    }

    fun UiPrimitiveMesh.localRoundRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Color) {
        roundRect(leftPx + x, topPx + y, width, height, radius, clipBoundsPx, color)
    }

    fun UiPrimitiveMesh.localCircle(x: Float, y: Float, radius: Float, color: Color) {
        circle(leftPx + x, topPx + y, radius, clipBoundsPx, color)
    }

    fun UiPrimitiveMesh.localOval(x: Float, y: Float, xRadius: Float, yRadius: Float, color: Color) {
        oval(leftPx + x, topPx + y, xRadius, yRadius, clipBoundsPx, color)
    }

    fun UiPrimitiveMesh.localRectBorder(x: Float, y: Float, width: Float, height: Float, borderWidth: Float, color: Color) {
        rectBorder(leftPx + x, topPx + y, width, height, borderWidth, clipBoundsPx, color)
    }

    fun UiPrimitiveMesh.localRoundRectBorder(x: Float, y: Float, width: Float, height: Float, radius: Float, borderWidth: Float, color: Color) {
        roundRectBorder(leftPx + x, topPx + y, width, height, radius, borderWidth, clipBoundsPx, color)
    }

    fun UiPrimitiveMesh.localCircleBorder(x: Float, y: Float, radius: Float, borderWidth: Float, color: Color) {
        circleBorder(leftPx + x, topPx + y, radius, borderWidth, clipBoundsPx, color)
    }

    fun UiPrimitiveMesh.localOvalBorder(x: Float, y: Float, xRadius: Float, yRadius: Float, borderWidth: Float, color: Color) {
        ovalBorder(leftPx + x, topPx + y, xRadius, yRadius, borderWidth, clipBoundsPx, color)
    }

    fun UiPrimitiveMesh.localRectGradient(
        x: Float, y: Float, width: Float, height: Float,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        rect(
            leftPx + x, topPx + y, width, height, clipBoundsPx,
            colorA, colorB, leftPx + gradientCx, topPx + gradientCy, gradientRx, gradientRy
        )
    }

    fun UiPrimitiveMesh.localRoundRectGradient(
        x: Float, y: Float, width: Float, height: Float, radius: Float,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        roundRect(
            leftPx + x, topPx + y, width, height, radius, clipBoundsPx,
            colorA, colorB, leftPx + gradientCx, topPx + gradientCy, gradientRx, gradientRy
        )
    }

    fun UiPrimitiveMesh.localCircleGradient(
        x: Float, y: Float, radius: Float,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        circle(
            leftPx + x, topPx + y, radius, clipBoundsPx,
            colorA, colorB, leftPx + gradientCx, topPx + gradientCy, gradientRx, gradientRy
        )
    }

    fun UiPrimitiveMesh.localOvalGradient(
        x: Float, y: Float, xRadius: Float, yRadius: Float,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        oval(
            leftPx + x, topPx + y, xRadius, yRadius, clipBoundsPx,
            colorA, colorB, leftPx + gradientCx, topPx + gradientCy, gradientRx, gradientRy
        )
    }

    fun UiPrimitiveMesh.localRectBorderGradient(
        x: Float, y: Float, width: Float, height: Float, borderWidth: Float,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        rectBorder(
            leftPx + x, topPx + y, width, height, borderWidth, clipBoundsPx,
            colorA, colorB, leftPx + gradientCx, topPx + gradientCy, gradientRx, gradientRy
        )
    }

    fun UiPrimitiveMesh.localRoundRectBorderGradient(
        x: Float, y: Float, width: Float, height: Float, radius: Float, borderWidth: Float,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        roundRectBorder(
            leftPx + x, topPx + y, width, height, radius, borderWidth, clipBoundsPx,
            colorA, colorB, leftPx + gradientCx, topPx + gradientCy, gradientRx, gradientRy
        )
    }

    fun UiPrimitiveMesh.localCircleBorderGradient(
        x: Float, y: Float, radius: Float, borderWidth: Float,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        circleBorder(
            leftPx + x, topPx + y, radius, borderWidth, clipBoundsPx,
            colorA, colorB, leftPx + gradientCx, topPx + gradientCy, gradientRx, gradientRy
        )
    }

    fun UiPrimitiveMesh.localOvalBorderGradient(
        x: Float, y: Float, xRadius: Float, yRadius: Float, borderWidth: Float,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        ovalBorder(
            leftPx + x, topPx + y, xRadius, yRadius, borderWidth, clipBoundsPx,
            colorA, colorB, leftPx + gradientCx, topPx + gradientCy, gradientRx, gradientRy
        )
    }
}