package de.fabmax.kool.util

import de.fabmax.kool.Assets
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.loadGltfModelAsync
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Model
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlin.reflect.KProperty

class ResourceGroup : BaseReleasable() {

    private val loadables = mutableListOf<Loadable<*>>()

    var loadInfoCallback: ((Loadable<*>) -> Unit)? = null

    suspend fun loadGroupSequential() {
        loadables.map {
            loadInfoCallback?.invoke(it)
            it.load()?.releaseWith(this)
        }
    }

    suspend fun loadGroupParallel() {
        loadables.map {
            loadInfoCallback?.invoke(it)
            it.loadAsync()
        }.forEach { it.await()?.releaseWith(this) }
    }

    fun hdriGradient(gradient: ColorGradient, name: String = "hdriGradient"): Hdri {
        val loadebleHdri = Hdri(name) {
            CompletableDeferred(EnvironmentHelper.gradientColorEnvironment(gradient))
        }
        return loadebleHdri.also { loadables += it }
    }

    fun hdriImage(path: String, brightness: Float = 1f): Hdri {
        val loadebleHdri = Hdri(path) {
            EnvironmentHelper.hdriEnvironmentAsync(path, brightness)
        }
        return loadebleHdri.also { loadables += it }
    }

    fun hdriSingleColor(color: Color, name: String = "hdriSingleColor"): Hdri {
        val loadebleHdri = Hdri(name) {
            CompletableDeferred(EnvironmentHelper.singleColorEnvironment(color))
        }
        return loadebleHdri.also { loadables += it }
    }

    fun model(path: String, config: GltfLoadConfig): GltfModel {
        return GltfModel(path, config).also { loadables += it }
    }

    fun texture2d(path: String, props: TextureProps = TextureProps()): Tex2d {
        return Tex2d(path, props).also { loadables += it }
    }

    abstract class Loadable<T: Releasable>(val name: String) {
        protected var loaded: T? = null
            set(value) {
                field = value
                value?.let { onLoaded.forEach { cb -> cb(it) } }
            }

        private val onLoaded = mutableListOf<(T) -> Unit>()

        suspend fun load(): Releasable? = loadAsync().await()
        abstract fun loadAsync(): Deferred<Releasable?>

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
        override fun loadAsync() = Assets.loadGltfModelAsync(name, config).also { deferred ->
            deferred.invokeOnCompletion { loaded = deferred.getCompleted() }
        }
    }

    inner class Hdri(name: String, val loader: () -> Deferred<EnvironmentMaps>) : Loadable<EnvironmentMaps>(name) {
        override fun loadAsync() = loader().also { deferred ->
            deferred.invokeOnCompletion { loaded = deferred.getCompleted() }
        }
    }

    inner class Tex2d(name: String, private val props: TextureProps) : Loadable<Texture2d>(name) {
        override fun loadAsync() = Assets.loadTexture2dAsync(name, props).also { deferred ->
            deferred.invokeOnCompletion { loaded = deferred.getCompleted() }
        }
    }

}