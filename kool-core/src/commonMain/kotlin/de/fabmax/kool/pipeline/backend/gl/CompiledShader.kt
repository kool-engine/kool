package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.releaseWith

sealed class CompiledShader(private val pipeline: PipelineBase, val program: GlProgram, val backend: RenderBackendGl) : BaseReleasable() {
    val gl: GlApi = backend.gl
    private val pipelineInfo = PipelineInfo(pipeline)

    private val plainUniformUbos = mutableSetOf<String>()
    private val uniformBindCtx: UniformBindContext

    var lastUsed = 0.0
        private set

    init {
        var uboIndex = 0
        var storageIndex = 0
        val uniformLocations = pipeline.bindGroupLayouts.asList
            .filter { it.group >= 0 }
            .map { groupLayout ->
                groupLayout.bindings.map { binding ->
                    when (binding) {
                        is UniformBufferLayout<*> -> {
                            var blockIndex = gl.getUniformBlockIndex(program, binding.name)
                            if (blockIndex == gl.INVALID_INDEX) {
                                // struct ubos have a different naming pattern
                                blockIndex = gl.getUniformBlockIndex(program, "${binding.name}_ubo")
                            }

                            if (blockIndex != gl.INVALID_INDEX) {
                                val uboBinding = uboIndex++
                                gl.uniformBlockBinding(program, blockIndex, uboBinding)
                                intArrayOf(uboBinding)
                            } else {
                                // binding does not describe an actual UBO but plain old uniforms
                                plainUniformUbos += binding.name
                                binding.structProvider().members.map { gl.getUniformLocation(program, it.memberName) }.toIntArray()
                            }
                        }
                        is StorageBufferLayout -> intArrayOf(storageIndex++)

                        is Texture1dLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))
                        is Texture2dLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))
                        is Texture3dLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))
                        is TextureCubeLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))
                        is Texture2dArrayLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))
                        is TextureCubeArrayLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))

                        is StorageTexture1dLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))
                        is StorageTexture2dLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))
                        is StorageTexture3dLayout -> intArrayOf(gl.getUniformLocation(program, binding.name))
                    }
                }
        }
        uniformBindCtx = UniformBindContext(uniformLocations)
        pipelineInfo.numInstances++
    }

    private fun mapBindGroup(bindGroupData: BindGroupData, pass: GpuPass): MappedBindGroup {
        return MappedBindGroup(bindGroupData, plainUniformUbos, pass, backend)
    }

    protected fun bindUniforms(pass: GpuPass, viewData: BindGroupData?, meshData: BindGroupData?): Boolean {
        lastUsed = Time.gameTime

        var uniformsOk = true
        uniformBindCtx.reset(pass)

        if (viewData != null) {
            val viewGroup = (viewData.gpuData as MappedBindGroup?) ?: mapBindGroup(viewData, pass).also { viewData.gpuData = it }
            uniformsOk = uniformsOk && viewGroup.bindUniforms(uniformBindCtx) != false
        }

        val pipelineData = pipeline.pipelineData
        val pipelineGroup = (pipelineData.gpuData as MappedBindGroup?) ?: mapBindGroup(pipelineData, pass).also { pipelineData.gpuData = it }
        uniformsOk = uniformsOk && pipelineGroup.bindUniforms(uniformBindCtx) != false

        if (meshData != null) {
            val meshGroup = (meshData.gpuData as MappedBindGroup?) ?: mapBindGroup(meshData, pass).also { meshData.gpuData = it }
            uniformsOk = uniformsOk && meshGroup.bindUniforms(uniformBindCtx) != false
        }
        return uniformsOk
    }

    override fun release() {
        super.release()
        if (!pipeline.isReleased) {
            pipeline.release()
        }
        pipelineInfo.deleted()
    }

    private fun getUniformLocations(name: String, arraySize: Int, program: GlProgram): IntArray {
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

    class UniformBindContext(val locations: List<List<IntArray>>) {
        var group: Int = 0
        var nextTexUnit: Int = 0
        lateinit var pass: GpuPass
            private set

        fun location(bindingIndex: Int): Int = locations[group][bindingIndex][0]
        fun locations(bindingIndex: Int): IntArray = locations[group][bindingIndex]

        fun reset(pass: GpuPass) {
            this.pass = pass
            group = 0
            nextTexUnit = 0
        }
    }

    class MappedBindGroup(
        val bindGroupData: BindGroupData,
        private val plainUniformUbos: Set<String>,
        private val pass: GpuPass,
        private val backend: RenderBackendGl
    ) : BaseReleasable(), GpuBindGroupData {
        private val gl: GlApi get() = backend.gl

        private val mappings = mutableListOf<MappedUniform>()

        init {
            bindGroupData.bindings.forEach { binding ->
                when (binding) {
                    is BindGroupData.UniformBufferBindingData<*> -> mapUbo(binding)
                    is BindGroupData.StorageBufferBindingData -> mapStorageBuffer(binding)

                    is BindGroupData.Texture1dBindingData -> mapTexture1d(binding)
                    is BindGroupData.Texture2dBindingData -> mapTexture2d(binding)
                    is BindGroupData.Texture3dBindingData -> mapTexture3d(binding)
                    is BindGroupData.TextureCubeBindingData -> mapTextureCube(binding)
                    is BindGroupData.Texture2dArrayBindingData -> mapTexture2dArray(binding)
                    is BindGroupData.TextureCubeArrayBindingData -> mapTextureCubeArray(binding)

                    is BindGroupData.StorageTexture1dBindingData -> mapStorageTexture1d(binding)
                    is BindGroupData.StorageTexture2dBindingData -> mapStorageTexture2d(binding)
                    is BindGroupData.StorageTexture3dBindingData -> mapStorageTexture3d(binding)
                }
            }
        }

        fun bindUniforms(uniformBindContext: UniformBindContext): Boolean {
            uniformBindContext.group = bindGroupData.layout.group
            var uniformsValid = true
            for (i in mappings.indices) {
                uniformsValid = uniformsValid && mappings[i].setUniform(uniformBindContext)
            }
            return uniformsValid
        }

        private fun createGpuBuffer(name: String): GpuBufferGl {
            val bufferCreationInfo = BufferCreationInfo(
                bufferName = name,
                renderPassName = pass.name,
                sceneName = pass.parentScene?.name ?: "scene:<null>"
            )
            val buffer = GpuBufferGl(backend.gl.UNIFORM_BUFFER, backend, bufferCreationInfo)
            buffer.releaseWith(this)
            return buffer
        }

        private fun mapUbo(ubo: BindGroupData.UniformBufferBindingData<*>) {
            mappings += if (ubo.name !in plainUniformUbos) {
                val buffer = createGpuBuffer("bindGroup[${bindGroupData.layout.scope}]-ubo-${ubo.name}")
                MappedUbo(ubo, buffer, backend)
            } else {
                MappedUboCompat(ubo, gl)
            }
        }

        private fun mapTexture1d(tex: BindGroupData.Texture1dBindingData) {
            mappings += MappedUniformTex1d(tex, backend)
        }

        private fun mapTexture2d(tex: BindGroupData.Texture2dBindingData) {
            mappings += MappedUniformTex2d(tex, backend)
        }

        private fun mapTexture3d(tex: BindGroupData.Texture3dBindingData) {
            mappings += MappedUniformTex3d(tex, backend)
        }

        private fun mapTextureCube(cubeMap: BindGroupData.TextureCubeBindingData) {
            mappings += MappedUniformTexCube(cubeMap, backend)
        }

        private fun mapTexture2dArray(tex: BindGroupData.Texture2dArrayBindingData) {
            mappings += MappedUniformTex2dArray(tex, backend)
        }

        private fun mapTextureCubeArray(tex: BindGroupData.TextureCubeArrayBindingData) {
            mappings += MappedUniformTexCubeArray(tex, backend)
        }

        private fun mapStorageBuffer(storage: BindGroupData.StorageBufferBindingData) {
            checkStorageBufferSupport()
            mappings += MappedStorageBuffer(storage, backend)
        }

        private fun mapStorageTexture1d(storage: BindGroupData.StorageTexture1dBindingData) {
            checkStorageBufferSupport()
            mappings += MappedStorageTexture1d(storage, backend)
        }

        private fun mapStorageTexture2d(storage: BindGroupData.StorageTexture2dBindingData) {
            checkStorageBufferSupport()
            mappings += MappedStorageTexture2d(storage, backend)
        }

        private fun mapStorageTexture3d(storage: BindGroupData.StorageTexture3dBindingData) {
            checkStorageBufferSupport()
            mappings += MappedStorageTexture3d(storage, backend)
        }

        private fun checkStorageBufferSupport() {
            check(backend.gl.version.isHigherOrEqualThan(4, 3)) {
                "Storage buffers require OpenGL 4.3 or higher"
            }
        }

        private fun checkStorageTextureSupport() {
            check(backend.gl.version.isHigherOrEqualThan(4, 3)) {
                "Storage textures require OpenGL 4.2 or higher"
            }
        }
    }
}
