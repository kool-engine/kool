package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.Assets
import de.fabmax.kool.MimeType
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.scene.Model
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logW

interface FileSystemItem {
    val path: String
    val name: String
}

interface FileSystemFile : FileSystemItem {
    override val name: String
        get() = path.substringAfterLast("/")

    val mimeType: String
        get() = MimeType.forFileName(name)

    val size: Long
    suspend fun read(): Uint8Buffer
}

interface FileSystemDirectory : FileSystemItem {
    override val name: String
        get() = path.removeSuffix("/").substringAfterLast("/") + "/"

    fun list(): List<FileSystemItem>
    operator fun get(name: String): FileSystemItem
}

interface WritableFileSystemFile : FileSystemFile {
    suspend fun write(data: Uint8Buffer)
    fun delete()
}

interface WritableFileSystemDirectory : FileSystemDirectory {
    fun delete()
}

suspend fun FileSystemFile.loadTexture2d(props: TextureProps = TextureProps()): Texture2d {
    val mimeType = this.mimeType
    if (mimeType == MimeType.BINARY_DATA) {
        logW { "file $name seems to be no image type" }
    }
    val texData = Assets.loadTextureDataFromBuffer(read(), mimeType, props)
    return Assets.loadTexture2d(texData, props, name)
}

suspend fun FileSystemFile.loadGltfModel(
    modelCfg: GltfLoadConfig = GltfLoadConfig(),
    scene: Int = 0
): Model {
    return GltfFile(read(), name).makeModel(modelCfg, scene)
}
