package de.fabmax.kool.modules.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.Texture
import de.fabmax.kool.assetTexture
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

class TileMesh(val globe: Globe, val tileName: TileName, ctx: KoolContext) :
        Mesh(MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS), "$tileName") {

    val key: Long get() = tileName.fusedKey

    val isCurrentlyVisible get() = isRendered

    private var tileShader: Shader
    private var tileTex: Texture?

    private val createTime: Double = ctx.time
    private var generatorJob: Job

    var isRemovable = false
    var isLoaded = false
        private set
    private val isFallbackShader
        get() = shader === fallbackShader

    val attributionInfo = mutableSetOf<AttributionInfo>()

    init {
        val provShader = globe.tileShaderProvider.getShader(tileName, ctx)
        tileTex = if (provShader.shader is BasicShader) { provShader.shader.texture } else { null }
        tileShader = provShader.shader
        shader = tileShader
        attributionInfo += provShader.attribution

        isVisible = false

        generatorJob = launch {
            globe.meshGenerator.generateMesh(globe, this@TileMesh, globe.meshDetailLevel)
        }
    }

    override fun preRender(ctx: KoolContext) {
        // check if texture is loaded
        val tex = tileTex
        if (tex != null) {
            if (tex.res?.isLoaded != true) {
                // tile texture is not yet loaded, trigger / poll texture loading
                ctx.textureMgr.bindTexture(tex, ctx)

            } else if (isFallbackShader && tex.res?.isLoaded == true) {
                // fallback texture was set because of load timeout, but correct texture is available now
                shader = tileShader
            }
        }

        if (!generatorJob.isCompleted) {
            // wait while mesh is generated in background
            return
        } else if (meshData.vertexList.size == 0) {
            logE { "mesh is still empty" }
        }

        if (!isLoaded && (tex?.res?.isLoaded == true || isFallbackShader)) {
            isLoaded = true
            globe.tileLoaded(this)
        }

        if (!isLoaded && ctx.time - createTime > TILE_TIMEOUT) {
            // set fall back texture
            shader = getFallbackShader(ctx)
        }

        super.preRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        // make sure we dispose the correct shader (not fall back shader)
        shader = tileShader
        super.dispose(ctx)
    }

    data class AttributionInfo(val text: String, val url: String?)

    companion object {
        const val TILE_TIMEOUT = 3.0

        private var fallbackShader: BasicShader? = null
        private fun getFallbackShader(ctx: KoolContext): BasicShader {
            if (fallbackShader == null) {
                fallbackShader = basicShader {
                    //colorModel = ColorModel.STATIC_COLOR
                    colorModel = ColorModel.TEXTURE_COLOR
                    lightModel = LightModel.PHONG_LIGHTING

                    specularIntensity = 0.25f
                    shininess = 25f
                    staticColor = Color.LIGHT_GRAY
                    texture = assetTexture("tile_empty.png", ctx, false)
                }
            }
            return fallbackShader!!
        }

        fun prepareDefaultTex(ctx: KoolContext) {
            val fbTex = getFallbackShader(ctx).texture
            if (fbTex != null && fbTex.res?.isLoaded != true) {
                ctx.textureMgr.bindTexture(fbTex, ctx)
            }
        }
    }
}