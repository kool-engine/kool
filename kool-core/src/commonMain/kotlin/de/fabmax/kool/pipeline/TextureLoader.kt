package de.fabmax.kool.pipeline

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

sealed class TextureLoader

class AsyncTextureLoader(val loader: (suspend CoroutineScope.(AssetManager) -> TextureData)) : TextureLoader() {
    fun loadTextureDataAsync(ctx: KoolContext): Deferred<TextureData> {
        return ctx.assetMgr.async { loader(ctx.assetMgr) }
    }
}

class BufferedTextureLoader(val data: TextureData) : TextureLoader()
