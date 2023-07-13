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
        minFilter = FilterMethod.NEAREST,
        magFilter = FilterMethod.NEAREST,
        mipMapping = false,
        maxAnisotropy = 1
    )

    val iconSize = Dp(18f)

    private val iconLoader = AsyncTextureLoader {
        val s = iconSize.value / 24f * windowScale
        val width = (iconMapSize.x * s).roundToInt()
        val height = (iconMapSize.y * s).roundToInt()
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

    val CUBE = iconMap.IconImageProvider(0, 0)
    val TREE = iconMap.IconImageProvider(1, 0)
    val LIGHT = iconMap.IconImageProvider(2, 0)
    val CAMERA = iconMap.IconImageProvider(3, 0)
    val CIRCLE_DOT = iconMap.IconImageProvider(4, 0)
    val CIRCLE_CROSSHAIR = iconMap.IconImageProvider(5, 0)
    val RECT_CROSSHAIR = iconMap.IconImageProvider(6, 0)
    val PICTURE = iconMap.IconImageProvider(7, 0)
    val SCALE = iconMap.IconImageProvider(8, 0)
    val ROTATE = iconMap.IconImageProvider(9, 0)
    val MOVE = iconMap.IconImageProvider(10, 0)
    val TRASH = iconMap.IconImageProvider(11, 0)
    val WORLD = iconMap.IconImageProvider(12, 0)
    val PALETTE = iconMap.IconImageProvider(13, 0)
    val EYE = iconMap.IconImageProvider(14, 0)
    val EYE_OFF = iconMap.IconImageProvider(15, 0)
    val GRAB = iconMap.IconImageProvider(16, 0)
    val SELECT_TOP_LT = iconMap.IconImageProvider(17, 0)
    val SELECT_INSIDE = iconMap.IconImageProvider(18, 0)
    val SELECT_OUTSIDE = iconMap.IconImageProvider(19, 0)
    val DUPLICATE = iconMap.IconImageProvider(0, 1)
    val UNDO = iconMap.IconImageProvider(1, 1)
    val REDO = iconMap.IconImageProvider(2, 1)
    val CODE = iconMap.IconImageProvider(3, 1)
    val CODE_FOLDER = iconMap.IconImageProvider(4, 1)
    val CODE_FILE = iconMap.IconImageProvider(5, 1)
    val DOUBLE_HEX = iconMap.IconImageProvider(6, 1)
    val PROPERTIES = iconMap.IconImageProvider(7, 1)
    val CONNECTED_CIRCLES = iconMap.IconImageProvider(8, 1)
    val QUAD_BOX = iconMap.IconImageProvider(9, 1)
    val SELECT = iconMap.IconImageProvider(10, 1)
    val SELECT_OFF = iconMap.IconImageProvider(11, 1)
    val SELECT_PLUS = iconMap.IconImageProvider(12, 1)
    val NODE_V = iconMap.IconImageProvider(13, 1)
    val NODE_H = iconMap.IconImageProvider(14, 1)
    val NODE_CIRCLE = iconMap.IconImageProvider(15, 1)
    val FOLDER = iconMap.IconImageProvider(16, 1)
    val FOLDER_OPEN = iconMap.IconImageProvider(17, 1)
    val SHADOW_INNER = iconMap.IconImageProvider(18, 1)
    val SHADOW = iconMap.IconImageProvider(19, 1)
    val TRANSFORM = iconMap.IconImageProvider(0, 2)
    val BACKGROUND = iconMap.IconImageProvider(1, 2)
}

fun ImageModifier.iconImage(imgProvider: ImageIconMap.IconImageProvider, tintColor: Color? = null): ImageModifier {
    size(IconMap.iconSize, IconMap.iconSize)
    imageProvider(imgProvider)
    tintColor?.let { tint(it) }
    return this
}
