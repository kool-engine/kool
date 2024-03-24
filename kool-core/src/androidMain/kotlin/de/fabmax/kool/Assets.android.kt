package de.fabmax.kool

import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.util.Uint8Buffer

actual fun fileSystemAssetLoader(baseDir: FileSystemDirectory): AssetLoader {
    TODO("Not yet implemented")
}

actual suspend fun decodeDataUri(dataUri: String): Uint8Buffer {
    TODO("Not yet implemented")
}

internal actual fun PlatformAssets(): PlatformAssets {
    TODO("Not yet implemented")
}