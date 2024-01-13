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
            Attribute(it.name, it.expressionType.gpuType)
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
    setupBindGroupLayoutTextures(bindGrpBuilder)
    setupBindGroupLayoutStorage(bindGrpBuilder)
    return bindGrpBuilder
}

private fun KslProgram.setupBindGroupLayoutUbos(bindGrpBuilder: BindGroupLayout.Builder) {
    uniformBuffers.filter { it.uniforms.isNotEmpty() }.forEach { kslUbo ->
        val uniforms = kslUbo.uniforms.values.map { uniform ->
            when(val type = uniform.value.expressionType)  {
                is KslFloat1 -> Uniform.float1(uniform.name)
                is KslFloat2 -> Uniform.float2(uniform.name)
                is KslFloat3 -> Uniform.float3(uniform.name)
                is KslFloat4 -> Uniform.float4(uniform.name)

                is KslInt1 -> Uniform.int1(uniform.name)
                is KslInt2 -> Uniform.int2(uniform.name)
                is KslInt3 -> Uniform.int3(uniform.name)
                is KslInt4 -> Uniform.int4(uniform.name)

                is KslMat2 -> Uniform.mat2(uniform.name)
                is KslMat3 -> Uniform.mat3(uniform.name)
                is KslMat4 -> Uniform.mat4(uniform.name)

                is KslArrayType<*> -> {
                    when (type.elemType) {
                        is KslFloat1 -> Uniform.float1Array(uniform.name, uniform.arraySize)
                        is KslFloat2 -> Uniform.float2Array(uniform.name, uniform.arraySize)
                        is KslFloat3 -> Uniform.float3Array(uniform.name, uniform.arraySize)
                        is KslFloat4 -> Uniform.float4Array(uniform.name, uniform.arraySize)

                        is KslInt1 -> Uniform.int1Array(uniform.name, uniform.arraySize)
                        is KslInt2 -> Uniform.int2Array(uniform.name, uniform.arraySize)
                        is KslInt3 -> Uniform.int3Array(uniform.name, uniform.arraySize)
                        is KslInt4 -> Uniform.int4Array(uniform.name, uniform.arraySize)

                        is KslMat2 -> Uniform.mat2Array(uniform.name, uniform.arraySize)
                        is KslMat3 -> Uniform.mat3Array(uniform.name, uniform.arraySize)
                        is KslMat4 -> Uniform.mat4Array(uniform.name, uniform.arraySize)

                        else -> throw IllegalStateException("Unsupported uniform array type: ${type.elemType.typeName}")
                    }
                }
                else -> throw IllegalStateException("Unsupported uniform type: ${type.typeName}")
            }
        }

        val uboStages = stages
            .filter { kslUbo.uniforms.values.any { u -> it.dependsOn(u) } }
            .map { it.type.pipelineStageType }
            .toSet()

        bindGrpBuilder.ubos += UniformBufferLayout(kslUbo.name, uniforms, uboStages)
    }
}

private fun KslProgram.setupBindGroupLayoutTextures(bindGrpBuilder: BindGroupLayout.Builder) {
    uniformSamplers.values.forEach { sampler ->
        val texStages = stages
            .filter { it.dependsOn(sampler) }
            .map { it.type.pipelineStageType }
            .toSet()

        val name = sampler.name

        bindGrpBuilder.textures += when(val type = sampler.value.expressionType)  {
            is KslDepthSampler2d -> Texture2dLayout(name, texStages, isDepthTexture = true)
            is KslDepthSamplerCube -> TextureCubeLayout(name, texStages, isDepthTexture = true)
            is KslColorSampler1d -> Texture1dLayout(name, texStages)
            is KslColorSampler2d -> Texture2dLayout(name, texStages)
            is KslColorSampler3d -> Texture3dLayout(name, texStages)
            is KslColorSamplerCube -> TextureCubeLayout(name, texStages)

            is KslArrayType<*> -> {
                when (type.elemType) {
                    is KslDepthSampler2d -> Texture2dLayout(name, texStages, sampler.arraySize, isDepthTexture = true)
                    is KslDepthSamplerCube -> TextureCubeLayout(name, texStages, sampler.arraySize, isDepthTexture = true)
                    is KslColorSampler1d -> Texture1dLayout(name, texStages, sampler.arraySize)
                    is KslColorSampler2d -> Texture2dLayout(name, texStages, sampler.arraySize)
                    is KslColorSamplerCube -> TextureCubeLayout(name, texStages, sampler.arraySize)
                    else -> throw IllegalStateException("Unsupported sampler array type: ${type.elemType.typeName}")
                }
            }
            else -> throw IllegalStateException("Unsupported sampler uniform type: ${type.typeName}")
        }
    }
}

private fun KslProgram.setupBindGroupLayoutStorage(bindGrpBuilder: BindGroupLayout.Builder) {
    uniformStorage.values.forEach { storage ->
        val storageStages = stages
            .filter { it.dependsOn(storage) }
            .map { it.type.pipelineStageType }
            .toSet()

        val name = storage.name
        val format = storage.storageType.elemType
        // todo: restrict this to read- / write-only if possible
        val accessType = StorageAccessType.READ_WRITE
        bindGrpBuilder.storage += when(storage.storageType)  {
            is KslStorage1dType<*> -> StorageTexture1dLayout(name, format, accessType, storageStages)
            is KslStorage2dType<*> -> StorageTexture2dLayout(name, format, accessType, storageStages)
            is KslStorage3dType<*> -> StorageTexture3dLayout(name, format, accessType, storageStages)
        }
    }
}
