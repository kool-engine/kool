package de.fabmax.kool.platform

import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.TextureProps
import de.fabmax.kool.TextureResource
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.Font

/**
 * @author fabmax
 */

abstract class Platform {

    companion object {
        // initialize platform implementation
        // once multiple platforms are supported the correct one can be determined at runtime here
        private var instance: Platform = NoopPlatform()

        val supportsMultiContext: Boolean
            get() = instance.supportsMultiContext
        val supportsUint32Indices: Boolean
            get() = instance.supportsUint32Indices

        fun initPlatform(platform: Platform) {
            instance = platform
        }

        fun createContext(props: RenderContext.InitProps): RenderContext {
            return instance.createContext(props)
        }

        fun createDefaultShaderGenerator(): ShaderGenerator {
            return instance.createDefaultShaderGenerator()
        }

        fun getGlImpl(): GL.Impl {
            return instance.getGlImpl()
        }

        fun createUint8Buffer(capacity: Int): Uint8Buffer {
            return instance.createUint8Buffer(capacity)
        }

        fun createUint16Buffer(capacity: Int): Uint16Buffer {
            return instance.createUint16Buffer(capacity)
        }

        fun createUint32Buffer(capacity: Int): Uint32Buffer {
            return instance.createUint32Buffer(capacity)
        }

        fun createFloat32Buffer(capacity: Int): Float32Buffer {
            return instance.createFloat32Buffer(capacity)
        }

        fun currentTimeMillis(): Long {
            return instance.currentTimeMillis()
        }

        fun loadTextureAsset(assetPath: String): TextureData {
            return instance.loadTextureAsset(assetPath)
        }

        fun createCharMap(font: Font, chars: String): CharMap {
            return instance.createCharMap(font, chars)
        }
    }

    abstract val supportsMultiContext: Boolean

    abstract val supportsUint32Indices: Boolean

    abstract fun createContext(props: RenderContext.InitProps): RenderContext

    abstract fun createDefaultShaderGenerator(): ShaderGenerator

    abstract fun getGlImpl(): GL.Impl

    abstract fun createUint8Buffer(capacity: Int): Uint8Buffer

    abstract fun createUint16Buffer(capacity: Int): Uint16Buffer

    abstract fun createUint32Buffer(capacity: Int): Uint32Buffer

    abstract fun createFloat32Buffer(capacity: Int): Float32Buffer

    abstract fun currentTimeMillis(): Long

    abstract fun loadTextureAsset(assetPath: String): TextureData

    abstract fun createCharMap(font: Font, chars: String): CharMap

    private class NoopPlatform : Platform() {

        override val supportsMultiContext: Boolean
            get() = throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")

        override val supportsUint32Indices: Boolean
            get() = throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")

        override fun createDefaultShaderGenerator(): ShaderGenerator {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun getGlImpl(): GL.Impl {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun createContext(props: RenderContext.InitProps): RenderContext {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun createUint8Buffer(capacity: Int): Uint8Buffer {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun createUint16Buffer(capacity: Int): Uint16Buffer {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun createUint32Buffer(capacity: Int): Uint32Buffer {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun createFloat32Buffer(capacity: Int): Float32Buffer {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun currentTimeMillis(): Long {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun loadTextureAsset(assetPath: String): TextureData {
            throw UnsupportedOperationException("No platform set, call PlatformImpl.init() first")
        }

        override fun createCharMap(font: Font, chars: String): CharMap {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}

