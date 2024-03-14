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
    val isDirectory: Boolean

    val parent: FileSystemDirectory?
}

interface FileSystemFile : FileSystemItem {
    override val name: String
        get() = path.substringAfterLast("/")

    override val isDirectory: Boolean
        get() = false

    val mimeType: String
        get() = MimeType.forFileName(name)

    val size: Long
    suspend fun read(): Uint8Buffer
}

interface FileSystemDirectory : FileSystemItem {
    override val name: String
        get() = path.removeSuffix("/").substringAfterLast("/") + "/"

    override val isDirectory: Boolean
        get() = true

    fun list(): List<FileSystemItem>
    fun getChildOrNull(name: String): FileSystemItem?
    operator fun get(name: String) = checkNotNull(getChildOrNull(name)) { "Not found: $name" }
    operator fun contains(name: String): Boolean
}

interface WritableFileSystemFile : FileSystemFile {
    suspend fun write(data: Uint8Buffer)
    fun delete()
}

interface WritableFileSystemDirectory : FileSystemDirectory {
    fun delete()
}

fun FileSystemDirectory.collect() = buildList {
    add(this@collect)
    fun FileSystemDirectory.appendAll(): Unit = list().forEach {
        add(it)
        if (it is FileSystemDirectory) {
            it.appendAll()
        }
    }
    appendAll()
}

fun FileSystemDirectory.getItemOrNull(path: String): FileSystemItem? {
    var it = this
    val names = path.split('/').filter { it.isNotBlank() }
    for (i in names.indices) {
        val name = names[i]
        val child = it.getChildOrNull(name) ?: it.getChildOrNull("${name}/") ?: return null
        if (i == names.lastIndex) {
            return child
        }
        if (child.isDirectory) {
            it = child as FileSystemDirectory
        } else {
            return null
        }
    }
    return null
}

fun FileSystemDirectory.getFileOrNull(path: String): FileSystemFile? = getItemOrNull(path) as? FileSystemFile?
fun FileSystemDirectory.getDirectoryOrNull(path: String): FileSystemDirectory? = getItemOrNull(path) as? FileSystemDirectory?

fun FileSystemDirectory.getItem(path: String): FileSystemItem = checkNotNull(getItemOrNull(path)) { "Item not found: $path" }
fun FileSystemDirectory.getFile(path: String): FileSystemFile = checkNotNull(getFileOrNull(path)) { "File not found: $path" }
fun FileSystemDirectory.getDirectory(path: String): FileSystemDirectory = checkNotNull(getDirectoryOrNull(path)) { "Directory not found: $path" }

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
