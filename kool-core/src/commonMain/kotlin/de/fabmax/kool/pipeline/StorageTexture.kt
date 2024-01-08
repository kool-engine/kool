package de.fabmax.kool.pipeline

import de.fabmax.kool.util.*

class StorageTexture1d(
    val width: Int,
    props: TextureProps,
    name: String = UniqueId.nextId("StorageTexture1d")
) : Texture1d(props, name, makeLoader1d(props.format, width)) {

    private val data: TextureData1d = (loader as BufferedTextureLoader).data as TextureData1d
    val format: TexFormat get() = props.format

    fun loadTextureData(block: (Buffer) -> Unit) {
        block(data.data)
        if (loadingState == LoadingState.LOADED) {
            dispose()
        }
    }

    fun readbackTextureData(block: (TextureData1d) -> Unit): Boolean {
        val loadedTex = loadedTexture ?: return false
        loadedTex.readTexturePixels(data)
        block(data)
        return true
    }
}

class StorageTexture2d(
    val width: Int,
    val height: Int,
    props: TextureProps,
    name: String = UniqueId.nextId("StorageTexture2d")
) : Texture2d(props, name, makeLoader2d(props.format, width, height)) {

    private val data: TextureData2d = (loader as BufferedTextureLoader).data as TextureData2d
    val format: TexFormat get() = props.format

    fun loadTextureData(block: (Buffer) -> Unit) {
        block(data.data)
        if (loadingState == LoadingState.LOADED) {
            dispose()
        }
    }

    fun readbackTextureData(block: (TextureData2d) -> Unit): Boolean {
        val loadedTex = loadedTexture ?: return false
        loadedTex.readTexturePixels(data)
        block(data)
        return true
    }
}

class StorageTexture3d(
    val width: Int,
    val height: Int,
    val depth: Int,
    props: TextureProps,
    name: String = UniqueId.nextId("StorageTexture3d")
) : Texture3d(props, name, makeLoader3d(props.format, width, height, depth)) {

    private val data: TextureData3d = (loader as BufferedTextureLoader).data as TextureData3d
    val format: TexFormat get() = props.format

    fun loadTextureData(block: (Buffer) -> Unit) {
        block(data.data)
        if (loadingState == LoadingState.LOADED) {
            dispose()
        }
    }

    fun readbackTextureData(block: (TextureData3d) -> Unit): Boolean {
        val loadedTex = loadedTexture ?: return false
        loadedTex.readTexturePixels(data)
        block(data)
        return true
    }
}

fun StorageTexture1d(width: Int, format: TexFormat) = StorageTexture1d(
    width,
    TextureProps(
        format = format,
        generateMipMaps = false,
        defaultSamplerSettings = SamplerSettings().clamped().nearest()
    )
)

fun StorageTexture2d(width: Int, height: Int, format: TexFormat) = StorageTexture2d(
    width,
    height,
    TextureProps(
        format = format,
        generateMipMaps = false,
        defaultSamplerSettings = SamplerSettings().clamped().nearest()
    )
)

fun StorageTexture3d(width: Int, height: Int, depth: Int, format: TexFormat) = StorageTexture3d(
    width,
    height,
    depth,
    TextureProps(
        format = format,
        generateMipMaps = false,
        defaultSamplerSettings = SamplerSettings().clamped().nearest()
    )
)

private fun makeLoader1d(format: TexFormat, width: Int): BufferedTextureLoader =
    BufferedTextureLoader(TextureData1d(makeStorageBuffer(format, width), width, format))

private fun makeLoader2d(format: TexFormat, width: Int, height: Int): BufferedTextureLoader =
    BufferedTextureLoader(TextureData2d(makeStorageBuffer(format, width, height), width, height, format))

private fun makeLoader3d(format: TexFormat, width: Int, height: Int, depth: Int): BufferedTextureLoader =
    BufferedTextureLoader(TextureData3d(makeStorageBuffer(format, width, height, depth), width, height, depth, format))

private fun makeStorageBuffer(format: TexFormat, sizeX: Int, sizeY: Int = 1, sizeZ: Int = 1): Buffer {
    check(format.isF32 || format.isI32 || format.isU32) {
        "StorageTextures must use a 32-bit format (TexFormat.*_F32, TexFormat.*_I32 or TexFormat.*_U32)"
    }

    val size = sizeX * sizeY * sizeZ * format.channels
    return when {
        format.isF32 -> Float32Buffer(size)
        format.isI32 -> Int32Buffer(size)
        else -> Uint32Buffer(size)
    }
}
