package de.fabmax.kool

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext

/**
 * @author fabmax
 */
class TextureManager internal constructor() : SharedResManager<TextureProps, TextureResource>() {

    companion object {
        val TEXTURE_UNITS = 32
    }

    private var activeTexUnit = 0
    private val boundTextures = Array<TextureResource?>(TEXTURE_UNITS, { i -> null })
    private val loadingTextures: MutableMap<String, TextureData> = mutableMapOf()

    fun bindTexture(texture: Texture, ctx: RenderContext): Int {
        if (!texture.isValid) {
            nextTexUnit()
            texture.onCreate(ctx)
        }
        val texRes = texture.res ?: throw KoolException("TextureResource is null although it was created")

        if (texRes.texUnit < 0) {
            nextTexUnit()
            bindToActiveTexUnit(texture.res)
        }
        loadTexture(texture, ctx)
        return texRes.texUnit
    }

    internal fun createTexture(props: TextureProps, ctx: RenderContext): TextureResource {
        return createResource(props, ctx)
    }

    internal fun deleteTexture(texture: Texture, ctx: RenderContext) {
        val res = texture.res
        if (res != null) {
            deleteResource(texture.props, res, ctx)
        }
    }

    private fun nextTexUnit() {
        activateTexUnit((activeTexUnit + 1) % TEXTURE_UNITS)
    }

    private fun activateTexUnit(unit: Int) {
        activeTexUnit = unit
        GL.activeTexture(GL.TEXTURE0 + unit)
    }

    private fun bindToActiveTexUnit(texRes: TextureResource?) {
        boundTextures[activeTexUnit]?.texUnit = -1

        GL.bindTexture(GL.TEXTURE_2D, texRes)
        texRes?.texUnit = activeTexUnit
        boundTextures[activeTexUnit] = texRes
    }

    private fun loadTexture(texture: Texture, ctx: RenderContext): Boolean {
        val res = texture.res ?: throw KoolException("Can't load a texture that wasn't created")
        if (res.isLoaded) {
            return true
        }

        var data = loadingTextures[texture.props.id]
        if (data == null) {
            data = texture.generator(texture)
            loadingTextures[texture.props.id] = data
        }
        if (data.isAvailable) {
            if (res.texUnit != activeTexUnit) {
                activateTexUnit(texture.res!!.texUnit)
            }
            data.loadData(texture, ctx)
            loadingTextures.remove(texture.props.id)
        }
        return res.isLoaded
    }

    override fun createResource(key: TextureProps, ctx: RenderContext): TextureResource {
        val texRes = TextureResource.create(GL.TEXTURE_2D, key, ctx)
        bindToActiveTexUnit(texRes)
        return texRes
    }

    override fun deleteResource(key: TextureProps, res: TextureResource, ctx: RenderContext) {
        res.delete(ctx)
    }
}
