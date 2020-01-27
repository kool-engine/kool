package de.fabmax.kool.platform.webgl

import de.fabmax.kool.TextureData
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.logE
import org.khronos.webgl.Float32Array
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
                is UniformMat3f -> MappedUniformMat3f(uniform, location)
                is UniformMat4f -> MappedUniformMat4f(uniform, location)
                else -> TODO()
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

abstract class MappedUniformTex(private val texUnit: Int, private val target: Int) : MappedUniform {
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
        sampler2d.texture?.let { checkLoadingState(ctx, it) }
    }
}

class MappedUniformCubeMap(private val samplerCube: CubeMapSampler, texUnit: Int, override val location: WebGLUniformLocation?) :
        MappedUniformTex(texUnit, TEXTURE_CUBE_MAP) {
    override fun setUniform(ctx: JsContext) {
        samplerCube.texture?.let { checkLoadingState(ctx, it) }
    }
}