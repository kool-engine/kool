package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

interface ImageScope : UiScope {
    override val modifier: ImageModifier
}

open class ImageModifier(surface: UiSurface) : UiModifier(surface) {
    var image: Texture2d? by property(null)
    var tint: Color by property(Color.WHITE)
    var customShader: Shader? by property(null)
    var imageZ: Int by property(0)
    var imageSize: ImageSize by property(ImageSize.FitContent)
    var uvTopLeft: Vec2f by property(Vec2f(0f, 0f))
    var uvBottomLeft: Vec2f by property(Vec2f(0f, 1f))
    var uvBottomRight: Vec2f by property(Vec2f(1f, 1f))
    var uvTopRight: Vec2f by property(Vec2f(1f, 0f))
}

fun <T: ImageModifier> T.image(image: Texture2d?): T { this.image = image; return this }
fun <T: ImageModifier> T.tint(color: Color): T { this.tint = color; return this }
fun <T: ImageModifier> T.imageZ(imageZ: Int): T { this.imageZ = imageZ; return this }
fun <T: ImageModifier> T.imageSize(size: ImageSize): T { this.imageSize = size; return this }
fun <T: ImageModifier> T.uv(topLeft: Vec2f, topRight: Vec2f, bottomLeft: Vec2f, bottomRight: Vec2f): T {
    uvTopLeft = topLeft
    uvTopRight = topRight
    uvBottomLeft = bottomLeft
    uvBottomRight = bottomRight
    return this
}
fun <T: ImageModifier> T.uv(topLeft: Vec2f, width: Float, height: Float): T {
    uvTopLeft = topLeft
    uvTopRight = Vec2f(topLeft.x + width, topLeft.y)
    uvBottomLeft = Vec2f(topLeft.x, topLeft.y + height)
    uvBottomRight = Vec2f(topLeft.x + width, topLeft.y + height)
    return this
}

fun <T: ImageModifier> T.mirror(x: Boolean = false, y: Boolean = false): T {
    if (x) {
        uvTopLeft = uvTopRight.also { uvTopRight = uvTopLeft }
        uvBottomLeft = uvBottomRight.also { uvBottomRight = uvBottomLeft }
    }
    if (y) {
        uvTopLeft = uvBottomLeft.also { uvBottomLeft = uvTopLeft }
        uvTopRight = uvBottomRight.also { uvBottomRight = uvTopRight }
    }
    return this
}

fun <T: ImageModifier> T.customShader(shader: Shader): T { customShader = shader; return this }

sealed class ImageSize {
    object Stretch : ImageSize()
    object ZoomContent : ImageSize()
    object FitContent : ImageSize()
    class FixedScale(val scale: Float = 1f) : ImageSize()
}

inline fun UiScope.Image(imageTex: Texture2d? = null, block: ImageScope.() -> Unit): ImageScope {
    val image = uiNode.createChild(ImageNode::class, ImageNode.factory)
    image.modifier.image(imageTex)
    image.block()
    return image
}

class ImageNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ImageScope {
    override val modifier = ImageModifier(surface)

    val imageWidth = mutableStateOf(1f)
    val imageHeight = mutableStateOf(1f)

    private var imageAr = 1f

    override fun measureContentSize(ctx: KoolContext) {
        imageAr = 1f
        if (modifier.imageSize != ImageSize.Stretch) {
            imageAr = computeImageAr()
            surface.onEachFrame {
                imageWidth.set(max(1f, modifier.image?.loadedTexture?.width?.toFloat() ?: 1f))
                imageHeight.set(max(1f, modifier.image?.loadedTexture?.height?.toFloat() ?: 1f))
            }
        }

        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth: Float
        val measuredHeight: Float

        when {
            modWidth is Dp && modHeight is Dp -> {
                measuredWidth = modWidth.px
                measuredHeight = modHeight.px
            }
            modWidth is Dp && modHeight !is Dp -> {
                // fixed width, measured height depends on width and chosen image size mode
                measuredWidth = modWidth.px
                measuredHeight = imageHeight(measuredWidth, imageAr)
            }
            modWidth !is Dp && modHeight is Dp -> {
                // fixed height, measured width depends on height and chosen image size mode
                measuredHeight = modHeight.px
                measuredWidth = imageWidth(measuredHeight, imageAr)
            }
            else -> {
                // dynamic (fit / grow) width and height
                val scale = (modifier.imageSize as? ImageSize.FixedScale)?.scale ?: 1f
                measuredWidth = imageWidth.value * scale
                measuredHeight = imageHeight.value * scale
            }
        }
        setContentSize(
            measuredWidth + paddingStartPx + paddingEndPx,
            measuredHeight + paddingTopPx + paddingBottomPx
        )
    }

    private fun computeImageAr(): Float {
        val uvWidth = abs(modifier.uvBottomRight.x - modifier.uvBottomLeft.x)
        val uvHeight = abs(modifier.uvTopLeft.y - modifier.uvBottomLeft.y)
        val uvAr = uvWidth / uvHeight
        return imageWidth.use() / imageHeight.use() * uvAr
    }

    private fun imageWidth(measuredHeightPx: Float, imageAr: Float): Float {
        return when (val sz = modifier.imageSize) {
            ImageSize.Stretch -> measuredHeightPx
            ImageSize.FitContent -> measuredHeightPx * imageAr
            ImageSize.ZoomContent -> measuredHeightPx * imageAr
            is ImageSize.FixedScale -> imageHeight.value * sz.scale
        }
    }

    private fun imageHeight(measuredWidthPx: Float, imageAr: Float): Float {
        return when (val sz = modifier.imageSize) {
            ImageSize.Stretch -> measuredWidthPx
            ImageSize.FitContent -> measuredWidthPx / imageAr
            ImageSize.ZoomContent -> measuredWidthPx / imageAr
            is ImageSize.FixedScale -> imageWidth.value * sz.scale
        }
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        modifier.image?.let {
            val imgMesh = surface.getMeshLayer(modifier.zLayer + modifier.imageZ).addImage(it)
            imgMesh.builder.clear()
            imgMesh.builder.configured(modifier.tint) {
                rect {
                    texCoordLowerLeft.set(modifier.uvTopLeft)
                    texCoordLowerRight.set(modifier.uvTopRight)
                    texCoordUpperLeft.set(modifier.uvBottomLeft)
                    texCoordUpperRight.set(modifier.uvBottomRight)

                    val imgW = imageWidth.value
                    val imgH = imageHeight.value
                    val cx = widthPx * 0.5f
                    val cy = heightPx * 0.5f

                    when (val sz = modifier.imageSize) {
                        ImageSize.FitContent -> {
                            val s = min(innerWidthPx / imgW, innerHeightPx / imgH)
                            origin.set(cx - imgW * s * 0.5f, cy - imgH * s * 0.5f, 0f)
                            size.set(imgW * s, imgH * s)
                        }
                        ImageSize.ZoomContent -> {
                            val s = max(innerWidthPx / imgW, innerHeightPx / imgH)
                            origin.set(cx - imgW * s * 0.5f, cy - imgH * s * 0.5f, 0f)
                            size.set(imgW * s, imgH * s)
                        }
                        ImageSize.Stretch -> {
                            origin.set(paddingStartPx, paddingTopPx, 0f)
                            size.set(innerWidthPx, innerHeightPx)
                        }
                        is ImageSize.FixedScale -> {
                            val s = sz.scale
                            origin.set(cx - imageWidth.value * s * 0.5f, cy - imageHeight.value * s * 0.5f, 0f)
                            size.set(imageWidth.value * s, imageHeight.value * s)
                        }
                    }
                }
            }
            imgMesh.applyShader(modifier.image, modifier.customShader)
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ImageNode = { parent, surface -> ImageNode(parent, surface) }
    }
}

class ImageMesh : Mesh(IndexedVertexList(Ui2Shader.UI_MESH_ATTRIBS)) {
    var defaultImageShader: ImageShader? = null
    val builder = MeshBuilder(geometry).apply { isInvertFaceOrientation = true }

    private fun getOrCreateDefaultShader(imageTex: Texture2d?): ImageShader {
        val shader = defaultImageShader ?: ImageShader().also { defaultImageShader = it }
        shader.image = imageTex
        return shader
    }

    fun applyShader(imageTex: Texture2d?, modShader: Shader?) {
        shader = modShader ?: getOrCreateDefaultShader(imageTex)
    }

}

class ImageShader : KslShader(Model(), pipelineConfig) {
    var image by texture2d("uImageTex")

    private class Model : KslProgram("UI2 image shader") {
        init {
            val texCoords = interStageFloat2()
            val screenPos = interStageFloat2()
            val tint = interStageFloat4()
            val clipBounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)

            vertexStage {
                main {
                    texCoords.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                    tint.input set vertexAttribFloat4(Attribute.COLORS.name)
                    clipBounds.input set vertexAttribFloat4(Ui2Shader.ATTRIB_CLIP.name)

                    val vertexPos = float4Var(float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f))
                    screenPos.input set vertexPos.xy
                    outPosition set mvpMatrix().matrix * vertexPos
                }
            }
            fragmentStage {
                main {
                    `if` (all(screenPos.output gt clipBounds.output.xy) and
                            all(screenPos.output lt clipBounds.output.zw)) {
                        val color = sampleTexture(texture2d("uImageTex"), texCoords.output) * tint.output
                        colorOutput(color.rgb * color.a, color.a)
                    }.`else` {
                        discard()
                    }
                }
            }
        }
    }

    companion object {
        private val pipelineConfig = PipelineConfig().apply {
            blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
            cullMethod = CullMethod.NO_CULLING
            depthTest = DepthCompareOp.DISABLED
        }
    }
}