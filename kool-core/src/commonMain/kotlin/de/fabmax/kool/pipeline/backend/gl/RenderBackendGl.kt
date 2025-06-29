package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.measureTime

expect fun createRenderBackendGl(ctx: KoolContext): RenderBackendGl

abstract class RenderBackendGl(val numSamples: Int, internal val gl: GlApi, internal val ctx: KoolContext) : RenderBackend {
    override val apiName: String
        get() = gl.version.versionName
    override val deviceName: String
        get() = gl.version.deviceInfo

    abstract val glslGeneratorHints: GlslGenerator.Hints

    override var deviceCoordinates: DeviceCoordinates = DeviceCoordinates.OPEN_GL
        protected set

    var useFloatDepthBuffer = true
    internal val shaderMgr = ShaderManager(this)

    protected val sceneRenderer = ScreenPassGl(numSamples, this)

    private val awaitedStorageBuffers = mutableListOf<ReadbackStorageBuffer>()

    protected fun setupGl() {
        if (gl.capabilities.hasClipControl) {
            logD { "Setting depth range to zero-to-one" }
            gl.clipControl(gl.LOWER_LEFT, gl.ZERO_TO_ONE)
            deviceCoordinates = DeviceCoordinates.OPEN_GL_ZERO_TO_ONE
        }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        sceneRenderer.applySize(ctx.windowWidth, ctx.windowHeight)
        ctx.backgroundScene.executePasses()

        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
                scene.executePasses()
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

        val format = texture.format
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

    override fun createOffscreenPass2d(parentPass: OffscreenPass2d): OffscreenPass2dImpl {
        return OffscreenPass2dGl(parentPass, this)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenPassCube): OffscreenPassCubeImpl {
        return OffscreenPassCubeGl(parentPass, this)
    }

    override fun createComputePass(parentPass: ComputePass): ComputePassImpl {
        return ComputePassGl(parentPass, this)
    }

    override fun initStorageTexture(storageTexture: StorageTexture, width: Int, height: Int, depth: Int) {
        val tex = storageTexture.asTexture
        val imageType = when (storageTexture) {
            is StorageTexture1d -> gl.TEXTURE_2D
            is StorageTexture2d -> gl.TEXTURE_2D
            is StorageTexture3d -> gl.TEXTURE_3D
        }
        val levels = if (tex.mipMapping.isMipMapped) numMipLevels(width, height, depth) else 1
        val format = tex.format.glInternalFormat(gl)

        val somePxSize = 16L
        val gpuTexture = LoadedTextureGl(imageType, gl.createTexture(), this, tex, width * height * depth * somePxSize)
        gpuTexture.setSize(width, height, depth)
        gpuTexture.bind()
        if (imageType == gl.TEXTURE_3D) {
            gl.texStorage3d(gl.TEXTURE_3D, levels, format, width, height, depth)
        } else {
            gl.texStorage2d(gl.TEXTURE_2D, levels, format, width, height)
        }
        storageTexture.gpuTexture?.release()
        storageTexture.gpuTexture = gpuTexture
    }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCodeGl {
        val src = GlslGenerator.generateProgram(shader.program, pipeline, glslGeneratorHints)
        return ShaderCodeGl(src.vertexSrc, src.fragmentSrc)
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCodeGl {
        if (!gl.capabilities.hasComputeShaders) {
            logW { "Compute shaders require OpenGL 4.3 or higher" }
        }
        val src = GlslGenerator.generateComputeProgram(shader.program, pipeline, glslGeneratorHints)
        return ComputeShaderCodeGl(src.computeSrc)
    }

    private fun Scene.executePasses() {
        sceneRecordTime += measureTime {
            for (i in sortedPasses.indices) {
                val pass = sortedPasses[i]
                if (pass.isEnabled) {
                    pass.beforePass()
                    pass.execute()
                    pass.afterPass()
                }
            }
        }
    }

    private fun GpuPass.execute() {
        when (this) {
            is Scene.ScreenPass -> sceneRenderer.draw(this)
            is OffscreenPass2d -> impl.draw()
            is OffscreenPassCube -> impl.draw()
            is ComputePass -> impl.dispatch()
            else -> error("Gpu pass type not implemented: $this")
        }
    }

    protected fun OffscreenPass2dImpl.draw() = (this as OffscreenPass2dGl).draw()
    protected fun OffscreenPassCubeImpl.draw() = (this as OffscreenPassCubeGl).draw()
    protected fun ComputePassImpl.dispatch() = (this as ComputePassGl).dispatch()

    override fun downloadBuffer(buffer: GpuBuffer, deferred: CompletableDeferred<Unit>, resultBuffer: Buffer) {
        awaitedStorageBuffers += ReadbackStorageBuffer(buffer, deferred, resultBuffer)
    }

    private fun readbackStorageBuffers() {
        gl.memoryBarrier(gl.SHADER_STORAGE_BARRIER_BIT)
        awaitedStorageBuffers.forEach { readback ->
            val gpuBuf = readback.storage.gpuBuffer as GpuBufferGl?
            if (gpuBuf == null || !gl.readBuffer(gpuBuf, readback.resultBuffer)) {
                readback.deferred.completeExceptionally(IllegalStateException("Failed reading buffer"))
            } else {
                readback.deferred.complete(Unit)
            }
        }
        awaitedStorageBuffers.clear()
    }

    private class ReadbackStorageBuffer(val storage: GpuBuffer, val deferred: CompletableDeferred<Unit>, val resultBuffer: Buffer)

    companion object : BackendProvider {
        override val displayName: String = "OpenGL"

        override suspend fun createBackend(ctx: KoolContext): Result<RenderBackendGl> {
            return Result.success(createRenderBackendGl(ctx))
        }
    }
}