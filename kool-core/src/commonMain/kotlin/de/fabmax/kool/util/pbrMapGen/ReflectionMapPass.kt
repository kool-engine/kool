package de.fabmax.kool.util.pbrMapGen

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import kotlin.math.PI

class ReflectionMapPass(val parentScene: Scene, hdriTexture: Texture) : OffscreenRenderPassCube(Group(), 256, 256, 7, TexFormat.RGBA_F16) {
    var hdriTexture = hdriTexture
        set(value) {
            reflMapShader?.texture = value
            field = value
        }

    private var mipIdx = 0

    private val uRoughness = Uniform1f(0.5f, "uRoughness")
    private var reflMapShader: ModeledShader.TextureColor? = null

    init {
        clearColor = null

        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS)) {
                isFrustumChecked = false
                generate {
                    cube { centered() }
                }

                val texName = "colorTex"
                val model = ShaderModel("Reflectance Convolution Sampler").apply {
                    val ifLocalPos: StageInterfaceNode
                    vertexStage {
                        ifLocalPos = stageInterfaceNode("ifLocalPos", attrPositions().output)
                        positionOutput = simpleVertexPositionNode().outVec4
                    }
                    fragmentStage {
                        val roughness = pushConstantNode1f(uRoughness)
                        val tex = textureNode(texName)
                        val convNd = addNode(ConvoluteReflectionNode(tex, stage)).apply {
                            inLocalPos = ifLocalPos.output
                            inRoughness = roughness.output
                        }
                        colorOutput(convNd.outColor)
                    }
                }
                reflMapShader = ModeledShader.TextureColor(hdriTexture, texName, model).apply {
                    onSetup += { it.cullMethod = CullMethod.CULL_FRONT_FACES }
                }
                pipelineLoader = reflMapShader
            }
        }

        update()

        parentScene.onDispose += { ctx ->
            this@ReflectionMapPass.dispose(ctx)
        }
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        uRoughness.value = mipIdx.toFloat() / (mipLevels - 1)
        targetMipLevel = mipIdx
        if (++mipIdx >= mipLevels) {
            parentScene.removeOffscreenPass(this)
        }
        super.collectDrawCommands(ctx)
    }

    fun update() {
        mipIdx = 0
        if (this !in parentScene.offscreenPasses) {
            parentScene.addOffscreenPass(this)
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private class ConvoluteReflectionNode(val texture: TextureNode, graph: ShaderGraph) : ShaderNode("convIrradiance", graph) {
        var inLocalPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
        var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0f))
        var maxLightIntensity = ShaderNodeIoVar(ModelVar1fConst(5000f))
        val outColor = ShaderNodeIoVar(ModelVar4f("convReflection_outColor"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inLocalPos)
            dependsOn(maxLightIntensity)
            dependsOn(texture)
        }

        override fun generateCode(generator: CodeGenerator) {
            super.generateCode(generator)

            generator.appendFunction("reflMapFuncs", """
                const vec2 invAtan = vec2(0.1591, 0.3183);
                vec3 sampleEquiRect(vec3 texCoord, float mipLevel) {
                    vec3 equiRect_in = normalize(texCoord);
                    vec2 uv = vec2(atan(equiRect_in.z, equiRect_in.x), -asin(equiRect_in.y));
                    uv *= invAtan;
                    uv += 0.5;
                    
                    // decode rgbe
                    vec4 rgbe = ${generator.sampleTexture2d(texture.name, "uv", "mipLevel")};
                    vec3 fRgb = rgbe.rgb;
                    float fExp = rgbe.a * 255.0 - 128.0;
                    return min(fRgb * pow(2.0, fExp), vec3(${maxLightIntensity.ref1f()}));
                }
                
                float RadicalInverse_VdC(uint bits) {
                    bits = (bits << 16u) | (bits >> 16u);
                    bits = ((bits & 0x55555555u) << 1u) | ((bits & 0xAAAAAAAAu) >> 1u);
                    bits = ((bits & 0x33333333u) << 2u) | ((bits & 0xCCCCCCCCu) >> 2u);
                    bits = ((bits & 0x0F0F0F0Fu) << 4u) | ((bits & 0xF0F0F0F0u) >> 4u);
                    bits = ((bits & 0x00FF00FFu) << 8u) | ((bits & 0xFF00FF00u) >> 8u);
                    return float(bits) * 2.3283064365386963e-10; // / 0x100000000
                }
                
                vec2 Hammersley(uint i, uint N) {
                    return vec2(float(i)/float(N), RadicalInverse_VdC(i));
                }
                
                vec3 ImportanceSampleGGX(vec2 Xi, vec3 N, float roughness) {
                    float a = roughness*roughness;
                    
                    float phi = 2.0 * $PI * Xi.x;
                    float cosTheta = sqrt((1.0 - Xi.y) / (1.0 + (a*a - 1.0) * Xi.y));
                    float sinTheta = sqrt(1.0 - cosTheta*cosTheta);
                    
                    // from spherical coordinates to cartesian coordinates
                    vec3 H;
                    H.x = cos(phi) * sinTheta;
                    H.y = sin(phi) * sinTheta;
                    H.z = cosTheta;
                    
                    // from tangent-space vector to world-space sample vector
                    vec3 up = abs(N.z) < 0.9999 ? vec3(0.0, 0.0, 1.0) : vec3(1.0, 0.0, 0.0);
                    vec3 tangent = normalize(cross(up, N));
                    vec3 bitangent = cross(N, tangent);
                    
                    vec3 sampleVec = tangent * H.x + bitangent * H.y + N * H.z;
                    return normalize(sampleVec);
                }
            """)

            generator.appendMain("""
                vec3 N = normalize(${inLocalPos.ref3f()});
                vec3 R = N;
                vec3 V = R;
                
                float mipLevel = ${inRoughness.ref1f()} * 16.0;
                uint SAMPLE_COUNT = uint(1024.0 * (1.0 + mipLevel));
                float totalWeight = 0.0;
                vec3 prefilteredColor = vec3(0.0);
                for(uint i = 0u; i < SAMPLE_COUNT; ++i) {
                    vec2 Xi = Hammersley(i, SAMPLE_COUNT);
                    vec3 H  = ImportanceSampleGGX(Xi, N, ${inRoughness.ref1f()});
                    vec3 L  = normalize(2.0 * dot(V, H) * H - V);
            
                    float NdotL = max(dot(N, L), 0.0);
                    if(NdotL > 0.0) {
                        prefilteredColor += sampleEquiRect(L, mipLevel).rgb * NdotL;
                        totalWeight += NdotL;
                    }
                }
                prefilteredColor = prefilteredColor / totalWeight;
                ${outColor.declare()} = vec4(prefilteredColor, 1.0);
            """)
        }
    }
}