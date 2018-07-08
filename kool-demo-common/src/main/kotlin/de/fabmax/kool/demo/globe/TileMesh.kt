package de.fabmax.kool.demo.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.Texture
import de.fabmax.kool.assetTexture
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logD

class TileMesh(val globe: Globe, val tileName: TileName, ctx: KoolContext) :
        Mesh(MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS), "$tileName") {

    val key: Long get() = tileName.fusedKey

    val isCurrentlyVisible get() = isRendered

    private var tileShader = globe.tileShaderProvider.getShader(tileName, ctx)
    private var tileTex: Texture?
    private var meshDetailLevel = 0

    private val createTime: Double = ctx.time

    var isRemovable = false
    var isLoaded = false
        private set
    private var isFallbackTex = false

    init {
        shader = tileShader
        tileTex = tileShader.texture
        isVisible = false

        generateGeometry(globe.meshDetailLevelInit)
    }

    private fun generateGeometry(stepsLog2: Int) {
        meshDetailLevel = stepsLog2
        globe.meshGenerator.generateMesh(globe, this, meshDetailLevel)
    }

    override fun preRender(ctx: KoolContext) {
        // increase mesh detail level if possible
        if (!isRemovable && isCurrentlyVisible && meshDetailLevel < globe.meshDetailLevelRefined && globe.allowedMeshRefinements > 0) {
            globe.allowedMeshRefinements--
            generateGeometry(globe.meshDetailLevelRefined)
        }

        // check if texture is loaded
        val tex = tileTex
        if (tex != null) {
            if (tex.res?.isLoaded != true) {
                // tile texture is not yet loaded, trigger / poll texture loading
                ctx.textureMgr.bindTexture(tex, ctx)

            } else if (isFallbackTex && tex.res?.isLoaded == true) {
                // fallback texture was set because of load timeout, but correct texture is available now
                shader = tileShader
                isFallbackTex = false
            }
        }

        if (!isLoaded && (tileShader.texture?.res?.isLoaded == true || isFallbackTex)) {
            isLoaded = true
            globe.tileLoaded(this)
        }

        if (!isLoaded && ctx.time - createTime > TILE_TIMEOUT) {
            logD { "Tile load timeout: $tileName" }
            // set fall back texture
            shader = getFallbackShader(ctx)
            isFallbackTex = true
        }

        super.preRender(ctx)
    }

    companion object {
        const val TILE_TIMEOUT = 3.0

        private var fallbackShader: Shader? = null
        private fun getFallbackShader(ctx: KoolContext): Shader {
            if (fallbackShader == null) {
                fallbackShader = basicShader {
                    colorModel = ColorModel.STATIC_COLOR
                    //colorModel = ColorModel.TEXTURE_COLOR
                    lightModel = LightModel.PHONG_LIGHTING
                    //lightModel = LightModel.NO_LIGHTING

                    specularIntensity = 0.25f
                    shininess = 25f
                    staticColor = Color.LIGHT_GRAY
                    texture = assetTexture("ground_color.png", ctx)
                }
            }
            return fallbackShader!!
        }
    }
}