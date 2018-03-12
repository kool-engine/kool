package de.fabmax.kool

import de.fabmax.kool.math.clamp
import de.fabmax.kool.platform.KoolActivity
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import java.util.*

/**
 * Android specific platform call implementations
 */

actual var glCapabilities = GlCapabilities.UNKNOWN_CAPABILITIES

actual fun now(): Double = System.nanoTime() / 1e6

actual fun createContext(props: RenderContext.InitProps): RenderContext {
    val androidProps = props as? AndroidInitProps ?: throw KoolException("Supplied props must sub-class ${AndroidInitProps::javaClass.name}")
    return androidProps.koolActivity.createContext(androidProps)
}

actual fun formatDouble(d: Double, precision: Int): String =
        java.lang.String.format(Locale.ENGLISH, "%.${precision.clamp(0, 12)}f", d)

actual fun createCharMap(fontProps: FontProps): CharMap =
        koolActivity?.createCharMap(fontProps) ?: throw KoolException("not initialized!")

actual fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) =
        koolActivity?.loadAsset(assetPath, onLoad) ?: throw KoolException("not initialized!")

actual fun loadTextureAsset(assetPath: String): TextureData =
        koolActivity?.loadTextureAsset(assetPath) ?: throw KoolException("not initialized!")

actual fun openUrl(url: String) =
        koolActivity?.openUrl(url) ?: throw KoolException("not initialized!")

actual fun getMemoryInfo(): String {
    val rt = Runtime.getRuntime()
    val freeMem = rt.freeMemory()
    val totalMem = rt.totalMemory()
    return "Heap: ${(totalMem - freeMem) / 1024 / 1024} / ${totalMem / 1024 / 1024} MB"
}

class AndroidInitProps(val koolActivity: KoolActivity) : RenderContext.InitProps()

// fixme: This is super bad practice and will be fixed soon
internal var koolActivity: KoolActivity? = null
