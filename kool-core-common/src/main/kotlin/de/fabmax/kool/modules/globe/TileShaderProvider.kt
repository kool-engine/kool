package de.fabmax.kool.modules.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.Texture
import de.fabmax.kool.assetTexture
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

        specularIntensity = 0.25f
        shininess = 25f
        texture = getTexture(tileName, ctx)
    }

    abstract fun getTexture(tileName: TileName, ctx: KoolContext): Texture
}

open class OsmTexImageTileShaderProvider : TexImageTileShaderProvider() {
    protected val tileUrls = mutableListOf("a.tile.openstreetmap.org", "b.tile.openstreetmap.org", "c.tile.openstreetmap.org")

    override fun getTexture(tileName: TileName, ctx: KoolContext): Texture {
        val srvIdx = (tileName.x xor tileName.y xor tileName.zoom) % tileUrls.size
        return assetTexture("https://${tileUrls[srvIdx]}/${tileName.zoom}/${tileName.x}/${tileName.y}.png", ctx)
    }
}
