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
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MockBackend(val shaderGen: KslGenerator = GlslGenerator(GlslGenerator.Hints("#version 330 core"))) : RenderBackend {

    override val name: String = "Mock backend"
    override val apiName: String = "MockAPI"
    override val deviceName: String = "Mock device"
    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.OPEN_GL
    override val features = BackendFeatures(
        computeShaders = false,
        cubeMapArrays = false,
        reversedDepth = false,
        depthOnlyShaderColorOutput = null,
        maxComputeWorkGroupsPerDimension = Vec3i.ZERO,
        maxComputeWorkGroupSize = Vec3i.ZERO,
        maxComputeInvocationsPerWorkgroup = 0
    )

    override var frameGpuTime: Duration = 0.0.seconds

    override fun renderFrame(ctx: KoolContext) { }

    override fun cleanup(ctx: KoolContext) { }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode {
        val output = shaderGen.generateProgram(shader.program, pipeline) as KslGenerator.GeneratedSourceOutput
        return ShaderCodeGl(output.vertexSrc, output.fragmentSrc)
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val output = shaderGen.generateComputeProgram(shader.program, pipeline) as KslGenerator.GeneratedSourceOutput
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

    override fun <T: ImageData> uploadTextureData(tex: Texture<T>) {
        tex.uploadData = null
    }

    override fun downloadStorageBuffer(storage: StorageBuffer, deferred: CompletableDeferred<Unit>) {
        deferred.complete(Unit)
    }

    override fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>) {
        TODO("Not yet implemented")
    }
}