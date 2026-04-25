package de.fabmax.kool.pipeline.ao

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.Uint8Buffer
import kotlin.math.*
import kotlin.random.Random

interface AoPipeline : Releasable {
    val aoMap: Texture2d
    var isEnabled: Boolean

    var radius: Float
    var strength: Float
    var falloff: Float
    var kernelSize: Int

    companion object {
        fun createForwardCompute(
            scene: Scene,
            camera: PerspectiveCamera = (scene.camera as PerspectiveCamera),
            drawNode: Node = scene
        ) = ComputeAoPipeline(scene, camera, drawNode)

        fun createForwardLegacy(
            scene: Scene,
            camera: PerspectiveCamera = (scene.camera as PerspectiveCamera),
            drawNode: Node = scene
        ) = ForwardAoPipeline(scene, camera, drawNode)

        fun createForward(
            scene: Scene,
            camera: PerspectiveCamera = (scene.camera as PerspectiveCamera),
            drawNode: Node = scene
        ): AoPipeline = if (KoolSystem.requireContext().backend.features.computeShaders) {
            createForwardCompute(scene, camera, drawNode)
        } else {
            createForwardLegacy(scene, camera, drawNode)
        }

        fun createDeferred(deferredPipeline: DeferredPipeline) = DeferredAoPipeline(deferredPipeline)
    }
}

internal fun generateAoSampleDirs(numDirs: Int): List<Vec3f> {
    val scales = (0 until numDirs).map { lerp(0.1f, 1f, (it.toFloat() / (numDirs-1)).pow(2)) }

    return buildList {
        repeat(numDirs) { i ->
            val xi = hammersley(i, numDirs)
            val phi = 2f * PI.toFloat() * xi.x
            val cosTheta = sqrt((1f - xi.y))
            val sinTheta = sqrt(1f - cosTheta * cosTheta)

            val k = MutableVec3f(sinTheta * cos(phi), sinTheta * sin(phi), cosTheta)
            add(k.norm().mul(scales[i]))
        }
    }
}

private fun hammersley(i: Int, n: Int): Vec2f {
    return Vec2f(i.toFloat() / n.toFloat(), radicalInverse(i))
}

private fun radicalInverse(pBits: Int): Float {
    var bits = pBits.toLong()
    bits = (bits shl 16) or (bits shr 16)
    bits = ((bits and 0x55555555) shl 1) or ((bits and 0xAAAAAAAA) shr 1)
    bits = ((bits and 0x33333333) shl 2) or ((bits and 0xCCCCCCCC) shr 2)
    bits = ((bits and 0x0F0F0F0F) shl 4) or ((bits and 0xF0F0F0F0) shr 4)
    bits = ((bits and 0x00FF00FF) shl 8) or ((bits and 0xFF00FF00) shr 8)
    return bits.toFloat() / 0x100000000
}

private fun lerp(a: Float, b: Float, f: Float): Float {
    return a + f * (b - a)
}

internal fun generateFilterNoiseTex(size: Int): Texture2d {
    val noiseLen = size * size
    val buf = Uint8Buffer(2 * noiseLen)
    val rand = Random(0x1deadb0b)
    val rotAngles = (0 until noiseLen).map { 2f * PI.toFloat() * it / noiseLen }.shuffled(rand)

    for (i in 0 until (size * size)) {
        val ang = rotAngles[i]
        val x = cos(ang)
        val y = sin(ang)
        buf[i*2+0] = ((x * 0.5f + 0.5f) * 255).toInt().toUByte()
        buf[i*2+1] = ((y * 0.5f + 0.5f) * 255).toInt().toUByte()
    }

    val data = BufferedImageData2d(buf, size, size, TexFormat.RG)
    return Texture2d(TexFormat.RG, MipMapping.Off, SamplerSettings().nearest(), "ao_noise_tex") { data }
}