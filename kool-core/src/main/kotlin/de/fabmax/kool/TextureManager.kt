package de.fabmax.kool

import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.RenderContext

/**
 * @author fabmax
 */
class TextureManager internal constructor() {

    companion object {
        val TEXTURE_UNITS = 32
    }

    private data class TextureReferenceCounter(val tex: Texture2d, var referenceCount: Int)

    private val texReferenceMap: MutableMap<Texture2d, TextureReferenceCounter> = mutableMapOf()
    private val assetTexMap: MutableMap<String, Texture2d> = mutableMapOf()
    private val assetPaths: MutableMap<Texture2d, String> = mutableMapOf()

    private var activeTexUnit = 0
    private var boundTextures = Array<TextureResource?>(TEXTURE_UNITS, { i -> null })

    internal fun deleteReference(tex: Texture2d?, ctx: RenderContext) {
        if (tex != null) {
            val counter = texReferenceMap[tex]
            if (counter != null) {
                if (--counter.referenceCount == 0) {
                    tex.delete(ctx)
                    texReferenceMap.remove(tex)

                    val assetPath = assetPaths[tex]
                    if (assetPath != null) {
                        assetTexMap.remove(assetPath)
                    }
                }
            }
        }
    }

    internal fun getAssetTexture(path: String, props: TextureResource.Props): Texture2d {
        // get texture from map or create it if requested the first time
        // todo: consider props, for now textures with same path will be identical, different props will be ignored
        var tex = assetTexMap[path]
        if (tex == null) {
            tex = Platform.loadTexture(path, props)
            assetTexMap.put(path, tex)
            assetPaths.put(tex, path)
        }

        // increase reference counter
        var counter = texReferenceMap[tex]
        if (counter == null) {
            counter = TextureReferenceCounter(tex, 0)
            texReferenceMap[tex] = counter
        }
        counter.referenceCount++

        return tex
    }

    fun bindTexture2d(texture: Texture2d?, texUnit: Int, ctx: RenderContext) {
        if (texUnit != activeTexUnit) {
            GL.activeTexture(GL.TEXTURE0 + texUnit)
            activeTexUnit = texUnit
        }

        if (texture != null && !texture.isValid) {
            texture.create(ctx)
            // this is a bit of a special case: usually texture resources are bound by the TextureManager (a few lines
            // below. However, when a texture is crated, the resource is created and bound automatically,
            // therefore immediately set the newly created texture as bound to the current texture unit
            boundTextures[texUnit] = texture.res
        }

        val texRes = texture?.res
        if (texRes != boundTextures[texUnit]) {
            GL.bindTexture(GL.TEXTURE_2D, texRes)
            boundTextures[texUnit] = texRes
        }

        if (texture != null && !texture.isLoaded && texture.isAvailable) {
            // after texture is bound we can load the texture data if not already happened and if data is available
            texture.load(ctx)
        }
    }
}

