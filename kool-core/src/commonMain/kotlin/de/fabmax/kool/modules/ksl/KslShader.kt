package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.logW

fun KslShader(
    name: String,
    pipelineConfig: PipelineConfig = PipelineConfig(),
    block: KslProgram.() -> Unit
): KslShader {
    val shader = KslShader(name)
    shader.pipelineConfig.set(pipelineConfig)
    shader.program.apply(block)
    return shader
}

open class KslShader private constructor(val program: KslProgram) : Shader(program.name) {
    constructor(name: String): this(KslProgram(name))

    constructor(program: KslProgram, pipelineConfig: PipelineConfig): this(program) {
        this.pipelineConfig.set(pipelineConfig)
    }

    /**
     * Retrieves the set of vertex attributes required by this shader. The [program] needs
     * to be complete for this.
     */
    fun findRequiredVertexAttributes(): Set<Attribute> {
        val vertexStage = program.vertexStage ?: return emptySet()
        return vertexStage.attributes.values.map {
            Attribute(it.name, it.expressionType.glslType)
        }.toSet()
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, updateEvent: RenderPass.UpdateEvent) {
        checkNotNull(program.vertexStage) {
            "KslProgram vertexStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }
        checkNotNull(program.fragmentStage) {
            "KslProgram fragmentStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }

        if (program.computeStage != null) {
            logW { "KslProgram has a compute stage defined, although it is used as a regular rendering shader. Compute stage is ignored." }
        }

        // prepare shader model for generating source code, also updates program dependencies (e.g. which
        // uniform is used by which shader stage)
        program.prepareGenerate()

        builder.name = program.name
        builder.vertexLayout.setupVertexLayout(mesh)
        builder.bindGroupLayout = program.setupBindGroupLayout()
        builder.shaderCodeGenerator = { updateEvent.ctx.backend.generateKslShader(this, it) }

        super.onPipelineSetup(builder, mesh, updateEvent)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, updateEvent: RenderPass.UpdateEvent) {
        super.onPipelineCreated(pipeline, mesh, updateEvent)

        pipeline.onUpdate += { cmd ->
            for (i in program.shaderListeners.indices) {
                program.shaderListeners[i].onUpdate(cmd)
            }
        }
        program.shaderListeners.forEach { it.onShaderCreated(this) }
    }

    private fun VertexLayout.Builder.setupVertexLayout(mesh: Mesh)  {
        var attribLocation = 0
        val verts = mesh.geometry
        val vertLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()
        val vertLayoutAttribsI = mutableListOf<VertexLayout.VertexAttribute>()
        var iBinding = 0

        val vertexStage = checkNotNull(program.vertexStage) { "vertexStage not defined" }

        vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Vertex }.forEach { vertexAttrib ->
            val attrib = verts.attributeByteOffsets.keys.find { it.name == vertexAttrib.name }
                ?: throw NoSuchElementException("Mesh does not include required vertex attribute: ${vertexAttrib.name} (for shader: ${program.name})")
            val off = verts.attributeByteOffsets[attrib]!!
            if (attrib.type.isInt) {
                vertLayoutAttribsI += VertexLayout.VertexAttribute(attribLocation, off, attrib)
            } else {
                vertLayoutAttribs += VertexLayout.VertexAttribute(attribLocation, off, attrib)
            }
            vertexAttrib.location = attribLocation
            attribLocation += attrib.locationIncrement
        }

        bindings += VertexLayout.Binding(
            iBinding++,
            InputRate.VERTEX,
            vertLayoutAttribs,
            verts.byteStrideF
        )
        if (vertLayoutAttribsI.isNotEmpty()) {
            bindings += VertexLayout.Binding(
                iBinding++,
                InputRate.VERTEX,
                vertLayoutAttribsI,
                verts.byteStrideI
            )
        }

        val instanceAttribs = vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Instance }
        val insts = mesh.instances
        if (insts != null) {
            val instLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()
            instanceAttribs.forEach { instanceAttrib ->
                val attrib = insts.attributeOffsets.keys.find { it.name == instanceAttrib.name }
                    ?: throw NoSuchElementException("Mesh does not include required instance attribute: ${instanceAttrib.name}")
                val off = insts.attributeOffsets[attrib]!!
                instLayoutAttribs += VertexLayout.VertexAttribute(attribLocation, off, attrib)
                instanceAttrib.location = attribLocation
                attribLocation += attrib.locationIncrement
            }
            bindings += VertexLayout.Binding(
                iBinding,
                InputRate.INSTANCE,
                instLayoutAttribs,
                insts.strideBytesF
            )
        } else if (instanceAttribs.isNotEmpty()) {
            throw IllegalStateException("Shader model requires instance attributes, but mesh doesn't provide any")
        }
    }
}

fun KslProgram.setupBindGroupLayout(): BindGroupLayout.Builder {
    val bindGrpBuilder = BindGroupLayout.Builder()
    setupBindGroupLayoutUbos(bindGrpBuilder)
    setupBindGroupLayoutSamplers(bindGrpBuilder)
    setupBindGroupLayoutStorage(bindGrpBuilder)
    return bindGrpBuilder
}

private fun KslProgram.setupBindGroupLayoutUbos(bindGrpBuilder: BindGroupLayout.Builder) {
    uniformBuffers.filter { it.uniforms.isNotEmpty() }.forEach { kslUbo ->
        val ubo = UniformBufferBinding.Builder()
        bindGrpBuilder.ubos += ubo

        ubo.name = kslUbo.name
        ubo.isShared = kslUbo.isShared
        stages.forEach {
            if (kslUbo.uniforms.values.any { u -> it.dependsOn(u) }) {
                ubo.stages += it.type.pipelineStageType
            }
        }

        kslUbo.uniforms.values.forEach { uniform ->
            // fixme: make sure to reuse the existing Uniform<*> object in case multiple pipeline instances are
            //  created from this KslShader instance - this was a hack and should not be needed anymore
            val createdUniform: Uniform<*> = /*shader?.uniforms?.get(uniform.name) ?:*/ when(val type = uniform.value.expressionType)  {
                is KslFloat1 -> { Uniform1f(uniform.name) }
                is KslFloat2 -> { Uniform2f(uniform.name) }
                is KslFloat3 -> { Uniform3f(uniform.name) }
                is KslFloat4 -> { Uniform4f(uniform.name) }

                is KslInt1 -> { Uniform1i(uniform.name) }
                is KslInt2 -> { Uniform2i(uniform.name) }
                is KslInt3 -> { Uniform3i(uniform.name) }
                is KslInt4 -> { Uniform4i(uniform.name) }

                //is KslTypeMat2 -> { UniformMat2f(uniform.name) }
                is KslMat3 -> { UniformMat3f(uniform.name) }
                is KslMat4 -> { UniformMat4f(uniform.name) }

                is KslArrayType<*> -> {
                    when (type.elemType) {
                        is KslFloat1 -> { Uniform1fv(uniform.name, uniform.arraySize) }
                        is KslFloat2 -> { Uniform2fv(uniform.name, uniform.arraySize) }
                        is KslFloat3 -> { Uniform3fv(uniform.name, uniform.arraySize) }
                        is KslFloat4 -> { Uniform4fv(uniform.name, uniform.arraySize) }

                        is KslInt1 -> { Uniform1iv(uniform.name, uniform.arraySize) }
                        is KslInt2 -> { Uniform2iv(uniform.name, uniform.arraySize) }
                        is KslInt3 -> { Uniform3iv(uniform.name, uniform.arraySize) }
                        is KslInt4 -> { Uniform4iv(uniform.name, uniform.arraySize) }

                        is KslMat3 -> { UniformMat3fv(uniform.name, uniform.arraySize) }
                        is KslMat4 -> { UniformMat4fv(uniform.name, uniform.arraySize) }

                        else -> throw IllegalStateException("Unsupported uniform array type: ${type.elemType.typeName}")
                    }
                }
                else -> throw IllegalStateException("Unsupported uniform type: ${type.typeName}")
            }
            ubo.uniforms += { createdUniform }
        }
    }
}

private fun KslProgram.setupBindGroupLayoutSamplers(bindGrpBuilder: BindGroupLayout.Builder) {
    uniformSamplers.values.forEach { sampler ->
        val binding = when(val type = sampler.value.expressionType)  {
            is KslDepthSampler2D -> Texture2dBinding.Builder().apply { isDepthSampler = true }
            is KslDepthSamplerCube -> TextureCubeBinding.Builder().apply { isDepthSampler = true }
            is KslColorSampler1d -> Texture1dBinding.Builder()
            is KslColorSampler2d -> Texture2dBinding.Builder()
            is KslColorSampler3d -> Texture3dBinding.Builder()
            is KslColorSamplerCube -> TextureCubeBinding.Builder()

            is KslArrayType<*> -> {
                when (type.elemType) {
                    is KslDepthSampler2D -> Texture2dBinding.Builder().apply {
                        isDepthSampler = true
                        arraySize = sampler.arraySize
                    }
                    is KslDepthSamplerCube -> TextureCubeBinding.Builder().apply {
                        isDepthSampler = true
                        arraySize = sampler.arraySize
                    }
                    is KslColorSampler1d -> Texture1dBinding.Builder().apply { arraySize = sampler.arraySize }
                    is KslColorSampler2d -> Texture2dBinding.Builder().apply { arraySize = sampler.arraySize }
                    is KslColorSampler3d -> Texture3dBinding.Builder().apply { arraySize = sampler.arraySize }
                    is KslColorSamplerCube -> TextureCubeBinding.Builder().apply { arraySize = sampler.arraySize }
                    else -> throw IllegalStateException("Unsupported sampler array type: ${type.elemType.typeName}")
                }
            }
            else -> throw IllegalStateException("Unsupported sampler uniform type: ${type.typeName}")
        }
        bindGrpBuilder.samplers += binding

        binding.name = sampler.name
        stages.forEach {
            if (it.dependsOn(sampler)) {
                binding.stages += it.type.pipelineStageType
            }
        }
    }
}

private fun KslProgram.setupBindGroupLayoutStorage(bindGrpBuilder: BindGroupLayout.Builder) {
    uniformStorage.values.forEach { storage ->
        val binding = when(storage.storageType)  {
            is KslStorage1dType<*> -> StorageTexture1dBinding.Builder()
            is KslStorage2dType<*> -> StorageTexture2dBinding.Builder()
            is KslStorage3dType<*> -> StorageTexture3dBinding.Builder()
        }
        bindGrpBuilder.storage += binding

        binding.name = storage.name
        stages.forEach {
            if (it.dependsOn(storage)) {
                binding.stages += it.type.pipelineStageType
            }
        }
        binding.format = storage.storageType.elemType
        // todo: restrict this to read / write only if possible
        binding.accessType = StorageAccessType.READ_WRITE
    }
}
