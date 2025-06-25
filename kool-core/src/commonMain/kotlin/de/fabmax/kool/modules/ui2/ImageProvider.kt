package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.SortedMap
import kotlin.math.abs
import kotlin.math.max

interface ImageProvider {
    val uvTopLeft: Vec2f
    val uvTopRight: Vec2f
    val uvBottomLeft: Vec2f
    val uvBottomRight: Vec2f
    fun getTexture(imgWidthPx: Float, imgHeightPx: Float): Texture2d?

    val isDynamicSize: Boolean
    fun getImageWidth(): Float = getTexture(0f, 0f)?.width?.toFloat() ?: 1f
    fun getImageHeight(): Float = getTexture(0f, 0f)?.height?.toFloat() ?: 1f

    fun getImageAspectRatio(): Float {
        val uvWidth = abs(uvBottomRight.x - uvBottomLeft.x)
        val uvHeight = abs(uvTopLeft.y - uvBottomLeft.y)
        val uvAr = uvWidth / uvHeight
        return getImageWidth() / getImageHeight() * uvAr
    }
}

class FlatImageProvider(
    val imageTex: Texture2d?,
    override val isDynamicSize: Boolean = imageTex?.gpuTexture == null
) : ImageProvider {

    override val uvTopLeft = MutableVec2f(0f, 0f)
    override val uvTopRight = MutableVec2f(1f, 0f)
    override val uvBottomLeft = MutableVec2f(0f, 1f)
    override val uvBottomRight = MutableVec2f(1f, 1f)
    override fun getTexture(imgWidthPx: Float, imgHeightPx: Float) = imageTex

    fun mirrorX(): FlatImageProvider {
        var xTmp = uvTopLeft.x
        uvTopLeft.x = uvTopRight.x
        uvTopRight.x = xTmp
        xTmp = uvBottomLeft.x
        uvBottomLeft.x = uvBottomRight.x
        uvBottomRight.x = xTmp
        return this
    }

    fun mirrorY(): FlatImageProvider {
        var yTmp = uvTopLeft.y
        uvTopLeft.y = uvBottomLeft.y
        uvBottomLeft.y = yTmp
        yTmp = uvTopRight.y
        uvTopRight.y = uvBottomRight.y
        uvBottomRight.y = yTmp
        return this
    }
}

class ImageIconMap(val gridSize: Int) {
    private val maps = SortedMap<Int, Texture2d>()
    private var maxSize = 0

    private val providers = Array(gridSize * gridSize) { IconImageProvider(it % gridSize, it / gridSize) }

    operator fun set(iconPxSize: Int, map: Texture2d) {
        maps[iconPxSize] = map
        maxSize = max(maxSize, iconPxSize)
    }

    operator fun get(iconX: Int, iconY: Int): IconImageProvider {
        return providers[iconY * gridSize + iconX]
    }

    operator fun get(iconXy: Vec2i): IconImageProvider {
        return providers[iconXy.y * gridSize + iconXy.x]
    }

    inner class IconImageProvider(x: Int, y: Int) : ImageProvider {
        override val uvTopLeft = MutableVec2f(x / gridSize.toFloat(), y / gridSize.toFloat())
        override val uvTopRight = MutableVec2f((x + 1) / gridSize.toFloat(), y / gridSize.toFloat())
        override val uvBottomLeft = MutableVec2f(x / gridSize.toFloat(), (y + 1) / gridSize.toFloat())
        override val uvBottomRight = MutableVec2f((x + 1) / gridSize.toFloat(), (y + 1) / gridSize.toFloat())

        override val isDynamicSize = false

        override fun getTexture(imgWidthPx: Float, imgHeightPx: Float): Texture2d? {
            val sz = max(imgWidthPx, imgHeightPx).toInt()
            val low = maps.floorEntry(sz)
            val high = maps.ceilingEntry(sz)
            return when {
                low == null && high == null -> null
                low == null -> high!!.value
                high == null -> low.value
                else -> {
                    val dLow = sz - low.key
                    val dHigh = high.key - sz
                    if (dLow < dHigh) low.value else high.value
                }
            }
        }

        override fun getImageWidth() = maxSize.toFloat()
        override fun getImageHeight() = maxSize.toFloat()
        override fun getImageAspectRatio() = 1f
    }
}