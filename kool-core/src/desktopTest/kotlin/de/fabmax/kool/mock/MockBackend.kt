package de.fabmax.kool.mock

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.gl.ComputeShaderCodeGl
import de.fabmax.kool.pipeline.backend.gl.GlslGenerator
import de.fabmax.kool.pipeline.backend.gl.ShaderCodeGl
import de.fabmax.kool.util.Buffer
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MockBackend() : RenderBackend {

    override val name: String = "Mock"
    override val apiName: String = "MockAPI"
    override val deviceName: String = "Mock device"
    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.OPEN_GL
    override val features = BackendFeatures(
        computeShaders = false,
        cubeMapArrays = false,
        reversedDepth = false,
        maxSamples = 4,
        readWriteStorageTextures = false,
        depthOnlyShaderColorOutput = null,
        maxComputeWorkGroupsPerDimension = Vec3i.ZERO,
        maxComputeWorkGroupSize = Vec3i.ZERO,
        maxComputeInvocationsPerWorkgroup = 0
    )

    override var frameGpuTime: Duration = 0.0.seconds

    val glslHints = GlslGenerator.Hints("#version 330 core")

    override fun renderFrame(ctx: KoolContext) { }

    override fun cleanup(ctx: KoolContext) { }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode {
        val output = GlslGenerator.generateProgram(shader.program, pipeline, glslHints) as KslGenerator.GeneratedSourceOutput
        return ShaderCodeGl(output.vertexSrc, output.fragmentSrc)
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val output = GlslGenerator.generateComputeProgram(shader.program, pipeline, glslHints) as KslGenerator.GeneratedSourceOutput
        return ComputeShaderCodeGl(output.computeSrc)
    }

    override fun createOffscreenPass2d(parentPass: OffscreenPass2d): OffscreenPass2dImpl {
        TODO("Not yet implemented")
    }

    override fun createOffscreenPassCube(parentPass: OffscreenPassCube): OffscreenPassCubeImpl {
        TODO("Not yet implemented")
    }

    override fun createComputePass(parentPass: ComputePass): ComputePassImpl {
        TODO("Not yet implemented")
    }

    override fun initStorageTexture(storageTexture: StorageTexture, width: Int, height: Int, depth: Int) {
        TODO("Not yet implemented")
    }

    override fun <T: ImageData> uploadTextureData(tex: Texture<T>) {
        tex.uploadData = null
    }

    override fun downloadBuffer(buffer: GpuBuffer, deferred: CompletableDeferred<Unit>, resultBuffer: Buffer) {
        deferred.complete(Unit)
    }

    override fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>) {
        TODO("Not yet implemented")
    }
}