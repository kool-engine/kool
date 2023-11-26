package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.logE

class CompiledShader(val program: GlProgram, val pipeline: Pipeline, val backend: RenderBackendGl) {

    private val pipelineId = pipeline.pipelineHash

    private val attributes = mutableMapOf<String, VertexLayout.VertexAttribute>()
    private val instanceAttributes = mutableMapOf<String, VertexLayout.VertexAttribute>()
    private val uniformLocations = mutableMapOf<String, IntArray>()
    private val uboLayouts = mutableMapOf<String, ExternalBufferLayout>()
    private val instances = mutableMapOf<Long, ShaderInstance>()

    private val ctx: KoolContext = backend.ctx
    private val gl: GlApi = backend.gl

    private val pipelineInfo = PipelineInfo(pipeline)

    init {
        pipeline.vertexLayout.bindings.forEach { bnd ->
            bnd.vertexAttributes.forEach { attr ->
                when (bnd.inputRate) {
                    InputRate.VERTEX -> attributes[attr.attribute.name] = attr
                    InputRate.INSTANCE -> instanceAttributes[attr.attribute.name] = attr
                }
            }
        }
        pipeline.bindGroupLayouts.forEach { group ->
            group.items.forEach { binding ->
                when (binding) {
                    is UniformBuffer -> {
                        val blockIndex = gl.getUniformBlockIndex(program, binding.name)
                        if (blockIndex == gl.INVALID_INDEX) {
                            // descriptor does not describe an actual UBO but plain old uniforms...
                            binding.uniforms.forEach { uniformLocations[it.name] = intArrayOf(gl.getUniformLocation(program, it.name)) }
                        } else {
                            setupUboLayout(binding, blockIndex)
                        }
                    }
                    is TextureSampler1d -> {
                        uniformLocations[binding.name] = getUniformLocations(binding.name, binding.arraySize)
                    }
                    is TextureSampler2d -> {
                        uniformLocations[binding.name] = getUniformLocations(binding.name, binding.arraySize)
                    }
                    is TextureSampler3d -> {
                        uniformLocations[binding.name] = getUniformLocations(binding.name, binding.arraySize)
                    }
                    is TextureSamplerCube -> {
                        uniformLocations[binding.name] = getUniformLocations(binding.name, binding.arraySize)
                    }
                }
            }
        }
    }

    private fun setupUboLayout(desc: UniformBuffer, blockIndex: Int) {
        val bufferSize = gl.getActiveUniformBlockParameter(program, blockIndex, gl.UNIFORM_BLOCK_DATA_SIZE)
        val uniformNames = desc.uniforms.map {
            if (it.size > 1) "${it.name}[0]" else it.name
        }.toTypedArray()

        val indices = gl.getUniformIndices(program, uniformNames)
        val offsets = gl.getActiveUniforms(program, indices, gl.UNIFORM_OFFSET)

        val sortedOffsets = offsets.sorted()
        val bufferPositions = Array(desc.uniforms.size) { i ->
            val off = offsets[i]
            val nextOffI = sortedOffsets.indexOf(off) + 1
            val nextOff = if (nextOffI < sortedOffsets.size) sortedOffsets[nextOffI] else bufferSize
            BufferPosition(off, nextOff - off)
        }

        gl.uniformBlockBinding(program, blockIndex, desc.binding)
        uboLayouts[desc.name] = ExternalBufferLayout(desc.uniforms, bufferPositions, bufferSize)
    }

    private fun getUniformLocations(name: String, arraySize: Int): IntArray {
        val locations = IntArray(arraySize)
        if (arraySize > 1) {
            for (i in 0 until arraySize) {
                locations[i] = gl.getUniformLocation(program, "$name[$i]")
            }
        } else {
            locations[0] = gl.getUniformLocation(program, name)
        }
        return locations
    }

    fun use() {
        gl.useProgram(program)
        attributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.locationIncrement) {
                val location = attr.location + i
                gl.enableVertexAttribArray(location)
                gl.vertexAttribDivisor(location, 0)
            }
        }
        instanceAttributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.locationIncrement) {
                val location = attr.location + i
                gl.enableVertexAttribArray(location)
                gl.vertexAttribDivisor(location, 1)
            }
        }
    }

    fun unUse() {
        attributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.locationIncrement) {
                gl.disableVertexAttribArray(attr.location + i)
            }
        }
        instanceAttributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.locationIncrement) {
                gl.disableVertexAttribArray(attr.location + i)
            }
        }
    }

    fun bindInstance(cmd: DrawCommand): ShaderInstance? {
        val pipelineInst = cmd.pipeline!!
        val inst = instances.getOrPut(pipelineInst.pipelineInstanceId) {
            ShaderInstance(cmd, pipelineInst)
        }
        return if (inst.bindInstance(cmd)) { inst } else { null }
    }

    fun destroyInstance(pipeline: Pipeline) {
        instances.remove(pipeline.pipelineInstanceId)?.destroyInstance()
    }

    fun isEmpty(): Boolean = instances.isEmpty()

    fun destroy() {
        pipelineInfo.deleted()
        gl.deleteProgram(program)
    }

    inner class ShaderInstance(cmd: DrawCommand, val pipeline: Pipeline) {
        var geometry: IndexedVertexList = cmd.geometry
        val instances: MeshInstanceList? = cmd.mesh.instances

        private val ubos = mutableListOf<UniformBuffer>()
        private val textures1d = mutableListOf<TextureSampler1d>()
        private val textures2d = mutableListOf<TextureSampler2d>()
        private val textures3d = mutableListOf<TextureSampler3d>()
        private val texturesCube = mutableListOf<TextureSamplerCube>()
        private val mappings = mutableListOf<MappedUniform>()
        private val attributeBinders = mutableListOf<GpuGeometryGl.AttributeBinder>()
        private val instanceAttribBinders = mutableListOf<GpuGeometryGl.AttributeBinder>()

        private var gpuGeometry: GpuGeometryGl? = null
        private var uboBuffers = mutableListOf<BufferResource>()

        private var nextTexUnit = gl.TEXTURE0

        val primitiveType = pipeline.vertexLayout.primitiveType.glElemType
        val indexType = gl.UNSIGNED_INT
        val numIndices: Int get() = gpuGeometry?.numIndices ?: 0

        init {
            pipeline.bindGroupLayouts.forEach { group ->
                group.items.forEach { binding ->
                    when (binding) {
                        is UniformBuffer -> mapUbo(binding)
                        is TextureSampler1d -> mapTexture1d(binding)
                        is TextureSampler2d -> mapTexture2d(binding)
                        is TextureSampler3d -> mapTexture3d(binding)
                        is TextureSamplerCube -> mapTextureCube(binding)
                    }
                }
            }
            createBuffers(cmd)
            pipelineInfo.numInstances++
        }

        private fun createBuffers(cmd: DrawCommand) {
            val creationInfo = BufferCreationInfo(cmd)

            var geom = geometry.gpuGeometry as? GpuGeometryGl
            if (geom == null || geom.isReleased) {
                if (geom?.isReleased == true) {
                    logE { "Mesh geometry is already released: ${pipeline.name}" }
                }
                geom = GpuGeometryGl(geometry, instances, backend, creationInfo)
                geometry.gpuGeometry = geom
            }
            gpuGeometry = geom

            attributeBinders += geom.createShaderVertexAttributeBinders(attributes)
            instanceAttribBinders += geom.createShaderInstanceAttributeBinders(instanceAttributes)

            mappings.filterIsInstance<MappedUbo>().forEachIndexed { i, mappedUbo ->
                val uboBuffer = BufferResource(gl.UNIFORM_BUFFER, backend, creationInfo.copy(bufferName = "${pipeline.name}.ubo-$i"))
                uboBuffers += uboBuffer
                mappedUbo.uboBuffer = uboBuffer
            }
        }

        private fun mapUbo(ubo: UniformBuffer) {
            ubos.add(ubo)
            val uboLayout = uboLayouts[ubo.name]
            if (uboLayout != null) {
                mappings += MappedUbo(ubo, uboLayout, gl)

            } else {
                ubo.uniforms.forEach {
                    val location = uniformLocations[it.name]
                    if (location != null) {
                        mappings += MappedUniform.mappedUniform(it, location[0], gl)
                    } else {
                        logE { "Uniform location not present for uniform ${ubo.name}.${it.name}" }
                    }
                }
            }
        }

        private fun mapTexture1d(tex: TextureSampler1d) {
            textures1d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex1d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTexture2d(tex: TextureSampler2d) {
            textures2d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex2d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTexture3d(tex: TextureSampler3d) {
            textures3d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex3d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTextureCube(cubeMap: TextureSamplerCube) {
            texturesCube.add(cubeMap)
            uniformLocations[cubeMap.name]?.let { locs ->
                mappings += MappedUniformTexCube(cubeMap, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        fun bindInstance(drawCmd: DrawCommand): Boolean {
            if (geometry !== drawCmd.geometry) {
                geometry = drawCmd.geometry
                destroyBuffers()
                createBuffers(drawCmd)
            }

            // call onUpdate callbacks
            for (i in pipeline.onUpdate.indices) {
                pipeline.onUpdate[i].invoke(drawCmd)
            }
            for (i in ubos.indices) {
                ubos[i].onUpdate?.invoke(ubos[i], drawCmd)
            }
            for (i in textures1d.indices) {
                textures1d[i].onUpdate?.invoke(textures1d[i], drawCmd)
            }
            for (i in textures2d.indices) {
                textures2d[i].onUpdate?.invoke(textures2d[i], drawCmd)
            }
            for (i in textures3d.indices) {
                textures3d[i].onUpdate?.invoke(textures3d[i], drawCmd)
            }
            for (i in texturesCube.indices) {
                texturesCube[i].onUpdate?.invoke(texturesCube[i], drawCmd)
            }

            // update geometry buffers (vertex + instance data)
            gpuGeometry?.checkBuffers()

            // bind uniform values
            var uniformsValid = true
            for (i in mappings.indices) {
                uniformsValid = uniformsValid && mappings[i].setUniform()
            }

            if (uniformsValid) {
                // bind vertex data
                gpuGeometry?.indexBuffer?.bind()
                attributeBinders.forEach { it.bindAttribute(it.loc) }
                instanceAttribBinders.forEach { it.bindAttribute(it.loc) }
            }
            return uniformsValid
        }

        private fun destroyBuffers() {
            attributeBinders.clear()
            instanceAttribBinders.clear()
            uboBuffers.forEach { it.delete() }
            uboBuffers.clear()
            gpuGeometry = null
        }

        fun destroyInstance() {
            destroyBuffers()

            ubos.clear()
            textures1d.clear()
            textures2d.clear()
            textures3d.clear()
            texturesCube.clear()
            mappings.clear()

            pipelineInfo.numInstances--
        }
    }

    private val PrimitiveType.glElemType: Int get() = when (this) {
        PrimitiveType.LINES -> gl.LINES
        PrimitiveType.POINTS -> gl.POINTS
        PrimitiveType.TRIANGLES -> gl.TRIANGLES
    }
}
