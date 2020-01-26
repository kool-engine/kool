package de.fabmax.kool.platform.webgl

import de.fabmax.kool.TextureData
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.logE
import kotlinx.coroutines.Deferred
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLUniformLocation
import org.khronos.webgl.set

abstract class MappedUniform(val location: WebGLUniformLocation?) {
    abstract fun setUniform(ctx: JsContext)

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

class MappedUniform1f(val uniform: Uniform1f, location: WebGLUniformLocation?) : MappedUniform(location) {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform1f(location, uniform.value)
    }
}

class MappedUniform2f(val uniform: Uniform2f, location: WebGLUniformLocation?) : MappedUniform(location) {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform2f(location, uniform.value.x, uniform.value.y)
    }
}

class MappedUniform3f(val uniform: Uniform3f, location: WebGLUniformLocation?) : MappedUniform(location) {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform3f(location, uniform.value.x, uniform.value.y, uniform.value.z)
    }
}

class MappedUniform4f(val uniform: Uniform4f, location: WebGLUniformLocation?) : MappedUniform(location) {
    override fun setUniform(ctx: JsContext) {
        ctx.gl.uniform4f(location, uniform.value.x, uniform.value.y, uniform.value.z, uniform.value.w)
    }
}

class MappedUniformMat3f(val uniform: UniformMat3f, location: WebGLUniformLocation?) : MappedUniform(location) {
    private val buf = Float32Array(9)
    override fun setUniform(ctx: JsContext) {
        for (i in 0..8) {
            buf[i] = uniform.value.matrix[i]
        }
        ctx.gl.uniformMatrix3fv(location, false, buf)
    }
}

class MappedUniformMat4f(val uniform: UniformMat4f, location: WebGLUniformLocation?) : MappedUniform(location) {
    private val buf = Float32Array(16)
    override fun setUniform(ctx: JsContext) {
        for (i in 0..15) {
            buf[i] = uniform.value.matrix[i]
        }
        ctx.gl.uniformMatrix4fv(location, false, buf)
    }
}

class MappedUniformTex2d(val sampler2d: TextureSampler, val texUnit: Int, location: WebGLUniformLocation?) : MappedUniform(location) {
    private val loadingTextures = mutableListOf<Deferred<TextureData>>()

    override fun setUniform(ctx: JsContext) {
        val gl = ctx.gl
        val texture = sampler2d.texture ?: return

        if (texture.loadingState == Texture.LoadingState.NOT_LOADED) {
            texture.loader?.let { loader ->
                texture.loadingState = Texture.LoadingState.LOADING
                val defTex = ctx.assetMgr.loadTextureAsync(loader)
                defTex.invokeOnCompletion { ex ->
                    if (ex != null) {
                        logE { "Texture loading failed: $ex" }
                        texture.loadingState = Texture.LoadingState.LOADING_FAILED
                    } else {
                        texture.loadedTexture = TextureLoader.loadTexture(ctx, defTex.getCompleted())
                        texture.loadingState = Texture.LoadingState.LOADED
                    }
                }
            }
        } else if (texture.loadingState == Texture.LoadingState.LOADED) {
            val tex = texture.loadedTexture!!
            gl.activeTexture(texUnit)
            gl.bindTexture(TEXTURE_2D, tex.texture)
        }
    }

    companion object {
        // todo: integrate texture manager
        private val loadedTextures = mutableMapOf<TextureData, LoadedTexture>()

        private fun getLoadedTex(texData: TextureData, ctx: JsContext): LoadedTexture {
            loadedTextures.values.removeAll { it.isDestroyed }
            return loadedTextures.getOrPut(texData) {
                val loaded = TextureLoader.loadTexture(ctx, texData)
                loaded
            }
        }
    }
}

class MappedUniformCubeMap(val samplerCube: CubeMapSampler, location: WebGLUniformLocation?) : MappedUniform(location) {
    override fun setUniform(ctx: JsContext) {
        samplerCube.texture?.let {
            //gl.uniform1i(location, it.)
        }
    }
}