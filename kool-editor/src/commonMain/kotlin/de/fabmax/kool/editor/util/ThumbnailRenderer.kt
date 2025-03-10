package de.fabmax.kool.editor.util

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.api.SceneShaderData
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.loadTexture2d
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.ToneMapping
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.ImageProvider
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.*
import kotlin.math.min

class ThumbnailRenderer(
    name: String,
    val numTiles: Vec2i = Vec2i(24, 24),
    tileSize: Vec2i = Vec2i(80 ,80)
) : OffscreenPass2d(
    drawNode = Node(),
    attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RGBA, ClearColorLoad),
    initialSize = numTiles * tileSize,
    name = name
) {

    var tileSize: Vec2i = tileSize
        private set

    private val freeIndices = mutableListOf<Vec2i>()
    private val thumbnails = mutableMapOf<Vec2i, Thumbnail>()
    private val renderQueue = mutableListOf<Pair<View, Thumbnail>>()

    init {
        views.clear()

        for (y in 0 until numTiles.y) {
            for (x in 0 until numTiles.x) {
                freeIndices += Vec2i(x, y)
            }
        }

        onAfterPass {
            val releasables = renderQueue.map { it.first.drawNode }
            launchDelayed(1) {
                releasables.forEach { it.release() }
            }
            renderQueue.forEach { (_, thumbnail) ->
                thumbnail.state.set(ThumbnailState.USABLE)
                thumbnails[thumbnail.tileIndex] = thumbnail
            }
            renderQueue.clear()
            views.clear()
            isEnabled = false
        }
    }

    fun updateTileSize(sz: Int) {
        if (sz != tileSize.x || sz != tileSize.y) {
            tileSize = Vec2i(sz, sz)
            setSize(numTiles.x * sz, numTiles.y * sz)
            thumbnails.values.forEach {
                it.state.set(ThumbnailState.DESTROYED)
            }
            thumbnails.clear()
        }
    }

    private fun enqueueView(view: View, thumbnail: Thumbnail) {
        renderQueue += view to thumbnail
        views += view
        isEnabled = true
    }

    fun renderThumbnail(content: suspend (View) -> Node?): Thumbnail {
        val result = Thumbnail(getNextTileIndex(), content)
        renderThumbnail(result)
        return result
    }

    private fun renderThumbnail(thumbNail: Thumbnail) {
        launchOnMainThread {
            val cam = OrthographicCamera().apply {
                left = -1f
                right = 1f
                top = 1f
                bottom = -1f
            }
            val view = View("thumbnail-view", Node(), cam).apply {
                val vpX = thumbNail.tileIndex.x * tileSize.x
                val vpY = thumbNail.tileIndex.y * tileSize.y
                viewport = Viewport(vpX, vpY, tileSize.x, tileSize.y)
            }

            val node = thumbNail.contentGenerator(view)
            if (node == null) {
                thumbNail.state.set(ThumbnailState.FAILED)
            } else {
                view.drawNode = node
                enqueueView(view, thumbNail)
            }
        }
    }

    private fun getNextTileIndex(): Vec2i {
        return if (freeIndices.isNotEmpty()) {
            freeIndices.removeLast()
        } else {
            val lru = thumbnails.values.minBy { it.lastUsed }.tileIndex
            val old = thumbnails.remove(lru)
            old?.state?.set(ThumbnailState.DESTROYED)
            lru
        }
    }

    inner class Thumbnail(val tileIndex: Vec2i, val contentGenerator: suspend (View) -> Node?) : ImageProvider {
        override val uvTopLeft: Vec2f
        override val uvTopRight: Vec2f
        override val uvBottomLeft: Vec2f
        override val uvBottomRight: Vec2f
        override val isDynamicSize = false

        val state = mutableStateOf(ThumbnailState.CREATED)
        var lastUsed = Time.frameCount

        init {
            val lt = tileIndex.x.toFloat() / numTiles.x
            val rt = (tileIndex.x + 1).toFloat() / numTiles.x
            var top = tileIndex.y.toFloat() / numTiles.y
            var bot = (tileIndex.y + 1).toFloat() / numTiles.y
            if (!KoolSystem.requireContext().backend.isInvertedNdcY) {
                top = 1f - top
                bot = 1f - bot
            }

            uvTopLeft = Vec2f(lt, top)
            uvTopRight = Vec2f(rt, top)
            uvBottomLeft = Vec2f(lt, bot)
            uvBottomRight = Vec2f(rt, bot)
        }

        fun update() {
            renderThumbnail(this)
        }

        override fun getTexture(imgWidthPx: Float, imgHeightPx: Float): Texture2d? = colorTexture
    }
}

enum class ThumbnailState(val isUsable: Boolean) {
    CREATED(false),
    USABLE(true),
    USABLE_OUTDATED(true),
    DESTROYED(false),
    FAILED(false)
}

fun ThumbnailRenderer.textureThumbnail(texPath: String): ThumbnailRenderer.Thumbnail = renderThumbnail {
    val texMesh = TextureMesh(name = "thumb:$texPath").apply {
        val assets = KoolEditor.instance.cachedAppAssets
        val ref = AssetReference.Texture(texPath)
        var tex = assets.getTextureIfLoaded(ref)
        var ar = 1f
        if (tex == null) {
            tex = assets.assetLoader.loadTexture2d(texPath).getOrNull()?.also {
                it.releaseWith(this)
                ar = it.width.toFloat() / it.height
            }
        }

        generateThumbnailRoundRect(ar)

        shader = KslShader("tex2d-thumbnail-shader") {
            val uv = interStageFloat2()
            vertexStage {
                main {
                    val pos = vertexAttribFloat3(Attribute.POSITIONS)
                    uv.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS)
                    outPosition set float4Value(pos, 1f.const) * cameraData().viewProjMat
                }
            }
            fragmentStage {
                main {
                    val p = float2Var(fract(uv.output * cameraData().viewport.zw / 20f.const))
                    val a = bool1Var((p.x lt 0.5f.const) eq (p.y lt 0.5f.const))
                    val bg = float4Var()
                    `if`(a) {
                        bg set (MdColor.GREY tone 200).const
                    }.`else` {
                        bg set (MdColor.GREY tone 350).const
                    }
                    val color = float4Var(sampleTexture(texture2d("thumb"), uv.output))
                    bg.rgb set mix(bg.rgb, color.rgb, color.a)
                    colorOutput(bg)
                }
            }
        }.also {
            it.texture2d("thumb", tex)
        }
    }

    Node().apply {
        addNode(ClearMesh())
        addNode(texMesh)
    }
}

fun ThumbnailRenderer.hdriThumbnail(texPath: String): ThumbnailRenderer.Thumbnail = renderThumbnail {
    val texMesh = TextureMesh().apply {
        val assets = KoolEditor.instance.cachedAppAssets
        val tex = assets.assetLoader.loadTexture2d(texPath, mipMapping = MipMapping.Off).getOrThrow()
        tex.releaseWith(this)

        generateThumbnailRoundRect(tex.width.toFloat() / tex.height)

        shader = KslShader("hdri-thumbnail-shader") {
            val uv = interStageFloat2()
            vertexStage {
                main {
                    val pos = vertexAttribFloat3(Attribute.POSITIONS)
                    uv.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS)
                    outPosition set float4Value(pos, 1f.const) * cameraData().viewProjMat
                }
            }
            fragmentStage {
                main {
                    val rgbe = float4Var(sampleTexture(texture2d("thumb"), uv.output))
                    val exp = float1Var(rgbe.w * 255f.const - 128f.const)
                    val rgb = float3Var(rgbe.rgb * pow(2f.const, exp))
                    colorOutput(convertColorSpace(rgb, ColorSpaceConversion.LinearToSrgbHdr(ToneMapping.Aces)))
                }
            }
        }.also {
            it.texture2d("thumb", tex)
        }
    }

    Node().apply {
        addNode(ClearMesh())
        addNode(texMesh)
    }
}

fun ThumbnailRenderer.materialThumbnail(material: MaterialComponent): ThumbnailRenderer.Thumbnail = renderThumbnail { view ->
    view.camera = PerspectiveCamera().apply {
        position.set(0.01f, 0.01f, 3f)
        lookAt.set(Vec3f.ZERO)
        fovY = 45f.deg
    }

    val scene = KoolEditor.instance.activeScene.value ?: return@renderThumbnail null
    val mesh = MaterialPreviewMesh()
    val previewShaderData = SceneShaderData(scene, false).apply {
        set(scene.shaderData)
        ssaoMap = null
        shadowMaps = emptyList()
    }
    if (!material.applyMaterialTo(mesh, previewShaderData, listOf(ModelMatrixComposition.UNIFORM_MODEL_MAT))) {
        return@renderThumbnail null
    }

    Node().apply {
        addNode(ClearMesh())
        addNode(SceneBgMesh(scene.shaderData))
        addNode(mesh)
    }
}

private class MaterialPreviewMesh :
    Mesh(vertexAttribs, name = "material-preview-mesh")
{
    init {
        geometry.addGeometry(sphereMeshData)
    }

    companion object {
        val vertexAttribs = listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)
        val sphereMeshData = IndexedVertexList(vertexAttribs).apply {
            MeshBuilder(this).apply {
                vertexModFun = { texCoord.x *= 2f }
                uvSphere { steps = 35 }
            }
            generateTangents()
        }
    }
}

private class SceneBgMesh(val shaderData: SceneShaderData) : Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
    init {
        generateThumbnailRoundRect()
        shader = KslShader("scene-bg-shader", PipelineConfig(depthTest = DepthCompareOp.ALWAYS)) {
            val uv = interStageFloat2()
            vertexStage {
                main {
                    val pos = vertexAttribFloat3(Attribute.POSITIONS)
                    uv.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS)
                    outPosition set float4Value(pos, 1f.const) * viewProj.const
                }
            }
            fragmentStage {
                main {
                    val envMap = shaderData.environmentMap
                    if (envMap != null) {
                        val sky = textureCube("sky")
                        val skyUv = float2Var(uv.output - 0.5f.const)
                        val dir = float3Var(normalize(float3Value(skyUv.x, -skyUv.y, (-1f).const)))
                        val colorConv = ColorSpaceConversion.LinearToSrgbHdr(shaderData.toneMapping)
                        colorOutput(convertColorSpace(sampleTexture(sky, dir, 2f.const).rgb, colorConv))
                    } else {
                        colorOutput(shaderData.ambientColorLinear.toSrgb().const)
                    }
                }
            }
        }.also {
            it.textureCube("sky", shaderData.environmentMap?.reflectionMap)
        }
    }

    companion object {
        val viewProj = Mat4f.orthographic(-1f, 1f, -1f, 1f, -1f, 1f, DepthRange.ZERO_TO_ONE, true)
    }
}

private class ClearMesh : Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
    init {
        generateFullscreenQuad()
        shader = KslShader("clear-shader", PipelineConfig(blendMode = BlendMode.DISABLED, depthTest = DepthCompareOp.ALWAYS)) {
            val uv = interStageFloat2()
            vertexStage { fullscreenQuadVertexStage(uv) }
            fragmentStage {
                main {
                    colorOutput(float4Value(0f, 0f, 0f, 0f))
                }
            }
        }
    }
}

private fun Mesh.generateThumbnailRoundRect(ar: Float = 1f) = generate {
    rect {
        if (ar > 1f) size.set(2f, 2f / ar) else size.set(2f * ar, 2f)
        cornerRadius = min(size.x, size.y) * 0.1f
    }
}
