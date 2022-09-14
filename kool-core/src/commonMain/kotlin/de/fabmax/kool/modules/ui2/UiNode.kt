package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logD
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

abstract class UiNode(val parent: UiNode?, override val surface: UiSurface) : UiScope {
    override val uiNode: UiNode get() = this

    val layer: Int = parent?.let { it.layer + 1 } ?: 0

    private val oldChildren = mutableListOf<UiNode>()
    private val mutChildren = mutableListOf<UiNode>()
    val children: List<UiNode> get() = mutChildren

    var contentWidth = 0f
        private set
    var contentHeight = 0f
        private set

    var minX = 0f
        private set
    var minY = 0f
        private set
    var maxX = 0f
        private set
    var maxY = 0f
        private set
    val width: Float get() = maxX - minX
    val height: Float get() = maxY - minY

    val clipBounds = MutableVec4f()
    val clippedMinX: Float get() = clipBounds.x
    val clippedMinY: Float get() = clipBounds.y
    val clippedMaxX: Float get() = clipBounds.z
    val clippedMaxY: Float get() = clipBounds.w
    val isInBounds: Boolean get() = clippedMaxX - clippedMinX > 0.5f && clippedMaxY - clippedMinY > 0.5f

    val paddingStart: Float get() = modifier.paddingStart.px
    val paddingEnd: Float get() = modifier.paddingEnd.px
    val paddingTop: Float get() = modifier.paddingTop.px
    val paddingBottom: Float get() = modifier.paddingBottom.px

    val marginStart: Float get() = modifier.marginStart.px
    val marginEnd: Float get() = modifier.marginEnd.px
    val marginTop: Float get() = modifier.marginTop.px
    val marginBottom: Float get() = modifier.marginBottom.px

    protected val setBoundsVertexMod: VertexView.() -> Unit = {
        getVec4fAttribute(Ui2Shader.ATTRIB_CLIP)?.set(clippedMinX, clippedMinY, clippedMaxX, clippedMaxY)
    }

    fun toLocal(screenX: Double, screenY: Double) = Vec2f(screenX.toFloat() - minX, screenY.toFloat() - minY)
    fun toLocal(screenX: Float, screenY: Float) = Vec2f(screenX - minX, screenY - minY)

    open fun setContentSize(width: Float, height: Float) {
        contentWidth = width
        contentHeight = height
    }

    open fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        this.minX = minX
        this.minY = minY
        this.maxX = maxX
        this.maxY = maxY

        if (parent != null) {
            clipBounds.x = max(parent.clippedMinX, minX)
            clipBounds.y = max(parent.clippedMinY, minY)
            clipBounds.z = min(parent.clippedMaxX, maxX)
            clipBounds.w = min(parent.clippedMaxY, maxY)
        } else {
            clipBounds.x = minX
            clipBounds.y = minY
            clipBounds.z = maxX
            clipBounds.w = maxY
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

    protected open fun resetDefaults() {
        oldChildren.clear()
        for (i in mutChildren.lastIndex downTo 0) {
            oldChildren += mutChildren[i]
        }
        mutChildren.clear()
        modifier.resetDefaults()
    }

    fun <T: UiNode> createChild(type: KClass<T>, factory: (UiNode, UiSurface) -> T): T {
        var child: T? = null
        if (oldChildren.isNotEmpty()) {
            val old = oldChildren.removeLast()
            if (old::class === type) {
                old.resetDefaults()
                @Suppress("UNCHECKED_CAST")
                child = old as T
            }
        }
        if (child == null) {
            logD { "Creating new child node of type $type" }
            child = factory(this, surface)
        }
        mutChildren += child
        return child
    }

    protected inline fun MeshBuilder.configured(color: Color?, block: MeshBuilder.() -> Unit) {
        val prevMod = vertexModFun
        vertexModFun = setBoundsVertexMod
        val prevColor = this.color
        color?.let { this.color = it }

        withTransform {
            translate(minX, minY, 0f)
            this.block()
        }

        this.vertexModFun = prevMod
        this.color = prevColor
    }

    fun UiPrimitiveMesh.localRect(x: Float, y: Float, width: Float, height: Float, color: Color) {
        rect(minX + x, minY + y, width, height, color, clipBounds)
    }

    fun UiPrimitiveMesh.localRoundRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Color) {
        roundRect(minX + x, minY + y, width, height, radius, color, clipBounds)
    }

    fun UiPrimitiveMesh.localCircle(x: Float, y: Float, radius: Float, color: Color) {
        circle(minX + x, minY + y, radius, color, clipBounds)
    }

    fun UiPrimitiveMesh.localOval(x: Float, y: Float, xRadius: Float, yRadius: Float, color: Color) {
        oval(minX + x, minY + y, xRadius, yRadius, color, clipBounds)
    }

    fun UiPrimitiveMesh.localRectBorder(x: Float, y: Float, width: Float, height: Float, borderWidth: Float, color: Color) {
        rectBorder(minX + x, minY + y, width, height, borderWidth, color, clipBounds)
    }

    fun UiPrimitiveMesh.localRoundRectBorder(x: Float, y: Float, width: Float, height: Float, radius: Float, borderWidth: Float, color: Color) {
        roundRectBorder(minX + x, minY + y, width, height, radius, borderWidth, color, clipBounds)
    }

    fun UiPrimitiveMesh.localCircleBorder(x: Float, y: Float, radius: Float, borderWidth: Float, color: Color) {
        circleBorder(minX + x, minY + y, radius, borderWidth, color, clipBounds)
    }

    fun UiPrimitiveMesh.localOvalBorder(x: Float, y: Float, xRadius: Float, yRadius: Float, borderWidth: Float, color: Color) {
        ovalBorder(minX + x, minY + y, xRadius, yRadius, borderWidth, color, clipBounds)
    }
}