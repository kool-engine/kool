package de.fabmax.kool.platform.webgl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.logE
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE0
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP
import org.khronos.webgl.WebGLUniformLocation
import org.khronos.webgl.set

interface MappedUniform {
    //val location: WebGLUniformLocation?

    fun setUniform(ctx: JsContext): Boolean

    companion object {
        fun mappedUniform(uniform: Uniform<*>, location: WebGLUniformLocation?): MappedUniform {
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

class MappedUniform1f(val uniform: Uniform1f, val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext): Boolean {
        ctx.gl.uniform1f(location, uniform.value)
        return true
    }
}

class MappedUniform2f(val uniform: Uniform2f, val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext): Boolean {
        ctx.gl.uniform2f(location, uniform.value.x, uniform.value.y)
        return true
    }
}

class MappedUniform3f(val uniform: Uniform3f, val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext): Boolean {
        ctx.gl.uniform3f(location, uniform.value.x, uniform.value.y, uniform.value.z)
        return true
    }
}

class MappedUniform4f(val uniform: Uniform4f, val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext): Boolean {
        ctx.gl.uniform4f(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
        return true
    }
}

class MappedUniform1fv(val uniform: Uniform1fv, val location: WebGLUniformLocation?) : MappedUniform {
    private val buffer = Float32Array(uniform.length)
    override fun setUniform(ctx: JsContext): Boolean {
        for (i in 0 until uniform.length) {
            buffer[i] = uniform.value[i]
        }
        ctx.gl.uniform1fv(location, buffer)
        return true
    }
}

class MappedUniform2fv(val uniform: Uniform2fv, val location: WebGLUniformLocation?) : MappedUniform {
    private val buffer = Float32Array(2 * uniform.length)
    override fun setUniform(ctx: JsContext): Boolean {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
        }
        ctx.gl.uniform2fv(location, buffer)
        return true
    }
}

class MappedUniform3fv(val uniform: Uniform3fv, val location: WebGLUniformLocation?) : MappedUniform {
    private val buffer = Float32Array(3 * uniform.length)
    override fun setUniform(ctx: JsContext): Boolean {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
            buffer[j++] = uniform.value[i].z
        }
        ctx.gl.uniform3fv(location, buffer)
        return true
    }
}

class MappedUniform4fv(val uniform: Uniform4fv, val location: WebGLUniformLocation?) : MappedUniform {
    private val buffer = Float32Array(4 * uniform.length)
    override fun setUniform(ctx: JsContext): Boolean {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
            buffer[j++] = uniform.value[i].z
            buffer[j++] = uniform.value[i].w
        }
        ctx.gl.uniform4fv(location, buffer)
        return true
    }
}

class MappedUniformColor(val uniform: UniformColor, val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext): Boolean {
        ctx.gl.uniform4f(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
        return true
    }
}

class MappedUniformMat3f(val uniform: UniformMat3f, val location: WebGLUniformLocation?) : MappedUniform {
    private val buf = Float32Array(9)
    override fun setUniform(ctx: JsContext): Boolean {
        for (i in 0..8) {
            buf[i] = uniform.value.matrix[i]
        }
        ctx.gl.uniformMatrix3fv(location, false, buf)
        return true
    }
}

class MappedUniformMat4f(val uniform: UniformMat4f, val location: WebGLUniformLocation?) : MappedUniform {
    private val buf = Float32Array(16)
    override fun setUniform(ctx: JsContext): Boolean {
        for (i in 0..15) {
            buf[i] = uniform.value.matrix[i]
        }
        ctx.gl.uniformMatrix4fv(location, false, buf)
        return true
    }
}

class MappedUniformMat4fv(val uniform: UniformMat4fv, val location: WebGLUniformLocation?) : MappedUniform {
    private val buf = Float32Array(16 * uniform.length)
    override fun setUniform(ctx: JsContext): Boolean {
        var bufI = 0
        for (i in 0 until uniform.length) {
            for (j in 0 until 16) {
                buf[bufI++] = uniform.value[i].matrix[j]
            }
        }
        ctx.gl.uniformMatrix4fv(location, false, buf)
        return true
    }
}

class MappedUniform1i(val uniform: Uniform1i, val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext): Boolean {
        ctx.gl.uniform1i(location, uniform.value)
        return true
    }
}

abstract class MappedUniformTex(val texUnit: Int, val target: Int) : MappedUniform {
    protected fun checkLoadingState(ctx: JsContext, texture: Texture, arrayIdx: Int): Boolean {
        val gl = ctx.gl
        if (texture.loadingState == Texture.LoadingState.NOT_LOADED) {
            texture.loader?.let { loader ->
                texture.loadingState = Texture.LoadingState.LOADING
                val defTex = ctx.assetMgr.loadTextureAsync(loader)
                defTex.invokeOnCompletion { ex ->
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
        if (texture.loadingState == Texture.LoadingState.LOADED) {
            val tex = texture.loadedTexture!!
            gl.activeTexture(texUnit + arrayIdx)
            gl.bindTexture(target, tex.texture)
            return true
        }

        return false
    }

    companion object {
        // todo: integrate texture manager
        private val loadedTextures = mutableMapOf<TextureData, LoadedTexture>()

        protected fun getLoadedTex(texData: TextureData, props: TextureProps, ctx: JsContext): LoadedTexture {
            loadedTextures.values.removeAll { it.isDestroyed }
            return loadedTextures.getOrPut(texData) {
                val loaded = TextureLoader.loadTexture(ctx, props, texData)
                loaded
            }
        }
    }
}

class MappedUniformTex2d(private val sampler2d: TextureSampler, texUnit: Int, val locations: List<WebGLUniformLocation?>) :
        MappedUniformTex(texUnit, TEXTURE_2D) {
    override fun setUniform(ctx: JsContext): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until sampler2d.arraySize) {
            val tex = sampler2d.textures[i]
            if (tex != null && checkLoadingState(ctx, tex, i)) {
                ctx.gl.uniform1i(locations[i], this.texUnit - TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}

class MappedUniformCubeMap(private val samplerCube: CubeMapSampler, texUnit: Int, val locations: List<WebGLUniformLocation?>) :
        MappedUniformTex(texUnit, TEXTURE_CUBE_MAP) {
    override fun setUniform(ctx: JsContext): Boolean {
        var texUnit = texUnit
        var isValid = true
        for (i in 0 until samplerCube.arraySize) {
            val tex = samplerCube.textures[i]
            if (tex != null && checkLoadingState(ctx, tex, i)) {
                ctx.gl.uniform1i(locations[i], this.texUnit - TEXTURE0 + i)
            } else {
                isValid = false
            }
            texUnit++
        }
        return isValid
    }
}