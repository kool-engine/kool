package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.releaseWith

sealed class CompiledShader(private val pipeline: PipelineBase, val program: GlProgram, val backend: RenderBackendGl) : BaseReleasable() {
    val gl: GlApi = backend.gl
    val pipelineInfo = PipelineInfo(pipeline)

    private val plainUniformUbos = mutableSetOf<String>()
    private val uniformBindCtx: UniformBindContext

    var lastUsed = 0.0
        private set

    init {
        var uboIndex = 0
        var storageIndex = 0
        val uniformLocations = pipeline.bindGroupLayouts.asList.map { groupLayout ->
            groupLayout.bindings.map { binding ->
                when (binding) {
                    is UniformBufferLayout -> {
                        val blockIndex = gl.getUniformBlockIndex(program, binding.name)
                        if (blockIndex != gl.INVALID_INDEX) {
                            val uboBinding = uboIndex++
                            gl.uniformBlockBinding(program, blockIndex, uboBinding)
                            intArrayOf(uboBinding)
                        } else {
                            // binding does not describe an actual UBO but plain old uniforms
                            plainUniformUbos += binding.name
                            binding.uniforms.map { gl.getUniformLocation(program, it.name) }.toIntArray()
                        }
                    }
                    is Texture1dLayout -> getUniformLocations(binding.name, binding.arraySize, program)
                    is Texture2dLayout -> getUniformLocations(binding.name, binding.arraySize, program)
                    is Texture3dLayout -> getUniformLocations(binding.name, binding.arraySize, program)
                    is TextureCubeLayout -> getUniformLocations(binding.name, binding.arraySize, program)
                    is StorageTexture1dLayout -> intArrayOf(storageIndex++)
                    is StorageTexture2dLayout -> intArrayOf(storageIndex++)
                    is StorageTexture3dLayout -> intArrayOf(storageIndex++)
                }
            }
        }
        uniformBindCtx = UniformBindContext(uniformLocations)
        pipelineInfo.numInstances++
    }

    private fun mapBindGroup(bindGroupData: BindGroupData, renderPass: RenderPass): MappedBindGroup {
        return MappedBindGroup(bindGroupData, plainUniformUbos, renderPass, backend)
    }

    protected fun bindUniforms(renderPass: RenderPass, viewData: BindGroupData?, meshData: BindGroupData?): Boolean {
        lastUsed = Time.gameTime

        var uniformsOk = true
        uniformBindCtx.reset()

        if (viewData != null) {
            val viewGroup = (viewData.gpuData as MappedBindGroup?) ?: mapBindGroup(viewData, renderPass).also { viewData.gpuData = it }
            uniformsOk = uniformsOk && viewGroup.bindUniforms(uniformBindCtx) != false
        }

        val pipelineData = pipeline.pipelineData
        val pipelineGroup = (pipelineData.gpuData as MappedBindGroup?) ?: mapBindGroup(pipelineData, renderPass).also { pipelineData.gpuData = it }
        uniformsOk = uniformsOk && pipelineGroup.bindUniforms(uniformBindCtx) != false

        if (meshData != null) {
            val meshGroup = (meshData.gpuData as MappedBindGroup?) ?: mapBindGroup(meshData, renderPass).also { meshData.gpuData = it }
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

        fun location(bindingIndex: Int): Int = locations[group][bindingIndex][0]
        fun locations(bindingIndex: Int): IntArray = locations[group][bindingIndex]

        fun group(scope: BindGroupScope) {
            group = scope.group
        }

        fun reset() {
            group = 0
            nextTexUnit = 0
        }
    }

    class MappedBindGroup(
        val bindGroupData: BindGroupData,
        private val plainUniformUbos: Set<String>,
        private val renderPass: RenderPass,
        private val backend: RenderBackendGl
    ) : BaseReleasable(), GpuBindGroupData {
        private val gl: GlApi get() = backend.gl

        private val mappings = mutableListOf<MappedUniform>()

        init {
            bindGroupData.layout.bindings.forEach { binding ->
                when (binding) {
                    is UniformBufferLayout -> mapUbo(binding)
                    is Texture1dLayout -> mapTexture1d(binding)
                    is Texture2dLayout -> mapTexture2d(binding)
                    is Texture3dLayout -> mapTexture3d(binding)
                    is TextureCubeLayout -> mapTextureCube(binding)
                    is StorageTexture1dLayout -> mapStorage1d(binding)
                    is StorageTexture2dLayout -> mapStorage2d(binding)
                    is StorageTexture3dLayout -> mapStorage3d(binding)
                }
            }
        }

        fun bindUniforms(uniformBindContext: UniformBindContext): Boolean {
            uniformBindContext.group(bindGroupData.layout.scope)
            var uniformsValid = true
            for (i in mappings.indices) {
                uniformsValid = uniformsValid && mappings[i].setUniform(this, uniformBindContext)
            }
            return uniformsValid
        }

        private fun mapUbo(ubo: UniformBufferLayout) {
            mappings += if (ubo.name !in plainUniformUbos) {
                val bufferCreationInfo = BufferCreationInfo(
                    bufferName = "bindGroup[${bindGroupData.layout.scope}]-ubo-${ubo.name}",
                    renderPassName = renderPass.name,
                    sceneName = renderPass.parentScene?.name ?: "scene:<null>"
                )
                val buffer = BufferResource(backend.gl.UNIFORM_BUFFER, backend, bufferCreationInfo)
                buffer.releaseWith(this)
                MappedUbo(ubo, buffer, backend)

            } else {
                MappedUboCompat(ubo, gl)
            }
        }

        private fun mapTexture1d(tex: Texture1dLayout) {
            mappings += MappedUniformTex1d(tex, backend)
        }

        private fun mapTexture2d(tex: Texture2dLayout) {
            mappings += MappedUniformTex2d(tex, backend)
        }

        private fun mapTexture3d(tex: Texture3dLayout) {
            mappings += MappedUniformTex3d(tex, backend)
        }

        private fun mapTextureCube(cubeMap: TextureCubeLayout) {
            mappings += MappedUniformTexCube(cubeMap, backend)
        }

        private fun mapStorage1d(storage: StorageTexture1dLayout) {
            checkStorageTexSupport()
            mappings += MappedUniformStorage1d(storage, backend)
        }

        private fun mapStorage2d(storage: StorageTexture2dLayout) {
            checkStorageTexSupport()
            mappings += MappedUniformStorage2d(storage, backend)
        }

        private fun mapStorage3d(storage: StorageTexture3dLayout) {
            checkStorageTexSupport()
            mappings += MappedUniformStorage3d(storage, backend)
        }

        private fun checkStorageTexSupport() {
            check(backend.gl.version.isHigherOrEqualThan(4, 2)) {
                "Storage textures require OpenGL 4.2 or higher"
            }
        }
    }
}
