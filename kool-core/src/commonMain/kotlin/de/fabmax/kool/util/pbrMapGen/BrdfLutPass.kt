package de.fabmax.kool.util.pbrMapGen

import de.fabmax.kool.OffscreenPass2d
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*
import kotlin.math.PI


class BrdfLutPass {
    val offscreenPass: OffscreenPass2d
    val brdfLut: Texture
        get() = offscreenPass.impl.texture

    init {
        offscreenPass = OffscreenPass2d(512, 512, 1, TexFormat.RG_F16).apply {
            isSingleShot = true
            scene = scene {
                camera = OrthographicCamera().apply {
                    isKeepAspectRatio = false
                    left = 0f
                    right = 1f
                    top = 1f
                    bottom = 0f
                }
                +mesh(setOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                    generator = {
                        rect {
                            size.set(1f, 1f)
                            fullTexCoords()
                        }
                    }

                    pipelineConfig {
                        shaderLoader = { mesh, buildCtx, ctx ->
                            val model = ShaderModel("BRDF LUT").apply {
                                val ifTexCoords: StageInterfaceNode
                                vertexStage {
                                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                                    positionOutput = simpleVertexPositionNode().outPosition
                                }
                                fragmentStage {
                                    val lutNd = addNode(BrdfLutNode(stage)).apply {
                                        inTexCoords = ifTexCoords.output
                                    }
                                    colorOutput = lutNd.outColor
                                }
                            }
                            ModeledShader.VertexColor(model).setup(mesh, buildCtx, ctx)
                        }
                    }
                }
            }
        }
    }

    private class BrdfLutNode(graph: ShaderGraph) : ShaderNode("brdfLut", graph) {
        var inTexCoords = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
        val outColor = ShaderNodeIoVar(ModelVar4f("brdfLut_outColor"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inTexCoords)
        }

        override fun generateCode(generator: CodeGenerator) {
            super.generateCode(generator)

            generator.appendFunction("brdfLut", """
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
                
                float GeometrySchlickGGX(float NdotV, float roughness) {
                    float a = roughness;
                    float k = (a * a) / 2.0;
                
                    float nom   = NdotV;
                    float denom = NdotV * (1.0 - k) + k;
                
                    return nom / denom;
                }
                
                float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
                    float NdotV = max(dot(N, V), 0.0);
                    float NdotL = max(dot(N, L), 0.0);
                    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
                    float ggx1 = GeometrySchlickGGX(NdotL, roughness);
                
                    return ggx1 * ggx2;
                }
                
                vec2 IntegrateBRDF(float NdotV, float roughness) {
                    vec3 V;
                    V.x = sqrt(1.0 - NdotV*NdotV);
                    V.y = 0.0;
                    V.z = NdotV;
                
                    float A = 0.0;
                    float B = 0.0;
                
                    vec3 N = vec3(0.0, 0.0, 1.0);
                
                    const uint SAMPLE_COUNT = 1024u;
                    for(uint i = 0u; i < SAMPLE_COUNT; ++i) {
                        vec2 Xi = Hammersley(i, SAMPLE_COUNT);
                        vec3 H  = ImportanceSampleGGX(Xi, N, roughness);
                        vec3 L  = normalize(2.0 * dot(V, H) * H - V);
                
                        float NdotL = max(L.z, 0.0);
                        float NdotH = max(H.z, 0.0);
                        float VdotH = max(dot(V, H), 0.0);
                
                        if(NdotL > 0.0) {
                            float G = GeometrySmith(N, V, L, roughness);
                            float G_Vis = (G * VdotH) / (NdotH * NdotV);
                            float Fc = pow(1.0 - VdotH, 5.0);
                
                            A += (1.0 - Fc) * G_Vis;
                            B += Fc * G_Vis;
                        }
                    }
                    A /= float(SAMPLE_COUNT);
                    B /= float(SAMPLE_COUNT);
                    return vec2(A, B);
                }
            """)

            generator.appendMain("""
                vec2 integratedBRDF = IntegrateBRDF(${inTexCoords.ref2f()}.x, ${inTexCoords.ref2f()}.y);
                ${outColor.declare()} = vec4(integratedBRDF, 0.0, 1.0);
                //${outColor.declare()} = vec4(1.0, 0.0, 0.0, 1.0);
            """)
        }
    }
}