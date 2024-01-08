package de.fabmax.kool.editor.ui

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logD
import kotlin.math.roundToInt

object IconMap {

    private var windowScale = 1f
    private val iconMapSize = Vec2i(480, 480)
    private val iconTexProps = TextureProps(
        format = TexFormat.RGBA,
        generateMipMaps = false,
        defaultSamplerSettings = SamplerSettings().nearest()
    )

    val small = EditorIconMap(Dp(18f))
    val medium = EditorIconMap(Dp(24f))

    init {
        val ctx = KoolSystem.requireContext()
        windowScale = ctx.windowScale
        ctx.onWindowScaleChanged += {
            windowScale = it.windowScale
            small.invalidate()
            medium.invalidate()
        }
    }

    class EditorIconMap(val iconSize: Dp) {
        private val iconLoader = AsyncTextureLoader {
            val s = iconSize.value / 24f * windowScale
            val width = (iconMapSize.x * s).roundToInt()
            val height = (iconMapSize.y * s).roundToInt()
            val loadProps = iconTexProps.copy(resolveSize = Vec2i(width, height))
            logD { "Render icon map: $width x $height" }
            Assets.loadTextureData("assets/icons/icons-24px.svg", loadProps)
        }

        private val iconTex = Texture2d(iconTexProps, "icon-map", iconLoader)
        private val iconMap = ImageIconMap(20)

        init {
            iconMap[24] = iconTex
        }

        fun invalidate() {
            if (iconTex.loadingState == Texture.LoadingState.LOADED) {
                iconLoader.invalidate()
                iconTex.dispose()
            }
        }

        val CUBE = IconProvider(this, iconMap.IconImageProvider(0, 0))
        val TREE = IconProvider(this, iconMap.IconImageProvider(1, 0))
        val LIGHT = IconProvider(this, iconMap.IconImageProvider(2, 0))
        val CAMERA = IconProvider(this, iconMap.IconImageProvider(3, 0))
        val CIRCLE_DOT = IconProvider(this, iconMap.IconImageProvider(4, 0))
        val CIRCLE_CROSSHAIR = IconProvider(this, iconMap.IconImageProvider(5, 0))
        val RECT_CROSSHAIR = IconProvider(this, iconMap.IconImageProvider(6, 0))
        val PICTURE = IconProvider(this, iconMap.IconImageProvider(7, 0))
        val SCALE = IconProvider(this, iconMap.IconImageProvider(8, 0))
        val ROTATE = IconProvider(this, iconMap.IconImageProvider(9, 0))
        val MOVE = IconProvider(this, iconMap.IconImageProvider(10, 0))
        val TRASH = IconProvider(this, iconMap.IconImageProvider(11, 0))
        val WORLD = IconProvider(this, iconMap.IconImageProvider(12, 0))
        val PALETTE = IconProvider(this, iconMap.IconImageProvider(13, 0))
        val EYE = IconProvider(this, iconMap.IconImageProvider(14, 0))
        val EYE_OFF = IconProvider(this, iconMap.IconImageProvider(15, 0))
        val GRAB = IconProvider(this, iconMap.IconImageProvider(16, 0))
        val RECT_TOP_LT = IconProvider(this, iconMap.IconImageProvider(17, 0))
        val RECT_INSIDE = IconProvider(this, iconMap.IconImageProvider(18, 0))
        val RECT_OUTSIDE = IconProvider(this, iconMap.IconImageProvider(19, 0))
        val DUPLICATE = IconProvider(this, iconMap.IconImageProvider(0, 1))
        val UNDO = IconProvider(this, iconMap.IconImageProvider(1, 1))
        val REDO = IconProvider(this, iconMap.IconImageProvider(2, 1))
        val CODE = IconProvider(this, iconMap.IconImageProvider(3, 1))
        val CODE_FOLDER = IconProvider(this, iconMap.IconImageProvider(4, 1))
        val CODE_FILE = IconProvider(this, iconMap.IconImageProvider(5, 1))
        val DOUBLE_HEX = IconProvider(this, iconMap.IconImageProvider(6, 1))
        val PROPERTIES = IconProvider(this, iconMap.IconImageProvider(7, 1))
        val CONNECTED_CIRCLES = IconProvider(this, iconMap.IconImageProvider(8, 1))
        val QUAD_BOX = IconProvider(this, iconMap.IconImageProvider(9, 1))
        val SELECT = IconProvider(this, iconMap.IconImageProvider(10, 1))
        val SELECT_OFF = IconProvider(this, iconMap.IconImageProvider(11, 1))
        val SELECT_PLUS = IconProvider(this, iconMap.IconImageProvider(12, 1))
        val NODE_V = IconProvider(this, iconMap.IconImageProvider(13, 1))
        val NODE_H = IconProvider(this, iconMap.IconImageProvider(14, 1))
        val NODE_CIRCLE = IconProvider(this, iconMap.IconImageProvider(15, 1))
        val FOLDER = IconProvider(this, iconMap.IconImageProvider(16, 1))
        val FOLDER_OPEN = IconProvider(this, iconMap.IconImageProvider(17, 1))
        val SHADOW_INNER = IconProvider(this, iconMap.IconImageProvider(18, 1))
        val SHADOW = IconProvider(this, iconMap.IconImageProvider(19, 1))
        val TRANSFORM = IconProvider(this, iconMap.IconImageProvider(0, 2))
        val BACKGROUND = IconProvider(this, iconMap.IconImageProvider(1, 2))
        val LIST_TREE = IconProvider(this, iconMap.IconImageProvider(2, 2))
        val DOWNLOAD = IconProvider(this, iconMap.IconImageProvider(3, 2))
        val CONSOLE = IconProvider(this, iconMap.IconImageProvider(4, 2))
        val GITHUB = IconProvider(this, iconMap.IconImageProvider(5, 2))
        val MAGNET = IconProvider(this, iconMap.IconImageProvider(6, 2))
        val MAGNET_OFF = IconProvider(this, iconMap.IconImageProvider(7, 2))
        val LOCK_OPEN = IconProvider(this, iconMap.IconImageProvider(8, 2))
        val LOCK = IconProvider(this, iconMap.IconImageProvider(9, 2))
        val SCROLL_LOCK = IconProvider(this, iconMap.IconImageProvider(10, 2))
        val COPY = IconProvider(this, iconMap.IconImageProvider(11, 2))
        val PASTE = IconProvider(this, iconMap.IconImageProvider(12, 2))
        val SAVE = IconProvider(this, iconMap.IconImageProvider(13, 2))
        val SETTINGS = IconProvider(this, iconMap.IconImageProvider(14, 2))
        val FILTER = IconProvider(this, iconMap.IconImageProvider(15, 2))
        val SEARCH = IconProvider(this, iconMap.IconImageProvider(16, 2))
        val PLUS = IconProvider(this, iconMap.IconImageProvider(17, 2))
        val MINUS = IconProvider(this, iconMap.IconImageProvider(18, 2))
        val EDIT = IconProvider(this, iconMap.IconImageProvider(19, 2))
    }
}

class IconProvider(val iconMap: IconMap.EditorIconMap, val provider: ImageIconMap.IconImageProvider)

fun ImageModifier.iconImage(iconProvider: IconProvider, tintColor: Color? = null): ImageModifier {
    size(iconProvider.iconMap.iconSize, iconProvider.iconMap.iconSize)
    imageProvider(iconProvider.provider)
    tintColor?.let { tint(it) }
    return this
}
