package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.logE

class CompiledShader(val program: GlProgram, val pipeline: PipelineBase, val backend: RenderBackendGl) {

    private val pipelineId = pipeline.pipelineHash

    private val attributes = mutableMapOf<String, VertexLayout.VertexAttribute>()
    private val instanceAttributes = mutableMapOf<String, VertexLayout.VertexAttribute>()
    private val uniformLocations = mutableMapOf<String, IntArray>()
    private val uboLayouts = mutableMapOf<String, BufferLayout>()
    private val instances = mutableMapOf<Long, ShaderInstance>()
    private val computeInstances = mutableMapOf<Long, ComputeShaderInstance>()

    private val ctx: KoolContext = backend.ctx
    private val gl: GlApi = backend.gl

    private val pipelineInfo = PipelineInfo(pipeline)

    init {
        (pipeline as? Pipeline)?.apply {
            vertexLayout.bindings.forEach { bnd ->
                bnd.vertexAttributes.forEach { attr ->
                    when (bnd.inputRate) {
                        InputRate.VERTEX -> attributes[attr.attribute.name] = attr
                        InputRate.INSTANCE -> instanceAttributes[attr.attribute.name] = attr
                    }
                }
            }
        }

        pipeline.bindGroupLayouts.forEach { group ->
            group.items.forEach { binding ->
                when (binding) {
                    is UniformBuffer -> {
                        val blockIndex = gl.getUniformBlockIndex(program, binding.name)
                        if (blockIndex == gl.INVALID_INDEX) {
                            // binding does not describe an actual UBO but plain old uniforms...
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
                    is Storage1d -> {
                        checkStorageTexSupport()
                        uniformLocations[binding.name] = intArrayOf(binding.binding)
                    }
                    is Storage2d -> {
                        checkStorageTexSupport()
                        uniformLocations[binding.name] = intArrayOf(binding.binding)
                    }
                    is Storage3d -> {
                        checkStorageTexSupport()
                        uniformLocations[binding.name] = intArrayOf(binding.binding)
                    }
                }
            }
        }
    }

    private fun checkStorageTexSupport() {
        check(backend.gl.version.isHigherOrEqualThan(4, 2)) {
            "Storage textures require OpenGL 4.2 or higher"
        }
    }

    private fun setupUboLayout(binding: UniformBuffer, blockIndex: Int) {
        gl.uniformBlockBinding(program, blockIndex, binding.binding)
        if (binding.isShared) {
            setupUboLayoutStd140(binding)
        } else {
            setupUboLayoutGlApi(binding, blockIndex)
        }
    }

    private fun setupUboLayoutStd140(binding: UniformBuffer) {
        uboLayouts[binding.name] = Std140BufferLayout(binding.uniforms)
    }

    private fun setupUboLayoutGlApi(binding: UniformBuffer, blockIndex: Int) {
        val bufferSize = gl.getActiveUniformBlockParameter(program, blockIndex, gl.UNIFORM_BLOCK_DATA_SIZE)
        val uniformNames = binding.uniforms.map {
            if (it.size > 1) "${it.name}[0]" else it.name
        }.toTypedArray()

        val indices = gl.getUniformIndices(program, uniformNames)
        val offsets = gl.getActiveUniforms(program, indices, gl.UNIFORM_OFFSET)

        val sortedOffsets = offsets.sorted()
        val bufferPositions = Array(binding.uniforms.size) { i ->
            val off = offsets[i]
            val nextOffI = sortedOffsets.indexOf(off) + 1
            val nextOff = if (nextOffI < sortedOffsets.size) sortedOffsets[nextOffI] else bufferSize
            BufferPosition(off, nextOff - off)
        }
        uboLayouts[binding.name] = ExternalBufferLayout(binding.uniforms, bufferPositions, bufferSize)
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

    fun bindComputeInstance(pipelineInstance: ComputePipeline, computePass: ComputeRenderPass): ComputeShaderInstance? {
        val inst = computeInstances.getOrPut(pipelineInstance.pipelineInstanceId) {
            ComputeShaderInstance(pipelineInstance, computePass)
        }
        return if (inst.bindInstance(computePass)) { inst } else { null }
    }

    fun destroyInstance(pipeline: Pipeline) {
        instances.remove(pipeline.pipelineInstanceId)?.destroyInstance()
    }

    fun isEmpty(): Boolean = instances.isEmpty()

    fun destroy() {
        pipelineInfo.deleted()
        gl.deleteProgram(program)
    }

    abstract inner class ShaderInstanceBase(private val pipelineInstance: PipelineBase) {

        protected val ubos = mutableListOf<UniformBuffer>()
        protected val textures1d = mutableListOf<TextureSampler1d>()
        protected val textures2d = mutableListOf<TextureSampler2d>()
        protected val textures3d = mutableListOf<TextureSampler3d>()
        protected val texturesCube = mutableListOf<TextureSamplerCube>()
        protected val storage1d = mutableListOf<Storage1d>()
        protected val storage2d = mutableListOf<Storage2d>()
        protected val storage3d = mutableListOf<Storage3d>()

        protected val mappings = mutableListOf<MappedUniform>()
        protected var uboBuffers = mutableListOf<BufferResource>()
        protected var nextTexUnit = gl.TEXTURE0

        init {
            pipelineInstance.bindGroupLayouts.forEach { group ->
                group.items.forEach { binding ->
                    when (binding) {
                        is UniformBuffer -> mapUbo(binding)
                        is TextureSampler1d -> mapTexture1d(binding)
                        is TextureSampler2d -> mapTexture2d(binding)
                        is TextureSampler3d -> mapTexture3d(binding)
                        is TextureSamplerCube -> mapTextureCube(binding)
                        is Storage1d -> mapStorage1d(binding)
                        is Storage2d -> mapStorage2d(binding)
                        is Storage3d -> mapStorage3d(binding)
                    }
                }
            }
            pipelineInfo.numInstances++
        }

        protected fun createUboBuffers(renderPass: RenderPass) {
            mappings.filterIsInstance<MappedUbo>().forEachIndexed { i, mappedUbo ->
                val creationInfo = BufferCreationInfo(
                    bufferName = "${pipelineInstance.name}.ubo-$i",
                    renderPassName = renderPass.name,
                    sceneName = renderPass.parentScene?.name ?: "scene:<null>"
                )

                val uboBuffer = BufferResource(
                    gl.UNIFORM_BUFFER,
                    backend,
                    creationInfo
                )
                uboBuffers += uboBuffer
                mappedUbo.uboBuffer = uboBuffer
            }
        }

        protected fun bindUniforms(): Boolean {
            var uniformsValid = true
            for (i in mappings.indices) {
                uniformsValid = uniformsValid && mappings[i].setUniform()
            }
            return uniformsValid
        }

        protected open fun destroyBuffers() {
            uboBuffers.forEach { it.delete() }
            uboBuffers.clear()
        }

        open fun destroyInstance() {
            destroyBuffers()

            ubos.clear()
            textures1d.clear()
            textures2d.clear()
            textures3d.clear()
            texturesCube.clear()
            storage1d.clear()
            storage2d.clear()
            storage3d.clear()
            mappings.clear()

            pipelineInfo.numInstances--
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

        private fun mapStorage1d(storage: Storage1d) {
            storage1d.add(storage)
            uniformLocations[storage.name]?.let { binding ->
                mappings += MappedUniformStorage1d(storage, binding[0], backend)
            }
        }

        private fun mapStorage2d(storage: Storage2d) {
            storage2d.add(storage)
            uniformLocations[storage.name]?.let { binding ->
                mappings += MappedUniformStorage2d(storage, binding[0], backend)
            }
        }

        private fun mapStorage3d(storage: Storage3d) {
            storage3d.add(storage)
            uniformLocations[storage.name]?.let { binding ->
                mappings += MappedUniformStorage3d(storage, binding[0], backend)
            }
        }
    }

    inner class ShaderInstance(cmd: DrawCommand, val pipelineInstance: Pipeline) : ShaderInstanceBase(pipelineInstance) {
        var geometry: IndexedVertexList = cmd.geometry
        val instances: MeshInstanceList? = cmd.mesh.instances

        private val attributeBinders = mutableListOf<GpuGeometryGl.AttributeBinder>()
        private val instanceAttribBinders = mutableListOf<GpuGeometryGl.AttributeBinder>()
        private var gpuGeometry: GpuGeometryGl? = null

        val primitiveType = pipelineInstance.vertexLayout.primitiveType.glElemType
        val indexType = gl.UNSIGNED_INT
        val numIndices: Int get() = gpuGeometry?.numIndices ?: 0

        init {
            createBuffers(cmd)
        }

        private fun createBuffers(cmd: DrawCommand) {
            val creationInfo = BufferCreationInfo(cmd)

            var geom = geometry.gpuGeometry as? GpuGeometryGl
            if (geom == null || geom.isReleased) {
                if (geom?.isReleased == true) {
                    logE { "Mesh geometry is already released: ${pipelineInstance.name}" }
                }
                geom = GpuGeometryGl(geometry, instances, backend, creationInfo)
                geometry.gpuGeometry = geom
            }
            gpuGeometry = geom

            attributeBinders += geom.createShaderVertexAttributeBinders(attributes)
            instanceAttribBinders += geom.createShaderInstanceAttributeBinders(instanceAttributes)

            createUboBuffers(cmd.queue.renderPass)
        }

        fun bindInstance(drawCmd: DrawCommand): Boolean {
            if (geometry !== drawCmd.geometry) {
                geometry = drawCmd.geometry
                destroyBuffers()
                createBuffers(drawCmd)
            }

            // call onUpdate callbacks
            for (i in pipelineInstance.onUpdate.indices) {
                pipelineInstance.onUpdate[i].invoke(drawCmd)
            }

            // update geometry buffers (vertex + instance data)
            gpuGeometry?.checkBuffers()

            val uniformsValid = bindUniforms()
            if (uniformsValid) {
                // bind vertex data
                gpuGeometry?.indexBuffer?.bind()
                attributeBinders.forEach { it.bindAttribute(it.loc) }
                instanceAttribBinders.forEach { it.bindAttribute(it.loc) }
            }
            return uniformsValid
        }

        override fun destroyBuffers() {
            super.destroyBuffers()
            attributeBinders.clear()
            instanceAttribBinders.clear()
            gpuGeometry = null
        }
    }

    inner class ComputeShaderInstance(val pipelineInstance: ComputePipeline, computePass: ComputeRenderPass) :
        ShaderInstanceBase(pipelineInstance)
    {
        init {
            createUboBuffers(computePass)
        }

        fun bindInstance(computePass: ComputeRenderPass): Boolean {
            // call onUpdate callbacks
            for (i in pipelineInstance.onUpdate.indices) {
                pipelineInstance.onUpdate[i].invoke(computePass)
            }
            return bindUniforms()
        }
    }

    private val PrimitiveType.glElemType: Int get() = when (this) {
        PrimitiveType.LINES -> gl.LINES
        PrimitiveType.POINTS -> gl.POINTS
        PrimitiveType.TRIANGLES -> gl.TRIANGLES
    }
}
