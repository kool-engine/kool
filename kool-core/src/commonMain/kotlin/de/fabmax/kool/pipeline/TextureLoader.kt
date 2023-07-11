package de.fabmax.kool.pipeline

import de.fabmax.kool.Assets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

sealed class TextureLoader

class AsyncTextureLoader(val loader: (suspend CoroutineScope.() -> TextureData)) : TextureLoader() {
    private var deferred: Deferred<TextureData>? = null

    fun loadTextureDataAsync(): Deferred<TextureData> {
        val def = deferred ?: Assets.async { loader() }
        if (deferred == null) {
            deferred = def
        }
        return def
    }

    fun invalidate() {
        deferred = null
    }
}

class SyncTextureLoader(val loader: () -> TextureData) : TextureLoader() {
    fun loadTextureDataSync(): TextureData {
        return loader()
    }
}

class BufferedTextureLoader(var data: TextureData) : TextureLoader()
