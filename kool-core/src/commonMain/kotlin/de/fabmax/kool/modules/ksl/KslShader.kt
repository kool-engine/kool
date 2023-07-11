package de.fabmax.kool.modules.ksl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import kotlin.reflect.KProperty

open class KslShader(val program: KslProgram, val pipelineConfig: PipelineConfig) : Shader() {

    val uniforms = mutableMapOf<String, Uniform<*>>()
    val texSamplers1d = mutableMapOf<String, TextureSampler1d>()
    val texSamplers2d = mutableMapOf<String, TextureSampler2d>()
    val texSamplers3d = mutableMapOf<String, TextureSampler3d>()
    val texSamplersCube = mutableMapOf<String, TextureSamplerCube>()

    val pipelineCfg = pipelineConfig.copy()

    val requiredVertexAttributes: Set<Attribute> = program.vertexStage.attributes.values.map {
        Attribute(it.name, it.expressionType.glslType)
    }.toSet()

    private val connectUniformListeners = mutableListOf<ConnectUniformListener>()

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        // prepare shader model for generating source code, also updates program dependencies (e.g. which
        // uniform is used by which shader stage)
        program.prepareGenerate()
        if (program.dumpCode) {
            program.vertexStage.hierarchy.printHierarchy()
        }

        setupAttributes(mesh, builder)
        setupUniforms(builder)

        builder.blendMode = pipelineConfig.blendMode
        builder.cullMethod = pipelineConfig.cullMethod
        builder.depthTest = pipelineConfig.depthTest
        builder.isWriteDepth = pipelineConfig.isWriteDepth
        builder.lineWidth = pipelineConfig.lineWidth

        builder.name = program.name
        builder.shaderCodeGenerator = { ctx.generateKslShader(this, it) }
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        pipeline.layout.descriptorSets.forEach { descSet ->
            descSet.descriptors.forEach { desc ->
                when (desc) {
                    is UniformBuffer -> desc.uniforms.forEach { uniforms[it.name] = it }
                    is TextureSampler1d -> texSamplers1d[desc.name] = desc
                    is TextureSampler2d -> texSamplers2d[desc.name] = desc
                    is TextureSampler3d -> texSamplers3d[desc.name] = desc
                    is TextureSamplerCube -> texSamplersCube[desc.name] = desc
                }
            }
        }

        pipeline.onUpdate += { cmd ->
            for (i in program.shaderListeners.indices) {
                program.shaderListeners[i].onUpdate(cmd)
            }
        }
        program.shaderListeners.forEach { it.onShaderCreated(this, pipeline, ctx) }

        // it can happen that onPipelineCreated is called repeatedly, in that case the connect-method of
        // most uniform listeners would overwrite the current uniform value with the initial default value
        // -> only connect listeners if not already connected
        // fixme: check if repeated onPipelineCreated() calls have other negative effects (at least performance is not
        //  optimal...)
        connectUniformListeners.forEach { it.connect() }

        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    private fun setupUniforms(builder: Pipeline.Builder) {
        val descBuilder = DescriptorSetLayout.Builder()
        builder.descriptorSetLayouts += descBuilder

        program.uniformBuffers.filter { it.uniforms.isNotEmpty() }.forEach { kslUbo ->
            val ubo = UniformBuffer.Builder()
            descBuilder.descriptors += ubo

            ubo.name = kslUbo.name
            if (kslUbo.uniforms.values.any { u -> program.vertexStage.dependsOn(u) }) {
                ubo.stages += ShaderStage.VERTEX_SHADER
            }
            if (kslUbo.uniforms.values.any { u -> program.fragmentStage.dependsOn(u) }) {
                ubo.stages += ShaderStage.FRAGMENT_SHADER
            }

            kslUbo.uniforms.values.forEach { uniform ->
                // make sure to reuse the existing Uniform<*> object in case multiple pipeline instances are
                // created from this KslShader instance
                val createdUniform: Uniform<*> = uniforms[uniform.name] ?: when(val type = uniform.value.expressionType)  {
                    is KslTypeFloat1 -> { Uniform1f(uniform.name) }
                    is KslTypeFloat2 -> { Uniform2f(uniform.name) }
                    is KslTypeFloat3 -> { Uniform3f(uniform.name) }
                    is KslTypeFloat4 -> { Uniform4f(uniform.name) }

                    is KslTypeInt1 -> { Uniform1i(uniform.name) }
                    is KslTypeInt2 -> { Uniform2i(uniform.name) }
                    is KslTypeInt3 -> { Uniform3i(uniform.name) }
                    is KslTypeInt4 -> { Uniform4i(uniform.name) }

                    //is KslTypeMat2 -> { UniformMat2f(uniform.name) }
                    is KslTypeMat3 -> { UniformMat3f(uniform.name) }
                    is KslTypeMat4 -> { UniformMat4f(uniform.name) }

                    is KslTypeArray<*> -> {
                        when (type.elemType) {
                            is KslTypeFloat1 -> { Uniform1fv(uniform.name, uniform.arraySize) }
                            is KslTypeFloat2 -> { Uniform2fv(uniform.name, uniform.arraySize) }
                            is KslTypeFloat3 -> { Uniform3fv(uniform.name, uniform.arraySize) }
                            is KslTypeFloat4 -> { Uniform4fv(uniform.name, uniform.arraySize) }

                            is KslTypeInt1 -> { Uniform1iv(uniform.name, uniform.arraySize) }
                            is KslTypeInt2 -> { Uniform2iv(uniform.name, uniform.arraySize) }
                            is KslTypeInt3 -> { Uniform3iv(uniform.name, uniform.arraySize) }
                            is KslTypeInt4 -> { Uniform4iv(uniform.name, uniform.arraySize) }

                            is KslTypeMat3 -> { UniformMat3fv(uniform.name, uniform.arraySize) }
                            is KslTypeMat4 -> { UniformMat4fv(uniform.name, uniform.arraySize) }

                            else -> throw IllegalStateException("Unsupported uniform array type: ${type.elemType.typeName}")
                        }
                    }
                    else -> throw IllegalStateException("Unsupported uniform type: ${type.typeName}")
                }
                ubo.uniforms += { createdUniform }
            }
        }

        if (program.uniformSamplers.isNotEmpty()) {
            program.uniformSamplers.values.forEach { sampler ->
                val desc = when(val type = sampler.value.expressionType)  {
                    is KslTypeDepthSampler2d -> TextureSampler2d.Builder().apply { isDepthSampler = true }
                    is KslTypeDepthSamplerCube -> TextureSamplerCube.Builder().apply { isDepthSampler = true }
                    is KslTypeColorSampler1d -> TextureSampler1d.Builder()
                    is KslTypeColorSampler2d -> TextureSampler2d.Builder()
                    is KslTypeColorSampler3d -> TextureSampler3d.Builder()
                    is KslTypeColorSamplerCube -> TextureSamplerCube.Builder()

                    is KslTypeArray<*> -> {
                        when (type.elemType) {
                            is KslTypeDepthSampler2d -> TextureSampler2d.Builder().apply {
                                isDepthSampler = true
                                arraySize = sampler.arraySize
                            }
                            is KslTypeDepthSamplerCube -> TextureSamplerCube.Builder().apply {
                                isDepthSampler = true
                                arraySize = sampler.arraySize
                            }
                            is KslTypeColorSampler1d -> TextureSampler1d.Builder().apply { arraySize = sampler.arraySize }
                            is KslTypeColorSampler2d -> TextureSampler2d.Builder().apply { arraySize = sampler.arraySize }
                            is KslTypeColorSampler3d -> TextureSampler3d.Builder().apply { arraySize = sampler.arraySize }
                            is KslTypeColorSamplerCube -> TextureSamplerCube.Builder().apply { arraySize = sampler.arraySize }
                            else -> throw IllegalStateException("Unsupported sampler array type: ${type.elemType.typeName}")
                        }
                    }
                    else -> throw IllegalStateException("Unsupported sampler uniform type: ${type.typeName}")
                }
                desc.name = sampler.name
                if (program.vertexStage.dependsOn(sampler)) {
                    desc.stages += ShaderStage.VERTEX_SHADER
                }
                if (program.fragmentStage.dependsOn(sampler)) {
                    desc.stages += ShaderStage.FRAGMENT_SHADER
                }
                descBuilder.descriptors += desc
            }
        }

        // todo: push constants (is there such thing in webgpu? otherwise only relevant for vulkan...)
    }

    private fun setupAttributes(mesh: Mesh, builder: Pipeline.Builder) {
        var attribLocation = 0
        val verts = mesh.geometry
        val vertLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()
        val vertLayoutAttribsI = mutableListOf<VertexLayout.VertexAttribute>()
        var iBinding = 0

        program.vertexStage.attributes.values.asSequence().filter { it.inputRate == KslInputRate.Vertex }.forEach { vertexAttrib ->
            val attrib = verts.attributeByteOffsets.keys.find { it.name == vertexAttrib.name }
                ?: throw NoSuchElementException("Mesh does not include required vertex attribute: ${vertexAttrib.name} (for shader: ${program.name})")
            val off = verts.attributeByteOffsets[attrib]!!
            if (attrib.type.isInt) {
                vertLayoutAttribsI += VertexLayout.VertexAttribute(attribLocation, off, attrib)
            } else {
                vertLayoutAttribs += VertexLayout.VertexAttribute(attribLocation, off, attrib)
            }
            vertexAttrib.location = attribLocation
            attribLocation += attrib.props.nSlots
        }

        builder.vertexLayout.bindings += VertexLayout.Binding(
            iBinding++,
            InputRate.VERTEX,
            vertLayoutAttribs,
            verts.byteStrideF
        )
        if (vertLayoutAttribsI.isNotEmpty()) {
            builder.vertexLayout.bindings += VertexLayout.Binding(
                iBinding++,
                InputRate.VERTEX,
                vertLayoutAttribsI,
                verts.byteStrideI
            )
        }

        val instanceAttribs = program.vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Instance }
        val insts = mesh.instances
        if (insts != null) {
            val instLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()
            instanceAttribs.forEach { instanceAttrib ->
                val attrib = insts.attributeOffsets.keys.find { it.name == instanceAttrib.name }
                    ?: throw NoSuchElementException("Mesh does not include required instance attribute: ${instanceAttrib.name}")
                val off = insts.attributeOffsets[attrib]!!
                instLayoutAttribs += VertexLayout.VertexAttribute(attribLocation, off, attrib)
                instanceAttrib.location = attribLocation
                attribLocation += attrib.props.nSlots
            }
            builder.vertexLayout.bindings += VertexLayout.Binding(
                iBinding,
                InputRate.INSTANCE,
                instLayoutAttribs,
                insts.strideBytesF
            )
        } else if (instanceAttribs.isNotEmpty()) {
            throw IllegalStateException("Shader model requires instance attributes, but mesh doesn't provide any")
        }
    }

    data class PipelineConfig(
        var blendMode: BlendMode = BlendMode.BLEND_MULTIPLY_ALPHA,
        var cullMethod: CullMethod = CullMethod.CULL_BACK_FACES,
        var depthTest: DepthCompareOp = DepthCompareOp.LESS_EQUAL,
        var isWriteDepth: Boolean = true,
        var lineWidth: Float = 1f
    )

    protected interface ConnectUniformListener {
        val isConnected: Boolean?
        fun connect()
    }

    protected fun uniform1f(uniformName: String?, defaultVal: Float? = null): UniformInput1f =
        UniformInput1f(uniformName, defaultVal ?: 0f).also { connectUniformListeners += it }
    protected fun uniform2f(uniformName: String?, defaultVal: Vec2f? = null): UniformInput2f =
        UniformInput2f(uniformName, defaultVal ?: Vec2f.ZERO).also { connectUniformListeners += it }
    protected fun uniform3f(uniformName: String?, defaultVal: Vec3f? = null): UniformInput3f =
        UniformInput3f(uniformName, defaultVal ?: Vec3f.ZERO).also { connectUniformListeners += it }
    protected fun uniform4f(uniformName: String?, defaultVal: Vec4f? = null): UniformInput4f =
        UniformInput4f(uniformName, defaultVal ?: Vec4f.ZERO).also { connectUniformListeners += it }

    protected fun uniform1i(uniformName: String?, defaultVal: Int? = null): UniformInput1i =
        UniformInput1i(uniformName, defaultVal ?: 0).also { connectUniformListeners += it }
    protected fun uniform2i(uniformName: String?, defaultVal: Vec2i? = null): UniformInput2i =
        UniformInput2i(uniformName, defaultVal ?: Vec2i.ZERO).also { connectUniformListeners += it }
    protected fun uniform3i(uniformName: String?, defaultVal: Vec3i? = null): UniformInput3i =
        UniformInput3i(uniformName, defaultVal ?: Vec3i.ZERO).also { connectUniformListeners += it }
    protected fun uniform4i(uniformName: String?, defaultVal: Vec4i? = null): UniformInput4i =
        UniformInput4i(uniformName, defaultVal ?: Vec4i.ZERO).also { connectUniformListeners += it }

    protected fun uniformMat3f(uniformName: String?, defaultVal: Mat3f? = null): UniformInputMat3f =
        UniformInputMat3f(uniformName, defaultVal).also { connectUniformListeners += it }
    protected fun uniformMat4f(uniformName: String?, defaultVal: Mat4f? = null): UniformInputMat4f =
        UniformInputMat4f(uniformName, defaultVal).also { connectUniformListeners += it }

    protected fun uniform1fv(uniformName: String?, arraySize: Int): UniformInput1fv =
        UniformInput1fv(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun uniform2fv(uniformName: String?, arraySize: Int): UniformInput2fv =
        UniformInput2fv(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun uniform3fv(uniformName: String?, arraySize: Int): UniformInput3fv =
        UniformInput3fv(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun uniform4fv(uniformName: String?, arraySize: Int): UniformInput4fv =
        UniformInput4fv(uniformName, arraySize).also { connectUniformListeners += it }

    protected fun uniform1iv(uniformName: String?, arraySize: Int): UniformInput1iv =
        UniformInput1iv(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun uniform2iv(uniformName: String?, arraySize: Int): UniformInput2iv =
        UniformInput2iv(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun uniform3iv(uniformName: String?, arraySize: Int): UniformInput3iv =
        UniformInput3iv(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun uniform4iv(uniformName: String?, arraySize: Int): UniformInput4iv =
        UniformInput4iv(uniformName, arraySize).also { connectUniformListeners += it }

    protected fun uniformMat3fv(uniformName: String?, arraySize: Int): UniformInputMat3fv =
        UniformInputMat3fv(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun uniformMat4fv(uniformName: String?, arraySize: Int): UniformInputMat4fv =
        UniformInputMat4fv(uniformName, arraySize).also { connectUniformListeners += it }

    protected fun texture1d(uniformName: String?, defaultVal: Texture1d? = null): UniformInputTexture1d =
        UniformInputTexture1d(uniformName, defaultVal).also { connectUniformListeners += it }
    protected fun texture2d(uniformName: String?, defaultVal: Texture2d? = null): UniformInputTexture2d =
        UniformInputTexture2d(uniformName, defaultVal).also { connectUniformListeners += it }
    protected fun texture3d(uniformName: String?, defaultVal: Texture3d? = null): UniformInputTexture3d =
        UniformInputTexture3d(uniformName, defaultVal).also { connectUniformListeners += it }
    protected fun textureCube(uniformName: String?, defaultVal: TextureCube? = null): UniformInputTextureCube =
        UniformInputTextureCube(uniformName, defaultVal).also { connectUniformListeners += it }

    protected fun texture1dArray(uniformName: String?, arraySize: Int): UniformInputTextureArray1d =
        UniformInputTextureArray1d(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun texture2dArray(uniformName: String?, arraySize: Int): UniformInputTextureArray2d =
        UniformInputTextureArray2d(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun texture3dArray(uniformName: String?, arraySize: Int): UniformInputTextureArray3d =
        UniformInputTextureArray3d(uniformName, arraySize).also { connectUniformListeners += it }
    protected fun textureCubeArray(uniformName: String?, arraySize: Int): UniformInputTextureArrayCube =
        UniformInputTextureArrayCube(uniformName, arraySize).also { connectUniformListeners += it }

    protected fun colorUniform(cfg: ColorBlockConfig): KslShader.UniformInput4f =
        uniform4f(cfg.primaryUniform?.uniformName, cfg.primaryUniform?.defaultColor)
    protected fun colorTexture(cfg: ColorBlockConfig): KslShader.UniformInputTexture2d =
        texture2d(cfg.primaryTexture?.textureName, cfg.primaryTexture?.defaultTexture)

    protected fun propertyUniform(cfg: PropertyBlockConfig): KslShader.UniformInput1f =
        uniform1f(cfg.primaryUniform?.uniformName, cfg.primaryUniform?.defaultValue)
    protected fun propertyTexture(cfg: PropertyBlockConfig): KslShader.UniformInputTexture2d =
        texture2d(cfg.primaryTexture?.textureName, cfg.primaryTexture?.defaultTexture)

    protected inner class UniformInput1f(val uniformName: String?, defaultVal: Float) : ConnectUniformListener {
        private var uniform: Uniform1f? = null
        private var buffer = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform1f)?.apply { value = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Float = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
            uniform?.let { it.value = value } ?: run { buffer = value }
        }
    }

    protected inner class UniformInput2f(val uniformName: String?, defaultVal: Vec2f) : ConnectUniformListener {
        private var uniform: Uniform2f? = null
        private val buffer = MutableVec2f(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform2f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec2f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec2f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInput3f(val uniformName: String?, defaultVal: Vec3f) : ConnectUniformListener {
        private var uniform: Uniform3f? = null
        private val buffer = MutableVec3f(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform3f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec3f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec3f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInput4f(val uniformName: String?, defaultVal: Vec4f) : ConnectUniformListener {
        private var uniform: Uniform4f? = null
        private val buffer = MutableVec4f(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform4f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec4f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec4f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInput1i(val uniformName: String?, defaultVal: Int) : ConnectUniformListener {
        private var uniform: Uniform1i? = null
        private var buffer = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform1i)?.apply { value = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            uniform?.let { it.value = value } ?: run { buffer = value }
        }
    }

    protected inner class UniformInput2i(val uniformName: String?, defaultVal: Vec2i) : ConnectUniformListener {
        private var uniform: Uniform2i? = null
        private val buffer = MutableVec2i(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform2i)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec2i = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec2i) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInput3i(val uniformName: String?, defaultVal: Vec3i) : ConnectUniformListener {
        private var uniform: Uniform3i? = null
        private val buffer = MutableVec3i(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform3i)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec3i = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec3i) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInput4i(val uniformName: String?, defaultVal: Vec4i) : ConnectUniformListener {
        private var uniform: Uniform4i? = null
        private val buffer = MutableVec4i(defaultVal)
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform4i)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec4i = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec4i) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInputMat3f(val uniformName: String?, defaultVal: Mat3f?) : ConnectUniformListener {
        var uniform: UniformMat3f? = null
        private val buffer = Mat3f().apply { defaultVal?.let { set(it) } }
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? UniformMat3f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Mat3f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Mat3f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInputMat4f(val uniformName: String?, defaultVal: Mat4f?) : ConnectUniformListener {
        var uniform: UniformMat4f? = null
        private val buffer = Mat4f().apply { defaultVal?.let { set(it) } }
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = (uniforms[uniformName] as? UniformMat4f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Mat4f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Mat4f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInput1fv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
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

    protected inner class UniformInput2fv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
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

    protected inner class UniformInput3fv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
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

    protected inner class UniformInput4fv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
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

    protected inner class UniformInput1iv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
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

    protected inner class UniformInput2iv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
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

    protected inner class UniformInput3iv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
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

    protected inner class UniformInput4iv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
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

    protected inner class UniformInputMat3fv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
        var uniform: UniformMat3fv? = null
        private val buffer = Array(arraySize) { Mat3f() }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? UniformMat3fv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                buffer.forEachIndexed { i, m -> value[i] = m }
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<Mat3f> = uniform?.value ?: buffer
    }

    protected inner class UniformInputMat4fv(val uniformName: String?, val arraySize: Int) : ConnectUniformListener {
        var uniform: UniformMat4fv? = null
        private val buffer = Array(arraySize) { Mat4f() }
        override val isConnected: Boolean = uniform != null
        override fun connect() {
            uniform = (uniforms[uniformName] as? UniformMat4fv)?.apply {
                check(size == arraySize) { "Mismatching uniform array size: $size != $arraySize" }
                buffer.forEachIndexed { i, m -> value[i] = m }
            }
        }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<Mat4f> = uniform?.value ?: buffer
    }

    protected inner class UniformInputTexture1d(val uniformName: String?, defaultVal: Texture1d?) : ConnectUniformListener {
        var uniform: TextureSampler1d? = null
        private var buffer: Texture1d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = texSamplers1d[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Texture1d? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Texture1d?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }

    protected inner class UniformInputTexture2d(val uniformName: String?, defaultVal: Texture2d?) : ConnectUniformListener {
        var uniform: TextureSampler2d? = null
        private var buffer: Texture2d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = texSamplers2d[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Texture2d? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Texture2d?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }

    protected inner class UniformInputTexture3d(val uniformName: String?, defaultVal: Texture3d?) : ConnectUniformListener {
        var uniform: TextureSampler3d? = null
        private var buffer: Texture3d? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = texSamplers3d[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Texture3d? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Texture3d?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }

    protected inner class UniformInputTextureCube(val uniformName: String?, defaultVal: TextureCube?) : ConnectUniformListener {
        var uniform: TextureSamplerCube? = null
        private var buffer: TextureCube? = defaultVal
        override val isConnected: Boolean = uniform != null
        override fun connect() { uniform = texSamplersCube[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): TextureCube? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TextureCube?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }

    protected inner class UniformInputTextureArray1d(val uniformName: String?, val arrSize: Int) : ConnectUniformListener {
        var uniform: TextureSampler1d? = null
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

    protected inner class UniformInputTextureArray2d(val uniformName: String?, val arrSize: Int) : ConnectUniformListener {
        var uniform: TextureSampler2d? = null
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

    protected inner class UniformInputTextureArray3d(val uniformName: String?, val arrSize: Int) : ConnectUniformListener {
        var uniform: TextureSampler3d? = null
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

    protected inner class UniformInputTextureArrayCube(val uniformName: String?, val arrSize: Int) : ConnectUniformListener {
        var uniform: TextureSamplerCube? = null
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
}