package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.UniqueId
import kotlin.reflect.KProperty

/**
 * Base class for all regular and compute shaders. Provides methods to easily connect to shader uniforms.
 */
abstract class ShaderBase<T: PipelineBase>(val name: String) {

    val uniforms = mutableMapOf<String, Uniform<*>>()

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
                is UniformBufferBinding -> binding.uniforms.forEach { uniforms[it.name] = it }
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

    protected interface PipelineBinding {
        val isBound: Boolean
        fun setup(pipeline: PipelineBase)
    }

    protected interface ConnectUniformListener {
        val isConnected: Boolean
        fun connect()
    }

    fun uniform1f(uniformName: String, defaultVal: Float? = null): ShaderBase<*>.UniformInput1f =
        connectUniformListeners.getOrPut(uniformName) { UniformInput1f(uniformName, defaultVal ?: 0f) } as ShaderBase<*>.UniformInput1f
    fun uniform2f(uniformName: String, defaultVal: Vec2f? = null): ShaderBase<*>.UniformInput2f =
        connectUniformListeners.getOrPut(uniformName) { UniformInput2f(uniformName, defaultVal ?: Vec2f.ZERO) } as ShaderBase<*>.UniformInput2f
    fun uniform3f(uniformName: String, defaultVal: Vec3f? = null): ShaderBase<*>.UniformInput3f =
        connectUniformListeners.getOrPut(uniformName) { UniformInput3f(uniformName, defaultVal ?: Vec3f.ZERO) } as ShaderBase<*>.UniformInput3f
    fun uniform4f(uniformName: String, defaultVal: Vec4f? = null): ShaderBase<*>.UniformInput4f =
        connectUniformListeners.getOrPut(uniformName) { UniformInput4f(uniformName, defaultVal ?: Vec4f.ZERO) } as ShaderBase<*>.UniformInput4f
    fun uniformColor(uniformName: String, defaultVal: Color? = null): ShaderBase<*>.UniformInputColor =
        connectUniformListeners.getOrPut(uniformName) { UniformInputColor(uniformName, defaultVal ?: Color.BLACK) } as ShaderBase<*>.UniformInputColor
    fun uniformQuat(uniformName: String, defaultVal: QuatF? = null): ShaderBase<*>.UniformInputQuat =
        connectUniformListeners.getOrPut(uniformName) { UniformInputQuat(uniformName, defaultVal ?: QuatF.IDENTITY) } as ShaderBase<*>.UniformInputQuat

    fun uniform1i(uniformName: String, defaultVal: Int? = null): ShaderBase<*>.UniformInput1i =
        connectUniformListeners.getOrPut(uniformName) { UniformInput1i(uniformName, defaultVal ?: 0) } as ShaderBase<*>.UniformInput1i
    fun uniform2i(uniformName: String, defaultVal: Vec2i? = null): ShaderBase<*>.UniformInput2i =
        connectUniformListeners.getOrPut(uniformName) { UniformInput2i(uniformName, defaultVal ?: Vec2i.ZERO) } as ShaderBase<*>.UniformInput2i
    fun uniform3i(uniformName: String, defaultVal: Vec3i? = null): ShaderBase<*>.UniformInput3i =
        connectUniformListeners.getOrPut(uniformName) { UniformInput3i(uniformName, defaultVal ?: Vec3i.ZERO) } as ShaderBase<*>.UniformInput3i
    fun uniform4i(uniformName: String, defaultVal: Vec4i? = null): ShaderBase<*>.UniformInput4i =
        connectUniformListeners.getOrPut(uniformName) { UniformInput4i(uniformName, defaultVal ?: Vec4i.ZERO) } as ShaderBase<*>.UniformInput4i

    fun uniformMat3f(uniformName: String, defaultVal: Mat3f? = null): ShaderBase<*>.UniformInputMat3f =
        connectUniformListeners.getOrPut(uniformName) { UniformInputMat3f(uniformName, defaultVal) } as ShaderBase<*>.UniformInputMat3f
    fun uniformMat4f(uniformName: String, defaultVal: Mat4f? = null): ShaderBase<*>.UniformInputMat4f =
        connectUniformListeners.getOrPut(uniformName) { UniformInputMat4f(uniformName, defaultVal) } as ShaderBase<*>.UniformInputMat4f

    fun uniform1fv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInput1fv =
        connectUniformListeners.getOrPut(uniformName) { UniformInput1fv(uniformName, arraySize) } as ShaderBase<*>.UniformInput1fv
    fun uniform2fv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInput2fv =
        connectUniformListeners.getOrPut(uniformName) { UniformInput2fv(uniformName, arraySize) } as ShaderBase<*>.UniformInput2fv
    fun uniform3fv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInput3fv =
        connectUniformListeners.getOrPut(uniformName) { UniformInput3fv(uniformName, arraySize) } as ShaderBase<*>.UniformInput3fv
    fun uniform4fv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInput4fv =
        connectUniformListeners.getOrPut(uniformName) { UniformInput4fv(uniformName, arraySize) } as ShaderBase<*>.UniformInput4fv

    fun uniform1iv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInput1iv =
        connectUniformListeners.getOrPut(uniformName) { UniformInput1iv(uniformName, arraySize) } as ShaderBase<*>.UniformInput1iv
    fun uniform2iv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInput2iv =
        connectUniformListeners.getOrPut(uniformName) { UniformInput2iv(uniformName, arraySize) } as ShaderBase<*>.UniformInput2iv
    fun uniform3iv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInput3iv =
        connectUniformListeners.getOrPut(uniformName) { UniformInput3iv(uniformName, arraySize) } as ShaderBase<*>.UniformInput3iv
    fun uniform4iv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInput4iv =
        connectUniformListeners.getOrPut(uniformName) { UniformInput4iv(uniformName, arraySize) } as ShaderBase<*>.UniformInput4iv

    fun uniformMat3fv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInputMat3fv =
        connectUniformListeners.getOrPut(uniformName) { UniformInputMat3fv(uniformName, arraySize) } as ShaderBase<*>.UniformInputMat3fv
    fun uniformMat4fv(uniformName: String, arraySize: Int): ShaderBase<*>.UniformInputMat4fv =
        connectUniformListeners.getOrPut(uniformName) { UniformInputMat4fv(uniformName, arraySize) } as ShaderBase<*>.UniformInputMat4fv

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

    fun colorUniform(cfg: ColorBlockConfig): ShaderBase<*>.UniformInputColor =
        uniformColor(cfg.primaryUniform?.uniformName ?: UniqueId.nextId("_"), cfg.primaryUniform?.defaultColor)
    fun colorTexture(cfg: ColorBlockConfig): ShaderBase<*>.UniformInputTexture2d =
        texture2d(cfg.primaryTexture?.textureName ?: UniqueId.nextId("_"), cfg.primaryTexture?.defaultTexture)

    fun propertyUniform(cfg: PropertyBlockConfig): ShaderBase<*>.UniformInput1f =
        uniform1f(cfg.primaryUniform?.uniformName ?: UniqueId.nextId("_"), cfg.primaryUniform?.defaultValue)
    fun propertyTexture(cfg: PropertyBlockConfig): ShaderBase<*>.UniformInputTexture2d =
        texture2d(cfg.primaryTexture?.textureName ?: UniqueId.nextId("_"), cfg.primaryTexture?.defaultTexture)

    abstract inner class UniformInput<T>(val uniformName: String, defaultVal: T) : PipelineBinding {

        private var bindGroup = -1
        private var binding = -1
        private var bufferPos: BufferPosition? = null

        override val isBound: Boolean get() = bufferPos != null

        var cachedValue: T = defaultVal
            set(value) {
                field = value
                createdPipeline?.updateBuffer()
            }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = cachedValue
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { cachedValue = value }

        override fun setup(pipeline: PipelineBase) {
            bindGroup = -1
            binding = -1
            bufferPos = null

            pipeline.bindGroupLayouts.find { group ->
                group.bindings.any { b -> b is UniformBufferBinding && b.uniforms.any { it.name == uniformName } }
            }?.let { group ->
                val uniform = group.bindings.first { it.name == uniformName } as UniformBufferBinding
                bindGroup = group.group
                binding = uniform.binding
                bufferPos = uniform.layout.uniformPositions[uniformName]
            }
            pipeline.updateBuffer()
        }

        private fun PipelineBase.updateBuffer() {
            val pos = bufferPos ?: return
            val data = bindGroupData[bindGroup].bindings[binding] as BindGroupData.UniformBufferData
            putInto(data.buffer, pos)
            data.isBufferDirty = true
        }

        protected abstract fun putInto(buffer: MixedBuffer, bufferPos: BufferPosition)
    }

    inner class UniformInput1f(val uniformName: String, defaultVal: Float) : ConnectUniformListener {
        private var uniform: Uniform1f? = null
        private var buffer = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform1f)?.apply { value = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Float = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
            uniform?.let { it.value = value } ?: run { buffer = value }
        }
    }

    inner class UniformInput2f(val uniformName: String, defaultVal: Vec2f) : ConnectUniformListener {
        private var uniform: Uniform2f? = null
        private val buffer = MutableVec2f(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform2f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec2f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec2f) = (uniform?.value ?: buffer).set(value)
    }

    inner class UniformInput3f(val uniformName: String, defaultVal: Vec3f) : ConnectUniformListener {
        private var uniform: Uniform3f? = null
        private val buffer = MutableVec3f(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform3f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec3f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec3f) = (uniform?.value ?: buffer).set(value)
    }

    inner class UniformInput4f(val uniformName: String, defaultVal: Vec4f) : ConnectUniformListener {
        var uniform: Uniform4f? = null
        val buffer = MutableVec4f(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform4f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec4f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec4f) = (uniform?.value ?: buffer).set(value)
    }

    inner class UniformInputColor(val uniformName: String, defaultVal: Color) : ConnectUniformListener {
        var uniform: Uniform4f? = null
        val buffer = MutableColor(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform4f)?.apply { value.set(buffer.r, buffer.g, buffer.b, buffer.a) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Color {
            uniform?.value?.let { buffer.set(it.x, it.y, it.z, it.w) }
            return buffer
        }
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Color) {
            val u = uniform?.value
            if (u != null) {
                u.set(value.r, value.g, value.b, value.a)
            } else {
                buffer.set(value)
            }
        }
    }

    inner class UniformInputQuat(val uniformName: String, defaultVal: QuatF) : ConnectUniformListener {
        var uniform: Uniform4f? = null
        val buffer = MutableQuatF(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform4f)?.apply { value.set(buffer.x, buffer.y, buffer.z, buffer.w) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): QuatF {
            uniform?.value?.let { buffer.set(it.x, it.y, it.z, it.w) }
            return buffer
        }
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: QuatF) {
            val u = uniform?.value
            if (u != null) {
                u.set(value.x, value.y, value.z, value.w)
            } else {
                buffer.set(value)
            }
        }
    }

    inner class UniformInput1i(val uniformName: String, defaultVal: Int) : ConnectUniformListener {
        private var uniform: Uniform1i? = null
        private var buffer = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform1i)?.apply { value = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            uniform?.let { it.value = value } ?: run { buffer = value }
        }
    }

    inner class UniformInput2i(val uniformName: String, defaultVal: Vec2i) : ConnectUniformListener {
        private var uniform: Uniform2i? = null
        private val buffer = MutableVec2i(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform2i)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec2i = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec2i) = (uniform?.value ?: buffer).set(value)
    }

    inner class UniformInput3i(val uniformName: String, defaultVal: Vec3i) : ConnectUniformListener {
        private var uniform: Uniform3i? = null
        private val buffer = MutableVec3i(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform3i)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec3i = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec3i) = (uniform?.value ?: buffer).set(value)
    }

    inner class UniformInput4i(val uniformName: String, defaultVal: Vec4i) : ConnectUniformListener {
        private var uniform: Uniform4i? = null
        private val buffer = MutableVec4i(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform4i)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec4i = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec4i) = (uniform?.value ?: buffer).set(value)
    }

    inner class UniformInputMat3f(val uniformName: String, defaultVal: Mat3f?) : ConnectUniformListener {
        var uniform: UniformMat3f? = null
        private val buffer = MutableMat3f().apply { defaultVal?.let { set(it) } }
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? UniformMat3f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): MutableMat3f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableMat3f) = (uniform?.value ?: buffer).set(value)
    }

    inner class UniformInputMat4f(val uniformName: String, defaultVal: Mat4f?) : ConnectUniformListener {
        var uniform: UniformMat4f? = null
        private val buffer = MutableMat4f().apply { defaultVal?.let { set(it) } }
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? UniformMat4f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): MutableMat4f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableMat4f) = (uniform?.value ?: buffer).set(value)
    }

    inner class UniformInput1fv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        private var uniform: Uniform1fv? = null
        private val buffer = FloatArray(arraySize)
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? Uniform1fv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                value = buffer
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): FloatArray = uniform?.value ?: buffer
    }

    inner class UniformInput2fv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        private var uniform: Uniform2fv? = null
        private val buffer = Array(arraySize) { MutableVec2f(Vec2f.ZERO) }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? Uniform2fv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                value = buffer
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<MutableVec2f> = uniform?.value ?: buffer
    }

    inner class UniformInput3fv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        private var uniform: Uniform3fv? = null
        private val buffer = Array(arraySize) { MutableVec3f(Vec3f.ZERO) }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? Uniform3fv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                value = buffer
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<MutableVec3f> = uniform?.value ?: buffer
    }

    inner class UniformInput4fv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        private var uniform: Uniform4fv? = null
        private val buffer = Array(arraySize) { MutableVec4f(Vec4f.ZERO) }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? Uniform4fv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                value = buffer
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<MutableVec4f> = uniform?.value ?: buffer
    }

    inner class UniformInput1iv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        private var uniform: Uniform1iv? = null
        private val buffer = IntArray(arraySize)
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? Uniform1iv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                value = buffer
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): IntArray = uniform?.value ?: buffer
    }

    inner class UniformInput2iv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        private var uniform: Uniform2iv? = null
        private val buffer = Array(arraySize) { MutableVec2i(Vec2i.ZERO) }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? Uniform2iv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                value = buffer
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<MutableVec2i> = uniform?.value ?: buffer
    }

    inner class UniformInput3iv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        private var uniform: Uniform3iv? = null
        private val buffer = Array(arraySize) { MutableVec3i(Vec3i.ZERO) }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? Uniform3iv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                value = buffer
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<MutableVec3i> = uniform?.value ?: buffer
    }

    inner class UniformInput4iv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        private var uniform: Uniform4iv? = null
        private val buffer = Array(arraySize) { MutableVec4i(Vec4i.ZERO) }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? Uniform4iv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                value = buffer
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<MutableVec4i> = uniform?.value ?: buffer
    }

    inner class UniformInputMat3fv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        var uniform: UniformMat3fv? = null
        private val buffer = Array(arraySize) { MutableMat3f() }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? UniformMat3fv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                buffer.forEachIndexed { i, m -> value[i] = m }
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<MutableMat3f> = uniform?.value ?: buffer
    }

    inner class UniformInputMat4fv(val uniformName: String, val arraySize: Int) : ConnectUniformListener {
        var uniform: UniformMat4fv? = null
        private val buffer = Array(arraySize) { MutableMat4f() }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? UniformMat4fv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                buffer.forEachIndexed { i, m -> value[i] = m }
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<MutableMat4f> = uniform?.value ?: buffer
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