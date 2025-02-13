package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.MixedBuffer

class BindGroupData(val layout: BindGroupLayout) : BaseReleasable() {

    val bindings: List<BindingData> = layout.bindings.map {
        when (it) {
            is UniformBufferLayout -> UniformBufferBindingData(it)

            is StorageBuffer1dLayout -> StorageBuffer1dBindingData(it)
            is StorageBuffer2dLayout -> StorageBuffer2dBindingData(it)
            is StorageBuffer3dLayout -> StorageBuffer3dBindingData(it)

            is Texture1dLayout -> Texture1dBindingData(it)
            is Texture2dLayout -> Texture2dBindingData(it)
            is Texture3dLayout -> Texture3dBindingData(it)
            is TextureCubeLayout -> TextureCubeBindingData(it)
            is Texture2dArrayLayout -> Texture2dArrayBindingData(it)
            is TextureCubeArrayLayout -> TextureCubeArrayBindingData(it)

            is StorageTexture1dLayout -> StorageTexture1dBindingData(it)
            is StorageTexture2dLayout -> StorageTexture2dBindingData(it)
            is StorageTexture3dLayout -> StorageTexture3dBindingData(it)
        }
    }
    var isDirty = true

    internal var checkFrame = -1
    internal var isCheckOk = false
    var modCnt = -1

    val isComplete: Boolean
        get() = bindings.all { it.isComplete }

    var gpuData: GpuBindGroupData? = null
        internal set

    fun uniformBufferBindingData(bindingIndex: Int) = bindings[bindingIndex] as UniformBufferBindingData

    fun storageBuffer1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageBuffer1dBindingData
    fun storageBuffer2dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageBuffer2dBindingData
    fun storageBuffer3dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageBuffer3dBindingData

    fun texture1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture1dBindingData
    fun texture2dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture2dBindingData
    fun texture3dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture3dBindingData
    fun textureCubeBindingData(bindingIndex: Int) = bindings[bindingIndex] as TextureCubeBindingData
    fun texture2dArrayBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture2dArrayBindingData
    fun textureCubeArrayBindingData(bindingIndex: Int) = bindings[bindingIndex] as TextureCubeArrayBindingData

    fun storageTexture1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture1dBindingData
    fun storageTexture2dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture2dBindingData
    fun storageTexture3dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture3dBindingData

    fun copy(): BindGroupData {
        val copy = BindGroupData(layout)
        bindings.forEachIndexed { i, bindingData ->
            when (bindingData) {
                is UniformBufferBindingData -> bindingData.copyTo(copy.bindings[i] as UniformBufferBindingData)

                is StorageBuffer1dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageBuffer1dBindingData)
                is StorageBuffer2dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageBuffer2dBindingData)
                is StorageBuffer3dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageBuffer3dBindingData)

                is Texture1dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture1dBindingData)
                is Texture2dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture2dBindingData)
                is Texture3dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture3dBindingData)
                is TextureCubeBindingData -> bindingData.copyTo(copy.bindings[i] as TextureCubeBindingData)
                is Texture2dArrayBindingData -> bindingData.copyTo(copy.bindings[i] as Texture2dArrayBindingData)
                is TextureCubeArrayBindingData -> bindingData.copyTo(copy.bindings[i] as TextureCubeArrayBindingData)

                is StorageTexture1dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageTexture1dBindingData)
                is StorageTexture2dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageTexture2dBindingData)
                is StorageTexture3dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageTexture3dBindingData)
            }
        }
        return copy
    }

    override fun release() {
        super.release()
        gpuData?.release()
    }

    sealed interface BindingData {
        val layout: BindingLayout
        val name: String get() = layout.name

        val isComplete: Boolean
    }

    inner class UniformBufferBindingData(override val layout: UniformBufferLayout) : BindingData {
        var modCount = 0
            private set
        val buffer: MixedBuffer = MixedBuffer(layout.layout.size)

        override val isComplete = true

        fun markDirty() {
            modCount++
        }

        fun copyTo(other: UniformBufferBindingData) {
            check(layout.hash == other.layout.hash)
            for (i in 0 until buffer.capacity) {
                other.buffer.setInt8(i, buffer.getInt8(i))
            }
            other.markDirty()
        }
    }

    abstract inner class StorageBufferBindingData<T: StorageBuffer> {
        abstract val layout: StorageBufferLayout
        var storageBuffer: T? = null
            set(value) {
                value?.let { checkDimensions(it) }
                field = value
                isDirty = true
            }

        abstract fun checkDimensions(storageBuffer: T)

        fun getAndClearDirtyFlag(): Boolean {
            val isDirty = storageBuffer?.isDirty == true
            storageBuffer?.isDirty = false
            return isDirty
        }

        fun copyTo(other: StorageBufferBindingData<T>) {
            other.storageBuffer = storageBuffer
        }
    }

    inner class StorageBuffer1dBindingData(override val layout: StorageBuffer1dLayout) :
        StorageBufferBindingData<StorageBuffer1d>(), BindingData
    {
        override val isComplete = true

        override fun checkDimensions(storageBuffer: StorageBuffer1d) = check(isMatchingDimensions(storageBuffer)) {
            "Incorrect buffer dimensions. Layout ${layout.name}: ${layout.sizeX}, provided: ${storageBuffer.sizeX}"
        }

        fun isMatchingDimensions(storageBuffer: StorageBuffer1d): Boolean {
            return layout.sizeX == null || layout.sizeX == storageBuffer.sizeX
        }
    }

    inner class StorageBuffer2dBindingData(override val layout: StorageBuffer2dLayout) :
        StorageBufferBindingData<StorageBuffer2d>(), BindingData
    {
        override val isComplete = true

        override fun checkDimensions(storageBuffer: StorageBuffer2d) = check(isMatchingDimensions(storageBuffer)) {
            "Incorrect buffer dimensions. Layout ${layout.name}: (${layout.sizeX}, ${layout.sizeY}), provided: (${storageBuffer.sizeX}, ${storageBuffer.sizeY})"
        }

        fun isMatchingDimensions(storageBuffer: StorageBuffer2d): Boolean {
            return layout.sizeX == storageBuffer.sizeX && (layout.sizeY == null || layout.sizeY == storageBuffer.sizeY)
        }
    }

    inner class StorageBuffer3dBindingData(override val layout: StorageBuffer3dLayout) :
        StorageBufferBindingData<StorageBuffer3d>(), BindingData
    {
        override val isComplete = true

        override fun checkDimensions(storageBuffer: StorageBuffer3d) = check(isMatchingDimensions(storageBuffer)) {
            "Incorrect buffer dimensions. Layout ${layout.name}: (${layout.sizeX}, ${layout.sizeY}, ${layout.sizeZ}), provided: (${storageBuffer.sizeX}, ${storageBuffer.sizeY}, ${storageBuffer.sizeZ})"
        }

        fun isMatchingDimensions(storageBuffer: StorageBuffer3d): Boolean {
            return layout.sizeX == storageBuffer.sizeX && layout.sizeY == storageBuffer.sizeY && (layout.sizeZ == null || layout.sizeZ == storageBuffer.sizeZ)
        }
    }

    abstract inner class TextureBindingData<T: Texture<*>> {
        abstract val layout: TextureLayout
        val isComplete get() = texture?.isLoaded == true

        var texture: T? = null
            set(value) {
                isDirty = field !== value
                field = value
            }

        var sampler: SamplerSettings? = null
            set(value) {
                isDirty = field != value
                field = value
            }

        fun copyTo(other: TextureBindingData<T>) {
            other.texture = texture
            other.sampler = sampler
        }
    }

    inner class Texture1dBindingData(override val layout: Texture1dLayout) : TextureBindingData<Texture1d>(), BindingData
    inner class Texture2dBindingData(override val layout: Texture2dLayout) : TextureBindingData<Texture2d>(), BindingData
    inner class Texture3dBindingData(override val layout: Texture3dLayout) : TextureBindingData<Texture3d>(), BindingData
    inner class TextureCubeBindingData(override val layout: TextureCubeLayout) : TextureBindingData<TextureCube>(), BindingData
    inner class Texture2dArrayBindingData(override val layout: Texture2dArrayLayout) : TextureBindingData<Texture2dArray>(), BindingData
    inner class TextureCubeArrayBindingData(override val layout: TextureCubeArrayLayout) : TextureBindingData<TextureCubeArray>(), BindingData

    abstract inner class StorageTextureBindingData<T: StorageTexture> {
        abstract val layout: StorageTextureLayout
        val isComplete = true

        var storageTexture: T? = null
            set(value) {
                isDirty = field !== value
                field = value
            }
        var mipLevel: Int = 0
            set(value) {
                isDirty = field != value
                field = value
            }

        fun copyTo(other: StorageTextureBindingData<T>) {
            other.storageTexture = storageTexture
            other.mipLevel = mipLevel
        }
    }

    inner class StorageTexture1dBindingData(override val layout: StorageTexture1dLayout) : StorageTextureBindingData<StorageTexture1d>(), BindingData
    inner class StorageTexture2dBindingData(override val layout: StorageTexture2dLayout) : StorageTextureBindingData<StorageTexture2d>(), BindingData
    inner class StorageTexture3dBindingData(override val layout: StorageTexture3dLayout) : StorageTextureBindingData<StorageTexture3d>(), BindingData
}
