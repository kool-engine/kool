package de.fabmax.kool.util

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.loadTexture2d
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.pipeline.MipMapping
import de.fabmax.kool.pipeline.SamplerSettings
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.pipeline.ibl.hdriEnvironment
import de.fabmax.kool.scene.Model
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.reflect.KProperty

class ResourceGroup(val assetLoader: AssetLoader = Assets.defaultLoader) : BaseReleasable() {

    private val loadables = mutableListOf<Loadable<*>>()

    var loadInfoCallback: ((Loadable<*>) -> Unit)? = null

    suspend fun loadSequential() {
        loadables.map { loadable ->
            loadInfoCallback?.invoke(loadable)
            loadable.load().onSuccess { it.releaseWith(this) }
        }
    }

    suspend fun loadParallel() {
        loadables
            .map { it to it.loadAsync() }
            .forEach { (loadable, deferred) ->
                loadInfoCallback?.invoke(loadable)
                deferred.await().onSuccess { it.releaseWith(this) }
            }
    }

    fun hdriGradient(gradient: ColorGradient, name: String = "hdriGradient"): Hdri {
        val loadebleHdri = Hdri(name) {
            Result.success(EnvironmentMap.fromGradientColor(gradient))
        }
        return loadebleHdri.also { loadables += it }
    }

    fun hdriImage(path: String, brightness: Float = 1f): Hdri {
        val loadebleHdri = Hdri(path) {
            assetLoader.hdriEnvironment(path, brightness)
        }
        return loadebleHdri.also { loadables += it }
    }

    fun hdriSingleColor(color: Color, name: String = "hdriSingleColor"): Hdri {
        val loadebleHdri = Hdri(name) {
            Result.success(EnvironmentMap.fromSingleColor(color))
        }
        return loadebleHdri.also { loadables += it }
    }

    fun model(path: String, config: GltfLoadConfig): GltfModel {
        return GltfModel(path, config).also { loadables += it }
    }

    fun texture2d(
        path: String,
        format: TexFormat = TexFormat.RGBA,
        mipMapping: MipMapping = MipMapping.Full,
        samplerSettings: SamplerSettings = SamplerSettings(),
        resolveSize: Vec2i? = null
    ): Tex2d {
        return Tex2d(path, format, mipMapping, samplerSettings, resolveSize).also { loadables += it }
    }

    abstract class Loadable<T: Releasable>(val name: String) {
        protected var loaded: T? = null
            set(value) {
                field = value
                value?.let { onLoaded.forEach { cb -> cb(it) } }
            }

        private val onLoaded = mutableListOf<(T) -> Unit>()

        abstract suspend fun load(): Result<T>
        fun loadAsync(): Deferred<Result<T>> = Assets.async { load() }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = loaded ?: error("$name not yet loaded")

        fun onLoaded(block: (T) -> Unit) {
            if (loaded != null) {
                block(loaded!!)
            } else {
                onLoaded += block
            }
        }
    }

    inner class GltfModel(name: String, val config: GltfLoadConfig) : Loadable<Model>(name) {
        override suspend fun load() = assetLoader.loadGltfModel(name, config).onSuccess { loaded = it }
    }

    inner class Hdri(name: String, val loader: suspend () -> Result<EnvironmentMap>) : Loadable<EnvironmentMap>(name) {
        override suspend fun load() = loader().onSuccess { loaded = it }
    }

    inner class Tex2d(
        name: String,
        private val format: TexFormat,
        private val mipMapping: MipMapping,
        private val samplerSettings: SamplerSettings,
        private val resolveSize: Vec2i?
    ) : Loadable<Texture2d>(name) {
        override suspend fun load() = Assets.loadTexture2d(name, format, mipMapping, samplerSettings, resolveSize).onSuccess { loaded = it }
    }
}