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
    fun setUniform(): Boolean

    companion object {
        fun mappedUniform(uniform: Uniform<*>, location: Int, gl: GlApi): MappedUniform {
            return when (uniform) {
                is Uniform1f -> MappedUniform1f(uniform, location, gl)
                is Uniform2f -> MappedUniform2f(uniform, location, gl)
                is Uniform3f -> MappedUniform3f(uniform, location, gl)
                is Uniform4f -> MappedUniform4f(uniform, location, gl)
                is Uniform1fv -> MappedUniform1fv(uniform, location, gl)
                is Uniform2fv -> MappedUniform2fv(uniform, location, gl)
                is Uniform3fv -> MappedUniform3fv(uniform, location, gl)
                is Uniform4fv -> MappedUniform4fv(uniform, location, gl)
                is UniformMat3f -> MappedUniformMat3f(uniform, location, gl)
                is UniformMat3fv -> MappedUniformMat3fv(uniform, location, gl)
                is UniformMat4f -> MappedUniformMat4f(uniform, location, gl)
                is UniformMat4fv -> MappedUniformMat4fv(uniform, location, gl)

                is Uniform1i -> MappedUniform1i(uniform, location, gl)
                is Uniform2i -> MappedUniform2i(uniform, location, gl)
                is Uniform3i -> MappedUniform3i(uniform, location, gl)
                is Uniform4i -> MappedUniform4i(uniform, location, gl)
                is Uniform1iv -> MappedUniform1iv(uniform, location, gl)
                is Uniform2iv -> MappedUniform2iv(uniform, location, gl)
                is Uniform3iv -> MappedUniform3iv(uniform, location, gl)
                is Uniform4iv -> MappedUniform4iv(uniform, location, gl)
            }
        }
    }
}

class MappedUbo(val uboDesc: UniformBuffer, val layout: ExternalBufferLayout, val gl: GlApi) : MappedUniform {
    var uboBuffer: BufferResource? = null
    val hostBuffer = MixedBuffer(layout.size)

    override fun setUniform(): Boolean {
        val gpuBuf = uboBuffer
        return if (gpuBuf != null) {
            layout.putToBuffer(uboDesc.uniforms, hostBuffer)
            gpuBuf.setData(hostBuffer, gl.DYNAMIC_DRAW)
            gl.bindBufferBase(gl.UNIFORM_BUFFER, uboDesc.binding, gpuBuf.buffer)
            true
        } else {
            false
        }
    }
}

class MappedUniform1f(val uniform: Uniform1f, val location: Int, val gl: GlApi) : MappedUniform {
    override fun setUniform(): Boolean {
        gl.uniform1f(location, uniform.value)
        return true
    }
}

class MappedUniform2f(val uniform: Uniform2f, val location: Int, val gl: GlApi) : MappedUniform {
    override fun setUniform(): Boolean {
        gl.uniform2f(location, uniform.value.x, uniform.value.y)
        return true
    }
}

class MappedUniform3f(val uniform: Uniform3f, val location: Int, val gl: GlApi) : MappedUniform {
    override fun setUniform(): Boolean {
        gl.uniform3f(location, uniform.value.x, uniform.value.y, uniform.value.z)
        return true
    }
}

class MappedUniform4f(val uniform: Uniform4f, val location: Int, val gl: GlApi) : MappedUniform {
    override fun setUniform(): Boolean {
        gl.uniform4f(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
        return true
    }
}

class MappedUniform1fv(val uniform: Uniform1fv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Float32Buffer(uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        buffer.put(uniform.value)
        gl.uniform1fv(location, buffer)
        return true
    }
}

class MappedUniform2fv(val uniform: Uniform2fv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Float32Buffer(2 * uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        for (i in 0 until uniform.size) {
            uniform.value[i].putTo(buffer)
        }
        gl.uniform2fv(location, buffer)
        return true
    }
}

class MappedUniform3fv(val uniform: Uniform3fv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Float32Buffer(3 * uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        for (i in 0 until uniform.size) {
            uniform.value[i].putTo(buffer)
        }
        gl.uniform3fv(location, buffer)
        return true
    }
}

class MappedUniform4fv(val uniform: Uniform4fv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Float32Buffer(4 * uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        for (i in 0 until uniform.size) {
            uniform.value[i].putTo(buffer)
        }
        gl.uniform4fv(location, buffer)
        return true
    }
}

class MappedUniformMat3f(val uniform: UniformMat3f, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Float32Buffer(9)
    override fun setUniform(): Boolean {
        buffer.clear()
        uniform.value.putTo(buffer)
        gl.uniformMatrix3fv(location, buffer)
        return true
    }
}

class MappedUniformMat3fv(val uniform: UniformMat3fv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Float32Buffer(9 * uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        for (i in 0 until uniform.size) {
            uniform.value[i].putTo(buffer)
        }
        gl.uniformMatrix3fv(location, buffer)
        return true
    }
}

class MappedUniformMat4f(val uniform: UniformMat4f, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Float32Buffer(16)
    override fun setUniform(): Boolean {
        buffer.clear()
        uniform.value.putTo(buffer)
        gl.uniformMatrix4fv(location, buffer)
        return true
    }
}

class MappedUniformMat4fv(val uniform: UniformMat4fv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Float32Buffer(16 * uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        for (i in 0 until uniform.size) {
            uniform.value[i].putTo(buffer)
        }
        gl.uniformMatrix4fv(location, buffer)
        return true
    }
}

class MappedUniform1i(val uniform: Uniform1i, val location: Int, val gl: GlApi) : MappedUniform {
    override fun setUniform(): Boolean {
        gl.uniform1i(location, uniform.value)
        return true
    }
}

class MappedUniform2i(val uniform: Uniform2i, val location: Int, val gl: GlApi) : MappedUniform {
    override fun setUniform(): Boolean {
        gl.uniform2i(location, uniform.value.x, uniform.value.y)
        return true
    }
}

class MappedUniform3i(val uniform: Uniform3i, val location: Int, val gl: GlApi) : MappedUniform {
    override fun setUniform(): Boolean {
        gl.uniform3i(location, uniform.value.x, uniform.value.y, uniform.value.z)
        return true
    }
}

class MappedUniform4i(val uniform: Uniform4i, val location: Int, val gl: GlApi) : MappedUniform {
    override fun setUniform(): Boolean {
        gl.uniform4i(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
        return true
    }
}

class MappedUniform1iv(val uniform: Uniform1iv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Int32Buffer(uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        buffer.put(uniform.value)
        gl.uniform1iv(location, buffer)
        return true
    }
}

class MappedUniform2iv(val uniform: Uniform2iv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Int32Buffer(2 * uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        for (i in 0 until uniform.size) {
            uniform.value[i].putTo(buffer)
        }
        gl.uniform2iv(location, buffer)
        return true
    }
}

class MappedUniform3iv(val uniform: Uniform3iv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Int32Buffer(3 * uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        for (i in 0 until uniform.size) {
            uniform.value[i].putTo(buffer)
        }
        gl.uniform3iv(location, buffer)
        return true
    }
}

class MappedUniform4iv(val uniform: Uniform4iv, val location: Int, val gl: GlApi) : MappedUniform {
    private val buffer = Int32Buffer(4 * uniform.size)
    override fun setUniform(): Boolean {
        buffer.clear()
        for (i in 0 until uniform.size) {
            uniform.value[i].putTo(buffer)
        }
        gl.uniform4iv(location, buffer)
        return true
    }
}

abstract class MappedUniformTex(val texUnit: Int, val target: Int, val backend: RenderBackendGl) : MappedUniform {
    protected val gl = backend.gl

    protected fun checkLoadingState(texture: Texture, arrayIdx: Int): Boolean {
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
            gl.bindTexture(target, tex.glTexture)
            return true
        }

        return false
    }

    companion object {
        private val loadedTextures = mutableMapOf<TextureData, LoadedTextureGl>()

        protected fun getLoadedTex(texData: TextureData, texture: Texture, backend: RenderBackendGl): LoadedTextureGl {
            loadedTextures.values.removeAll { it.isDestroyed }
            return loadedTextures.getOrPut(texData) {
                val loaded = when (texture) {
                    is Texture1d -> TextureLoaderGl.loadTexture1d(texture, texData, backend)
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

class MappedUniformTex1d(private val sampler1d: TextureSampler1d, texUnit: Int, val locations: IntArray, backend: RenderBackendGl) :
    MappedUniformTex(texUnit, backend.gl.TEXTURE_2D, backend)
{
    // 1d texture internally uses a 2d texture to be compatible with glsl version 300 es

    override fun setUniform(): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until sampler1d.arraySize) {
            val tex = sampler1d.textures[i]
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

class MappedUniformTex2d(private val sampler2d: TextureSampler2d, texUnit: Int, val locations: IntArray, backend: RenderBackendGl) :
    MappedUniformTex(texUnit, backend.gl.TEXTURE_2D, backend)
{
    override fun setUniform(): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until sampler2d.arraySize) {
            val tex = sampler2d.textures[i]
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

class MappedUniformTex3d(private val sampler3d: TextureSampler3d, texUnit: Int, val locations: IntArray, backend: RenderBackendGl) :
    MappedUniformTex(texUnit, backend.gl.TEXTURE_3D, backend)
{
    override fun setUniform(): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until sampler3d.arraySize) {
            val tex = sampler3d.textures[i]
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

class MappedUniformTexCube(private val samplerCube: TextureSamplerCube, texUnit: Int, val locations: IntArray, backend: RenderBackendGl) :
    MappedUniformTex(texUnit, backend.gl.TEXTURE_CUBE_MAP, backend)
{
    override fun setUniform(): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until samplerCube.arraySize) {
            val tex = samplerCube.textures[i]
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