package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.*
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.scene.Model
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logW

abstract class FileSystemAssetLoader(val baseDir: FileSystemDirectory) : AssetLoader() {
    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        val blob = loadData(blobRef.path)
        return LoadedBlobAsset(blobRef, blob)
    }

    override suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        val refCopy = TextureData2dRef(textureRef.path, textureRef.props)
        return LoadedTextureAsset(textureRef, loadTextureData2d(refCopy).result)
    }

    protected suspend fun loadData(path: String): Result<Uint8Buffer> {
        return try {
            if (Assets.isDataUri(path)) {
                Result.success(decodeDataUri(path))
            } else {
                Result.success(baseDir.getFile(path).read())
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}

suspend fun FileSystemFile.loadTexture2d(props: TextureProps = TextureProps()): Result<Texture2d> {
    return try {
        val mimeType = this.mimeType
        if (mimeType == MimeType.BINARY_DATA) {
            logW { "file $name seems to be no image type" }
        }
        val texData = Assets.loadTextureDataFromBuffer(read(), mimeType, props)
        Result.success(Assets.loadTexture2d(texData, props, name))
    } catch (t: Throwable) {
        Result.failure(t)
    }
}

suspend fun FileSystemFile.loadGltfModel(
    modelCfg: GltfLoadConfig = GltfLoadConfig(),
    scene: Int = 0
): Result<Model> {
    return try {
        GltfFile(read(), name).map { it.makeModel(modelCfg, scene) }
    } catch (t: Throwable) {
        Result.failure(t)
    }
}
