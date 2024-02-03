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
            is StorageTexture1dLayout -> StorageTexture1dBindingData(it)
            is StorageTexture2dLayout -> StorageTexture2dBindingData(it)
            is StorageTexture3dLayout -> StorageTexture3dBindingData(it)
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
    fun storageTexture1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture1dBindingData
    fun storageTexture2dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture2dBindingData
    fun storageTexture3dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture3dBindingData

    fun copy(): BindGroupData {
        val copy = BindGroupData(layout)
        bindings.forEachIndexed { i, bindingData ->
            when (bindingData) {
                is UniformBufferBindingData -> bindingData.copyTo(copy.bindings[i] as UniformBufferBindingData)
                is Texture1dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture1dBindingData)
                is Texture2dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture2dBindingData)
                is Texture3dBindingData -> bindingData.copyTo(copy.bindings[i] as Texture3dBindingData)
                is TextureCubeBindingData -> bindingData.copyTo(copy.bindings[i] as TextureCubeBindingData)
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
                isDirty = false
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

    inner class StorageTexture1dBindingData(override val layout: StorageTexture1dLayout) : BindingData {
        var storageTexture: StorageTexture1d? = null
            set(value) {
                field = value
                isDirty = true
            }
        override val isComplete get() = storageTexture?.loadingState == Texture.LoadingState.LOADED

        fun copyTo(other: StorageTexture1dBindingData) {
            other.storageTexture = storageTexture
        }
    }

    inner class StorageTexture2dBindingData(override val layout: StorageTexture2dLayout) : BindingData {
        var storageTexture: StorageTexture2d? = null
            set(value) {
                field = value
                isDirty = true
            }
        override val isComplete get() = storageTexture?.loadingState == Texture.LoadingState.LOADED

        fun copyTo(other: StorageTexture2dBindingData) {
            other.storageTexture = storageTexture
        }
    }

    inner class StorageTexture3dBindingData(override val layout: StorageTexture3dLayout) : BindingData {
        var storageTexture: StorageTexture3d? = null
            set(value) {
                field = value
                isDirty = true
            }
        override val isComplete get() = storageTexture?.loadingState == Texture.LoadingState.LOADED

        fun copyTo(other: StorageTexture3dBindingData) {
            other.storageTexture = storageTexture
        }
    }
}
