package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.logE

interface MappedUniform {
    fun setUniform(bindCtx: CompiledShader.UniformBindContext): Boolean
}

class MappedUbo(val ubo: BindGroupData.UniformBufferBindingData, val gpuBuffer: BufferResource, val backend: RenderBackendGl) : MappedUniform {
    val gl: GlApi get() = backend.gl
    private var modCount = -1

    override fun setUniform(bindCtx: CompiledShader.UniformBindContext): Boolean {
        if (modCount != ubo.modCount) {
            modCount = ubo.modCount
            gpuBuffer.setData(ubo.buffer, gl.DYNAMIC_DRAW)
        }
        gl.bindBufferBase(gl.UNIFORM_BUFFER, bindCtx.location(ubo.layout.bindingIndex), gpuBuffer.buffer)
        return true
    }
}

class MappedUboCompat(val ubo: BindGroupData.UniformBufferBindingData, val gl: GlApi) : MappedUniform {
    private val floatBuffers = buildList {
        ubo.layout.uniforms.forEach {
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
        ubo.layout.uniforms.forEach {
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

    override fun setUniform(bindCtx: CompiledShader.UniformBindContext): Boolean {
        ubo.layout.uniforms.forEachIndexed { i, uniform ->
            val loc = bindCtx.locations(ubo.layout.bindingIndex)[i]
            val buf = ubo.buffer
            val pos = ubo.layout.layout.uniformPositions[uniform.name]!!.byteIndex

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

sealed class MappedStorageBuffer<T: StorageBuffer>(val backend: RenderBackendGl) : MappedUniform {
    val gl = backend.gl

    protected abstract val ssbo: BindGroupData.StorageBufferBindingData<T>

    override fun setUniform(bindCtx: CompiledShader.UniformBindContext): Boolean {
        val storage = ssbo.storageBuffer ?: return false
        var gpuBuffer = storage.gpuBuffer as BufferResource?
        if (gpuBuffer == null) {
            val bufferCreationInfo = BufferCreationInfo(
                bufferName = storage.name,
                renderPassName = bindCtx.pass.name,
                sceneName = bindCtx.pass.parentScene?.name ?: "scene:<null>"
            )
            gpuBuffer = BufferResource(backend.gl.SHADER_STORAGE_BUFFER, backend, bufferCreationInfo)
            storage.gpuBuffer = gpuBuffer
        }

        if (ssbo.getAndClearDirtyFlag()) {
            ssbo.storageBuffer?.let {
                when (it.buffer) {
                    is Float32Buffer -> gpuBuffer.setData(it.buffer, gl.DYNAMIC_DRAW)
                    is Int32Buffer -> gpuBuffer.setData(it.buffer, gl.DYNAMIC_DRAW)
                    else -> error("Invalid buffer type")
                }
            }
        }
        gl.bindBufferBase(gl.SHADER_STORAGE_BUFFER, bindCtx.location(ssbo.layout.bindingIndex), gpuBuffer.buffer)
        return true
    }
}

class MappedStorageBuffer1d(override val ssbo: BindGroupData.StorageBuffer1dBindingData, backend: RenderBackendGl) :
    MappedStorageBuffer<StorageBuffer1d>(backend)

class MappedStorageBuffer2d(override val ssbo: BindGroupData.StorageBuffer2dBindingData, backend: RenderBackendGl) :
    MappedStorageBuffer<StorageBuffer2d>(backend)

class MappedStorageBuffer3d(override val ssbo: BindGroupData.StorageBuffer3dBindingData, backend: RenderBackendGl) :
    MappedStorageBuffer<StorageBuffer3d>(backend)

sealed class MappedUniformTex(val target: Int, val backend: RenderBackendGl) : MappedUniform {
    protected val gl = backend.gl

    protected abstract val sampler: BindGroupData.TextureBindingData<*>

    private fun <T: ImageData> checkLoadingState(texture: Texture<T>, texUnit: Int): Boolean {
        if (texture.isReleased) {
            logE { "Texture is already released: ${texture.name}" }
            return false
        }
        texture.uploadData?.let { TextureLoaderGl.loadTexture(texture, backend) }
        (texture.gpuTexture as LoadedTextureGl?)?.let { tex ->
            gl.activeTexture(gl.TEXTURE0 + texUnit)
            tex.bind()
            tex.applySamplerSettings(sampler.sampler)
            return true
        }
        return false
    }

    override fun setUniform(bindCtx: CompiledShader.UniformBindContext): Boolean {
        val texture = sampler.texture
        val texUnit = bindCtx.nextTexUnit++
        if (texture != null && checkLoadingState(texture, texUnit)) {
            gl.uniform1i(bindCtx.location(sampler.layout.bindingIndex), texUnit)
            return true
        }
        return false
    }
}

class MappedUniformTex1d(override val sampler: BindGroupData.Texture1dBindingData, backend: RenderBackendGl) :
    MappedUniformTex(backend.gl.TEXTURE_2D, backend) // 1d texture internally uses a 2d texture to be compatible with OpenGL ES

class MappedUniformTex2d(override val sampler: BindGroupData.Texture2dBindingData, backend: RenderBackendGl) :
    MappedUniformTex(backend.gl.TEXTURE_2D, backend)

class MappedUniformTex3d(override val sampler: BindGroupData.Texture3dBindingData, backend: RenderBackendGl) :
    MappedUniformTex(backend.gl.TEXTURE_3D, backend)

class MappedUniformTexCube(override val sampler: BindGroupData.TextureCubeBindingData, backend: RenderBackendGl) :
    MappedUniformTex(backend.gl.TEXTURE_CUBE_MAP, backend)

class MappedUniformTex2dArray(override val sampler: BindGroupData.Texture2dArrayBindingData, backend: RenderBackendGl) :
    MappedUniformTex(backend.gl.TEXTURE_2D_ARRAY, backend)

class MappedUniformTexCubeArray(override val sampler: BindGroupData.TextureCubeArrayBindingData, backend: RenderBackendGl) :
    MappedUniformTex(backend.gl.TEXTURE_CUBE_MAP_ARRAY, backend)

sealed class MappedStorageTexture<T: Texture<*>>(private val backend: RenderBackendGl) : MappedUniform {
    val gl = backend.gl

    protected abstract val storageTex: BindGroupData.StorageTextureBindingData<*>

    override fun setUniform(bindCtx: CompiledShader.UniformBindContext): Boolean {
        val texUnit = bindCtx.nextTexUnit++
        val texture = storageTex.storageTexture?.asTexture
        if (texture != null && checkLoadingState(texture)) {
            val glTex = texture.gpuTexture as LoadedTextureGl
            gl.bindImageTexture(
                unit = texUnit,
                texture = glTex.glTexture,
                level = storageTex.mipLevel,
                layered = false,
                layer = 0,
                access = storageTex.layout.accessType.glAccessType(gl),
                format = texture.format.glInternalFormat(gl)
            )
            gl.uniform1i(bindCtx.location(storageTex.layout.bindingIndex), texUnit)
            return true
        }
        return false
    }

    private fun checkLoadingState(texture: Texture<*>): Boolean {
        if (texture.isReleased) {
            logE { "Storage texture is already released: ${texture.name}" }
            return false
        }
        texture.uploadData?.let { TextureLoaderGl.loadTexture(texture, backend) }
        return true
    }
}

class MappedStorageTexture1d(override val storageTex: BindGroupData.StorageTexture1dBindingData, backend: RenderBackendGl) :
    MappedStorageTexture<Texture1d>(backend)

class MappedStorageTexture2d(override val storageTex: BindGroupData.StorageTexture2dBindingData, backend: RenderBackendGl) :
    MappedStorageTexture<Texture2d>(backend)

class MappedStorageTexture3d(override val storageTex: BindGroupData.StorageTexture3dBindingData, backend: RenderBackendGl) :
    MappedStorageTexture<Texture3d>(backend)
