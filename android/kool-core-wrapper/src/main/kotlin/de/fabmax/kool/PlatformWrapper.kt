package de.fabmax.kool

import de.fabmax.kool.gl.GlImpl
import de.fabmax.kool.gl.glImpl
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import java.awt.Desktop
import java.net.URI

internal var platformImpl: PlatformImpl = NoPlatformImpl()

actual var glCapabilities = GlCapabilities.UNKNOWN_CAPABILITIES

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun createContext(props: RenderContext.InitProps): RenderContext {
    val wrapperProps = props as? WrapperInitProps ?: throw KoolException("Supplied props must sub-class ${WrapperInitProps::javaClass.name}")
    platformImpl = wrapperProps.platformImpl
    glImpl = wrapperProps.glImpl
    return platformImpl.createContext(props)
}

actual fun createCharMap(fontProps: FontProps): CharMap = platformImpl.createCharMap(fontProps)
actual fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) = platformImpl.loadAsset(assetPath, onLoad)
actual fun loadTextureAsset(assetPath: String): TextureData = platformImpl.loadTextureAsset(assetPath)
actual fun openUrl(url: String) = platformImpl.openUrl(url)
actual fun getMemoryInfo(): String = platformImpl.getMemoryInfo()

interface PlatformImpl {
    fun createContext(props: RenderContext.InitProps): RenderContext
    fun createCharMap(fontProps: FontProps): CharMap
    fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit)
    fun loadTextureAsset(assetPath: String): TextureData
    fun openUrl(url: String)
    fun getMemoryInfo(): String
}

private class NoPlatformImpl : PlatformImpl {
    override fun createContext(props: RenderContext.InitProps): RenderContext { throw KoolException("No implementation set") }
    override fun createCharMap(fontProps: FontProps): CharMap { throw KoolException("No implementation set") }
    override fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) { throw KoolException("No implementation set") }
    override fun loadTextureAsset(assetPath: String): TextureData { throw KoolException("No implementation set") }
    override fun openUrl(url: String) { throw KoolException("No implementation set") }
    override fun getMemoryInfo(): String { throw KoolException("No implementation set") }
}

class WrapperInitProps(val platformImpl: PlatformImpl, val glImpl: GlImpl) : RenderContext.InitProps()
