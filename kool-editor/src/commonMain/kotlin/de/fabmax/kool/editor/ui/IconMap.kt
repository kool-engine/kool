package de.fabmax.kool.editor.ui

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.ImageIconMap
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logD
import kotlin.math.roundToInt

class IconMap {

    private var windowScale = 1f

    val iconSize = 24f

    private val iconLoader = AsyncTextureLoader {
        val width = (iconMapSize.x * windowScale).roundToInt()
        val height = (iconMapSize.y * windowScale).roundToInt()
        val loadProps = iconTexProps.copy(preferredSize = Vec2i(width, height))
        logD { "Render icon map: $width x $height" }
        Assets.loadTextureData("assets/icons/icons-24px.svg", loadProps)
    }

    private val iconTex = Texture2d(iconTexProps, "icon-map", iconLoader)
    private val iconMap = ImageIconMap(20)

    init {
        val ctx = KoolSystem.requireContext()
        windowScale = ctx.windowScale
        ctx.onWindowScaleChanged += {
            windowScale = it.windowScale
            if (iconTex.loadingState == Texture.LoadingState.LOADED) {
                iconLoader.invalidate()
                iconTex.dispose()
            }
        }
        iconMap[24] = iconTex
    }

    fun iconProvider(gridPos: Vec2i) = iconMap.IconImageProvider(gridPos.x, gridPos.y)

    companion object {
        private val iconMapSize = Vec2i(480, 480)

        private val iconTexProps = TextureProps(
            format = TexFormat.RGBA,
            minFilter = FilterMethod.NEAREST,
            magFilter = FilterMethod.NEAREST,
            mipMapping = false,
            maxAnisotropy = 1
        )
    }
}