package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.util.*

class BindGroupData(val layout: BindGroupLayout, val name: String) : BaseReleasable(), DoubleBuffered {
    var gpuData: GpuBindGroupData? = null

    private val bindings: List<BindingData> = layout.bindings.toData()
    private val _bufferedBindingData: List<BindingData> = layout.bindings.toData()
    private var captured = false

    val size: Int get() = bindings.size
    val isComplete: Boolean get() = bindings.all { it.isComplete }

    val bufferedBindings: List<BindingData> get() {
        if (!captured) {
            logE { "BindGroupData not captured! $name / ${layout.scope}" }
            return bindings
        }
        return _bufferedBindingData
    }

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
        val copy = BindGroupData(layout, name)
        bindings.copyTo(copy.bindings)
        return copy
    }

    override fun captureBuffer() {
        bindings.copyToIfModded(_bufferedBindingData)
        captured = true
    }

    override fun doRelease() {
        gpuData?.releaseDelayed(1)
    }

    private fun List<BindingLayout>.toData() = map {
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

    private fun List<BindingData>.copyTo(dst: List<BindingData>) {
        for (i in indices) {
            when (val bindingData = this[i]) {
                is UniformBufferBindingData<*> -> bindingData.copyTo(dst[i] as UniformBufferBindingData<*>)
                is StorageBufferBindingData -> bindingData.copyTo(dst[i] as StorageBufferBindingData)

                is Texture1dBindingData -> bindingData.copyTo(dst[i] as Texture1dBindingData)
                is Texture2dBindingData -> bindingData.copyTo(dst[i] as Texture2dBindingData)
                is Texture3dBindingData -> bindingData.copyTo(dst[i] as Texture3dBindingData)
                is TextureCubeBindingData -> bindingData.copyTo(dst[i] as TextureCubeBindingData)
                is Texture2dArrayBindingData -> bindingData.copyTo(dst[i] as Texture2dArrayBindingData)
                is TextureCubeArrayBindingData -> bindingData.copyTo(dst[i] as TextureCubeArrayBindingData)

                is StorageTexture1dBindingData -> bindingData.copyTo(dst[i] as StorageTexture1dBindingData)
                is StorageTexture2dBindingData -> bindingData.copyTo(dst[i] as StorageTexture2dBindingData)
                is StorageTexture3dBindingData -> bindingData.copyTo(dst[i] as StorageTexture3dBindingData)
            }
        }
    }

    private fun List<BindingData>.copyToIfModded(dst: List<BindingData>) {
        for (i in indices) {
            val src = this[i]
            val dst = dst[i]
            if (src.modCount.isDirty(dst.modCount)) {
                when (src) {
                    is UniformBufferBindingData<*> -> src.copyTo(dst as UniformBufferBindingData<*>)
                    is StorageBufferBindingData -> src.copyTo(dst as StorageBufferBindingData)

                    is Texture1dBindingData -> src.copyTo(dst as Texture1dBindingData)
                    is Texture2dBindingData -> src.copyTo(dst as Texture2dBindingData)
                    is Texture3dBindingData -> src.copyTo(dst as Texture3dBindingData)
                    is TextureCubeBindingData -> src.copyTo(dst as TextureCubeBindingData)
                    is Texture2dArrayBindingData -> src.copyTo(dst as Texture2dArrayBindingData)
                    is TextureCubeArrayBindingData -> src.copyTo(dst as TextureCubeArrayBindingData)

                    is StorageTexture1dBindingData -> src.copyTo(dst as StorageTexture1dBindingData)
                    is StorageTexture2dBindingData -> src.copyTo(dst as StorageTexture2dBindingData)
                    is StorageTexture3dBindingData -> src.copyTo(dst as StorageTexture3dBindingData)
                }
            }
        }
    }

    sealed interface BindingData {
        val layout: BindingLayout
        val name: String get() = layout.name
        val modCount: ModCounter
        val isComplete: Boolean
    }

    inner class UniformBufferBindingData<T: Struct>(override val layout: UniformBufferLayout<T>) : BindingData {
        override val modCount = ModCounter()
        val buffer: StructBuffer<T> = StructBuffer(layout.struct, 1)

        override val isComplete = true

        inline fun set(block: MutableStructBufferView<T>.(T) -> Unit) {
            buffer.set(0, block)
            markDirty()
        }

        fun markDirty() {
            modCount.increment()
        }

        fun copyTo(other: UniformBufferBindingData<*>) {
            check(layout.hash == other.layout.hash)
            other.buffer.clear()
            @Suppress("UNCHECKED_CAST")
            (other.buffer as StructBuffer<T>).putAll(buffer)
            other.modCount.reset(modCount)
        }
    }

    inner class StorageBufferBindingData(override val layout: StorageBufferLayout) : BindingData {
        override val modCount = ModCounter()
        var storageBuffer: GpuBuffer? = null
            set(value) {
                value?.let { checkDimensions(it) }
                field = value
                modCount.increment()
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
            other.modCount.reset(modCount)
        }
    }

    abstract inner class TextureBindingData<T: Texture<*>> {
        abstract val layout: TextureLayout
        val modCount = ModCounter()
        val isComplete get() = texture?.isLoaded == true

        var texture: T? = null
            set(value) {
                modCount.incrementIf(field !== value)
                field = value
            }

        var sampler: SamplerSettings? = null
            set(value) {
                modCount.incrementIf(field !== value)
                field = value
            }

        fun copyTo(other: TextureBindingData<T>) {
            other.texture = texture
            other.sampler = sampler
            other.modCount.reset(modCount)
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
        val modCount = ModCounter()
        val isComplete = true

        var storageTexture: T? = null
            set(value) {
                modCount.incrementIf(field !== value)
                field = value
            }
        var mipLevel: Int = 0
            set(value) {
                modCount.incrementIf(field != value)
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
