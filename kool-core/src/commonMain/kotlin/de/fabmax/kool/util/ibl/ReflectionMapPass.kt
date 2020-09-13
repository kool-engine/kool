package de.fabmax.kool.util.ibl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.logD
import kotlin.math.PI

class ReflectionMapPass private constructor(val parentScene: Scene, hdriMap: Texture2d?, cubeMap: TextureCube?) :
        OffscreenRenderPassCube(Group(), renderPassConfig {
            name = "ReflectionMapPass"
            setSize(256, 256)
            mipLevels = 7
            addColorTexture(TexFormat.RGBA_F16)
            clearDepthTexture()
        }) {

    private val uRoughness = Uniform1f(0.5f, "uRoughness")

    init {
        isEnabled = true

        onSetupMipLevel = { mipLevel, _ ->
            uRoughness.value = mipLevel.toFloat() / (config.mipLevels - 1)
        }

        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS), "reflectionMap") {
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
                        if (hdriMap != null) {
                            addNode(EnvEquiRectSamplerNode(texture2dNode(texName), stage))
                        } else {
                            addNode(EnvCubeSamplerNode(textureCubeNode(texName), stage))
                        }
                        val convNd = addNode(ConvoluteReflectionNode(stage)).apply {
                            inLocalPos = ifLocalPos.output
                            inRoughness = pushConstantNode1f(uRoughness).output
                        }
                        colorOutput(convNd.outColor)
                    }
                }
                if (hdriMap != null) {
                    shader = ModeledShader.TextureColor(hdriMap, texName, model).apply {
                        onPipelineSetup += { builder, _, _ -> builder.cullMethod = CullMethod.CULL_FRONT_FACES }
                    }
                } else {
                    shader = ModeledShader.CubeMapColor(cubeMap, texName, model).apply {
                        onPipelineSetup += { builder, _, _ -> builder.cullMethod = CullMethod.CULL_FRONT_FACES }
                    }
                }
            }
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterDraw += { ctx ->
            if (hdriMap != null) {
                logD { "Generated reflection map from HDRI: ${hdriMap.name}" }
            } else {
                logD { "Generated reflection map from cube map: ${cubeMap?.name}" }
            }
            parentScene.removeOffscreenPass(this)
            ctx.runDelayed(1) { dispose(ctx) }
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private class ConvoluteReflectionNode(graph: ShaderGraph) : ShaderNode("convIrradiance", graph) {
        var inLocalPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
        var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0f))
        var maxLightIntensity = ShaderNodeIoVar(ModelVar1fConst(5000f))
        val outColor = ShaderNodeIoVar(ModelVar4f("convReflection_outColor"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inLocalPos)
            dependsOn(maxLightIntensity)
        }

        override fun generateCode(generator: CodeGenerator) {
            super.generateCode(generator)

            generator.appendFunction("reflMapFuncs", """
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
                        prefilteredColor += sampleEnv(L, mipLevel) * NdotL;
                        totalWeight += NdotL;
                    }
                }
                prefilteredColor = prefilteredColor / totalWeight;
                ${outColor.declare()} = vec4(prefilteredColor, 1.0);
            """)
        }
    }

    companion object {
        fun reflectionMapFromHdri(scene: Scene, hdri: Texture2d) = ReflectionMapPass(scene, hdri, null)
        fun reflectionMapFromCube(scene: Scene, cube: TextureCube) = ReflectionMapPass(scene, null, cube)
    }
}