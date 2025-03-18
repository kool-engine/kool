package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.NormalMapConfig
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.UniqueId

/**
 * Base class for all regular and compute shaders. Provides methods to easily connect to shader uniforms.
 */
abstract class ShaderBase<T: PipelineBase>(val name: String) {

    private val pipelineBindings = mutableMapOf<String, PipelineBinding>()

    var createdPipeline: T? = null
        private set

    protected open fun pipelineCreated(pipeline: T) {
        createdPipeline = pipeline
        pipelineBindings.values.forEach { it.setup(pipeline) }
        pipeline.onRelease { createdPipeline = null }
    }

    private fun getOrCreateBinding(name: String, block: () -> PipelineBinding): PipelineBinding {
        return pipelineBindings.getOrPut(name) {
            block().also { binding -> createdPipeline?.let { binding.setup(it) } }
        }
    }

    fun uniform1f(uniformName: String, defaultVal: Float = 0f): UniformBinding1f =
        getOrCreateBinding(uniformName) { UniformBinding1f(uniformName, defaultVal, this) } as UniformBinding1f
    fun uniform2f(uniformName: String, defaultVal: Vec2f = Vec2f.ZERO): UniformBinding2f =
        getOrCreateBinding(uniformName) { UniformBinding2f(uniformName, defaultVal, this) } as UniformBinding2f
    fun uniform3f(uniformName: String, defaultVal: Vec3f = Vec3f.ZERO): UniformBinding3f =
        getOrCreateBinding(uniformName) { UniformBinding3f(uniformName, defaultVal, this) } as UniformBinding3f
    fun uniform4f(uniformName: String, defaultVal: Vec4f = Vec4f.ZERO): UniformBinding4f =
        getOrCreateBinding(uniformName) { UniformBinding4f(uniformName, defaultVal, this) } as UniformBinding4f
    fun uniformColor(uniformName: String, defaultVal: Color = Color.BLACK): UniformBindingColor =
        getOrCreateBinding(uniformName) { UniformBindingColor(uniformName, defaultVal, this) } as UniformBindingColor
    fun uniformQuat(uniformName: String, defaultVal: QuatF = QuatF.IDENTITY): UniformBindingQuat =
        getOrCreateBinding(uniformName) { UniformBindingQuat(uniformName, defaultVal, this) } as UniformBindingQuat

    fun uniform1i(uniformName: String, defaultVal: Int = 0): UniformBinding1i =
        getOrCreateBinding(uniformName) { UniformBinding1i(uniformName, defaultVal, this) } as UniformBinding1i
    fun uniform2i(uniformName: String, defaultVal: Vec2i = Vec2i.ZERO): UniformBinding2i =
        getOrCreateBinding(uniformName) { UniformBinding2i(uniformName, defaultVal, this) } as UniformBinding2i
    fun uniform3i(uniformName: String, defaultVal: Vec3i = Vec3i.ZERO): UniformBinding3i =
        getOrCreateBinding(uniformName) { UniformBinding3i(uniformName, defaultVal, this) } as UniformBinding3i
    fun uniform4i(uniformName: String, defaultVal: Vec4i = Vec4i.ZERO): UniformBinding4i =
        getOrCreateBinding(uniformName) { UniformBinding4i(uniformName, defaultVal, this) } as UniformBinding4i

    fun uniformMat2f(uniformName: String, defaultVal: Mat2f = Mat2f.IDENTITY): UniformBindingMat2f =
        getOrCreateBinding(uniformName) { UniformBindingMat2f(uniformName, defaultVal, this) } as UniformBindingMat2f
    fun uniformMat3f(uniformName: String, defaultVal: Mat3f = Mat3f.IDENTITY): UniformBindingMat3f =
        getOrCreateBinding(uniformName) { UniformBindingMat3f(uniformName, defaultVal, this) } as UniformBindingMat3f
    fun uniformMat4f(uniformName: String, defaultVal: Mat4f = Mat4f.IDENTITY): UniformBindingMat4f =
        getOrCreateBinding(uniformName) { UniformBindingMat4f(uniformName, defaultVal, this) } as UniformBindingMat4f

    fun uniform1fv(uniformName: String, arraySize: Int = 0): UniformBinding1fv =
        getOrCreateBinding(uniformName) { UniformBinding1fv(uniformName, arraySize, this) } as UniformBinding1fv
    fun uniform2fv(uniformName: String, arraySize: Int = 0): UniformBinding2fv =
        getOrCreateBinding(uniformName) { UniformBinding2fv(uniformName, arraySize, this) } as UniformBinding2fv
    fun uniform3fv(uniformName: String, arraySize: Int = 0): UniformBinding3fv =
        getOrCreateBinding(uniformName) { UniformBinding3fv(uniformName, arraySize, this) } as UniformBinding3fv
    fun uniform4fv(uniformName: String, arraySize: Int = 0): UniformBinding4fv =
        getOrCreateBinding(uniformName) { UniformBinding4fv(uniformName, arraySize, this) } as UniformBinding4fv

    fun uniform1iv(uniformName: String, arraySize: Int = 0): UniformBinding1iv =
        getOrCreateBinding(uniformName) { UniformBinding1iv(uniformName, arraySize, this) } as UniformBinding1iv
    fun uniform2iv(uniformName: String, arraySize: Int = 0): UniformBinding2iv =
        getOrCreateBinding(uniformName) { UniformBinding2iv(uniformName, arraySize, this) } as UniformBinding2iv
    fun uniform3iv(uniformName: String, arraySize: Int = 0): UniformBinding3iv =
        getOrCreateBinding(uniformName) { UniformBinding3iv(uniformName, arraySize, this) } as UniformBinding3iv
    fun uniform4iv(uniformName: String, arraySize: Int = 0): UniformBinding4iv =
        getOrCreateBinding(uniformName) { UniformBinding4iv(uniformName, arraySize, this) } as UniformBinding4iv

    fun uniformMat2fv(uniformName: String, arraySize: Int = 0): UniformBindingMat2fv =
        getOrCreateBinding(uniformName) { UniformBindingMat2fv(uniformName, arraySize, this) } as UniformBindingMat2fv
    fun uniformMat3fv(uniformName: String, arraySize: Int = 0): UniformBindingMat3fv =
        getOrCreateBinding(uniformName) { UniformBindingMat3fv(uniformName, arraySize, this) } as UniformBindingMat3fv
    fun uniformMat4fv(uniformName: String, arraySize: Int = 0): UniformBindingMat4fv =
        getOrCreateBinding(uniformName) { UniformBindingMat4fv(uniformName, arraySize, this) } as UniformBindingMat4fv

    @Suppress("UNCHECKED_CAST")
    fun <S: Struct> uniformStruct(uniformName: String, provider: () -> S) =
        getOrCreateBinding(uniformName) { UniformStructBinding<S>(uniformName, this, provider) } as UniformStructBinding<S>

    fun texture1d(textureName: String, defaultVal: Texture1d? = null, defaultSampler: SamplerSettings? = null): Texture1dBinding =
        getOrCreateBinding(textureName) { Texture1dBinding(textureName, defaultVal, defaultSampler, this) } as Texture1dBinding
    fun texture2d(textureName: String, defaultVal: Texture2d? = null, defaultSampler: SamplerSettings? = null): Texture2dBinding =
        getOrCreateBinding(textureName) { Texture2dBinding(textureName, defaultVal, defaultSampler, this) } as Texture2dBinding
    fun texture3d(textureName: String, defaultVal: Texture3d? = null, defaultSampler: SamplerSettings? = null): Texture3dBinding =
        getOrCreateBinding(textureName) { Texture3dBinding(textureName, defaultVal, defaultSampler, this) } as Texture3dBinding
    fun textureCube(textureName: String, defaultVal: TextureCube? = null, defaultSampler: SamplerSettings? = null): TextureCubeBinding =
        getOrCreateBinding(textureName) { TextureCubeBinding(textureName, defaultVal, defaultSampler, this) } as TextureCubeBinding

    fun texture2dArray(textureName: String, defaultVal: Texture2dArray? = null, defaultSampler: SamplerSettings? = null): Texture2dArrayBinding =
        getOrCreateBinding(textureName) { Texture2dArrayBinding(textureName, defaultVal, defaultSampler, this) } as Texture2dArrayBinding
    fun textureCubeArray(textureName: String, defaultVal: TextureCubeArray? = null, defaultSampler: SamplerSettings? = null): TextureCubeArrayBinding =
        getOrCreateBinding(textureName) { TextureCubeArrayBinding(textureName, defaultVal, defaultSampler, this) } as TextureCubeArrayBinding

    fun storage(storageName: String, defaultVal: GpuBuffer? = null): StorageBufferBinding =
        getOrCreateBinding(storageName) { StorageBufferBinding(storageName, defaultVal, this) } as StorageBufferBinding

    fun storageTexture1d(storageTextureName: String, defaultVal: StorageTexture1d? = null, defaultMipLevel: Int = 0): StorageTexture1dBinding =
        getOrCreateBinding(storageTextureName) { StorageTexture1dBinding(storageTextureName, defaultVal, defaultMipLevel, this) } as StorageTexture1dBinding
    fun storageTexture2d(storageTextureName: String, defaultVal: StorageTexture2d? = null, defaultMipLevel: Int = 0): StorageTexture2dBinding =
        getOrCreateBinding(storageTextureName) { StorageTexture2dBinding(storageTextureName, defaultVal, defaultMipLevel, this) } as StorageTexture2dBinding
    fun storageTexture3d(storageTextureName: String, defaultVal: StorageTexture3d? = null, defaultMipLevel: Int = 0): StorageTexture3dBinding =
        getOrCreateBinding(storageTextureName) { StorageTexture3dBinding(storageTextureName, defaultVal, defaultMipLevel, this) } as StorageTexture3dBinding

    fun colorUniform(cfg: ColorBlockConfig): UniformBindingColor =
        uniformColor(cfg.primaryUniform?.uniformName ?: UniqueId.nextId("_"), cfg.primaryUniform?.defaultColor ?: Color.BLACK)
    fun colorTexture(cfg: ColorBlockConfig): Texture2dBinding =
        texture2d(cfg.primaryTexture?.textureName ?: UniqueId.nextId("_"), cfg.primaryTexture?.defaultTexture)

    fun normalTexture(cfg: NormalMapConfig): Texture2dBinding =
        if (cfg.isNormalMapped && !cfg.isArrayNormalMap) texture2d(cfg.textureName, cfg.defaultNormalMap) else Texture2dBinding("-", null, null, this)

    fun propertyUniform(cfg: PropertyBlockConfig): UniformBinding1f =
        uniform1f(cfg.primaryUniform?.uniformName ?: UniqueId.nextId("_"), cfg.primaryUniform?.defaultValue ?: 0f)
    fun propertyTexture(cfg: PropertyBlockConfig): Texture2dBinding =
        texture2d(cfg.primaryTexture?.textureName ?: UniqueId.nextId("_"), cfg.primaryTexture?.defaultTexture)

    protected interface ConnectUniformListener {
        val isConnected: Boolean
        fun connect()
    }
}