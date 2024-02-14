package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.MixedBuffer

class BindGroupData(val layout: BindGroupLayout) : BaseReleasable() {

    val bindings: List<BindingData> = layout.bindings.map {
        when (it) {
            is UniformBufferLayout -> UniformBufferBindingData(it)
            is Texture1dLayout -> Texture1dBindingData(it)
            is Texture2dLayout -> Texture2dBindingData(it)
            is Texture3dLayout -> Texture3dBindingData(it)
            is TextureCubeLayout -> TextureCubeBindingData(it)
            is StorageBuffer1dLayout -> StorageBuffer1dBindingData(it)
            is StorageBuffer2dLayout -> StorageBuffer2dBindingData(it)
            is StorageBuffer3dLayout -> StorageBuffer3dBindingData(it)
        }
    }
    var isDirty = true

    val isComplete: Boolean
        get() = bindings.all { it.isComplete }

    var gpuData: GpuBindGroupData? = null
        internal set

    fun uniformBufferBindingData(bindingIndex: Int) = bindings[bindingIndex] as UniformBufferBindingData
    fun texture1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture1dBindingData
    fun texture2dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture2dBindingData
    fun texture3dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture3dBindingData
    fun textureCubeBindingData(bindingIndex: Int) = bindings[bindingIndex] as TextureCubeBindingData
    fun storageTexture1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageBuffer1dBindingData
    fun storageTexture2dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageBuffer2dBindingData
    fun storageTexture3dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageBuffer3dBindingData

    fun copy(): BindGroupData {
        val copy = BindGroupData(layout)
        bindings.forEachIndexed { i, bindingData ->
            when (bindingData) {
                is UniformBufferBindingData -> bindingData.copyTo(copy.bindings[i] as UniformBufferBindingData)
                is Texture1dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture1dBindingData)
                is Texture2dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture2dBindingData)
                is Texture3dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture3dBindingData)
                is TextureCubeBindingData -> bindingData.copyTo(copy.bindings[i] as TextureCubeBindingData)
                is StorageBuffer1dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageBuffer1dBindingData)
                is StorageBuffer2dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageBuffer2dBindingData)
                is StorageBuffer3dBindingData -> bindingData.copyTo(copy.bindings[i] as StorageBuffer3dBindingData)
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
        var isBufferDirty = true
        val buffer: MixedBuffer = MixedBuffer(layout.layout.size)

        override val isComplete = true

        fun getAndClearDirtyFlag(): Boolean {
            val isDirty = isBufferDirty
            isBufferDirty = false
            return isDirty
        }

        fun copyTo(other: UniformBufferBindingData) {
            check(layout.hash == other.layout.hash)
            for (i in 0 until buffer.capacity) {
                other.buffer.setInt8(i, buffer.getInt8(i))
            }
            other.isBufferDirty = true
        }
    }

    abstract inner class TextureBindingData<T: Texture> {
        val isComplete get() = texture?.loadingState == Texture.LoadingState.LOADED

        var texture: T? = null
            set(value) {
                field = value
                isDirty = true
            }

        var sampler: SamplerSettings? = null
            set(value) {
                field = value
                isDirty = true
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

    abstract inner class StorageBufferBindingData<T: StorageBuffer> {
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
}
