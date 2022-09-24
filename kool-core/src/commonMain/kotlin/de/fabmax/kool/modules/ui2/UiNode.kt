package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.scene.ui.FontProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logD
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

abstract class UiNode(val parent: UiNode?, override val surface: UiSurface) : UiScope {
    override val uiNode: UiNode get() = this

    var nodeIndex = 0
        private set

    private val oldChildren = mutableListOf<UiNode>()
    private val mutChildren = mutableListOf<UiNode>()
    val children: List<UiNode> get() = mutChildren

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

        if (modifier.onPositioned.isNotEmpty()) {
            modifier.onPositioned.forEach { it(this) }
        }
    }

    fun computeWidthFromDimension(scaledGrowSpace: Float): Float {
        return dimensionToPx(modifier.width, contentWidthPx, scaledGrowSpace, true)
    }

    fun computeHeightFromDimension(scaledGrowSpace: Float): Float {
        return dimensionToPx(modifier.height, contentHeightPx, scaledGrowSpace, true)
    }

    private fun dimensionToPx(dim: Dimension, contentPx: Float, scaledGrowSpace: Float, isGrowAllowed: Boolean): Float {
        return when (dim) {
            WrapContent -> contentPx
            is Dp -> dim.px
            is Grow -> {
                if (isGrowAllowed) {
                    val min = dimensionToPx(dim.min, contentPx, 0f, false)
                    val max = dimensionToPx(dim.max, contentPx, 0f, false)
                    (scaledGrowSpace * dim.weight).clamp(min, max)
                } else {
                    0f
                }
            }
        }
    }

    fun computeChildLocationX(child: UiNode, measuredChildWidth: Float): Float {
        return leftPx + when (child.modifier.alignX) {
            AlignmentX.Start -> max(paddingStartPx, child.marginStartPx)
            AlignmentX.Center -> (widthPx - measuredChildWidth) * 0.5f
            AlignmentX.End -> widthPx - measuredChildWidth - max(paddingEndPx, child.marginEndPx)
        }
    }

    fun computeChildLocationY(child: UiNode, measuredChildHeight: Float): Float {
        return topPx + when (child.modifier.alignY) {
            AlignmentY.Top -> max(paddingTopPx, child.marginTopPx)
            AlignmentY.Center -> (heightPx - measuredChildHeight) * 0.5f
            AlignmentY.Bottom -> heightPx - measuredChildHeight - max(paddingBottomPx, child.marginBottomPx)
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
    }

    fun <T: UiNode> createChild(type: KClass<T>, factory: (UiNode, UiSurface) -> T): T {
        var child: T? = null
        if (oldChildren.isNotEmpty()) {
            val old = oldChildren.removeLast()
            if (old::class === type) {
                @Suppress("UNCHECKED_CAST")
                child = old as T
            }
        }
        if (child == null) {
            logD { "Creating new child node of type $type" }
            child = factory(this, surface)
        }
        child.applyDefaults()
        mutChildren += child
        return child
    }

    inline fun MeshBuilder.configured(color: Color?, block: MeshBuilder.() -> Unit) {
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

    fun getTextBuilder(fontProps: FontProps, ctx: KoolContext, layerOffset: Int = 0): MeshBuilder {
        return surface.getMeshLayer(modifier.zLayer + layerOffset).getTextBuilder(fontProps, ctx)
    }

    fun UiPrimitiveMesh.localRect(x: Float, y: Float, width: Float, height: Float, color: Color) {
        rect(leftPx + x, topPx + y, width, height, color, clipBoundsPx)
    }

    fun UiPrimitiveMesh.localRoundRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Color) {
        roundRect(leftPx + x, topPx + y, width, height, radius, color, clipBoundsPx)
    }

    fun UiPrimitiveMesh.localCircle(x: Float, y: Float, radius: Float, color: Color) {
        circle(leftPx + x, topPx + y, radius, color, clipBoundsPx)
    }

    fun UiPrimitiveMesh.localOval(x: Float, y: Float, xRadius: Float, yRadius: Float, color: Color) {
        oval(leftPx + x, topPx + y, xRadius, yRadius, color, clipBoundsPx)
    }

    fun UiPrimitiveMesh.localRectBorder(x: Float, y: Float, width: Float, height: Float, borderWidth: Float, color: Color) {
        rectBorder(leftPx + x, topPx + y, width, height, borderWidth, color, clipBoundsPx)
    }

    fun UiPrimitiveMesh.localRoundRectBorder(x: Float, y: Float, width: Float, height: Float, radius: Float, borderWidth: Float, color: Color) {
        roundRectBorder(leftPx + x, topPx + y, width, height, radius, borderWidth, color, clipBoundsPx)
    }

    fun UiPrimitiveMesh.localCircleBorder(x: Float, y: Float, radius: Float, borderWidth: Float, color: Color) {
        circleBorder(leftPx + x, topPx + y, radius, borderWidth, color, clipBoundsPx)
    }

    fun UiPrimitiveMesh.localOvalBorder(x: Float, y: Float, xRadius: Float, yRadius: Float, borderWidth: Float, color: Color) {
        ovalBorder(leftPx + x, topPx + y, xRadius, yRadius, borderWidth, color, clipBoundsPx)
    }
}