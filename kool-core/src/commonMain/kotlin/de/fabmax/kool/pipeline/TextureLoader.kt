package de.fabmax.kool.pipeline

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

sealed class TextureLoader

class AsyncTextureLoader(val loader: (suspend CoroutineScope.(AssetManager) -> TextureData)) : TextureLoader() {
    private var deferred: Deferred<TextureData>? = null

    fun loadTextureDataAsync(ctx: KoolContext): Deferred<TextureData> {
        val def = deferred ?: ctx.assetMgr.async { loader(ctx.assetMgr) }
        if (deferred == null) {
            deferred = def
        }
        return def
    }
}

class SyncTextureLoader(val loader: (KoolContext) -> TextureData) : TextureLoader() {
    fun loadTextureDataSync(ctx: KoolContext): TextureData {
        return loader(ctx)
    }
}

class BufferedTextureLoader(var data: TextureData) : TextureLoader()
