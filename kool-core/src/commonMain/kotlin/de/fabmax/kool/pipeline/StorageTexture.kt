package de.fabmax.kool.pipeline

import de.fabmax.kool.util.*

class StorageTexture1d(
    val width: Int,
    val format: TexFormat,
    props: TextureProps = TextureProps(
        addressModeU = AddressMode.CLAMP_TO_EDGE,
        mipMapping = false,
        format = format
    ),
    name: String = UniqueId.nextId("StorageTexture1d")
) : Texture1d(
    props,
    name,
    BufferedTextureLoader(TextureData1d(makeStorageBuffer(format, width), width, format))
) {
    private val data: TextureData1d = (loader as BufferedTextureLoader).data as TextureData1d

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
    val format: TexFormat,
    props: TextureProps = TextureProps(
        addressModeU = AddressMode.CLAMP_TO_EDGE,
        addressModeV = AddressMode.CLAMP_TO_EDGE,
        mipMapping = false,
        format = format
    ),
    name: String = UniqueId.nextId("StorageTexture2d")
) : Texture2d(
    props,
    name,
    BufferedTextureLoader(TextureData2d(makeStorageBuffer(format, width, height), width, height, format))
) {
    private val data: TextureData2d = (loader as BufferedTextureLoader).data as TextureData2d

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
    val format: TexFormat,
    props: TextureProps = TextureProps(
        addressModeU = AddressMode.CLAMP_TO_EDGE,
        addressModeV = AddressMode.CLAMP_TO_EDGE,
        addressModeW = AddressMode.CLAMP_TO_EDGE,
        mipMapping = false,
        format = format
    ),
    name: String = UniqueId.nextId("StorageTexture3d")
) : Texture3d(
    props,
    name,
    BufferedTextureLoader(TextureData3d(makeStorageBuffer(format, width, height, depth), width, height, depth, format))
) {
    private val data: TextureData3d = (loader as BufferedTextureLoader).data as TextureData3d

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
