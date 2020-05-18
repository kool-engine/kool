package de.fabmax.kool.util.aoMapGen

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.createUint8Buffer
import kotlin.math.*
import kotlin.random.Random

class AmbientOcclusionPass(screenCam: Camera, depthPass: NormalLinearDepthMapPass) : OffscreenRenderPass2d(Group(), depthPass.texWidth, depthPass.texHeight, colorFormat = TexFormat.R) {

    var radius = 1f
    var intensity = 1.5f
    var bias = 0.05f
    var kernelSz = 32
        set(value) {
            field = value
            setKernelSize(value)
        }

    private var aoNode: AoNode? = null

    init {
        camera = OrthographicCamera().apply {
            projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN
            isKeepAspectRatio = false
            left = 0f
            right = 1f
            top = 1f
            bottom = 0f
        }

        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
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
                        colorOutput(aoNd.outColor)
                    }
                }
                pipelineLoader = ModeledShader(model).apply {
                    onCreated += {
                        model.findNode<TextureNode>("linearDepthMap")!!.sampler.texture = depthPass.colorTexture
                        model.findNode<TextureNode>("noiseTex")!!.sampler.texture = makeNoiseTexture()
                        aoNode = model.findNode("aoNode")!!
                        setKernelSize(kernelSz)
                    }
                }
            }
        }
    }

    private fun setKernelSize(nKernels: Int) {
        val n = min(nKernels, MAX_KERNEL_SIZE)
        aoNode?.apply {
            val scales = (0 until n)
                    .map { lerp(0.1f, 1f, (it.toFloat() / n).pow(2)) }
                    .shuffled(Random(17))

            for (i in 0 until n) {
                val xi = hammersley(i, n)
                val phi = 2f * PI.toFloat() * xi.x
                val cosTheta = sqrt((1f - xi.y))
                val sinTheta = sqrt(1f - cosTheta * cosTheta)

                val k = MutableVec3f(
                        sinTheta * cos(phi),
                        sinTheta * sin(phi),
                        -cosTheta
                )
                uKernel.value[i] = k.norm().scale(scales[i])
            }
            uKernelN.value = n
        }
    }

    private fun radicalInverse(pBits: Int): Float {
        var bits = pBits.toLong()
        bits = (bits shl 16) or (bits shr 16)
        bits = ((bits and 0x55555555) shl 1) or ((bits and 0xAAAAAAAA) shr 1)
        bits = ((bits and 0x33333333) shl 2) or ((bits and 0xCCCCCCCC) shr 2)
        bits = ((bits and 0x0F0F0F0F) shl 4) or ((bits and 0xF0F0F0F0) shr 4)
        bits = ((bits and 0x00FF00FF) shl 8) or ((bits and 0xFF00FF00) shr 8)
        return bits.toFloat() * 2.3283064365386963e-10f // / 0x100000000
    }

    private fun hammersley(i: Int, n: Int): Vec2f {
        return Vec2f(i.toFloat() / n.toFloat(), radicalInverse(i))
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
        val uKernelN = Uniform1i(32, "uKernelN")
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
                float bias = uBias * uRadius;
                for (int i = 0; i < uKernelN; i++) {
                    vec3 kernel = tbn * uKernel[i];
                    vec3 samplePos = origin + kernel * uRadius;
                    
                    vec4 sampleProj = uProj * vec4(samplePos, 1.0);
                    sampleProj.xyz /= sampleProj.w;
                    sampleProj.xy = sampleProj.xy * 0.5 + 0.5;
                    
                    float sampleDepth = ${generator.sampleTexture2d(depthTex.name, "sampleProj.xy")}.w;
                    
                    float rangeCheck = 1.0 - smoothstep(0.0, 1.0, abs(origin.z - sampleDepth) / (4.0 * uRadius));
                    occlusion += (sampleDepth < samplePos.z - bias ? 1.0 : 0.0) * rangeCheck;
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
