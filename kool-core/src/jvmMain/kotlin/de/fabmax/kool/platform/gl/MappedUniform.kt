package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.createFloat32Buffer
import de.fabmax.kool.util.logE
import org.lwjgl.opengl.GL20.*

interface MappedUniform {
    fun setUniform(ctx: Lwjgl3Context): Boolean

    companion object {
        fun mappedUniform(uniform: Uniform<*>, location: Int): MappedUniform {
            return when (uniform) {
                is Uniform1f -> MappedUniform1f(uniform, location)
                is Uniform2f -> MappedUniform2f(uniform, location)
                is Uniform3f -> MappedUniform3f(uniform, location)
                is Uniform4f -> MappedUniform4f(uniform, location)
                is Uniform1fv -> MappedUniform1fv(uniform, location)
                is Uniform2fv -> MappedUniform2fv(uniform, location)
                is Uniform3fv -> MappedUniform3fv(uniform, location)
                is Uniform4fv -> MappedUniform4fv(uniform, location)
                is UniformColor -> MappedUniformColor(uniform, location)
                is UniformMat3f -> MappedUniformMat3f(uniform, location)
                is UniformMat4f -> MappedUniformMat4f(uniform, location)
                is UniformMat4fv -> MappedUniformMat4fv(uniform, location)

                is Uniform1i -> MappedUniform1i(uniform, location)
                else -> TODO("Uniform type mapping not implemented")
            }
        }
    }
}

class MappedUniform1f(val uniform: Uniform1f, val location: Int) : MappedUniform {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        glUniform1f(location, uniform.value)
        return true
    }
}

class MappedUniform2f(val uniform: Uniform2f, val location: Int) : MappedUniform {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        glUniform2f(location, uniform.value.x, uniform.value.y)
        return true
    }
}

class MappedUniform3f(val uniform: Uniform3f, val location: Int) : MappedUniform {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        glUniform3f(location, uniform.value.x, uniform.value.y, uniform.value.z)
        return true
    }
}

class MappedUniform4f(val uniform: Uniform4f, val location: Int) : MappedUniform {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        glUniform4f(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
        return true
    }
}

class MappedUniform1fv(val uniform: Uniform1fv, val location: Int) : MappedUniform {
    private val buffer = createFloat32Buffer(uniform.length) as Float32BufferImpl
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        for (i in 0 until uniform.length) {
            buffer[i] = uniform.value[i]
        }
        glUniform1fv(location, buffer.buffer)
        return true
    }
}

class MappedUniform2fv(val uniform: Uniform2fv, val location: Int) : MappedUniform {
    private val buffer = createFloat32Buffer(2 * uniform.length) as Float32BufferImpl
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
        }
        glUniform2fv(location, buffer.buffer)
        return true
    }
}

class MappedUniform3fv(val uniform: Uniform3fv, val location: Int) : MappedUniform {
    private val buffer = createFloat32Buffer(3 * uniform.length) as Float32BufferImpl
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
            buffer[j++] = uniform.value[i].z
        }
        glUniform3fv(location, buffer.buffer)
        return true
    }
}

class MappedUniform4fv(val uniform: Uniform4fv, val location: Int) : MappedUniform {
    private val buffer = createFloat32Buffer(4 * uniform.length) as Float32BufferImpl
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
            buffer[j++] = uniform.value[i].z
            buffer[j++] = uniform.value[i].w
        }
        glUniform4fv(location, buffer.buffer)
        return true
    }
}

class MappedUniformColor(val uniform: UniformColor, val location: Int) : MappedUniform {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        glUniform4f(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
        return true
    }
}

class MappedUniformMat3f(val uniform: UniformMat3f, val location: Int) : MappedUniform {
    private val buf = createFloat32Buffer(9) as Float32BufferImpl
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        for (i in 0..8) {
            buf[i] = uniform.value.matrix[i]
        }
        glUniformMatrix3fv(location, false, buf.buffer)
        return true
    }
}

class MappedUniformMat4f(val uniform: UniformMat4f, val location: Int) : MappedUniform {
    private val buf = createFloat32Buffer(16) as Float32BufferImpl
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        for (i in 0..15) {
            buf[i] = uniform.value.matrix[i]
        }
        glUniformMatrix4fv(location, false, buf.buffer)
        return true
    }
}

class MappedUniformMat4fv(val uniform: UniformMat4fv, val location: Int) : MappedUniform {
    private val buf = createFloat32Buffer(16 * uniform.length) as Float32BufferImpl
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        var bufI = 0
        for (i in 0 until uniform.length) {
            for (j in 0 until 16) {
                buf[bufI++] = uniform.value[i].matrix[j]
            }
        }
        glUniformMatrix4fv(location, false, buf.buffer)
        return true
    }
}

class MappedUniform1i(val uniform: Uniform1i, val location: Int) : MappedUniform {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        glUniform1i(location, uniform.value)
        return true
    }
}

abstract class MappedUniformTex(val texUnit: Int, val target: Int) : MappedUniform {
    protected fun checkLoadingState(ctx: Lwjgl3Context, texture: Texture, arrayIdx: Int): Boolean {
        if (texture.loadingState == Texture.LoadingState.NOT_LOADED) {
            texture.loader?.let { loader ->
                texture.loadingState = Texture.LoadingState.LOADING
                val defTex = ctx.assetMgr.loadTextureAsync(loader)
                defTex.invokeOnCompletion { ex ->
                    ctx.runOnMainThread {
                        if (ex != null) {
                            logE { "Texture loading failed: $ex" }
                            texture.loadingState = Texture.LoadingState.LOADING_FAILED
                        } else {
                            texture.loadedTexture = getLoadedTex(defTex.getCompleted(), texture.props, ctx)
                            texture.loadingState = Texture.LoadingState.LOADED
                        }
                    }
                }
            }

        }
        if (texture.loadingState == Texture.LoadingState.LOADED) {
            val tex = texture.loadedTexture as LoadedTextureGl
            glActiveTexture(texUnit + arrayIdx)
            glBindTexture(target, tex.texture)
            return true
        }

        return false
    }

    companion object {
        // todo: integrate texture manager
        private val loadedTextures = mutableMapOf<TextureData, LoadedTextureGl>()

        protected fun getLoadedTex(texData: TextureData, props: TextureProps, ctx: Lwjgl3Context): LoadedTextureGl {
            loadedTextures.values.removeAll { it.isDestroyed }
            return loadedTextures.getOrPut(texData) {
                val loaded = TextureLoader.loadTexture(ctx, props, texData)
                loaded
            }
        }
    }
}

class MappedUniformTex1d(private val sampler1d: TextureSampler1d, texUnit: Int, val locations: List<Int>) :
        MappedUniformTex(texUnit, GL_TEXTURE_2D) {
    // 1d texture internally uses a 2d texture to be compatible with glsl version 300 es

    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until sampler1d.arraySize) {
            val tex = sampler1d.textures[i]
            if (tex != null && checkLoadingState(ctx, tex, i)) {
                glUniform1i(locations[i], this.texUnit - GL_TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}

class MappedUniformTex2d(private val sampler2d: TextureSampler2d, texUnit: Int, val locations: List<Int>) :
        MappedUniformTex(texUnit, GL_TEXTURE_2D) {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until sampler2d.arraySize) {
            val tex = sampler2d.textures[i]
            if (tex != null && checkLoadingState(ctx, tex, i)) {
                glUniform1i(locations[i], this.texUnit - GL_TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}

class MappedUniformTex3d(private val sampler3d: TextureSampler3d, texUnit: Int, val locations: List<Int>) :
        MappedUniformTex(texUnit, GL_TEXTURE_3D) {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until sampler3d.arraySize) {
            val tex = sampler3d.textures[i]
            if (tex != null && checkLoadingState(ctx, tex, i)) {
                glUniform1i(locations[i], this.texUnit - GL_TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}

class MappedUniformTexCube(private val samplerCube: TextureSamplerCube, texUnit: Int, val locations: List<Int>) :
        MappedUniformTex(texUnit, GL_TEXTURE_CUBE_MAP) {
    override fun setUniform(ctx: Lwjgl3Context): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until samplerCube.arraySize) {
            val tex = samplerCube.textures[i]
            if (tex != null && checkLoadingState(ctx, tex, i)) {
                glUniform1i(locations[i], this.texUnit - GL_TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}