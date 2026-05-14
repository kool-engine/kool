package de.fabmax.kool.pipeline.ao

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.blocks.getLinearDepthReversed
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.SyncedScope
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.releaseWith
import kotlinx.coroutines.launch

class ComputeAoPipeline(val scene: Scene, camera: PerspectiveCamera, drawNode: Node) : AoPipeline, BaseReleasable() {
    private val proxyCamera = PerspectiveProxyCam(camera)
    val depthPass = NormalDepthMapPass(
        drawNode = drawNode,
        initialSize = Vec2i(
            scene.mainRenderPass.viewport.width.coerceAtLeast(1),
            scene.mainRenderPass.viewport.height.coerceAtLeast(1)
        )
    )
    val computePass: ComputeAoPass = ComputeAoPass(
        normalDepthPass = depthPass,
        camera = proxyCamera,
        distFormat = TexFormat.R_F16,
    )

    override val aoMap: Texture2d
        get() = computePass.finalAo
    override var isEnabled: Boolean = true
        set(value) {
            field = value
            applyEnabled(value)
        }

    override var radius: Float by computePass::radius
    override var strength: Float by computePass::strength
    override var falloff: Float by computePass::falloff
    override var kernelSize: Int by computePass::kernelSize

    private val onRenderSceneCallback = OnRenderScene { onRenderScene() }

    init {
        depthPass.camera = proxyCamera
        depthPass.isUpdateDrawNode = false
        depthPass.isReleaseDrawNode = false
        depthPass.onBeforeCollectDrawCommands += { proxyCamera.sync(it) }

        scene.addOffscreenPass(depthPass)
        scene.addComputePass(computePass)

        scene.onRenderScene += onRenderSceneCallback
    }

    private fun applyEnabled(isEnabled: Boolean) {
        depthPass.isEnabled = isEnabled
        computePass.applyEnabled(isEnabled)
    }

    private fun onRenderScene() {
        val mapW = scene.mainRenderPass.viewport.width
        val mapH = scene.mainRenderPass.viewport.height

        if (mapW > 0 && mapH > 0) {
            depthPass.setSize(mapW, mapH)
            computePass.applySizeFromDepthPass()
        }
    }

    override fun doRelease() {
        scene.onRenderScene -= onRenderSceneCallback
        scene.removeOffscreenPass(depthPass)
        scene.removeComputePass(computePass)
        depthPass.release()
        computePass.release()
    }
}

class ComputeAoPass(
    val normalDepthPass: NormalDepthMapPass,
    val camera: Camera,
    val distFormat: TexFormat,
) : ComputePass("AO Pass") {
    private val inputDepth: Texture2d get() = normalDepthPass.depthTexture!!
    private val inputNormals: Texture2d get() = normalDepthPass.colorTexture!!

    private val fullWidth: Int get() = normalDepthPass.width.coerceAtLeast(1)
    private val fullHeight: Int get() = normalDepthPass.height.coerceAtLeast(1)
    private val scaledWidth: Int get() = (fullWidth / 2).coerceAtLeast(1)
    private val scaledHeight: Int get() = (fullHeight / 2).coerceAtLeast(1)

    val scaledNormals = StorageTexture2d(scaledWidth, scaledHeight, TexFormat.RGBA, mipMapping = MipMapping.Limited(SCALE_LEVELS), name = "normalOutput")
    val scaledDists = StorageTexture2d(scaledWidth, scaledHeight, distFormat, mipMapping = MipMapping.Limited(SCALE_LEVELS), name = "distOutput")
    val aoNoisy = StorageTexture2d(scaledWidth, scaledHeight, TexFormat.R, name = "aoOutputNoisy")
    val filteredAo = StorageTexture2d(scaledWidth, scaledHeight, TexFormat.R, name = "filteredAo")
    val finalAo = StorageTexture2d(fullWidth, fullHeight, TexFormat.R, name = "finalAo")
    private val noiseTex = generateFilterNoiseTex(NOISE_TEX_SZ)

    private val downSampleShader = initDownSamplePass()
    private val downSampleLowerShader = initDownSampleLowerPass()
    private val aoShader = initAoPass()
    private val denoiseShader = initDenoisePass()
    private val upsampleShader = initUpsamplePass()
    private val clearShader = initClearPass()

    private var uProj by aoShader.bindUniformMat4("uProj")
    private var uInvProj by aoShader.bindUniformMat4("uInvProj")
    private var uCamNear by aoShader.bindUniformFloat1("uCamNear")

    var kernelSize = 16
        set(value) {
            field = value.coerceIn(1, MAX_KERNEL_SIZE)
            generateAoSampleDirs(kernelSize).forEachIndexed { i, kernel -> uKernels[i] = kernel }
            uKernelSize = field
        }
    private var uKernelSize by aoShader.bindUniformInt1("uKernelRange", kernelSize)
    private val uKernels = aoShader.bindUniformFloat3Array("uKernel", MAX_KERNEL_SIZE)

    var radius: Float = 1f
        set(value) {
            field = value
            uAoRadius = value
            uDenoiseRadius = value
        }
    private var uAoRadius by aoShader.bindUniformFloat1("uRadius", radius)
    private var uDenoiseRadius by denoiseShader.bindUniformFloat1("uRadius", radius)

    var strength by aoShader.bindUniformFloat1("uStrength", 1.25f)
    var falloff by aoShader.bindUniformFloat1("uFalloff", 1.5f)

    private var doClear = false
    private var isConfigured = false

    companion object {
        const val MAX_KERNEL_SIZE = 64
        private const val NOISE_TEX_SZ = 4
        private const val SCALE_LEVELS = 4
    }

    init {
        scaledNormals.releaseWith(this)
        scaledDists.releaseWith(this)
        noiseTex.releaseWith(this)

        generateAoSampleDirs(kernelSize).forEachIndexed { i, kernel -> uKernels[i] = kernel }

        onUpdate {
            uProj = camera.proj
            uInvProj = camera.invProj
            uCamNear = camera.clipNear
        }
    }

    override fun doRelease() {
        super.doRelease()
        scaledNormals.release()
        scaledDists.release()
        aoNoisy.release()
        filteredAo.release()
        finalAo.release()
        noiseTex.release()
    }

    internal fun applySizeFromDepthPass() {
        if (!isEnabled) {
            return
        }

        if (doClear) {
            if (!isConfigured || finalAo.width != 8 || finalAo.height != 8) {
                finalAo.resize(8, 8)
                SyncedScope.launch {
                    clearAndReleaseTasks()
                    makeClearPass()
                }
            }
        } else if (!isConfigured || fullWidth != finalAo.width || fullHeight != finalAo.height) {
            val requiredWidth = scaledWidth
            val requiredHeight = scaledHeight
            logD { "Resizing AO pass to $requiredWidth x $requiredHeight, ao output size: $fullWidth x $fullHeight" }
            scaledNormals.resize(requiredWidth, requiredHeight)
            scaledDists.resize(requiredWidth, requiredHeight)
            aoNoisy.resize(requiredWidth, requiredHeight)
            filteredAo.resize(requiredWidth, requiredHeight)
            finalAo.resize(fullWidth, fullHeight)

            SyncedScope.launch {
                clearAndReleaseTasks()
                makeDownSamplePass()
                makeDownSampleLowerPasses()
                makeAoPass()
                makeDenoisePass()
                makeUpsamplePass()
            }
        }
        isConfigured = true
    }

    internal fun applyEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            this.isEnabled = true
            doClear = false
        } else {
            doClear = true
        }
        isConfigured = false
    }

    private fun initDownSamplePass(): KslComputeShader {
        val downSampleShader = downSamplingShader()
        downSampleShader.bindTexture2d("normalInput", inputNormals, SamplerSettings().nearest())
        downSampleShader.bindTexture2d("distInput", inputDepth, SamplerSettings().nearest())
        downSampleShader.bindStorageTexture2d("normalOutput", scaledNormals)
        downSampleShader.bindStorageTexture2d("distOutput", scaledDists)
        downSampleShader.bindUniformFloat1("camNear", camera.clipNear)
        return downSampleShader
    }

    private fun initDownSampleLowerPass(): KslComputeShader {
        return downSamplingLowerShader()
    }

    private fun initAoPass(): KslComputeShader {
        val aoShader = aoShader()
        aoShader.bindTexture2d("normalInput", scaledNormals, SamplerSettings().nearest().clamped())
        aoShader.bindTexture2d("distInput", scaledDists, SamplerSettings().nearest().clamped())
        aoShader.bindTexture2d("noiseTex", noiseTex, SamplerSettings().nearest())
        aoShader.bindStorageTexture2d("aoOutput", aoNoisy)
        return aoShader
    }

    private fun initDenoisePass(): KslComputeShader {
        val denoiseShader = denoiseShader()
        denoiseShader.bindTexture2d("distInput", scaledDists, SamplerSettings().nearest().clamped())
        denoiseShader.bindTexture2d("normalInput", scaledNormals, SamplerSettings().nearest().clamped())
        denoiseShader.bindTexture2d("noisyAo", aoNoisy, SamplerSettings().nearest())
        denoiseShader.bindStorageTexture2d("filteredAo", filteredAo)
        return denoiseShader
    }

    private fun initUpsamplePass(): KslComputeShader {
        val upsampleShader = upsampleShader()
        upsampleShader.bindTexture2d("filteredAo", filteredAo, SamplerSettings().nearest())
        upsampleShader.bindTexture2d("distInput", inputDepth, SamplerSettings().nearest())
        upsampleShader.bindTexture2d("scaledDistInput", scaledDists, SamplerSettings().nearest())
        upsampleShader.bindUniformFloat1("camNear", camera.clipNear)
        upsampleShader.bindStorageTexture2d("finalAo", finalAo)
        return upsampleShader
    }

    private fun initClearPass(): KslComputeShader {
        val clearShader = clearShader()
        clearShader.bindStorageTexture2d("finalAo", finalAo)
        return clearShader
    }

    private fun makeDownSamplePass() {
        val groupsX = (scaledWidth + 7) / 8
        val groupsY = (scaledHeight + 7) / 8
        addTask(downSampleShader, Vec3i(groupsX, groupsY, 1))
    }

    private fun makeDownSampleLowerPasses() {
        val distInput = downSampleLowerShader.bindTexture2d("distInput")
        val loadLod = downSampleLowerShader.bindUniformInt1("loadLod")
        val distOutput = downSampleLowerShader.bindStorageTexture2d("distOutput")

        for (level in 1 until SCALE_LEVELS) {
            val groupsX = ((scaledWidth shr level) + 7) / 8
            val groupsY = ((scaledHeight shr level) + 7) / 8
            val task = addTask(downSampleLowerShader, Vec3i(groupsX, groupsY, 1))

            val key = "$level"
            task.onBeforeDispatch {
                downSampleLowerShader.swapPipelineDataCapturing(key) {
                    distInput.set(scaledDists)
                    loadLod.set(level - 1)
                    distOutput.set(scaledDists, level)
                }
            }
        }
    }

    private fun makeAoPass() {
        val groupsX = (scaledWidth + 7) / 8
        val groupsY = (scaledHeight + 7) / 8
        addTask(aoShader, Vec3i(groupsX, groupsY, 1))
    }

    private fun makeDenoisePass() {
        val groupsX = (scaledWidth + 7) / 8
        val groupsY = (scaledHeight + 7) / 8
        addTask(denoiseShader, Vec3i(groupsX, groupsY, 1))
    }

    private fun makeUpsamplePass() {
        val groupsX = (fullWidth + 7) / 8
        val groupsY = (fullHeight + 7) / 8
        addTask(upsampleShader, Vec3i(groupsX, groupsY, 1))
    }

    private fun makeClearPass() {
        val task = addTask(clearShader, Vec3i(1, 1, 1))
        task.onAfterDispatch {
            isEnabled = false
            doClear = false
        }
    }

    private fun downSamplingShader() = KslComputeShader("down-sample-shader") {
        computeStage(8, 8) {
            val normalInput = texture2d("normalInput")
            val distInput = texture2d("distInput", isUnfilterable = true)
            val normalOutput = storageTexture2d<KslFloat4>("normalOutput", TexFormat.RGBA)
            val distOutput = storageTexture2d<KslFloat1>("distOutput", distFormat)
            val camNear = uniformFloat1("camNear")

            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
                val loadCoord by baseCoord * 2.const
                val normal by 0f.const4
                val maxDepth by 1f.const
                val samplePos = listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(0, 1), Vec2i(1, 1))
                samplePos.forEach { sample ->
                    val sampleDepth = float1Var(distInput.load(loadCoord + sample.const).x)
                    `if`(sampleDepth lt maxDepth) {
                        maxDepth set sampleDepth
                        normal set normalInput.load(loadCoord + sample.const)
                    }
                }
                val linearDepth by getLinearDepthReversed(maxDepth, camNear)
                normalOutput.store(baseCoord, normal)
                distOutput.store(baseCoord, float4Value(linearDepth, 0f.const, 0f.const, 0f.const))
            }
        }
    }

    private fun downSamplingLowerShader() = KslComputeShader("down-sample-lower-shader") {
        computeStage(8, 8) {
            val distInput = texture2d("distInput", isUnfilterable = true)
            val distOutput = storageTexture2d<KslFloat1>("distOutput", distFormat)
            val loadLod = uniformInt1("loadLod")

            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
                val loadCoord by baseCoord * 2.const
                val maxDepth by 0f.const
                val samplePos = listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(0, 1), Vec2i(1, 1))
                samplePos.forEach { sample ->
                    val sampleDepth = float1Var(distInput.load(loadCoord + sample.const, loadLod).x)
                    `if`(sampleDepth gt maxDepth) {
                        maxDepth set sampleDepth
                    }
                }
                distOutput.store(baseCoord, float4Value(maxDepth, 0f.const, 0f.const, 0f.const))
            }
        }
    }

    private fun aoShader() = KslComputeShader("ao-shader") {
        computeStage(8, 8) {
            val normalInput = texture2d("normalInput")
            val distInput = texture2d("distInput")
            val noiseTex = texture2d("noiseTex")
            val aoOutput = storageTexture2d<KslFloat1>("aoOutput", aoNoisy.format)

            val uProj = uniformMat4("uProj")
            val uInvProj = uniformMat4("uInvProj")
            val uCamNear = uniformFloat1("uCamNear")
            val uKernel = uniformFloat3Array("uKernel", MAX_KERNEL_SIZE)
            val uKernelSize = uniformInt1("uKernelRange")
            val uRadius = uniformFloat1("uRadius")
            val uStrength = uniformFloat1("uStrength")
            val uFalloff = uniformFloat1("uFalloff")

            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
                val uv by (baseCoord.toFloat2() + 0.5f.const) / normalInput.size().toFloat2()
                val encodedNormal = float4Var(normalInput.load(baseCoord))
                val occlFac by 1f.const

                val isValid = encodedNormal.a gt 0f.const
                `if`(isValid) {
                    val depth by distInput.load(baseCoord).x
                    val normal by decodeNormalRgb(encodedNormal.rgb)

                    val sampleR by uRadius
                    `if`(sampleR lt 0f.const) {
                        sampleR *= -depth
                    }

                    `if`(depth lt sampleR * 200f.const) {
                        val viewXy by (uv * 2f.const - 1f.const) * depth
                        val viewProjPos by float4Value(viewXy, uCamNear, depth)
                        val viewPos by (uInvProj * viewProjPos).xyz

                        // compute kernel rotation
                        val rotVec by noiseTex.load(baseCoord % NOISE_TEX_SZ.const).xy * 2f.const - 1f.const
                        val tan1 by normalize(cross(Vec3f(1f, 1.1337e-6f, 1.1337e-6f).const, normal))
                        val tan2 by cross(tan1, normal)
                        val tan1Rot by tan1 * rotVec.x + tan2 * rotVec.y
                        val tan2Rot by cross(normal, tan1Rot)
                        val tbn by mat3Value(tan1Rot, tan2Rot, normal)

                        val occlusion by 0f.const
                        fori(0.const, uKernelSize) { i ->
                            val kernel by tbn * uKernel[i]
                            if (KoolSystem.requireContext().backend.isInvertedNdcY) {
                                kernel.y *= (-1f).const
                            }
                            val samplePos by viewPos + kernel * sampleR
                            val sampleDepth by -samplePos.z
                            val sampleProj by uProj * float4Value(samplePos, 1f.const)
                            val sampleUv by sampleProj.xy / sampleProj.w * 0.5f.const + 0.5f.const
                            val sampleLod by clamp(length(sampleUv - uv) * 25f.const / uRadius, 0f.const, (SCALE_LEVELS - 1f).const)
                            sampleLod set clamp(sampleLod, 0f.const, (SCALE_LEVELS-1).toFloat().const)
                            val sampleScreenDepth by distInput.sample(sampleUv, sampleLod).x

                            val occlusionDistance by clamp((sampleDepth - sampleScreenDepth) * 10f.const, 0f.const, 1f.const)
                            val occlusionFalloff by 1f.const - smoothStep(0f.const, 1f.const, abs(depth - sampleScreenDepth) / (4f.const * sampleR))
                            occlusion += occlusionDistance * occlusionFalloff
                        }

                        occlusion /= uKernelSize.toFloat1()
                        val distFac by 1f.const - smoothStep(sampleR * 150f.const, sampleR * 200f.const, depth)
                        occlFac set pow(clamp(1f.const - occlusion * distFac * uStrength, 0f.const, 1f.const), uFalloff)
                    }
                }
                aoOutput.store(baseCoord, float4Value(occlFac, 0f.const, 0f.const, 1f.const))
            }
        }
    }

    private fun denoiseShader() = KslComputeShader("ao-denoise-shader") {
        val noisyAo = texture2d("noisyAo")
        val distInput = texture2d("distInput")
        val normalInput = texture2d("normalInput")
        val uRadius = uniformFloat1("uRadius")
        val filteredAo = storageTexture2d<KslFloat1>("filteredAo", filteredAo.format)

        computeStage(8, 8) {
            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
                val dim = NOISE_TEX_SZ / 2

                val baseDist by distInput.load(baseCoord).x
                val baseNormal by decodeNormalRgb(normalInput.load(baseCoord).rgb)
                val ao by noisyAo.load(baseCoord).x
                val sumWeight by 1f.const

                val sampleR by uRadius
                `if`(sampleR lt 0f.const) {
                    sampleR *= -baseDist
                }
                for (y in -dim until dim) {
                    for (x in -dim until dim) {
                        if (x != 0 || y != 0) {
                            val coord = int2Var(baseCoord + Vec2i(x, y).const)
                            val dist = float1Var(distInput.load(coord).x)
                            val distWeight = float1Var(1f.const - smoothStep(0f.const, sampleR, abs(dist - baseDist)))
                            val normalWeight = float1Var(saturate(dot(decodeNormalRgb(normalInput.load(coord).rgb), baseNormal)))
                            val weight = float1Var(0.0001f.const + distWeight * normalWeight)
                            ao += noisyAo.load(coord).x * weight
                            sumWeight += weight
                        }
                    }
                }
                ao /= sumWeight
                filteredAo.store(baseCoord, float4Value(ao, 0f.const, 0f.const, 0f.const))
            }
        }
    }

    private fun upsampleShader() = KslComputeShader("ao-upsample-shader") {
        val filteredAo = texture2d("filteredAo")
        val distInput = texture2d("distInput", isUnfilterable = true)
        val scaledDistInput = texture2d("scaledDistInput")
        val camNear = uniformFloat1("camNear")
        val finalAo = storageTexture2d<KslFloat1>("finalAo", finalAo.format)

        computeStage(8, 8) {
            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
                val baseDepth by getLinearDepthReversed(distInput.load(baseCoord).x, camNear)

                val ca by (baseCoord + Vec2i(1, 1).const) / 2.const
                val cb by (baseCoord + Vec2i(1, 1).const) / 2.const - int2Value(0, 1)
                val cc by (baseCoord + Vec2i(1, 1).const) / 2.const - int2Value(1, 0)
                val cd by (baseCoord + Vec2i(1, 1).const) / 2.const - int2Value(1, 1)

                val thresh = baseDepth / 100f.const
                val wa by 1.0001f.const - smoothStep(0f.const, thresh, abs(scaledDistInput.load(ca).x - baseDepth))
                val wb by 1.0001f.const - smoothStep(0f.const, thresh, abs(scaledDistInput.load(cb).x - baseDepth))
                val wc by 1.0001f.const - smoothStep(0f.const, thresh, abs(scaledDistInput.load(cc).x - baseDepth))
                val wd by 1.0001f.const - smoothStep(0f.const, thresh, abs(scaledDistInput.load(cd).x - baseDepth))

                val ao by filteredAo.load(ca).x * wa
                ao += filteredAo.load(cb).x * wb
                ao += filteredAo.load(cc).x * wc
                ao += filteredAo.load(cd).x * wd
                ao /= wa + wb + wc + wd
                finalAo.store(baseCoord, float4Value(ao, 0f.const, 0f.const, 0f.const))
            }
        }
    }

    private fun clearShader() = KslComputeShader("ao-clear-shader") {
        val finalAo = storageTexture2d<KslFloat1>("finalAo", finalAo.format)
        computeStage(8, 8) {
            main {
                val baseCoord by inGlobalInvocationId.xy.toInt2()
                finalAo.store(baseCoord, float4Value(1f.const, 0f.const, 0f.const, 0f.const))
            }
        }
    }
}