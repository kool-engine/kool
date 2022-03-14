package de.fabmax.kool.modules.ksl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.generator.GlslGenerator
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslScope
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Mesh
import kotlin.reflect.KProperty

open class KslShader(val program: KslProgram, val pipelineConfig: PipelineConfig) : Shader() {

    val uniforms = mutableMapOf<String, Uniform<*>>()
    val texSamplers1d = mutableMapOf<String, TextureSampler1d>()
    val texSamplers2d = mutableMapOf<String, TextureSampler2d>()
    val texSamplers3d = mutableMapOf<String, TextureSampler3d>()
    val texSamplersCube = mutableMapOf<String, TextureSamplerCube>()

    private val connectUniformListeners = mutableListOf<ConnectUniformListener>()
    private val listeners = mutableListOf<KslShaderListener>()

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        setupAttributes(mesh, builder)
        setupUniforms(builder)

        listeners += program.uniformBuffers.filterIsInstance<KslShaderListener>()
        collectProgramListeners(program.vertexStage.globalScope)
        collectProgramListeners(program.fragmentStage.globalScope)

        builder.blendMode = pipelineConfig.blendMode
        builder.cullMethod = pipelineConfig.cullMethod
        builder.depthTest = pipelineConfig.depthTest
        builder.isWriteDepth = pipelineConfig.isWriteDepth
        builder.lineWidth = pipelineConfig.lineWidth

        builder.name = program.name
        builder.shaderCodeGenerator = {
            val src = GlslGenerator().generateProgram(program)
            //src.dump()
            shaderCodeFromSource(src.vertexSrc, src.fragmentSrc)
        }
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
            for (i in listeners.indices) {
                listeners[i].onUpdate(cmd)
            }
        }
        listeners.forEach { it.onShaderCreated(this, pipeline, ctx) }
        connectUniformListeners.forEach { it.connect() }

        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    private fun collectProgramListeners(scope: KslScope) {
        scope.ops.forEach {
            if (it is KslShaderListener) {
                listeners += it
            }
            it.childScopes.forEach { cs -> collectProgramListeners(cs) }
        }
    }

    private fun setupUniforms(builder: Pipeline.Builder) {
        val descBuilder = DescriptorSetLayout.Builder()
        builder.descriptorSetLayouts += descBuilder

        val ubo = UniformBuffer.Builder()
        descBuilder.descriptors += ubo
        program.uniforms.values.forEach { uniform ->
            when(val type = uniform.value.expressionType)  {
                is KslTypeFloat1 -> ubo.uniforms += { Uniform1f(uniform.name) }
                is KslTypeFloat2 -> ubo.uniforms += { Uniform2f(uniform.name) }
                is KslTypeFloat3 -> ubo.uniforms += { Uniform3f(uniform.name) }
                is KslTypeFloat4 -> ubo.uniforms += { Uniform4f(uniform.name) }

                is KslTypeInt1 -> ubo.uniforms += { Uniform1i(uniform.name) }
                is KslTypeInt2 -> ubo.uniforms += { Uniform2i(uniform.name) }
                is KslTypeInt3 -> ubo.uniforms += { Uniform3i(uniform.name) }
                is KslTypeInt4 -> ubo.uniforms += { Uniform4i(uniform.name) }

                //is KslTypeMat2 -> ubo.uniforms += { UniformMat2f(uniform.name) }
                is KslTypeMat3 -> ubo.uniforms += { UniformMat3f(uniform.name) }
                is KslTypeMat4 -> ubo.uniforms += { UniformMat4f(uniform.name) }

                is KslTypeArray<*> -> {
                    when (type.elemType) {
                        is KslTypeFloat1 -> ubo.uniforms += { Uniform1fv(uniform.name, uniform.arraySize) }
                        is KslTypeFloat2 -> ubo.uniforms += { Uniform2fv(uniform.name, uniform.arraySize) }
                        is KslTypeFloat3 -> ubo.uniforms += { Uniform3fv(uniform.name, uniform.arraySize) }
                        is KslTypeFloat4 -> ubo.uniforms += { Uniform4fv(uniform.name, uniform.arraySize) }

                        is KslTypeInt1 -> ubo.uniforms += { Uniform1iv(uniform.name, uniform.arraySize) }
                        is KslTypeInt2 -> ubo.uniforms += { Uniform2iv(uniform.name, uniform.arraySize) }
                        is KslTypeInt3 -> ubo.uniforms += { Uniform3iv(uniform.name, uniform.arraySize) }
                        is KslTypeInt4 -> ubo.uniforms += { Uniform4iv(uniform.name, uniform.arraySize) }

                        is KslTypeMat3 -> ubo.uniforms += { UniformMat3fv(uniform.name, uniform.arraySize) }
                        is KslTypeMat4 -> ubo.uniforms += { UniformMat4fv(uniform.name, uniform.arraySize) }

                        else -> throw IllegalStateException("Unsupported uniform array type: ${type.elemType.typeName}")
                    }
                }

                is KslTypeDepthSampler2d -> descBuilder.descriptors += TextureSampler2d.Builder().apply {
                    name = uniform.name
                    isDepthSampler = true
                }
                is KslTypeDepthSampler2dArray -> descBuilder.descriptors += TextureSampler2d.Builder().apply {
                    name = uniform.name
                    arraySize = uniform.arraySize
                    isDepthSampler = true
                }
                is KslTypeDepthSamplerCube -> descBuilder.descriptors += TextureSamplerCube.Builder().apply {
                    name = uniform.name
                    isDepthSampler = true
                }
                is KslTypeDepthSamplerCubeArray -> descBuilder.descriptors += TextureSamplerCube.Builder().apply {
                    name = uniform.name
                    arraySize = uniform.arraySize
                    isDepthSampler = true
                }
                is KslTypeColorSampler1d -> descBuilder.descriptors += TextureSampler1d.Builder().apply { name = uniform.name }
                is KslTypeColorSampler2d -> descBuilder.descriptors += TextureSampler2d.Builder().apply { name = uniform.name }
                is KslTypeColorSampler2dArray -> descBuilder.descriptors += TextureSampler2d.Builder().apply {
                    name = uniform.name
                    arraySize = uniform.arraySize
                }
                is KslTypeColorSampler3d -> descBuilder.descriptors += TextureSampler3d.Builder().apply { name = uniform.name }
                is KslTypeColorSamplerCube -> descBuilder.descriptors += TextureSamplerCube.Builder().apply { name = uniform.name }
                is KslTypeColorSamplerCubeArray -> descBuilder.descriptors += TextureSamplerCube.Builder().apply {
                    name = uniform.name
                    arraySize = uniform.arraySize
                }
                else -> throw IllegalStateException("Unsupported uniform type: ${type.typeName}")
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
                ?: throw NoSuchElementException("Mesh does not include required vertex attribute: ${vertexAttrib.name}")
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

    class PipelineConfig {
        var blendMode = BlendMode.BLEND_MULTIPLY_ALPHA
        var cullMethod = CullMethod.CULL_BACK_FACES
        var depthTest = DepthCompareOp.LESS_EQUAL
        var isWriteDepth = true
        var lineWidth = 1f
    }

    interface KslShaderListener {
        fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) { }
        fun onUpdate(cmd: DrawCommand) { }
    }

    protected interface ConnectUniformListener {
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

    protected fun uniformMat3f(uniformName: String?, defaultVal: Mat3f? = null): UniformInputMat3f =
        UniformInputMat3f(uniformName, defaultVal).also { connectUniformListeners += it }
    protected fun uniformMat4f(uniformName: String?, defaultVal: Mat4f? = null): UniformInputMat4f =
        UniformInputMat4f(uniformName, defaultVal).also { connectUniformListeners += it }

    protected fun texture2d(uniformName: String?, defaultVal: Texture2d? = null): UniformInputTexture2d =
        UniformInputTexture2d(uniformName, defaultVal).also { connectUniformListeners += it }

    protected inner class UniformInput1f(val uniformName: String?, defaultVal: Float) : ConnectUniformListener {
        private var uniform: Uniform1f? = null
        private var buffer = defaultVal
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform1f)?.apply { value = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Float = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
            uniform?.let { it.value = value } ?: run { buffer = value }
        }
    }

    protected inner class UniformInput2f(val uniformName: String?, defaultVal: Vec2f) : ConnectUniformListener {
        private var uniform: Uniform2f? = null
        private val buffer = MutableVec2f(defaultVal)
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform2f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec2f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec2f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInput3f(val uniformName: String?, defaultVal: Vec3f) : ConnectUniformListener {
        private var uniform: Uniform3f? = null
        private val buffer = MutableVec3f(defaultVal)
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform3f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec3f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec3f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInput4f(val uniformName: String?, defaultVal: Vec4f) : ConnectUniformListener {
        private var uniform: Uniform4f? = null
        private val buffer = MutableVec4f(defaultVal)
        override fun connect() { uniform = (uniforms[uniformName] as? Uniform4f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec4f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec4f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInputMat3f(val uniformName: String?, defaultVal: Mat3f?) : ConnectUniformListener {
        var uniform: UniformMat3f? = null
        private val buffer = Mat3f().apply { defaultVal?.let { set(it) } }
        override fun connect() { uniform = (uniforms[uniformName] as? UniformMat3f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Mat3f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Mat3f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInputMat4f(val uniformName: String?, defaultVal: Mat4f?) : ConnectUniformListener {
        var uniform: UniformMat4f? = null
        private val buffer = Mat4f().apply { defaultVal?.let { set(it) } }
        override fun connect() { uniform = (uniforms[uniformName] as? UniformMat4f)?.apply { value.set(buffer) } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Mat4f = uniform?.value ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Mat4f) = (uniform?.value ?: buffer).set(value)
    }

    protected inner class UniformInputTexture2d(val uniformName: String?, defaultVal: Texture2d?) : ConnectUniformListener {
        var uniform: TextureSampler2d? = null
        private var buffer: Texture2d? = defaultVal
        override fun connect() { uniform = texSamplers2d[uniformName]?.apply { texture = buffer } }
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Texture2d? = uniform?.texture ?: buffer
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Texture2d?) {
            uniform?.let { it.texture = value } ?: run { buffer = value }
        }
    }
}