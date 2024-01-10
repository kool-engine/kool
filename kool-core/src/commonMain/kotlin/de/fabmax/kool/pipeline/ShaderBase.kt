package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.UniqueId
import kotlin.reflect.KProperty

/**
 * Base class for all regular and compute shaders. Provides methods to easily connect to shader uniforms.
 */
abstract class ShaderBase<T: PipelineBase>(val name: String) {

    val texSamplers1d = mutableMapOf<String, Texture1dBinding>()
    val texSamplers2d = mutableMapOf<String, Texture2dBinding>()
    val texSamplers3d = mutableMapOf<String, Texture3dBinding>()
    val texSamplersCube = mutableMapOf<String, TextureCubeBinding>()

    val storage1d = mutableMapOf<String, StorageTexture1dBinding>()
    val storage2d = mutableMapOf<String, StorageTexture2dBinding>()
    val storage3d = mutableMapOf<String, StorageTexture3dBinding>()

    private val connectUniformListeners = mutableMapOf<String, ConnectUniformListener>()
    private val pipelineBindings = mutableMapOf<String, PipelineBinding>()

    var createdPipeline: T? = null
        private set

    protected fun pipelineCreated(pipeline: T) {
        createdPipeline = pipeline

        pipeline.bindGroupLayouts.flatMap { it.bindings }.forEach { binding ->
            when (binding) {
                is UniformBufferBinding -> { } //binding.uniforms.forEach { uniforms[it.name] = it }
                is Texture1dBinding -> texSamplers1d[binding.name] = binding
                is Texture2dBinding -> texSamplers2d[binding.name] = binding
                is Texture3dBinding -> texSamplers3d[binding.name] = binding
                is TextureCubeBinding -> texSamplersCube[binding.name] = binding
                is StorageTexture1dBinding -> storage1d[binding.name] = binding
                is StorageTexture2dBinding -> storage2d[binding.name] = binding
                is StorageTexture3dBinding -> storage3d[binding.name] = binding
            }
        }
        connectUniformListeners.values.forEach { it.connect() }
        pipelineBindings.values.forEach { it.setup(pipeline) }
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

    fun uniformMat3fv(uniformName: String, arraySize: Int = 0): UniformBindingMat3fv =
        getOrCreateBinding(uniformName) { UniformBindingMat3fv(uniformName, arraySize, this) } as UniformBindingMat3fv
    fun uniformMat4fv(uniformName: String, arraySize: Int = 0): UniformBindingMat4fv =
        getOrCreateBinding(uniformName) { UniformBindingMat4fv(uniformName, arraySize, this) } as UniformBindingMat4fv

    fun texture1d(uniformName: String, defaultVal: Texture1d? = null): ShaderBase<*>.UniformInputTexture1d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputTexture1d(uniformName, defaultVal) } as ShaderBase<*>.UniformInputTexture1d
    fun texture2d(uniformName: String, defaultVal: Texture2d? = null): ShaderBase<*>.UniformInputTexture2d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputTexture2d(uniformName, defaultVal) } as ShaderBase<*>.UniformInputTexture2d
    fun texture3d(uniformName: String, defaultVal: Texture3d? = null): ShaderBase<*>.UniformInputTexture3d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputTexture3d(uniformName, defaultVal) } as ShaderBase<*>.UniformInputTexture3d
    fun textureCube(uniformName: String, defaultVal: TextureCube? = null): ShaderBase<*>.UniformInputTextureCube =
        connectUniformListeners.getOrPut(uniformName) { UniformInputTextureCube(uniformName, defaultVal) } as ShaderBase<*>.UniformInputTextureCube

    fun texture1dArray(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInputTextureArray1d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputTextureArray1d(uniformName, arraySize) } as ShaderBase<*>.UniformInputTextureArray1d
    fun texture2dArray(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInputTextureArray2d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputTextureArray2d(uniformName, arraySize) } as ShaderBase<*>.UniformInputTextureArray2d
    fun texture3dArray(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInputTextureArray3d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputTextureArray3d(uniformName, arraySize) } as ShaderBase<*>.UniformInputTextureArray3d
    fun textureCubeArray(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInputTextureArrayCube =
        connectUniformListeners.getOrPut(uniformName) { UniformInputTextureArrayCube(uniformName, arraySize) } as ShaderBase<*>.UniformInputTextureArrayCube

    fun storage1d(uniformName: String, defaultVal: StorageTexture1d? = null): ShaderBase<*>.UniformInputStorage1d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputStorage1d(uniformName, defaultVal) } as ShaderBase<*>.UniformInputStorage1d
    fun storage2d(uniformName: String, defaultVal: StorageTexture2d? = null): ShaderBase<*>.UniformInputStorage2d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputStorage2d(uniformName, defaultVal) } as ShaderBase<*>.UniformInputStorage2d
    fun storage3d(uniformName: String, defaultVal: StorageTexture3d? = null): ShaderBase<*>.UniformInputStorage3d =
        connectUniformListeners.getOrPut(uniformName) { UniformInputStorage3d(uniformName, defaultVal) } as ShaderBase<*>.UniformInputStorage3d

    fun colorUniform(cfg: ColorBlockConfig): UniformBindingColor =
        uniformColor(cfg.primaryUniform?.uniformName ?: UniqueId.nextId("_"), cfg.primaryUniform?.defaultColor ?: Color.BLACK)
    fun colorTexture(cfg: ColorBlockConfig): ShaderBase<*>.UniformInputTexture2d =
        texture2d(cfg.primaryTexture?.textureName ?: UniqueId.nextId("_"), cfg.primaryTexture?.defaultTexture)

    fun propertyUniform(cfg: PropertyBlockConfig): UniformBinding1f =
        uniform1f(cfg.primaryUniform?.uniformName ?: UniqueId.nextId("_"), cfg.primaryUniform?.defaultValue ?: 0f)
    fun propertyTexture(cfg: PropertyBlockConfig): ShaderBase<*>.UniformInputTexture2d =
        texture2d(cfg.primaryTexture?.textureName ?: UniqueId.nextId("_"), cfg.primaryTexture?.defaultTexture)

    protected interface ConnectUniformListener {
        val isConnected: Boolean
        fun connect()
    }

    inner class UniformInputTexture1d(val uniformName: String, defaultVal: Texture1d?) : ConnectUniformListener {
        var uniform: Texture1dBinding? = null
        private var buffer: Texture1d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = texSamplers1d[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Texture1d? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Texture1d?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }

    inner class UniformInputTexture2d(val uniformName: String, defaultVal: Texture2d?) : ConnectUniformListener {
        var uniform: Texture2dBinding? = null
        private var buffer: Texture2d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = texSamplers2d[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Texture2d? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Texture2d?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }

    inner class UniformInputTexture3d(val uniformName: String, defaultVal: Texture3d?) : ConnectUniformListener {
        var uniform: Texture3dBinding? = null
        private var buffer: Texture3d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = texSamplers3d[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Texture3d? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Texture3d?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }

    inner class UniformInputTextureCube(val uniformName: String, defaultVal: TextureCube?) : ConnectUniformListener {
        var uniform: TextureCubeBinding? = null
        private var buffer: TextureCube? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = texSamplersCube[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): TextureCube? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TextureCube?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }

    inner class UniformInputTextureArray1d(val uniformName: String, val arrSize: Int) : ConnectUniformListener {
        var uniform: Texture1dBinding? = null
        private val buffer = Array<Texture1d?>(arrSize) { null }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = texSamplers1d[uniformName]?.apply {
                check(arraySize == arrSize) { "Mismatching texture array size: $arraySize != $arrSize" }
                for (i in textures.indices) { textures[i] = buffer[i] }
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<Texture1d?> = uniform?.textures ?: buffer
    }

    inner class UniformInputTextureArray2d(val uniformName: String, val arrSize: Int) : ConnectUniformListener {
        var uniform: Texture2dBinding? = null
        private val buffer = Array<Texture2d?>(arrSize) { null }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = texSamplers2d[uniformName]?.apply {
                check(arraySize == arrSize) { "Mismatching texture array size: $arraySize != $arrSize" }
                for (i in textures.indices) { textures[i] = buffer[i] }
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<Texture2d?> = uniform?.textures ?: buffer
    }

    inner class UniformInputTextureArray3d(val uniformName: String, val arrSize: Int) : ConnectUniformListener {
        var uniform: Texture3dBinding? = null
        private val buffer = Array<Texture3d?>(arrSize) { null }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = texSamplers3d[uniformName]?.apply {
                check(arraySize == arrSize) { "Mismatching texture array size: $arraySize != $arrSize" }
                for (i in textures.indices) { textures[i] = buffer[i] }
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<Texture3d?> = uniform?.textures ?: buffer
    }

    inner class UniformInputTextureArrayCube(val uniformName: String, val arrSize: Int) : ConnectUniformListener {
        var uniform: TextureCubeBinding? = null
        private val buffer = Array<TextureCube?>(arrSize) { null }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = texSamplersCube[uniformName]?.apply {
                check(arraySize == arrSize) { "Mismatching texture array size: $arraySize != $arrSize" }
                for (i in textures.indices) { textures[i] = buffer[i] }
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<TextureCube?> = uniform?.textures ?: buffer
    }

    inner class UniformInputStorage1d(val uniformName: String, defaultVal: StorageTexture1d?) : ConnectUniformListener {
        var uniform: StorageTexture1dBinding? = null
        private var buffer: StorageTexture1d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = storage1d[uniformName]?.apply { storageTex = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): StorageTexture1d? = uniform?.storageTex ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: StorageTexture1d?) {
            uniform?.let { it.storageTex = value } ?: run { buffer = value }
        }
    }

    inner class UniformInputStorage2d(val uniformName: String, defaultVal: StorageTexture2d?) : ConnectUniformListener {
        var uniform: StorageTexture2dBinding? = null
        private var buffer: StorageTexture2d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = storage2d[uniformName]?.apply { storageTex = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): StorageTexture2d? = uniform?.storageTex ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: StorageTexture2d?) {
            uniform?.let { it.storageTex = value } ?: run { buffer = value }
        }
    }

    inner class UniformInputStorage3d(val uniformName: String, defaultVal: StorageTexture3d?) : ConnectUniformListener {
        var uniform: StorageTexture3dBinding? = null
        private var buffer: StorageTexture3d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = storage3d[uniformName]?.apply { storageTex = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): StorageTexture3d? = uniform?.storageTex ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: StorageTexture3d?) {
            uniform?.let { it.storageTex = value } ?: run { buffer = value }
        }
    }
}