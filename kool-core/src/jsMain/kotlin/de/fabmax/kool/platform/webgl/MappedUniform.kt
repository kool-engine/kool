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
    val location: WebGLUniformLocation?

    fun setUniform(ctx: JsContext)

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

                is Uniform1i -> MappedUniform1i(uniform, location)
                else -> TODO("Uniform type mapping not implemented")
            }
        }
    }
}

class MappedUniform1f(val uniform: Uniform1f, override val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform1f(location, uniform.value)
    }
}

class MappedUniform2f(val uniform: Uniform2f, override val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform2f(location, uniform.value.x, uniform.value.y)
    }
}

class MappedUniform3f(val uniform: Uniform3f, override val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform3f(location, uniform.value.x, uniform.value.y, uniform.value.z)
    }
}

class MappedUniform4f(val uniform: Uniform4f, override val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform4f(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
    }
}

class MappedUniform1fv(val uniform: Uniform1fv, override val location: WebGLUniformLocation?) : MappedUniform {
    private val buffer = Float32Array(uniform.length)
    override fun setUniform(ctx: JsContext) {
        for (i in 0 until uniform.length) {
            buffer[i] = uniform.value[i]
        }
        ctx.gl.uniform1fv(location, buffer)
    }
}

class MappedUniform2fv(val uniform: Uniform2fv, override val location: WebGLUniformLocation?) : MappedUniform {
    private val buffer = Float32Array(2 * uniform.length)
    override fun setUniform(ctx: JsContext) {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
        }
        ctx.gl.uniform2fv(location, buffer)
    }
}

class MappedUniform3fv(val uniform: Uniform3fv, override val location: WebGLUniformLocation?) : MappedUniform {
    private val buffer = Float32Array(3 * uniform.length)
    override fun setUniform(ctx: JsContext) {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
            buffer[j++] = uniform.value[i].z
        }
        ctx.gl.uniform3fv(location, buffer)
    }
}

class MappedUniform4fv(val uniform: Uniform4fv, override val location: WebGLUniformLocation?) : MappedUniform {
    private val buffer = Float32Array(4 * uniform.length)
    override fun setUniform(ctx: JsContext) {
        var j = 0
        for (i in 0 until uniform.length) {
            buffer[j++] = uniform.value[i].x
            buffer[j++] = uniform.value[i].y
            buffer[j++] = uniform.value[i].z
            buffer[j++] = uniform.value[i].w
        }
        ctx.gl.uniform4fv(location, buffer)
    }
}

class MappedUniformColor(val uniform: UniformColor, override val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform4f(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
    }
}

class MappedUniformMat3f(val uniform: UniformMat3f, override val location: WebGLUniformLocation?) : MappedUniform {
    private val buf = Float32Array(9)
    override fun setUniform(ctx: JsContext) {
        for (i in 0..8) {
            buf[i] = uniform.value.matrix[i]
        }
        ctx.gl.uniformMatrix3fv(location, false, buf)
    }
}

class MappedUniformMat4f(val uniform: UniformMat4f, override val location: WebGLUniformLocation?) : MappedUniform {
    private val buf = Float32Array(16)
    override fun setUniform(ctx: JsContext) {
        for (i in 0..15) {
            buf[i] = uniform.value.matrix[i]
        }
        ctx.gl.uniformMatrix4fv(location, false, buf)
    }
}

class MappedUniform1i(val uniform: Uniform1i, override val location: WebGLUniformLocation?) : MappedUniform {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform1i(location, uniform.value)
    }
}

abstract class MappedUniformTex(val texUnit: Int, val target: Int) : MappedUniform {
    protected fun checkLoadingState(ctx: JsContext, texture: Texture): Boolean {
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
                        texture.loadedTexture = getLoadedTex(defTex.getCompleted(), ctx)
                        texture.loadingState = Texture.LoadingState.LOADED
                    }
                }
            }

        } else if (texture.loadingState == Texture.LoadingState.LOADED) {
            val tex = texture.loadedTexture!!
            gl.activeTexture(texUnit)
            gl.bindTexture(target, tex.texture)
            return true
        }

        return false
    }

    companion object {
        // todo: integrate texture manager
        private val loadedTextures = mutableMapOf<TextureData, LoadedTexture>()

        protected fun getLoadedTex(texData: TextureData, ctx: JsContext): LoadedTexture {
            loadedTextures.values.removeAll { it.isDestroyed }
            return loadedTextures.getOrPut(texData) {
                val loaded = TextureLoader.loadTexture(ctx, texData)
                loaded
            }
        }
    }
}

class MappedUniformTex2d(private val sampler2d: TextureSampler, texUnit: Int, override val location: WebGLUniformLocation?) :
        MappedUniformTex(texUnit, TEXTURE_2D) {
    override fun setUniform(ctx: JsContext) {
        //println("tex2d ${sampler2d.name}: $texUnit")
        sampler2d.texture?.let {
            if (checkLoadingState(ctx, it)) {
                ctx.gl.uniform1i(location, texUnit - TEXTURE0)
            }
        }
    }
}

class MappedUniformCubeMap(private val samplerCube: CubeMapSampler, texUnit: Int, override val location: WebGLUniformLocation?) :
        MappedUniformTex(texUnit, TEXTURE_CUBE_MAP) {
    override fun setUniform(ctx: JsContext) {
        //println("cube ${samplerCube.name}: $texUnit")
        samplerCube.texture?.let {
            if (checkLoadingState(ctx, it)) {
                ctx.gl.uniform1i(location, texUnit - TEXTURE0)
            }
        }
    }
}