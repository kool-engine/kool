package de.fabmax.kool

import de.fabmax.kool.gl.*

/**
 * @author fabmax
 */
class TextureManager internal constructor() : SharedResManager<TextureProps, TextureResource>() {

    var maxTextureLoadsPerFrame = 5

    private var boundTextures = Array<TextureResource?>(16, { null })
    private val loadingTextures: MutableMap<String, TextureData> = mutableMapOf()
    private var activeTexUnit = 0

    private var allowedTexLoads = maxTextureLoadsPerFrame

    fun onNewFrame(ctx: KoolContext) {
        allowedTexLoads = maxTextureLoadsPerFrame

        if (boundTextures.size != ctx.glCapabilities.maxTexUnits) {
            boundTextures = Array(ctx.glCapabilities.maxTexUnits, { null })
        }

        // safety first: unbind all textures
        for (i in boundTextures.indices) {
            val tex = boundTextures[i]
            if (tex != null) {
                glActiveTexture(GL_TEXTURE0 + i)
                glBindTexture(GL_TEXTURE_2D, null)
                tex.texUnit = -1
                boundTextures[i] = null
            }
        }

        // set activeTexUnit, so that nextTextUnit() will choose unit 0 (some GL implementations don't like
        // tex unit 0 to be left out...)
        activeTexUnit = boundTextures.size-1
    }

    fun unbindTexture(texture: Texture?, ctx: KoolContext) {
        texture ?: return

        if (texture.isValid) {
            val texRes = texture.res ?: throw KoolException("TextureResource is null although it was created")
            if (texRes.texUnit >= 0) {
                activateTexUnit(texRes.texUnit)
                glBindTexture(GL_TEXTURE_2D, null)
                boundTextures[texRes.texUnit] = null
                texRes.texUnit = -1
            }
        }
    }

    fun bindTexture(texture: Texture, ctx: KoolContext, makeActive: Boolean = false): Int {
        if (!texture.isValid) {
            nextTexUnit()
            texture.onCreate(ctx)
        }
        val texRes = texture.res ?: throw KoolException("TextureResource is null although it was created")

        if (texRes.texUnit < 0) {
            nextTexUnit()
            bindToActiveTexUnit(texture.res)
        } else if (makeActive) {
            activateTexUnit(texRes.texUnit)
        }

        // upload texture data to GPU if that hasn't happened yet
        if (!texRes.isLoaded) {
            loadTexture(texture, ctx)
        }

        return texRes.texUnit
    }

    internal fun createTexture(props: TextureProps, ctx: KoolContext): TextureResource {
        return addReference(props, ctx)
    }

    internal fun deleteTexture(texture: Texture, ctx: KoolContext) {
        val res = texture.res
        if (res != null) {
            removeReference(texture.props, ctx)
        }
    }

    private fun nextTexUnit() {
        activateTexUnit((activeTexUnit + 1) % boundTextures.size)
    }

    private fun activateTexUnit(unit: Int) {
        activeTexUnit = unit
        glActiveTexture(GL_TEXTURE0 + unit)
    }

    private fun bindToActiveTexUnit(texRes: TextureResource?) {
        boundTextures[activeTexUnit]?.texUnit = -1

        val target = texRes?.target ?: GL_TEXTURE_2D
        glBindTexture(target, texRes)
        texRes?.texUnit = activeTexUnit
        boundTextures[activeTexUnit] = texRes
    }

    private fun loadTexture(texture: Texture, ctx: KoolContext) {
        val res = texture.res ?: throw KoolException("Can't load a texture that wasn't created")

        // check if texture is already loading
        var data = loadingTextures[texture.props.id]
        if (data == null) {
            // initiate loading of texture data
            data = texture.generator(texture, ctx)
            loadingTextures[texture.props.id] = data
        }
        // texture data is available (depending on the texture source that might not be the case immediately)
        if (data.isAvailable && (!texture.delayLoading || allowedTexLoads > 0)) {
            if (res.texUnit != activeTexUnit) {
                //bindToActiveTexUnit(res)
                activateTexUnit(texture.res!!.texUnit)
            }
            texture.loadData(data, ctx)
            loadingTextures.remove(texture.props.id)
            allowedTexLoads--
        }
    }

    override fun createResource(key: TextureProps, ctx: KoolContext): TextureResource {
        val texRes = TextureResource.create(key.target, key, ctx)
        bindToActiveTexUnit(texRes)
        return texRes
    }

    override fun deleteResource(key: TextureProps, res: TextureResource, ctx: KoolContext) {
        res.delete(ctx)
    }
}
