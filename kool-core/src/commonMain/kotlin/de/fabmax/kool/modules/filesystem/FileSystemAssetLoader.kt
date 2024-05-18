package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.*
import de.fabmax.kool.util.Uint8Buffer

abstract class FileSystemAssetLoader(val baseDir: FileSystemDirectory) : AssetLoader() {
    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        val blob = loadData(blobRef.path)
        return LoadedBlobAsset(blobRef, blob)
    }

    override suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        val refCopy = TextureData2dRef(textureRef.path, textureRef.props)
        val texData = loadTextureData2d(refCopy).data
        return LoadedTextureAsset(textureRef, texData)
    }

    protected suspend fun loadData(path: String): Uint8Buffer? {
        return if (Assets.isDataUri(path)) {
            decodeDataUri(path)
        } else {
            baseDir.getFileOrNull(path)?.read()
        }
    }
}