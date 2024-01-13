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

        pipeline.bindGroupLayouts.flatMap { it.bindings }.forEach { binding ->
            when (binding) {
                is UniformBufferLayout -> {
                    val blockIndex = gl.getUniformBlockIndex(program, binding.name)
                    if (blockIndex == gl.INVALID_INDEX) {
                        // binding does not describe an actual UBO but plain old uniforms
                        val locations = binding.uniforms.map { gl.getUniformLocation(program, it.name) }.toIntArray()
                        uniformLocations[binding.name] = locations

                    } else {
                        gl.uniformBlockBinding(program, blockIndex, binding.bindingIndex)
                    }
                }
                is Texture1dLayout -> {
                    uniformLocations[binding.name] = getUniformLocations(binding.name, binding.arraySize)
                }
                is Texture2dLayout -> {
                    uniformLocations[binding.name] = getUniformLocations(binding.name, binding.arraySize)
                }
                is Texture3dLayout -> {
                    uniformLocations[binding.name] = getUniformLocations(binding.name, binding.arraySize)
                }
                is TextureCubeLayout -> {
                    uniformLocations[binding.name] = getUniformLocations(binding.name, binding.arraySize)
                }
                is StorageTexture1dLayout -> {
                    checkStorageTexSupport()
                    uniformLocations[binding.name] = intArrayOf(binding.bindingIndex)
                }
                is StorageTexture2dLayout -> {
                    checkStorageTexSupport()
                    uniformLocations[binding.name] = intArrayOf(binding.bindingIndex)
                }
                is StorageTexture3dLayout -> {
                    checkStorageTexSupport()
                    uniformLocations[binding.name] = intArrayOf(binding.bindingIndex)
                }
            }
        }
    }

    private fun checkStorageTexSupport() {
        check(backend.gl.version.isHigherOrEqualThan(4, 2)) {
            "Storage textures require OpenGL 4.2 or higher"
        }
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

        protected val ubos = mutableListOf<UniformBufferLayout>()
        protected val textures1d = mutableListOf<Texture1dLayout>()
        protected val textures2d = mutableListOf<Texture2dLayout>()
        protected val textures3d = mutableListOf<Texture3dLayout>()
        protected val texturesCube = mutableListOf<TextureCubeLayout>()
        protected val storage1d = mutableListOf<StorageTexture1dLayout>()
        protected val storage2d = mutableListOf<StorageTexture2dLayout>()
        protected val storage3d = mutableListOf<StorageTexture3dLayout>()

        protected val mappings = mutableListOf<Pair<Int, MappedUniform>>()
        protected var uboBuffers = mutableListOf<BufferResource>()
        protected var nextTexUnit = gl.TEXTURE0

        init {
            pipelineInstance.bindGroupLayouts.forEachIndexed { group, bindGroupLayout ->
                bindGroupLayout.bindings.forEach { binding ->
                    when (binding) {
                        is UniformBufferLayout -> mapUbo(group, binding)
                        is Texture1dLayout -> mapTexture1d(group, binding)
                        is Texture2dLayout -> mapTexture2d(group, binding)
                        is Texture3dLayout -> mapTexture3d(group, binding)
                        is TextureCubeLayout -> mapTextureCube(group, binding)
                        is StorageTexture1dLayout -> mapStorage1d(group, binding)
                        is StorageTexture2dLayout -> mapStorage2d(group, binding)
                        is StorageTexture3dLayout -> mapStorage3d(group, binding)
                    }
                }
            }
            pipelineInfo.numInstances++
        }

        protected fun createUboBuffers(renderPass: RenderPass) {
            mappings.filter { (_, mapping) -> mapping is MappedUbo }.forEachIndexed { i, (_, mappedUbo) ->
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
                (mappedUbo as MappedUbo).uboBuffer = uboBuffer
            }
        }

        protected fun bindUniforms(bindGroupData: List<BindGroupData>): Boolean {
            var uniformsValid = true
            for (i in mappings.indices) {
                val (group, mapping) = mappings[i]
                uniformsValid = uniformsValid && mapping.setUniform(bindGroupData[group])
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

        private fun mapUbo(group: Int, ubo: UniformBufferLayout) {
            ubos.add(ubo)
            val uniformLocations = uniformLocations[ubo.name]
            mappings += if (uniformLocations != null) {
                group to MappedUboCompat(ubo, uniformLocations, gl)
            } else {
                group to MappedUbo(ubo, gl)
            }
        }

        private fun mapTexture1d(group: Int, tex: Texture1dLayout) {
            textures1d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += group to MappedUniformTex1d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTexture2d(group: Int, tex: Texture2dLayout) {
            textures2d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += group to MappedUniformTex2d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTexture3d(group: Int, tex: Texture3dLayout) {
            textures3d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += group to MappedUniformTex3d(tex, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapTextureCube(group: Int, cubeMap: TextureCubeLayout) {
            texturesCube.add(cubeMap)
            uniformLocations[cubeMap.name]?.let { locs ->
                mappings += group to MappedUniformTexCube(cubeMap, nextTexUnit, locs, backend)
                nextTexUnit += locs.size
            }
        }

        private fun mapStorage1d(group: Int, storage: StorageTexture1dLayout) {
            storage1d.add(storage)
            uniformLocations[storage.name]?.let { binding ->
                mappings += group to MappedUniformStorage1d(storage, binding[0], backend)
            }
        }

        private fun mapStorage2d(group: Int, storage: StorageTexture2dLayout) {
            storage2d.add(storage)
            uniformLocations[storage.name]?.let { binding ->
                mappings += group to MappedUniformStorage2d(storage, binding[0], backend)
            }
        }

        private fun mapStorage3d(group: Int, storage: StorageTexture3dLayout) {
            storage3d.add(storage)
            uniformLocations[storage.name]?.let { binding ->
                mappings += group to MappedUniformStorage3d(storage, binding[0], backend)
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

            val uniformsValid = bindUniforms(pipelineInstance.bindGroupData)
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
            return bindUniforms(pipelineInstance.bindGroupData)
        }
    }

    private val PrimitiveType.glElemType: Int get() = when (this) {
        PrimitiveType.LINES -> gl.LINES
        PrimitiveType.POINTS -> gl.POINTS
        PrimitiveType.TRIANGLES -> gl.TRIANGLES
    }
}
