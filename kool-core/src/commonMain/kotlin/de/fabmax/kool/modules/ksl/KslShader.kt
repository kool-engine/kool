package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

fun KslShader(
    name: String,
    pipelineConfig: PipelineConfig = PipelineConfig(),
    block: KslProgram.() -> Unit
): KslShader {
    val shader = KslShader(name)
    shader.pipelineConfig = pipelineConfig
    shader.program.apply(block)
    return shader
}

open class KslShader private constructor(val program: KslProgram) : DrawShader(program.name) {
    constructor(name: String): this(KslProgram(name))

    constructor(program: KslProgram, pipelineConfig: PipelineConfig): this(program) {
        this.pipelineConfig = pipelineConfig
    }

    var pipelineConfig: PipelineConfig = PipelineConfig()
        set(value) {
            if (createdPipeline == null) {
                field = value
            } else {
                logE { "pipelineConfig cannot be changed after the pipeline is created" }
            }
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

    override fun createPipeline(mesh: Mesh, updateEvent: RenderPass.UpdateEvent): DrawPipeline {
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

        return DrawPipeline(
            name = program.name,
            pipelineConfig = pipelineConfig,
            vertexLayout = makeVertexLayout(mesh),
            bindGroupLayouts = program.makeBindGroupLayout(),
            shaderCodeGenerator = { updateEvent.ctx.backend.generateKslShader(this, it) }
        )
    }

    override fun pipelineCreated(pipeline: DrawPipeline) {
        super.pipelineCreated(pipeline)
        pipeline.onUpdate { cmd ->
            for (i in program.shaderListeners.indices) {
                program.shaderListeners[i].onUpdate(cmd)
            }
        }
        program.shaderListeners.forEach { it.onShaderCreated(this) }
    }

    private fun makeVertexLayout(mesh: Mesh): VertexLayout {
        val vertexStage = checkNotNull(program.vertexStage) { "vertexStage not defined" }

        val verts = mesh.geometry
        val insts = mesh.instances
        val vertLayoutAttribsF = mutableListOf<VertexLayout.VertexAttribute>()
        val vertLayoutAttribsI = mutableListOf<VertexLayout.VertexAttribute>()
        val instLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()

        var attribLocation = 0
        vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Vertex }.forEach { vertexAttrib ->
            val attrib = checkNotNull(verts.attributeByteOffsets.keys.find { it.name == vertexAttrib.name }) {
                "Mesh does not include required vertex attribute: ${vertexAttrib.name} (for shader: ${program.name})"
            }
            val off = verts.attributeByteOffsets[attrib]!!
            if (attrib.type.isInt) {
                vertLayoutAttribsI += VertexLayout.VertexAttribute(attribLocation, off, attrib)
            } else {
                vertLayoutAttribsF += VertexLayout.VertexAttribute(attribLocation, off, attrib)
            }
            attribLocation++
        }

        val instanceAttribs = vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Instance }
        if (insts != null) {
            instanceAttribs.forEach { instanceAttrib ->
                val attrib = checkNotNull(insts.attributeOffsets.keys.find { it.name == instanceAttrib.name }) {
                    "Mesh does not include required instance attribute: ${instanceAttrib.name}"
                }
                val off = insts.attributeOffsets[attrib]!!
                instLayoutAttribs += VertexLayout.VertexAttribute(attribLocation++, off, attrib)
            }
        } else if (instanceAttribs.isNotEmpty()) {
            throw IllegalStateException("Shader model requires instance attributes, but mesh doesn't provide any")
        }

        var iBinding = 0
        val bindings = buildList {
            this += VertexLayout.Binding(
                iBinding++,
                InputRate.VERTEX,
                vertLayoutAttribsF,
                verts.byteStrideF
            )
            if (vertLayoutAttribsI.isNotEmpty()) {
                this += VertexLayout.Binding(
                    iBinding++,
                    InputRate.VERTEX,
                    vertLayoutAttribsI,
                    verts.byteStrideI
                )
            }
            if (insts != null) {
                this += VertexLayout.Binding(
                    iBinding,
                    InputRate.INSTANCE,
                    instLayoutAttribs,
                    insts.strideBytesF
                )
            }
        }
        return VertexLayout(bindings, mesh.geometry.primitiveType)
    }
}

fun KslProgram.makeBindGroupLayout(): BindGroupLayouts {
    return BindGroupLayouts(
        makeBindGroupLayout(BindGroupScope.VIEW),
        makeBindGroupLayout(BindGroupScope.PIPELINE),
        makeBindGroupLayout(BindGroupScope.MESH),
    )
}

private fun KslProgram.makeBindGroupLayout(scope: BindGroupScope): BindGroupLayout {
    val bindGrpBuilder = BindGroupLayout.Builder(scope)
    setupBindGroupLayoutUbos(bindGrpBuilder)
    setupBindGroupLayoutTextures(bindGrpBuilder)
    setupBindGroupLayoutStorage(bindGrpBuilder)
    return bindGrpBuilder.create()
}

private fun KslProgram.setupBindGroupLayoutUbos(bindGrpBuilder: BindGroupLayout.Builder) {
    uniformBuffers.filter { it.uniforms.isNotEmpty() && it.scope == bindGrpBuilder.scope }.forEach { kslUbo ->
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
    if (bindGrpBuilder.scope != BindGroupScope.PIPELINE) {
        // todo: add bind group scope to ksl textures -> for now we use pipeline scope for all of them
        return
    }

    uniformSamplers.values.forEach { samplerUniform ->
        val (sampler, sampleType) = samplerUniform
        val texStages = stages
            .filter { it.dependsOn(sampler) }
            .map { it.type.pipelineStageType }
            .toSet()

        bindGrpBuilder.textures += when(val type = sampler.value.expressionType)  {
            is KslDepthSampler2d -> Texture2dLayout(sampler.name, texStages, sampleType)
            is KslDepthSamplerCube -> TextureCubeLayout(sampler.name, texStages, sampleType)
            is KslColorSampler1d -> Texture1dLayout(sampler.name, texStages, sampleType)
            is KslColorSampler2d -> Texture2dLayout(sampler.name, texStages, sampleType)
            is KslColorSampler3d -> Texture3dLayout(sampler.name, texStages, sampleType)
            is KslColorSamplerCube -> TextureCubeLayout(sampler.name, texStages, sampleType)
            else -> throw IllegalStateException("Unsupported sampler uniform type: ${type.typeName}")
        }
    }
}

private fun KslProgram.setupBindGroupLayoutStorage(bindGrpBuilder: BindGroupLayout.Builder) {
    if (bindGrpBuilder.scope != BindGroupScope.PIPELINE) {
        // todo: add bind group scope to ksl storage -> for now we use pipeline scope for all of them
        return
    }

    storageBuffers.values.forEach { storage ->
        val storageStages = stages
            .filter { it.dependsOn(storage) }
            .map { it.type.pipelineStageType }
            .toSet()
        val format = when (storage.storageType.elemType) {
            KslFloat1 -> GpuType.FLOAT1
            KslFloat2 -> GpuType.FLOAT2
            KslFloat4 -> GpuType.FLOAT4
            KslInt1 -> GpuType.INT1
            KslInt2 -> GpuType.INT2
            KslInt4 -> GpuType.INT4
            KslUint1 -> GpuType.INT1
            KslUint2 -> GpuType.INT2
            KslUint4 -> GpuType.INT4
            else -> error("Invalid storage type: ${storage.storageType.elemType} (only 1, 2, and 4 dimensional float and int types are allowed)")
        }

        val name = storage.name
        bindGrpBuilder.storage += when(storage)  {
            is KslStorage1d<*> -> StorageBuffer1dLayout(name, format, storage.sizeX, storage.accessType, storageStages)
            is KslStorage2d<*> -> StorageBuffer2dLayout(name, format, storage.sizeX, storage.sizeY, storage.accessType, storageStages)
            is KslStorage3d<*> -> StorageBuffer3dLayout(name, format, storage.sizeX, storage.sizeY, storage.sizeZ, storage.accessType, storageStages)
        }
    }
}
