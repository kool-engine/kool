package de.fabmax.kool.util

import de.fabmax.kool.Assets
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Model
import kotlin.reflect.KProperty

class ResourceGroup : BaseReleasable() {

    private val loadables = mutableListOf<Loadable>()

    var loadInfoCallback: ((Loadable) -> Unit)? = null

    suspend fun loadGroup() {
        loadables.forEach {
            loadInfoCallback?.invoke(it)
            it.load()?.releaseWith(this)
        }
    }

    fun hdriGradient(gradient: ColorGradient, name: String = "hdriGradient"): Hdri {
        return Hdri(name) { EnvironmentHelper.gradientColorEnvironment(gradient) }.also { loadables += it }
    }

    fun hdriImage(path: String, brightness: Float = 1f): Hdri {
        return Hdri(path) {
            EnvironmentHelper.hdriEnvironment(path, brightness)
        }.also { loadables += it }
    }

    fun hdriSingleColor(color: Color, name: String = "hdriSingleColor"): Hdri {
        return Hdri(name) { EnvironmentHelper.singleColorEnvironment(color) }.also { loadables += it }
    }

    fun model(path: String, config: GltfLoadConfig): GltfModel {
        return GltfModel(path, config).also { loadables += it }
    }

    fun texture2d(path: String, props: TextureProps = TextureProps()): Tex2d {
        return Tex2d(path, props).also { loadables += it }
    }

    interface Loadable {
        val name: String
        suspend fun load(): Releasable?
    }

    inner class GltfModel(override val name: String, val config: GltfLoadConfig) : Loadable {
        private lateinit var loadedGltf: Model

        override suspend fun load(): Releasable? {
            Assets.loadGltfModel(name, config).also { loadedGltf = it }
            // models don't need to be released
            return null
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Model = loadedGltf
    }

    inner class Hdri(override val name: String, val loader: suspend () -> EnvironmentMaps) : Loadable {
        private lateinit var loadedHdri: EnvironmentMaps
        override suspend fun load() = loader().also { loadedHdri = it }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): EnvironmentMaps = loadedHdri
    }

    inner class Tex2d(override val name: String, private val props: TextureProps) : Loadable {
        private lateinit var loadedTex: Texture2d
        override suspend fun load() = Assets.loadTexture2d(name, props).also { loadedTex = it }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Texture2d = loadedTex
    }

}