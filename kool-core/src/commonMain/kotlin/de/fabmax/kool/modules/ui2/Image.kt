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

interface ImageScope : UiScope {
    override val modifier: ImageModifier
}

open class ImageModifier(surface: UiSurface) : UiModifier(surface) {
    var image: Texture2d? by property(null)
    var tint: Color by property(Color.WHITE)
    var customShader: Shader? by property(null)
    var imageScale: Float by property(1f)
    var uvTopLeft: Vec2f by property(Vec2f(0f, 0f))
    var uvBottomLeft: Vec2f by property(Vec2f(0f, 1f))
    var uvBottomRight: Vec2f by property(Vec2f(1f, 1f))
    var uvTopRight: Vec2f by property(Vec2f(1f, 0f))
}

fun <T: ImageModifier> T.image(image: Texture2d?): T { this.image = image; return this }
fun <T: ImageModifier> T.tint(color: Color): T { this.tint = color; return this }
fun <T: ImageModifier> T.imageScale(scale: Float): T { this.imageScale = scale; return this }
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

inline fun UiScope.Image(imageTex: Texture2d? = null, block: ImageScope.() -> Unit): ImageScope {
    val image = uiNode.createChild(ImageNode::class, ImageNode.factory)
    image.modifier.image(imageTex)
    image.block()
    image.imageWidth.set(image.modifier.image?.loadedTexture?.width?.toFloat() ?: 0f)
    image.imageHeight.set(image.modifier.image?.loadedTexture?.height?.toFloat() ?: 0f)
    return image
}

class ImageNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ImageScope {
    override val modifier = ImageModifier(surface)

    val imageWidth = mutableStateOf(0f)
    val imageHeight = mutableStateOf(0f)

    override fun measureContentSize(ctx: KoolContext) {
        val modWidth = modifier.width
        val modHeight = modifier.height
        var isUsingTexSize = false

        val measuredWidth = if (modWidth is Dp) {
            modWidth.px
        } else {
            isUsingTexSize = true
            imageWidth.use() * modifier.imageScale + paddingStartPx + paddingEndPx
        }
        val measuredHeight = if (modHeight is Dp) {
            modHeight.px
        } else {
            isUsingTexSize = true
            imageHeight.use() * modifier.imageScale + paddingTopPx + paddingBottomPx
        }
        setContentSize(measuredWidth, measuredHeight)

        if (isUsingTexSize) {
            surface.onEachFrame {
                imageWidth.set(modifier.image?.loadedTexture?.width?.toFloat() ?: 0f)
                imageHeight.set(modifier.image?.loadedTexture?.height?.toFloat() ?: 0f)
            }
        }
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        modifier.image?.let {
            val imgMesh = surface.getMeshLayer(modifier.zLayer).addImage(it)
            imgMesh.builder.clear()
            imgMesh.builder.configured(modifier.tint) {
                rect {
                    origin.set(paddingStartPx, paddingTopPx, 0f)
                    size.set(innerWidthPx, innerHeightPx)
                    texCoordLowerLeft.set(modifier.uvTopLeft)
                    texCoordLowerRight.set(modifier.uvTopRight)
                    texCoordUpperLeft.set(modifier.uvBottomLeft)
                    texCoordUpperRight.set(modifier.uvBottomRight)
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
    val builder = MeshBuilder(geometry)

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