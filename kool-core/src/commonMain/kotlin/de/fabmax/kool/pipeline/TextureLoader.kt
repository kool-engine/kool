package de.fabmax.kool.pipeline

import de.fabmax.kool.Assets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

sealed class TextureLoader

class DeferredTextureLoader(val loader: (suspend CoroutineScope.() -> ImageData)) : TextureLoader() {
    private var deferred: Deferred<ImageData>? = null

    fun loadTextureDataAsync(): Deferred<ImageData> {
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

class ImageTextureLoader(var data: ImageData) : TextureLoader()
