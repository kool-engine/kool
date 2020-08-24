package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.createUint8Buffer

class ReflectionPass(val mrtPass: DeferredMrtPass, val pbrLightingPass: PbrLightingPass, val baseReflectionStep: Float) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "ReflectionPass"
            setSize(pbrLightingPass.config.width, pbrLightingPass.config.height)
            addColorTexture(TexFormat.RGBA)
            clearDepthTexture()
        }) {

    private var uRoughThreshLow: PushConstantNode1f? = null
    var roughnessThresholdLow = 0.5f
        set(value) {
            field = value
            uRoughThreshLow?.uniform?.value = value
        }
    private var uRoughThreshHigh: PushConstantNode1f? = null
    var roughnessThresholdHigh = 0.6f
        set(value) {
            field = value
            uRoughThreshHigh?.uniform?.value = value
        }
    private var uMaxIterations: PushConstantNode1i? = null
    var scrSpcReflectionIterations = 24
        set(value) {
            field = value
            uMaxIterations?.uniform?.value = value
        }
    private val noiseTex = generateScrSpcReflectionNoiseTex()

    init {
        clearColor = Color(0f, 0f, 0f, 0f)

        (drawNode as Group).apply {
            isFrustumChecked = false
            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                isFrustumChecked = false
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
                    }
                }
                shader = ReflectionShader()
            }
        }

        dependsOn(mrtPass)
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        noiseTex.dispose()
        super.dispose(ctx)
    }

    private inner class ReflectionShader : ModeledShader(reflectionShaderModel()) {
        override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
            builder.depthTest = DepthCompareOp.ALWAYS
            builder.blendMode = BlendMode.DISABLED
            super.onPipelineSetup(builder, mesh, ctx)
        }

        override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
            val deferredCameraNode = model.findNode<DeferredCameraNode>("deferredCam")
            deferredCameraNode?.let { it.sceneCam = mrtPass.camera }

            val positionAoSampler = model.findNode<TextureNode>("positionAo")?.sampler
            positionAoSampler?.let { it.texture = mrtPass.positionAo }
            val normalRoughnessSampler = model.findNode<TextureNode>("normalRoughness")?.sampler
            normalRoughnessSampler?.let { it.texture = mrtPass.normalRoughness }

            val ssrSampler = model.findNode<TextureNode>("ssrMap")?.sampler
            ssrSampler?.let { it.texture = pbrLightingPass.colorTexture }
            val ssrNoiseSampler = model.findNode<TextureNode>("ssrNoiseTex")?.sampler
            ssrNoiseSampler?.let { it.texture = noiseTex }

            uRoughThreshLow = model.findNode("uRoughThreshLow")
            uRoughThreshLow?.uniform?.value = roughnessThresholdLow
            uRoughThreshHigh = model.findNode("uRoughThreshHigh")
            uRoughThreshHigh?.uniform?.value = roughnessThresholdHigh
            uMaxIterations = model.findNode("uMaxIterations")
            uMaxIterations?.uniform?.value = scrSpcReflectionIterations
        }
    }

    private fun reflectionShaderModel() = ShaderModel("ReflectionShaderModel").apply {
        val ifTexCoords: StageInterfaceNode

        vertexStage {
            ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
            positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
        }
        fragmentStage {
            val coord = ifTexCoords.output

            val posAoTex = textureNode("positionAo")
            val mrtDeMultiplex = addNode(DeferredPbrShader.MrtDeMultiplexNode(stage)).apply {
                inPositionAo = textureSamplerNode(posAoTex, coord).outColor
                inNormalRough = textureSamplerNode(textureNode("normalRoughness"), coord).outColor
            }

            val roughnessWeight = addNode(DiscardRoughSurfacesNode(stage)).apply {
                inRoughness = mrtDeMultiplex.outRoughness
                inThreshLow = pushConstantNode1f("uRoughThreshLow").output
                inThreshHigh = pushConstantNode1f("uRoughThreshHigh").output
            }

            val defCam = addNode(DeferredCameraNode(stage))
            val ssrNoiseTex = textureNode("ssrNoiseTex")
            val noise = noiseTextureSamplerNode(ssrNoiseTex, constVec2i(Vec2i(NOISE_SIZE, NOISE_SIZE))).outNoise
            val sceneColorTex = textureNode("ssrMap")
            val viewPos = mrtDeMultiplex.outViewPos
            val viewDir = normalizeNode(viewPos).output
            val rayOffset = splitNode(noise, "a").output
            val rayDir = reflectNode(viewDir, normalizeNode(mrtDeMultiplex.outViewNormal).output).outDirection

            val rayDirNoise = vecFromColorNode(splitNode(noise, "xyz").output).output
            val rayDirMod = multiplyNode(rayDirNoise, mrtDeMultiplex.outRoughness).output
            val roughRayDir = normalizeNode(addNode(rayDir, rayDirMod).output).output

            val rayTraceNode = addNode(ScreenSpaceRayTraceNode(posAoTex, stage)).apply {
                inProjMat = defCam.outProjMat
                inRayOrigin = viewPos
                inRayDirection = roughRayDir
                inRayOffset = rayOffset

                maxIterations = pushConstantNode1i("uMaxIterations").output
                baseStepFac = constFloat(baseReflectionStep)
            }

            val color = textureSamplerNode(sceneColorTex, rayTraceNode.outSamplePos).outColor
            val alpha = multiplyNode(rayTraceNode.outSampleWeight, roughnessWeight.outWeight).output
            val srgb = gammaNode(color, constFloat(2.2f)).outColor
            colorOutput(combineXyzWNode(srgb, alpha).output)
        }
    }

    private fun generateScrSpcReflectionNoiseTex(): Texture {
        val sz = NOISE_SIZE
        val buf = createUint8Buffer(sz * sz * 4)
        val rand = Random(0x1deadb0b)
        val vec = MutableVec3f()
        for (i in 0 until (sz * sz)) {
            do {
                vec.set(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), rand.randomF(-1f, 1f))
            } while (vec.length() > 1f)
            vec.norm().scale(0.25f)
            buf[i * 4 + 0] = ((vec.x + 1f) * 127.5f).toInt().toByte()
            buf[i * 4 + 1] = ((vec.y + 1f) * 127.5f).toInt().toByte()
            buf[i * 4 + 2] = ((vec.z + 1f) * 127.5f).toInt().toByte()
            buf[i * 4 + 3] = rand.randomI(0..255).toByte()
        }
        val data = BufferedTextureData(buf, sz, sz, TexFormat.RGBA)
        val texProps = TextureProps(TexFormat.RGBA, AddressMode.REPEAT, AddressMode.REPEAT,
                minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST,
                mipMapping = false, maxAnisotropy = 1)
        return Texture("ssr_noise_tex", texProps) { data }
    }

    private class DiscardRoughSurfacesNode(graph: ShaderGraph) : ShaderNode("discardRough", graph) {
        var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0f))
        var inThreshHigh = ShaderNodeIoVar(ModelVar1fConst(0.6f))
        var inThreshLow = ShaderNodeIoVar(ModelVar1fConst(0.5f))

        var outWeight = ShaderNodeIoVar(ModelVar1f("outRoughWeight"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inRoughness, inThreshHigh, inThreshLow)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                if (${inRoughness.ref1f()} > ${inThreshHigh.ref1f()}) discard;
                ${outWeight.declare()} = 1.0 - (clamp(${inRoughness.ref1f()}, ${inThreshLow.ref1f()}, ${inThreshHigh.ref1f()}) - ${inThreshLow.ref1f()})
                        / (${inThreshHigh.ref1f()} - ${inThreshLow.ref1f()});
            """)
        }
    }

    companion object {
        const val NOISE_SIZE = 4
    }
}