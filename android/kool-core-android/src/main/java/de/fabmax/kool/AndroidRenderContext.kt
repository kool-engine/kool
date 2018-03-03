package de.fabmax.kool

import android.opengl.GLSurfaceView
import android.util.Log
import de.fabmax.kool.gl.GL_DEPTH_COMPONENT
import de.fabmax.kool.gl.GL_LINEAR
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by max on 03.03.2018.
 */
class AndroidRenderContext : RenderContext(), GLSurfaceView.Renderer {
    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set

    private var prevRenderTime = System.nanoTime()

    override fun run() {
        // nothing to do here
    }

    override fun destroy() {
        TODO("not implemented")
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glCapabilities = GlCapabilities(
                uint32Indices = true,
                shaderIntAttribs = false,
                depthTextures = false,
                depthComponentIntFormat = GL_DEPTH_COMPONENT,
                depthFilterMethod = GL_LINEAR,
                framebufferWithoutColor = true,
                glslDialect = GlslDialect.GLSL_DIALECT_100,
                glVersion = GlVersion("OpenGL ES", 2, 0),
                anisotropicTexFilterInfo = AnisotropicTexFilterInfo.NOT_SUPPORTED)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        windowWidth = width
        windowHeight = height
    }

    override fun onDrawFrame(gl: GL10) {
        // determine time delta
        val time = System.nanoTime()
        val dt = (time - prevRenderTime) / 1e9
        prevRenderTime = time

        // render engine content
        render(dt)
    }
}