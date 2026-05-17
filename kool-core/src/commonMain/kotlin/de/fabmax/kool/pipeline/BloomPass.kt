package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.SyncedScope
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.releaseWith
import kotlinx.coroutines.launch
import kotlin.math.max

class BloomPass(
    inputTexture: Texture2d,
    val inPlace: Boolean = KoolSystem.requireContext().backend.features.readWriteStorageTextures
) : ComputePass("Bloom Pass") {

    val inputShader = downSamplingShader()
    var inputTexture by inputShader.bindTexture2d(
        textureName = "sampleInput",
        defaultVal = inputTexture,
        defaultSampler = SamplerSettings().clamped().copy(baseMipLevel = 0, numMipLevels = 1),
    )

    private val idealWidth: Int get() = (checkNotNull(inputTexture).width / 2).coerceAtLeast(1)
    private val idealHeight: Int get() = (checkNotNull(inputTexture).height / 2).coerceAtLeast(1)
    private var levels: Int = levelsForSize(idealWidth, idealHeight)

    var threshold = 1f
    var thresholdLuminanceFactors = defaultThresholdLuminanceFactors
    var radius = 2f
    var strength = 1f

    val bloomMap = StorageTexture2d(
        width = idealWidth,
        height = idealHeight,
        format = TexFormat.RG11B10_F,
        mipMapping = MipMapping.Limited(levels),
        name = "bloomMap",
        samplerSettings = SamplerSettings().clamped()
    )
    val downSampleTex = if (inPlace) bloomMap else StorageTexture2d(
        width = idealWidth,
        height = idealHeight,
        format = TexFormat.RG11B10_F,
        mipMapping = MipMapping.Limited(levels),
        name = "downSampleTex"
    )

    private val downSampleShaderLower = downSamplingShader()
    private val upSampleShader = upSamplingShader()

    val width: Int get() = bloomMap.width
    val height: Int get() = bloomMap.height

    init {
        logD { "Using $levels bloom levels, in place: $inPlace" }
        makeDownSampleLowerPasses()
        makeUpSamplePasses()
        bloomMap.releaseWith(this)
        if (!inPlace) {
            downSampleTex.releaseWith(this)
        }

        onUpdate {
            val requiredWidth = idealWidth
            val requiredHeight = idealHeight
            if (requiredWidth != width || requiredHeight != height) {
                SyncedScope.launch {
                    levels = levelsForSize(requiredWidth, requiredHeight)
                    logD { "Resizing bloom pass to $requiredWidth x $requiredHeight ($levels levels)" }
                    bloomMap.resize(requiredWidth, requiredHeight, MipMapping.Limited(levels))
                    if (!inPlace) {
                        downSampleTex.resize(requiredWidth, requiredHeight, MipMapping.Limited(levels))
                    }

                    clearAndReleaseTasks()
                    makeDownSampleFirstPass()
                    makeDownSampleLowerPasses()
                    makeUpSamplePasses()
                }
            }
        }
    }

    private fun makeDownSampleFirstPass() {
        val groupsX = (width + 7) / 8
        val groupsY = (height + 7) / 8
        val task = addTask(inputShader, Vec3i(groupsX, groupsY, 1))

        var uThreshold by inputShader.bindUniformFloat4("threshold")
        val inputTexelSize = Vec2f(1f / (2 * width), 1f / (2 * height))
        inputShader.bindUniformFloat2("inputTexelSize").set(inputTexelSize)
        inputShader.bindStorageTexture2d("downSampled", downSampleTex, 0)
        task.onBeforeDispatch {
            uThreshold = Vec4f(thresholdLuminanceFactors, threshold)
        }
    }

    private fun makeDownSampleLowerPasses() {
        val sampleInput = downSampleShaderLower.bindTexture2d("sampleInput")
        val downSampled = downSampleShaderLower.bindStorageTexture2d("downSampled")
        var uThreshold by downSampleShaderLower.bindUniformFloat4("threshold")
        var uInputTexelSize by downSampleShaderLower.bindUniformFloat2("inputTexelSize")

        for (level in 1 until levels) {
            val groupsX = ((width shr level) + 7) / 8
            val groupsY = ((height shr level) + 7) / 8
            val task = addTask(downSampleShaderLower, Vec3i(groupsX, groupsY, 1))
            val input = downSampleTex
            val inputSampler = SamplerSettings().clamped().copy(baseMipLevel = (level - 1).coerceAtLeast(0), numMipLevels = 1)
            val inputTexelSize = Vec2f(1f / ((2 * width) shr level), 1f / ((2 * height) shr level))
            val key = "$level"

            task.onBeforeDispatch {
                downSampleShaderLower.swapPipelineDataCapturing(key) {
                    sampleInput.set(input, inputSampler)
                    downSampled.set(downSampleTex, level)
                    uThreshold = Vec4f.ZERO
                    uInputTexelSize = inputTexelSize
                }
            }
        }
    }

    private fun makeUpSamplePasses() {
        val sampleInput = upSampleShader.bindTexture2d("sampleInput")
        val downSampled = upSampleShader.bindStorageTexture2d("downSampled")
        val upSampled = upSampleShader.bindStorageTexture2d("upSampled")
        var uInputTexelSize by upSampleShader.bindUniformFloat2("inputTexelSize")
        var uRadius by upSampleShader.bindUniformFloat1("radius", radius)
        var uOutputScale by upSampleShader.bindUniformFloat1("outputScale", radius)

        for (level in (levels - 2) downTo 0) {
            val groupsX = ((width shr level) + 7) / 8
            val groupsY = ((height shr level) + 7) / 8
            val task = addTask(upSampleShader, Vec3i(groupsX, groupsY, 1))
            val input = if (level == levels - 2) downSampleTex else bloomMap
            val inputSampler = SamplerSettings().clamped().copy(baseMipLevel = level + 1, numMipLevels = 1)
            val inputTexelSize = Vec2f(1f / (width shr (level + 1)), 1f / (height shr (level + 1)))
            val key = "$level"

            task.onBeforeDispatch {
                upSampleShader.createdPipeline?.swapPipelineDataCapturing(key) {
                    sampleInput.set(input, inputSampler)
                    upSampled.set(bloomMap, level)
                    if (!inPlace) {
                        downSampled.set(downSampleTex, level)
                    }
                    uInputTexelSize = inputTexelSize
                    uRadius = radius
                    uOutputScale = if (level == 0) strength / levels else 1f
                }
            }
        }
    }

    private fun downSamplingShader() = KslComputeShader("down-sample-shader") {
        computeStage(8, 8) {
            val sampleInput = texture2d("sampleInput")
            val downSampled = storageTexture2d<KslFloat3>("downSampled", TexFormat.RG11B10_F)
            val threshold = uniformFloat4("threshold")
            val inputTexelSize = uniformFloat2("inputTexelSize")

            main {
                val texelCoord = int2Var(inGlobalInvocationId.xy.toInt2())

                val sampleUv = float2Var((texelCoord.toFloat2() + 0.5f.const) * inputTexelSize * 2f.const)
                val u = sampleUv.x
                val v = sampleUv.y
                val rx = inputTexelSize.x
                val ry = inputTexelSize.y
                val rx2 = float1Var(inputTexelSize.x * 2f.const)
                val ry2 = float1Var(inputTexelSize.y * 2f.const)

                val a = float3Var(sampleInput.sample(float2Value(u - rx2, v + ry2), 0f.const).rgb)
                val b = float3Var(sampleInput.sample(float2Value(u, v + ry2), 0f.const).rgb)
                val c = float3Var(sampleInput.sample(float2Value(u + rx2, v + ry2), 0f.const).rgb)

                val d = float3Var(sampleInput.sample(float2Value(u - rx2, v), 0f.const).rgb)
                val e = float3Var(sampleInput.sample(float2Value(u, v), 0f.const).rgb)
                val f = float3Var(sampleInput.sample(float2Value(u + rx2, v), 0f.const).rgb)

                val g = float3Var(sampleInput.sample(float2Value(u - rx2, v - ry2), 0f.const).rgb)
                val h = float3Var(sampleInput.sample(float2Value(u, v - ry2), 0f.const).rgb)
                val i = float3Var(sampleInput.sample(float2Value(u + rx2, v - ry2), 0f.const).rgb)

                val j = float3Var(sampleInput.sample(float2Value(u - rx, v + ry), 0f.const).rgb)
                val k = float3Var(sampleInput.sample(float2Value(u + rx, v + ry), 0f.const).rgb)
                val l = float3Var(sampleInput.sample(float2Value(u - rx, v - ry), 0f.const).rgb)
                val m = float3Var(sampleInput.sample(float2Value(u + rx, v - ry), 0f.const).rgb)

                val weightedAvg = float3Var(e * 0.125f.const)
                weightedAvg += (j + k + l + m) * 0.125.const
                weightedAvg += (b + d + f + h) * 0.0625.const
                weightedAvg += (a + c + g + i) * 0.03125.const

                `if`(threshold.a gt 0f.const) {
                    val luminance = float1Var(dot(weightedAvg, threshold.rgb))
                    luminance set smoothStep(threshold.a, threshold.a * 2f.const, luminance)
                    weightedAvg set weightedAvg * luminance
                }

                `if` (none(isNan(weightedAvg))) {
                    downSampled[texelCoord] = float4Value(weightedAvg, 0f.const)
                }
            }
        }
    }

    fun upSamplingShader() = KslComputeShader("up-sample-shader") {
        computeStage(8, 8) {
            val sampleInput = texture2d("sampleInput")
            val upSampled = storageTexture2d<KslFloat3>("upSampled", TexFormat.RG11B10_F)
            val downSampled = if (inPlace) upSampled else storageTexture2d<KslFloat3>("downSampled", TexFormat.RG11B10_F)
            val inputTexelSize = uniformFloat2("inputTexelSize")
            val outputScale = uniformFloat1("outputScale")
            val radius = uniformFloat1("radius")

            main {
                val texelCoord = int2Var(inGlobalInvocationId.xy.toInt2())

                val sampleUv = float2Var((texelCoord.toFloat2() + 0.5f.const) * inputTexelSize * 0.5f.const)
                val rx = float1Var(inputTexelSize.x * radius)
                val ry = float1Var(inputTexelSize.y * radius)
                val u = sampleUv.x
                val v = sampleUv.y

                val a = float3Var(sampleInput.sample(float2Value(u - rx, v + ry), 0f.const).rgb)
                val b = float3Var(sampleInput.sample(float2Value(u, v + ry), 0f.const).rgb)
                val c = float3Var(sampleInput.sample(float2Value(u + rx, v + ry), 0f.const).rgb)

                val d = float3Var(sampleInput.sample(float2Value(u - rx, v), 0f.const).rgb)
                val e = float3Var(sampleInput.sample(float2Value(u, v), 0f.const).rgb)
                val f = float3Var(sampleInput.sample(float2Value(u + rx, v), 0f.const).rgb)

                val g = float3Var(sampleInput.sample(float2Value(u - rx, v - ry), 0f.const).rgb)
                val h = float3Var(sampleInput.sample(float2Value(u, v - ry), 0f.const).rgb)
                val i = float3Var(sampleInput.sample(float2Value(u + rx, v - ry), 0f.const).rgb)

                val filtered = float3Var(downSampled[texelCoord].rgb)
                filtered += e * (4f / 16f).const
                filtered += (b + d + f + h) * (2f / 16f).const
                filtered += (a + c + g + i) * (1f / 16f).const
                upSampled[texelCoord] = float4Value(filtered * outputScale, 0f.const)
            }
        }
    }

    private fun levelsForSize(width: Int, height: Int): Int {
        var mipLevels = 1
        var s = max(width, height)
        while (s >= 16) {
            mipLevels++
            s /= 2
        }
        return mipLevels
    }

    companion object {
        /**
         * RGB weights for computing bloom threshold luminance. Note that these diverge from the usual
         * luminance factors for perceived brightness. Feel free to choose different values (whatever looks right).
         */
        val defaultThresholdLuminanceFactors = Vec3f(0.37f, 0.41f, 0.22f)
    }
}