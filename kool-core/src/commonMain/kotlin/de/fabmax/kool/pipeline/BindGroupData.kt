package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.StructBuffer

class BindGroupData(val layout: BindGroupLayout) : BaseReleasable() {

    val bindings: List<BindingData> = layout.bindings.map {
        when (it) {
            is UniformBufferLayout<*> -> UniformBufferBindingData(it)
            is StorageBufferLayout -> StorageBufferBindingData(it)

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

    var checkFrame = -1
    var isCheckOk = false
    var modCnt = -1

    val isComplete: Boolean
        get() = bindings.all { it.isComplete }

    var gpuData: GpuBindGroupData? = null

    @Suppress("UNCHECKED_CAST")
    fun <S: Struct> uniformStructBindingData(binding: UniformBufferLayout<S>) = bindings[binding.bindingIndex] as UniformBufferBindingData<S>
    fun uniformBufferBindingData(bindingIndex: Int) = bindings[bindingIndex] as UniformBufferBindingData<*>
    fun storageBuffer1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageBufferBindingData

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
                is UniformBufferBindingData<*> -> bindingData.copyTo(copy.bindings[i] as UniformBufferBindingData<*>)
                is StorageBufferBindingData -> bindingData.copyTo(copy.bindings[i] as StorageBufferBindingData)

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

    inner class UniformBufferBindingData<T: Struct>(override val layout: UniformBufferLayout<T>) : BindingData {
        var modCount = 0
            private set
        val buffer: StructBuffer<T> = StructBuffer(1, layout.structProvider())

        override val isComplete = true

        inline fun set(block: T.() -> Unit) {
            buffer.set(0, block)
            markDirty()
        }

        fun markDirty() {
            modCount++
        }

        fun copyTo(other: UniformBufferBindingData<*>) {
            check(layout.hash == other.layout.hash)
            for (i in 0 until buffer.buffer.capacity / 4) {
                other.buffer.buffer.setInt32(i * 4, buffer.buffer.getInt32(i * 4))
            }
            other.markDirty()
        }
    }

    inner class StorageBufferBindingData(override val layout: StorageBufferLayout) : BindingData {
        var storageBuffer: GpuBuffer? = null
            set(value) {
                value?.let { checkDimensions(it) }
                field = value
                isDirty = true
            }
        override val isComplete = true

        fun checkDimensions(storageBuffer: GpuBuffer) = check(isMatchingDimensions(storageBuffer)) {
            "Incorrect buffer dimensions. Layout ${layout.name}: ${layout.size}, provided: ${storageBuffer.size}"
        }

        fun isMatchingDimensions(storageBuffer: GpuBuffer): Boolean {
            return layout.size == null || layout.size == storageBuffer.size
        }

        fun copyTo(other: StorageBufferBindingData) {
            other.storageBuffer = storageBuffer
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
