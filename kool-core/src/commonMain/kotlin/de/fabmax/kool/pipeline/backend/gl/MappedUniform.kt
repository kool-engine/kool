package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.RenderLoop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface MappedUniform {
    fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean
}

class MappedUbo(val ubo: UniformBufferLayout, val uboIndex: Int, val backend: RenderBackendGl) : MappedUniform {
    val gl: GlApi get() = backend.gl

    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        val uboData = glData.bindGroupData.uniformBufferBindingData(ubo.bindingIndex)
        val gpuBuffer = checkNotNull(glData.buffers[ubo.bindingIndex])
        if (uboData.getAndClearDirtyFlag()) {
            gpuBuffer.setData(uboData.buffer, gl.DYNAMIC_DRAW)
        }
        gl.bindBufferBase(gl.UNIFORM_BUFFER, uboIndex, gpuBuffer.buffer)
        return true
    }
}

class MappedUboCompat(val ubo: UniformBufferLayout, val locations: IntArray, val gl: GlApi) : MappedUniform {
    private val floatBuffers = buildList {
        ubo.uniforms.forEach {
            val bufferSize = if (it.isArray) {
                when (it.type) {
                    GpuType.FLOAT1 ->  1 * it.arraySize
                    GpuType.FLOAT2 ->  2 * it.arraySize
                    GpuType.FLOAT3 ->  3 * it.arraySize
                    GpuType.FLOAT4 ->  4 * it.arraySize
                    GpuType.MAT3   ->  9 * it.arraySize
                    GpuType.MAT4   -> 16 * it.arraySize
                    else           ->  1
                }
            } else {
                when (it.type) {
                    GpuType.MAT3   ->  9
                    GpuType.MAT4   -> 16
                    else           ->  1
                }
            }
            add(Float32Buffer(bufferSize))
        }
    }

    private val intBuffers = buildList {
        ubo.uniforms.forEach {
            val bufferSize = if (it.isArray) {
                when (it.type) {
                    GpuType.INT1 -> 1 * it.arraySize
                    GpuType.INT2 -> 2 * it.arraySize
                    GpuType.INT3 -> 3 * it.arraySize
                    GpuType.INT4 -> 4 * it.arraySize
                    else         -> 1
                }
            } else { 1 }
            add(Int32Buffer(bufferSize))
        }
    }

    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        ubo.uniforms.forEachIndexed { i, uniform ->
            val loc = locations[i]
            val buf = glData.bindGroupData.uniformBufferBindingData(ubo.bindingIndex).buffer
            val pos = ubo.layout.uniformPositions[uniform.name]!!.byteIndex

            if (uniform.isArray) {
                when (uniform.type) {
                    GpuType.FLOAT1 -> gl.uniform1fv(loc, floatBuffers[i].copyPadded(buf, pos, 1, uniform.arraySize))
                    GpuType.FLOAT2 -> gl.uniform2fv(loc, floatBuffers[i].copyPadded(buf, pos, 2, uniform.arraySize))
                    GpuType.FLOAT3 -> gl.uniform3fv(loc, floatBuffers[i].copyPadded(buf, pos, 3, uniform.arraySize))
                    GpuType.FLOAT4 -> gl.uniform4fv(loc, floatBuffers[i].copyPadded(buf, pos, 4, uniform.arraySize))
                    GpuType.INT1 -> gl.uniform1iv(loc, intBuffers[i].copyPadded(buf, pos, 1, uniform.arraySize))
                    GpuType.INT2 -> gl.uniform2iv(loc, intBuffers[i].copyPadded(buf, pos, 2, uniform.arraySize))
                    GpuType.INT3 -> gl.uniform3iv(loc, intBuffers[i].copyPadded(buf, pos, 3, uniform.arraySize))
                    GpuType.INT4 -> gl.uniform4iv(loc, intBuffers[i].copyPadded(buf, pos, 4, uniform.arraySize))
                    GpuType.MAT2 -> gl.uniformMatrix2fv(loc, floatBuffers[i].copyPadded(buf, pos, 2, 2 * uniform.arraySize))
                    GpuType.MAT3 -> gl.uniformMatrix3fv(loc, floatBuffers[i].copyPadded(buf, pos, 3, 3 * uniform.arraySize))
                    GpuType.MAT4 -> gl.uniformMatrix4fv(loc, floatBuffers[i].copyPadded(buf, pos, 4, 4 * uniform.arraySize))
                }
            } else {
                when (uniform.type) {
                    GpuType.FLOAT1 -> gl.uniform1f(loc, buf.getFloat32(pos))
                    GpuType.FLOAT2 -> gl.uniform2f(loc, buf.getFloat32(pos), buf.getFloat32(pos + 4))
                    GpuType.FLOAT3 -> gl.uniform3f(loc, buf.getFloat32(pos), buf.getFloat32(pos + 4), buf.getFloat32(pos + 8))
                    GpuType.FLOAT4 -> gl.uniform4f(loc, buf.getFloat32(pos), buf.getFloat32(pos + 4), buf.getFloat32(pos + 8), buf.getFloat32(pos + 12))
                    GpuType.INT1 -> gl.uniform1i(loc, buf.getInt32(pos))
                    GpuType.INT2 -> gl.uniform2i(loc, buf.getInt32(pos), buf.getInt32(pos + 4))
                    GpuType.INT3 -> gl.uniform3i(loc, buf.getInt32(pos), buf.getInt32(pos + 4), buf.getInt32(pos + 8))
                    GpuType.INT4 -> gl.uniform4i(loc, buf.getInt32(pos), buf.getInt32(pos + 4), buf.getInt32(pos + 8), buf.getInt32(pos + 12))
                    GpuType.MAT2 -> gl.uniformMatrix2fv(loc, floatBuffers[i].copyPadded(buf, pos, 2, 2))
                    GpuType.MAT3 -> gl.uniformMatrix3fv(loc, floatBuffers[i].copyPadded(buf, pos, 3, 3))
                    GpuType.MAT4 -> gl.uniformMatrix4fv(loc, floatBuffers[i].copyPadded(buf, pos, 4, 4))
                }
            }
        }
        return true
    }

    private fun Float32Buffer.copyPadded(src: MixedBuffer, start: Int, values: Int, count: Int = 1): Float32Buffer {
        var pSrc = start
        var pDst = 0
        for (i in 0 until count) {
            for (iVal in 0 until values) {
                set(pDst++, src.getFloat32(pSrc))
                pSrc += 4
            }
            pSrc += 4 * (4 - values)
        }
        return this
    }

    private fun Int32Buffer.copyPadded(src: MixedBuffer, start: Int, values: Int, count: Int = 1): Int32Buffer {
        var pSrc = start
        var pDst = 0
        for (i in 0 until count) {
            for (iVal in 0 until values) {
                set(pDst++, src.getInt32(pSrc))
                pSrc += 4
            }
            pSrc += 4 * (4 - values)
        }
        return this
    }
}

sealed class MappedUniformTex(val texUnit: Int, val target: Int, val backend: RenderBackendGl) : MappedUniform {
    protected val gl = backend.gl

    protected fun checkLoadingState(texture: Texture, arrayIdx: Int): Boolean {
        texture.checkIsNotReleased()

        if (texture.loadingState == Texture.LoadingState.NOT_LOADED) {
            when (texture.loader) {
                is AsyncTextureLoader -> {
                    texture.loadingState = Texture.LoadingState.LOADING
                    CoroutineScope(Dispatchers.RenderLoop).launch {
                        val texData = texture.loader.loadTextureDataAsync().await()
                        texture.loadedTexture = getLoadedTex(texData, texture, backend)
                        texture.loadingState = Texture.LoadingState.LOADED
                    }
                }
                is SyncTextureLoader -> {
                    val data = texture.loader.loadTextureDataSync()
                    texture.loadedTexture = getLoadedTex(data, texture, backend)
                    texture.loadingState = Texture.LoadingState.LOADED
                }
                is BufferedTextureLoader -> {
                    texture.loadedTexture = getLoadedTex(texture.loader.data, texture, backend)
                    texture.loadingState = Texture.LoadingState.LOADED
                }
                else -> {
                    // loader is null
                    texture.loadingState = Texture.LoadingState.LOADING_FAILED
                }
            }
        }
        if (texture.loadingState == Texture.LoadingState.LOADED) {
            val tex = texture.loadedTexture as LoadedTextureGl
            gl.activeTexture(texUnit + arrayIdx)
            tex.bind()
            tex.applySamplerSettings(null)
            return true
        }

        return false
    }

    companion object {
        private val loadedTextures = mutableMapOf<TextureData, LoadedTextureGl>()

        internal fun getLoadedTex(texData: TextureData, texture: Texture, backend: RenderBackendGl): LoadedTextureGl {
            loadedTextures.values.removeAll { it.isReleased }
            return loadedTextures.getOrPut(texData) {
                val loaded = when (texture) {
                    is StorageTexture1d -> TextureLoaderGl.loadTexture1d(texture, texData, backend)
                    is Texture1d -> TextureLoaderGl.loadTexture1dCompat(texture, texData, backend)
                    is Texture2d -> TextureLoaderGl.loadTexture2d(texture, texData, backend)
                    is Texture3d -> TextureLoaderGl.loadTexture3d(texture, texData, backend)
                    is TextureCube -> TextureLoaderGl.loadTextureCube(texture, texData as TextureDataCube, backend)
                    else -> throw IllegalArgumentException("Unsupported texture type")
                }
                loaded
            }
        }
    }
}

class MappedUniformTex1d(private val sampler1d: Texture1dLayout, texUnit: Int, val locations: IntArray, backend: RenderBackendGl) :
    MappedUniformTex(texUnit, backend.gl.TEXTURE_2D, backend)
{
    // 1d texture internally uses a 2d texture to be compatible with glsl version 300 es

    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        var texUnit = texUnit
        var isValid = true
        val textures = glData.bindGroupData.texture1dBindingData(sampler1d.bindingIndex).textures
        for (i in 0 until sampler1d.arraySize) {
            val tex = textures[i]
            if (tex != null && checkLoadingState(tex, i)) {
                gl.uniform1i(locations[i], this.texUnit - gl.TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}

class MappedUniformTex2d(private val sampler2d: Texture2dLayout, texUnit: Int, val locations: IntArray, backend: RenderBackendGl) :
    MappedUniformTex(texUnit, backend.gl.TEXTURE_2D, backend)
{
    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        var texUnit = texUnit
        var isValid = true
        val textures = glData.bindGroupData.texture2dBindingData(sampler2d.bindingIndex).textures
        for (i in 0 until sampler2d.arraySize) {
            val tex = textures[i]
            if (tex != null && checkLoadingState(tex, i)) {
                gl.uniform1i(locations[i], this.texUnit - gl.TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}

class MappedUniformTex3d(private val sampler3d: Texture3dLayout, texUnit: Int, val locations: IntArray, backend: RenderBackendGl) :
    MappedUniformTex(texUnit, backend.gl.TEXTURE_3D, backend)
{
    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        var texUnit = texUnit
        var isValid = true
        val textures = glData.bindGroupData.texture3dBindingData(sampler3d.bindingIndex).textures
        for (i in 0 until sampler3d.arraySize) {
            val tex = textures[i]
            if (tex != null && checkLoadingState(tex, i)) {
                gl.uniform1i(locations[i], this.texUnit - gl.TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}

class MappedUniformTexCube(private val samplerCube: TextureCubeLayout, texUnit: Int, val locations: IntArray, backend: RenderBackendGl) :
    MappedUniformTex(texUnit, backend.gl.TEXTURE_CUBE_MAP, backend)
{
    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        var texUnit = texUnit
        var isValid = true
        val textures = glData.bindGroupData.textureCubeBindingData(samplerCube.bindingIndex).textures
        for (i in 0 until samplerCube.arraySize) {
            val tex = textures[i]
            if (tex != null && checkLoadingState(tex, i)) {
                gl.uniform1i(locations[i], this.texUnit - gl.TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}

sealed class MappedUniformStorage(
    private val storage: StorageTextureLayout,
    private val binding: Int,
    private val backend: RenderBackendGl
) : MappedUniform {
    protected val gl = backend.gl

    protected fun bindStorageTex(storageTex: Texture): Boolean {
        val loadedTex = checkLoadingState(storageTex)
        gl.bindImageTexture(
            unit = binding,
            texture = loadedTex.glTexture,
            level = storage.level,
            layered = false,
            layer = 0,
            access = storage.accessType.glAccessType(gl),
            format = storage.format.glFormat(gl)
        )
        return true
    }

    private fun checkLoadingState(storageTex: Texture): LoadedTextureGl {
        if (storageTex.loadingState == Texture.LoadingState.NOT_LOADED) {
            val loader = storageTex.loader as BufferedTextureLoader
            storageTex.loadedTexture = MappedUniformTex.getLoadedTex(loader.data, storageTex, backend).also {
                it.bind()
                it.applySamplerSettings(storageTex.props.defaultSamplerSettings)
            }
            storageTex.loadingState = Texture.LoadingState.LOADED
        }
        return storageTex.loadedTexture as LoadedTextureGl
    }
}

class MappedUniformStorage1d(
    private val storage: StorageTexture1dLayout,
    binding: Int,
    backend: RenderBackendGl
) : MappedUniformStorage(storage, binding, backend) {
    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        val storageTex = glData.bindGroupData.storageTexture1dBindingData(storage.bindingIndex).storageTexture ?: return false
        bindStorageTex(storageTex)
        return true
    }
}

class MappedUniformStorage2d(
    private val storage: StorageTexture2dLayout,
    binding: Int,
    backend: RenderBackendGl
) : MappedUniformStorage(storage, binding, backend) {
    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        val storageTex = glData.bindGroupData.storageTexture2dBindingData(storage.bindingIndex).storageTexture ?: return false
        bindStorageTex(storageTex)
        return true
    }
}

class MappedUniformStorage3d(
    private val storage: StorageTexture3dLayout,
    binding: Int,
    backend: RenderBackendGl
) : MappedUniformStorage(storage, binding, backend) {
    override fun setUniform(glData: CompiledShader.GlBindGroupData, renderPass: RenderPass): Boolean {
        val storageTex = glData.bindGroupData.storageTexture3dBindingData(storage.bindingIndex).storageTexture ?: return false
        bindStorageTex(storageTex)
        return true
    }
}