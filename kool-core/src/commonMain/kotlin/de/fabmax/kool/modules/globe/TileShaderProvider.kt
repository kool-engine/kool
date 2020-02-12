package de.fabmax.kool.modules.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Texture

interface TileShaderProvider {
    fun getShader(tileName: TileName, ctx: KoolContext): TileShader
}

class TileShader(val shader: Any, val attribution: TileMesh.AttributionInfo)

abstract class TexImageTileShaderProvider : TileShaderProvider {
    protected var specularIntensity = 0.25f
    protected var shininess = 25f

    override fun getShader(tileName: TileName, ctx: KoolContext): TileShader{
//        val shader = basicShader {
//            colorModel = ColorModel.TEXTURE_COLOR
//            lightModel = LightModel.PHONG_LIGHTING
//
//            specularIntensity = this@TexImageTileShaderProvider.specularIntensity
//            shininess = this@TexImageTileShaderProvider.shininess
//            texture = getTexture(tileName, ctx)
//        }
//        return TileShader(shader, getAttributionInfo(tileName))
        TODO()
    }

    abstract fun getAttributionInfo(tileName: TileName): TileMesh.AttributionInfo

    abstract fun getTexture(tileName: TileName, ctx: KoolContext): Texture
}

open class OsmTexImageTileShaderProvider : TexImageTileShaderProvider() {
    override fun getTexture(tileName: TileName, ctx: KoolContext): Texture {
        return Texture { it.loadTextureData("https://tile.openstreetmap.org/${tileName.zoom}/${tileName.x}/${tileName.y}.png") }
    }

    override fun getAttributionInfo(tileName: TileName): TileMesh.AttributionInfo =
            TileMesh.AttributionInfo("Imagery: © OpenStreetMap", "http://www.openstreetmap.org/copyright")
}
