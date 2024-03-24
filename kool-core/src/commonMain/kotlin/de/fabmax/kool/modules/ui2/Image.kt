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
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.Color
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.min

interface ImageScope : UiScope {
    override val modifier: ImageModifier
}

open class ImageModifier(surface: UiSurface) : UiModifier(surface) {
    var imageProvider: ImageProvider? by property(null)
    var tint: Color by property(Color.WHITE)
    var customShader: DrawShader? by property(null)
    var imageZ: Int by property(0)
    var imageSize: ImageSize by property(ImageSize.FitContent)
}

fun <T: ImageModifier> T.image(image: Texture2d?): T { this.imageProvider = FlatImageProvider(image); return this }
fun <T: ImageModifier> T.imageProvider(imageProvider: ImageProvider?): T { this.imageProvider = imageProvider; return this }
fun <T: ImageModifier> T.tint(color: Color): T { this.tint = color; return this }
fun <T: ImageModifier> T.imageZ(imageZ: Int): T { this.imageZ = imageZ; return this }
fun <T: ImageModifier> T.imageSize(size: ImageSize): T { this.imageSize = size; return this }

fun <T: ImageModifier> T.customShader(shader: DrawShader): T { customShader = shader; return this }

sealed class ImageSize {
    object Stretch : ImageSize()
    object ZoomContent : ImageSize()
    object FitContent : ImageSize()
    class FixedScale(val scale: Float = 1f) : ImageSize()
}

inline fun UiScope.Image(
    imageTex: Texture2d? = null,
    scopeName: String? = null,
    block: ImageScope.() -> Unit
): ImageScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val image = uiNode.createChild(scopeName, ImageNode::class, ImageNode.factory)
    image.modifier.image(imageTex)
    image.block()
    return image
}

class ImageNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ImageScope {
    override val modifier = ImageModifier(surface)

    val imageWidth = mutableStateOf(1f)
    val imageHeight = mutableStateOf(1f)

    private val imgUvTpLt: Vec2f get() = modifier.imageProvider?.uvTopLeft ?: Vec2f.ZERO
    private val imgUvTpRt: Vec2f get() = modifier.imageProvider?.uvTopRight ?: Vec2f.ZERO
    private val imgUvBtLt: Vec2f get() = modifier.imageProvider?.uvBottomLeft ?: Vec2f.ZERO
    private val imgUvBtRt: Vec2f get() = modifier.imageProvider?.uvBottomRight ?: Vec2f.ZERO
    private var imageAr = 1f

    override fun measureContentSize(ctx: KoolContext) {
        imageAr = 1f
        if (modifier.imageSize != ImageSize.Stretch) {
            imageAr = modifier.imageProvider?.getImageAspectRatio() ?: 1f
            updateImageSize()
            if (modifier.imageProvider?.isDynamicSize == true) {
                surface.onEachFrame { updateImageSize() }
            }
        }

        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth: Float
        val measuredHeight: Float

        val padH = paddingStartPx + paddingEndPx
        val padV = paddingTopPx + paddingBottomPx

        when {
            modWidth is Dp && modHeight is Dp -> {
                measuredWidth = modWidth.px
                measuredHeight = modHeight.px
            }
            modWidth is Dp && modHeight !is Dp -> {
                // fixed width, measured height depends on width and chosen image size mode
                measuredWidth = modWidth.px
                measuredHeight = imageHeight(measuredWidth - padH, imageAr) + padV
            }
            modWidth !is Dp && modHeight is Dp -> {
                // fixed height, measured width depends on height and chosen image size mode
                measuredHeight = modHeight.px
                measuredWidth = imageWidth(measuredHeight - padV, imageAr) + padH
            }
            else -> {
                // dynamic (fit / grow) width and height
                val scale = (modifier.imageSize as? ImageSize.FixedScale)?.scale ?: 1f
                measuredWidth = imageWidth.use() * scale + padH
                measuredHeight = imageHeight.use() * scale + padV
            }
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    private fun updateImageSize() {
        imageWidth.set(max(1f, modifier.imageProvider?.getImageWidth() ?: 1f))
        imageHeight.set(max(1f, modifier.imageProvider?.getImageHeight() ?: 1f))
    }

    private fun imageWidth(measuredHeightPx: Float, imageAr: Float): Float {
        // make sure we use() image dimensions, so we get updated when they change
        imageWidth.use()
        val imgH = imageHeight.use()
        return when (val sz = modifier.imageSize) {
            ImageSize.Stretch -> measuredHeightPx
            ImageSize.FitContent -> measuredHeightPx * imageAr
            ImageSize.ZoomContent -> measuredHeightPx * imageAr
            is ImageSize.FixedScale -> imgH * sz.scale
        }
    }

    private fun imageHeight(measuredWidthPx: Float, imageAr: Float): Float {
        // make sure we use() image dimensions, so we get updated when they change
        imageHeight.use()
        val imgW = imageWidth.use()
        return when (val sz = modifier.imageSize) {
            ImageSize.Stretch -> measuredWidthPx
            ImageSize.FitContent -> measuredWidthPx / imageAr
            ImageSize.ZoomContent -> measuredWidthPx / imageAr
            is ImageSize.FixedScale -> imgW * sz.scale
        }
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        modifier.imageProvider?.getTexture(innerWidthPx, innerHeightPx)?.let {
            val imgMesh = surface.getMeshLayer(modifier.zLayer + modifier.imageZ).addImage(it)
            imgMesh.builder.clear()
            imgMesh.builder.configured(modifier.tint) {
                rect {
                    isCenteredOrigin = false

                    texCoordLowerLeft.set(imgUvTpLt)
                    texCoordLowerRight.set(imgUvTpRt)
                    texCoordUpperLeft.set(imgUvBtLt)
                    texCoordUpperRight.set(imgUvBtRt)

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
            imgMesh.applyShader(it, modifier.customShader)
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ImageNode = { parent, surface -> ImageNode(parent, surface) }
    }
}

class ImageMesh(name: String) : Mesh(IndexedVertexList(Ui2Shader.UI_MESH_ATTRIBS), name = name) {
    var defaultImageShader: ImageShader? = null
    val builder = MeshBuilder(geometry).apply { isInvertFaceOrientation = true }

    init {
        geometry.usage = Usage.DYNAMIC
        isCastingShadow = false
    }

    private fun getOrCreateDefaultShader(imageTex: Texture2d?): ImageShader {
        val shader = defaultImageShader ?: ImageShader().also { defaultImageShader = it }
        shader.image = imageTex
        return shader
    }

    fun applyShader(imageTex: Texture2d?, modShader: DrawShader?) {
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
                    val color = float4Var(sampleTexture(texture2d("uImageTex"), texCoords.output) * tint.output)
                    `if` (all(screenPos.output gt clipBounds.output.xy) and
                            all(screenPos.output lt clipBounds.output.zw)) {
                        colorOutput(color.rgb * color.a, color.a)
                    }.`else` {
                        discard()
                    }
                }
            }
        }
    }

    companion object {
        private val pipelineConfig = PipelineConfig(
            blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS
        )
    }
}