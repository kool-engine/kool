package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred

abstract class RenderBackendGl(val numSamples: Int, internal val gl: GlApi, internal val ctx: KoolContext) : RenderBackend {
    override val name = "Common GL Backend"
    override val apiName: String
        get() = gl.version.versionName
    override val deviceName: String
        get() = gl.version.deviceInfo

    abstract val glslGeneratorHints: GlslGenerator.Hints

    override var deviceCoordinates: DeviceCoordinates = DeviceCoordinates.OPEN_GL
        protected set

    var useFloatDepthBuffer = true
    internal val shaderMgr = ShaderManager(this)

    private val windowViewport = Viewport(0, 0, 0, 0)
    protected val sceneRenderer = SceneRenderPassGl(numSamples, this)

    private val awaitedStorageBuffers = mutableListOf<Pair<StorageBuffer, CompletableDeferred<Unit>>>()

    protected fun setupGl() {
        if (gl.capabilities.hasClipControl) {
            logD { "Setting depth range to zero-to-one" }
            gl.clipControl(gl.LOWER_LEFT, gl.ZERO_TO_ONE)
            deviceCoordinates = DeviceCoordinates.OPEN_GL_ZERO_TO_ONE
        }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        getWindowViewport(windowViewport)
        sceneRenderer.applySize(windowViewport.width, windowViewport.height)

        doOffscreenPasses(ctx.backgroundScene)

        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
                val t = Time.precisionTime
                doOffscreenPasses(scene)
                sceneRenderer.draw(scene)
                scene.sceneDrawTime = Time.precisionTime - t
            }
        }

        if (useFloatDepthBuffer) {
            sceneRenderer.resolve(gl.DEFAULT_FRAMEBUFFER, gl.COLOR_BUFFER_BIT)
        }

        if (awaitedStorageBuffers.isNotEmpty()) {
            readbackStorageBuffers()
        }
    }

    override fun <T: ImageData> uploadTextureData(tex: Texture<T>) {
        if (tex.uploadData == null) {
            logE { "texture provided to uploadTextureData() has no uploadData" }
            return
        }
        TextureLoaderGl.loadTexture(tex, this)
    }

    override fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>) {
        val glTex = texture.gpuTexture as LoadedTextureGl?
        if (glTex == null) {
            deferred.completeExceptionally(IllegalStateException("Texture not yet uploaded to GPU"))
            return
        }

        val format = texture.props.format
        val buffer = ImageData.createBuffer(format, glTex.width, glTex.height, glTex.depth)
        val targetData = when (texture) {
            is Texture1d -> BufferedImageData1d(buffer, glTex.width, format)
            is Texture2d -> BufferedImageData2d(buffer, glTex.width, glTex.height, format)
            is Texture3d -> BufferedImageData3d(buffer, glTex.width, glTex.height, glTex.depth, format)
            else -> {
                deferred.completeExceptionally(IllegalStateException("Unsupported texture type: ${texture::class.simpleName} (texture: ${texture.name})"))
                return
            }
        }

        if (!gl.readTexturePixels(glTex, targetData)) {
            deferred.completeExceptionally(IllegalStateException("Failed reading texture data of texture ${texture.name}"))
        } else {
            deferred.complete(targetData)
        }
    }

    override fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl {
        return OffscreenRenderPass2dGl(parentPass, this)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
        return OffscreenRenderPassCubeGl(parentPass, this)
    }

    override fun createComputePass(parentPass: ComputeRenderPass): ComputePassImpl {
        return ComputeRenderPassGl(parentPass, this)
    }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCodeGl {
        val src = GlslGenerator(glslGeneratorHints).generateProgram(shader.program, pipeline)
        if (shader.program.dumpCode) {
            src.dump()
        }
        return ShaderCodeGl(src.vertexSrc, src.fragmentSrc)
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCodeGl {
        if (!gl.capabilities.hasComputeShaders) {
            logW { "Compute shaders require OpenGL 4.3 or higher" }
        }
        val src = GlslGenerator(glslGeneratorHints).generateComputeProgram(shader.program, pipeline)
        if (shader.program.dumpCode) {
            src.dump()
        }
        return ComputeShaderCodeGl(src.computeSrc)
    }

    private fun doOffscreenPasses(scene: Scene) {
        for (i in scene.sortedOffscreenPasses.indices) {
            val pass = scene.sortedOffscreenPasses[i]
            if (pass.isEnabled) {
                drawOffscreen(pass)
                pass.afterDraw()
            }
        }
    }

    private fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
        when (offscreenPass) {
            is OffscreenRenderPass2d -> offscreenPass.impl.draw()
            is OffscreenRenderPassCube -> offscreenPass.impl.draw()
            is ComputeRenderPass -> offscreenPass.impl.dispatch()
            is OffscreenRenderPass2dPingPong -> drawOffscreenPingPong(offscreenPass)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $offscreenPass")
        }
    }

    private fun drawOffscreenPingPong(offscreenPass: OffscreenRenderPass2dPingPong) {
        for (i in 0 until offscreenPass.pingPongPasses) {
            offscreenPass.onDrawPing?.invoke(i)
            offscreenPass.ping.impl.draw()
            offscreenPass.onDrawPong?.invoke(i)
            offscreenPass.pong.impl.draw()
        }
    }

    protected fun OffscreenPass2dImpl.draw() = (this as OffscreenRenderPass2dGl).draw()
    protected fun OffscreenPassCubeImpl.draw() = (this as OffscreenRenderPassCubeGl).draw()
    protected fun ComputePassImpl.dispatch() = (this as ComputeRenderPassGl).dispatch()

    override fun downloadStorageBuffer(storage: StorageBuffer, deferred: CompletableDeferred<Unit>) {
        awaitedStorageBuffers += storage to deferred
    }

    private fun readbackStorageBuffers() {
        gl.memoryBarrier(gl.SHADER_STORAGE_BARRIER_BIT)
        awaitedStorageBuffers.forEach { (storage, deferredBuffer) ->
            val gpuBuf = storage.gpuBuffer as BufferResource?
            if (gpuBuf == null || !gl.readBuffer(gpuBuf, storage.buffer)) {
                deferredBuffer.completeExceptionally(IllegalStateException("Failed reading buffer"))
            } else {
                deferredBuffer.complete(Unit)
            }
        }
        awaitedStorageBuffers.clear()
    }
}