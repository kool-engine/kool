package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.util.Color
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

    var clippedMinX = 0f
        private set
    var clippedMinY = 0f
        private set
    var clippedMaxX = 0f
        private set
    var clippedMaxY = 0f
        private set
    val isInBounds: Boolean get() = clippedMaxX - clippedMinX > 0.5f && clippedMaxY - clippedMinY > 0.5f

    val paddingStart: Float get() = modifier.paddingStart.value * surface.measuredScale
    val paddingEnd: Float get() = modifier.paddingEnd.value * surface.measuredScale
    val paddingTop: Float get() = modifier.paddingTop.value * surface.measuredScale
    val paddingBottom: Float get() = modifier.paddingBottom.value * surface.measuredScale

    val marginStart: Float get() = modifier.marginStart.value * surface.measuredScale
    val marginEnd: Float get() = modifier.marginEnd.value * surface.measuredScale
    val marginTop: Float get() = modifier.marginTop.value * surface.measuredScale
    val marginBottom: Float get() = modifier.marginBottom.value * surface.measuredScale

    protected val setBoundsVertexMod: VertexView.() -> Unit = {
        getVec4fAttribute(Ui2Shader.ATTRIB_BOUNDS)?.set(clippedMinX, -clippedMaxY, clippedMaxX, -clippedMinY)
    }

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
            clippedMinX = max(parent.clippedMinX, minX)
            clippedMinY = max(parent.clippedMinY, minY)
            clippedMaxX = min(parent.clippedMaxX, maxX)
            clippedMaxY = min(parent.clippedMaxY, maxY)
        } else {
            clippedMinX = minX
            clippedMinY = minY
            clippedMaxX = maxX
            clippedMaxY = maxY
        }
    }

    open fun render(ctx: KoolContext) {
        modifier.background?.let {
            surface.defaultBuilder.configured(it) {
                rect {
                    size.set(this@UiNode.width, this@UiNode.height)
                }
            }
        }
    }

    open fun measureContentSize(ctx: KoolContext) = CellLayout.measureContentSize(this)

    open fun layoutChildren(ctx: KoolContext) = CellLayout.layoutChildren(this)

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
}