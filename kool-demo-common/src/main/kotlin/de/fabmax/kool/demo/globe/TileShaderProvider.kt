package de.fabmax.kool.demo.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.Texture
import de.fabmax.kool.assetTexture
import de.fabmax.kool.math.randomI
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader

interface TileShaderProvider {
    fun getShader(tileName: TileName, ctx: KoolContext): BasicShader
}

abstract class TexImageTileShaderProvider : TileShaderProvider {
    override fun getShader(tileName: TileName, ctx: KoolContext): BasicShader = basicShader {
        colorModel = ColorModel.TEXTURE_COLOR
        lightModel = LightModel.PHONG_LIGHTING
        isAlpha = true

        alpha = 0f
        specularIntensity = 0.25f
        shininess = 25f
        texture = getTexture(tileName, ctx)
    }

    abstract fun getTexture(tileName: TileName, ctx: KoolContext): Texture
}

open class OsmTexImageTileShaderProvider : TexImageTileShaderProvider() {
    protected val tileUrls = mutableListOf("tile.openstreetmap.org")

    override fun getTexture(tileName: TileName, ctx: KoolContext) =
            assetTexture("https://${tileUrls[randomI(tileUrls.indices)]}/${tileName.zoom}/${tileName.x}/${tileName.y}.png", ctx)
}
