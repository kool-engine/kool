package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.util.BaseReleasable

sealed class CompiledShader(private val pipeline: PipelineBase, val program: GlProgram, val backend: RenderBackendGl) : BaseReleasable() {
    val gl: GlApi = backend.gl
    val pipelineInfo = PipelineInfo(pipeline)

    private var uboIndex = 0
    private var storageIndex = 0
    private var texUnit = gl.TEXTURE0

    private val mappedViewGroup = mapBindGroup(BindGroupScope.VIEW, true)
    private val mappedPipelineGroup = mapBindGroup(BindGroupScope.PIPELINE, true)

    protected fun mapBindGroup(scope: BindGroupScope, incrementLocations: Boolean = false): MappedBindGroup? {
        val oldUboIndex = uboIndex
        val oldStorageIndex = storageIndex
        val oldTexUnit = texUnit

        val mappedGroup = pipeline.bindGroupLayouts.getOrNull(scope.group)?.let { MappedBindGroup(scope, it) }

        if (!incrementLocations) {
            uboIndex = oldUboIndex
            storageIndex = oldStorageIndex
            texUnit = oldTexUnit
        }
        return mappedGroup
    }

    protected fun bindUniforms(renderPass: RenderPass, view: RenderPass.View?): Boolean {
        val viewOk = view?.let { mappedViewGroup?.bindUniforms(it.viewPipelineData.getPipelineData(pipeline), renderPass) } != false
        val pipelineOk = mappedPipelineGroup?.bindUniforms(pipeline.pipelineData, renderPass) != false
        return viewOk && pipelineOk
    }

    override fun release() {
        super.release()
        if (!pipeline.isReleased) {
            pipeline.release()
        }
        pipelineInfo.deleted()
    }

    inner class MappedBindGroup(val scope: BindGroupScope, val layout: BindGroupLayout) {
        private val mappings = mutableListOf<MappedUniform>()
        private val compatUbos = mutableSetOf<String>()

        init {
            layout.bindings.forEach { binding ->
                when (binding) {
                    is UniformBufferLayout -> {
                        val blockIndex = gl.getUniformBlockIndex(program, binding.name)
                        val locations = if (blockIndex != gl.INVALID_INDEX) {
                            val uboBinding = uboIndex++
                            gl.uniformBlockBinding(program, blockIndex, uboBinding)
                            intArrayOf(uboBinding)
                        } else {
                            // binding does not describe an actual UBO but plain old uniforms
                            compatUbos += binding.name
                            binding.uniforms.map { gl.getUniformLocation(program, it.name) }.toIntArray()
                        }
                        mapUbo(binding, locations)
                    }
                    is Texture1dLayout -> {
                        mapTexture1d(binding, getUniformLocations(binding.name, binding.arraySize))
                    }
                    is Texture2dLayout -> {
                        mapTexture2d(binding, getUniformLocations(binding.name, binding.arraySize))
                    }
                    is Texture3dLayout -> {
                        mapTexture3d(binding, getUniformLocations(binding.name, binding.arraySize))
                    }
                    is TextureCubeLayout -> {
                        mapTextureCube(binding, getUniformLocations(binding.name, binding.arraySize))
                    }
                    is StorageTexture1dLayout -> {
                        checkStorageTexSupport()
                        mapStorage1d(binding, storageIndex++)
                    }
                    is StorageTexture2dLayout -> {
                        checkStorageTexSupport()
                        mapStorage2d(binding, storageIndex++)
                    }
                    is StorageTexture3dLayout -> {
                        checkStorageTexSupport()
                        mapStorage3d(binding, storageIndex++)
                    }
                }
            }
        }

        fun bindUniforms(bindGroupData: BindGroupData, renderPass: RenderPass): Boolean {
            if (bindGroupData.gpuData == null) {
                bindGroupData.gpuData = GlBindGroupData(bindGroupData, renderPass, backend)
            }
            val glData = bindGroupData.gpuData as GlBindGroupData

            var uniformsValid = true
            for (i in mappings.indices) {
                uniformsValid = uniformsValid && mappings[i].setUniform(glData, renderPass)
            }
            return uniformsValid
        }

        private fun mapUbo(ubo: UniformBufferLayout, locations: IntArray) {
            mappings += if (ubo.name !in compatUbos) {
                MappedUbo(ubo, locations[0], backend)
            } else {
                MappedUboCompat(ubo, locations, gl)
            }
        }

        private fun mapTexture1d(tex: Texture1dLayout, locations: IntArray) {
            mappings += MappedUniformTex1d(tex, texUnit, locations, backend)
            texUnit += locations.size
        }

        private fun mapTexture2d(tex: Texture2dLayout, locations: IntArray) {
            mappings += MappedUniformTex2d(tex, texUnit, locations, backend)
            texUnit += locations.size
        }

        private fun mapTexture3d(tex: Texture3dLayout, locations: IntArray) {
            mappings += MappedUniformTex3d(tex, texUnit, locations, backend)
            texUnit += locations.size
        }

        private fun mapTextureCube(cubeMap: TextureCubeLayout, locations: IntArray) {
            mappings += MappedUniformTexCube(cubeMap, texUnit, locations, backend)
            texUnit += locations.size
        }

        private fun mapStorage1d(storage: StorageTexture1dLayout, location: Int) {
            mappings += MappedUniformStorage1d(storage, location, backend)
        }

        private fun mapStorage2d(storage: StorageTexture2dLayout, location: Int) {
            mappings += MappedUniformStorage2d(storage, location, backend)
        }

        private fun mapStorage3d(storage: StorageTexture3dLayout, location: Int) {
            mappings += MappedUniformStorage3d(storage, location, backend)
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

        private fun checkStorageTexSupport() {
            check(backend.gl.version.isHigherOrEqualThan(4, 2)) {
                "Storage textures require OpenGL 4.2 or higher"
            }
        }
    }

    class GlBindGroupData(val bindGroupData: BindGroupData, renderPass: RenderPass, backend: RenderBackendGl) : BaseReleasable(),
        GpuBindGroupData {
        val buffers: List<BufferResource?> = bindGroupData.bindings.map {
            if (it is BindGroupData.UniformBufferBindingData) {
                val bufferCreationInfo = BufferCreationInfo(
                    bufferName = "bindGroup[${bindGroupData.layout.scope}]-ubo-${it.binding.name}",
                    renderPassName = renderPass.name,
                    sceneName = renderPass.parentScene?.name ?: "scene:<null>"
                )
                BufferResource(backend.gl.UNIFORM_BUFFER, backend, bufferCreationInfo)
            } else {
                null
            }
        }
    }
}
