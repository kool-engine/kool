package de.fabmax.kool.util.aoMapGen

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.createUint8Buffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class AmbientOcclusionPass(screenCam: Camera, depthPass: NormalLinearDepthMapPass) : OffscreenRenderPass2D(Group(), depthPass.texWidth, depthPass.texHeight, colorFormat = TexFormat.R) {

    var radius = 1f
    var intensity = 1.5f
    var bias = 0f

    var kernelSz = 32
        set(value) {
            field = value
            generateKernel(value)
        }

    private var aoNode: AoNode? = null

    init {
        (drawNode as Group).apply {
            camera = OrthographicCamera().apply {
                projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN
                isKeepAspectRatio = false
                left = 0f
                right = 1f
                top = 1f
                bottom = 0f
            }

            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                generate {
                    rect {
                        size.set(1f, 1f)
                    }
                }

                val model = ShaderModel("AoPass").apply {
                    val ifTexCoords: StageInterfaceNode
                    vertexStage {
                        ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                        positionOutput = simpleVertexPositionNode().outPosition
                    }
                    fragmentStage {
                        val depthMap = textureNode("linearDepthMap")
                        val noiseTex = textureNode("noiseTex")
                        val aoNd = addNode(AoNode(screenCam, depthMap, noiseTex, stage))
                        aoNd.inScreenPos = ifTexCoords.output
                        colorOutput = aoNd.outColor
                    }
                }
                pipelineLoader = ModeledShader(model).apply {
                    onCreated += {
                        model.findNode<TextureNode>("linearDepthMap")!!.sampler.texture = depthPass.colorTexture
                        model.findNode<TextureNode>("noiseTex")!!.sampler.texture = makeNoiseTexture()
                        aoNode = model.findNode<AoNode>("aoNode")!!
                        generateKernel(kernelSz)
                    }
                }
            }
        }
    }

    private val rand = Random(17)

    private fun generateKernel(nKernels: Int) {
        val n = min(nKernels, MAX_KERNEL_SIZE)
        aoNode?.apply {
            var i = 0
            var seqI = 0
            while (i < n) {
                // generate random samples in a sphere (not hemisphere!)
                val k = MutableVec3f(
                        halton(seqI++, 2) * 2 - 1,
                        halton(seqI++, 2) * 2 - 1,
                        halton(seqI++, 2) * -1
                )

                if (k.length() < 1f) {
                    val iNorm = i.toFloat() / nKernels
                    val scale = lerp(0.1f, 1f, iNorm * iNorm)
                    uKernel.value[i++] = k.norm().scale(scale)
                }
            }
            uKernelN.value = nKernels
        }
    }

    /**
     * Low discrepancy pseudo random distribution
     * https://en.wikipedia.org/wiki/Halton_sequence
     */
    private fun halton(index: Int, base: Int): Float {
        var i = index
        var f = 1.0
        var r = 0.0
        while (i > 0) {
            f /= base
            r += f * (i % base)
            i /= base
        }
        return r.toFloat()
    }

    private fun lerp(a: Float, b: Float, f: Float): Float {
        return a + f * (b - a)
    }

    private fun makeNoiseTexture(): Texture {
        val buf = createUint8Buffer(4 * 16)
        val rotAngles = (0 until 16).map { PI.toFloat() * it / 8 }.shuffled()

        for (i in 0 until 16) {
            val ang = rotAngles[i]
            val x = cos(ang)
            val y = sin(ang)
            buf[i*4+0] = ((x * 0.5f + 0.5f) * 255).toByte()
            buf[i*4+1] = ((y * 0.5f + 0.5f) * 255).toByte()
            buf[i*4+2] = 0
            buf[i*4+3] = 1
        }

        val data = BufferedTextureData(buf, 4, 4, TexFormat.RGBA)
        val texProps = TextureProps(TexFormat.RGBA, AddressMode.REPEAT, AddressMode.REPEAT, minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST)
        return Texture(texProps) { data }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private inner class AoNode(val cam: Camera, val depthTex: TextureNode, val noiseTex: TextureNode, graph: ShaderGraph) : ShaderNode("aoNode", graph) {
        val uKernel = Uniform3fv("uKernel", MAX_KERNEL_SIZE)
        val uKernelN = Uniform1i("uKernelN")
        val uProj = UniformMat4f("uProj")
        val uInvProj = UniformMat4f("uInvProj")
        val uNoiseScale = Uniform2f("uNoiseScale")
        val uRadius = Uniform1f("uRadius")
        val uIntensity = Uniform1f("uIntensity")
        val uBias = Uniform1f("uBias")

        var inScreenPos = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))

        val outColor = ShaderNodeIoVar(ModelVar4f("colorOut"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            dependsOn(depthTex)
            dependsOn(noiseTex)
            dependsOn(inScreenPos)
            super.setup(shaderGraph)

            generateKernel(64)

            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uKernel }
                    +{ uProj }
                    +{ uInvProj }
                    +{ uNoiseScale }
                    +{ uRadius }
                    +{ uIntensity }
                    +{ uBias }
                    +{ uKernelN }
                    onUpdate = { _, _ ->
                        uProj.value.set(cam.proj)
                        uInvProj.value.set(cam.invProj)
                        uNoiseScale.value.set(texWidth / 4f, texHeight / 4f)
                        uRadius.value = radius
                        uIntensity.value = intensity
                        uBias.value = bias
                    }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                vec4 projPos = vec4(${inScreenPos.ref2f()} * 2.0 - vec2(1.0), 1.0, 1.0);
                vec4 viewPos = ${uInvProj.name} * projPos;
                
                vec4 nrmDepth = ${generator.sampleTexture2d(depthTex.name, inScreenPos.ref2f())};
                vec3 origin = viewPos.xyz / viewPos.w;
                origin *= (nrmDepth.w / origin.z);
                
                // compute kernel rotation
                vec2 noiseCoord = ${inScreenPos.ref2f()} * ${uNoiseScale.name};
                vec3 normal = normalize(nrmDepth.xyz * 2.0 - 1.0);
                vec3 rotVec = ${generator.sampleTexture2d(noiseTex.name, "noiseCoord")}.xyz * 2.0 - 1.0;
                vec3 tangent = normalize(rotVec - normal * dot(rotVec, normal));
                vec3 bitangent = cross(normal, tangent);
                mat3 tbn = mat3(tangent, bitangent, normal);
                
                float occlusion = 0.0;
                for (int i = 0; i < uKernelN; i++) {
                    vec3 kernel = tbn * uKernel[i];
                    vec3 samplePos = origin + kernel * uRadius;
                    
                    vec4 sampleProj = uProj * vec4(samplePos, 1.0);
                    sampleProj.xyz /= sampleProj.w;
                    sampleProj.xy = sampleProj.xy * 0.5 + 0.5;
                    
                    float sampleDepth = ${generator.sampleTexture2d(depthTex.name, "sampleProj.xy")}.w;
                    
                    float rangeCheck = 1.0 - smoothstep(0.0, 1.0, abs(origin.z - sampleDepth) / (4.0 * uRadius));
                    occlusion += (sampleDepth < samplePos.z - uBias ? 1.0 : 0.0) * rangeCheck;
                }
                occlusion /= float(uKernelN);
                float occlFac = clamp(1.0 - occlusion * uIntensity, 0.0, 1.0);
                
                ${outColor.declare()} = vec4(occlFac, occlFac, occlFac, 1.0);
            """)
        }
    }

    companion object {
        const val MAX_KERNEL_SIZE = 128
    }
}
