package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glGetTexImage
import org.lwjgl.opengl.GL43.glCopyImageSubData

internal object TextureCopyHelper {

    fun readTexturePixels(src: LoadedTextureGl, dst: TextureData) {
        glBindTexture(src.target, src.glTexture.handle)
        when (val buf = dst.data) {
            is Uint8BufferImpl -> buf.useRaw {
                glGetTexImage(src.target, 0, dst.format.glFormat(GlImpl), dst.format.glType(GlImpl), it)
            }
            is Float32BufferImpl -> buf.useRaw {
                glGetTexImage(src.target, 0, dst.format.glFormat(GlImpl), dst.format.glType(GlImpl), it)
            }
            else -> throw IllegalArgumentException("Unsupported target buffer type")
        }
    }

    fun copyTexturesFast(renderPass: OffscreenRenderPassCubeGl, backend: RenderBackendGlImpl) {
        val pass = renderPass.parent
        for (i in pass.copyTargetsColor.indices) {
            val copyTarget = pass.copyTargetsColor[i]
            var width = copyTarget.loadedTexture?.width ?: 0
            var height = copyTarget.loadedTexture?.height ?: 0
            if (width != pass.width || height != pass.height) {
                copyTarget.loadedTexture?.release()
                copyTarget.createCopyTexColor(pass, backend)
                width = copyTarget.loadedTexture!!.width
                height = copyTarget.loadedTexture!!.height
            }

            val target = copyTarget.loadedTexture as LoadedTextureGl
            for (mipLevel in 0 until pass.mipLevels) {
                if (pass.colorAttachment is OffscreenRenderPass.TextureColorAttachment) {
                    glCopyImageSubData(renderPass.glColorTex.handle, GlImpl.TEXTURE_CUBE_MAP, mipLevel, 0, 0, 0,
                        target.glTexture.handle, GlImpl.TEXTURE_CUBE_MAP, mipLevel, 0, 0, 0, width, height, 6)
                } else {
                    throw IllegalStateException("Cubemap color copy from renderbuffer is not supported")
                }

                width = width shr 1
                height = height shr 1
            }
        }
    }

    fun copyTexturesFast(renderPass: OffscreenRenderPass2dGl, backend: RenderBackendGlImpl) {
        val pass = renderPass.parent
        for (i in pass.copyTargetsColor.indices) {
            val copyTarget = pass.copyTargetsColor[i]
            var width = copyTarget.loadedTexture?.width ?: 0
            var height = copyTarget.loadedTexture?.height ?: 0
            if (width != pass.width || height != pass.height) {
                // recreate target texture if size has changed
                copyTarget.loadedTexture?.release()
                copyTarget.createCopyTexColor(pass, backend)
                width = copyTarget.loadedTexture!!.width
                height = copyTarget.loadedTexture!!.height
            }

            val target = copyTarget.loadedTexture as LoadedTextureGl
            for (mipLevel in 0 until pass.mipLevels) {
                if (pass.colorAttachment is OffscreenRenderPass.TextureColorAttachment) {
                    glCopyImageSubData(renderPass.colorTextures[0].handle, GlImpl.TEXTURE_2D, mipLevel, 0, 0, 0,
                            target.glTexture.handle, GlImpl.TEXTURE_2D, mipLevel, 0, 0, 0, width, height, 1)
                } else {
                    glCopyImageSubData(renderPass.rbos[mipLevel].handle, GlImpl.RENDERBUFFER, 0, 0, 0, 0,
                            target.glTexture.handle, GlImpl.TEXTURE_2D, mipLevel, 0, 0, 0, width, height, 1)
                }
                width = width shr 1
                height = height shr 1
            }
        }
    }

    private fun Texture2d.createCopyTexColor(pass: OffscreenRenderPass2d, backend: RenderBackendGlImpl) {
        val gl = backend.gl
        val intFormat = props.format.glInternalFormat(gl)
        val width = pass.width
        val height = pass.height
        val mipLevels = pass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, props.format.pxSize).toLong()
        val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, this, estSize)
        tex.setSize(width, height, 1)
        tex.applySamplerProps(props)
        gl.texStorage2D(gl.TEXTURE_2D, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }

    private fun TextureCube.createCopyTexColor(pass: OffscreenRenderPassCube, backend: RenderBackendGlImpl) {
        val gl = backend.gl
        val intFormat = props.format.glInternalFormat(gl)
        val width = pass.width
        val height = pass.height
        val mipLevels = pass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 6, mipLevels, props.format.pxSize).toLong()
        val tex = LoadedTextureGl(gl.TEXTURE_CUBE_MAP, gl.createTexture(), backend, this, estSize)
        tex.setSize(width, height, 1)
        tex.applySamplerProps(props)
        gl.texStorage2D(gl.TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }
}
