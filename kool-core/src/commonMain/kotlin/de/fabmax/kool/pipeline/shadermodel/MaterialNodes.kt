package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Color
import kotlin.math.PI

class UnlitMaterialNode(graph: ShaderGraph) : ShaderNode("Unlit Material", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inColor: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    val outColor = ShaderNodeIoVar(ModelVar4f("unlitMat_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inColor)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outColor.declare()} = vec4(${inColor.ref3f()} * ${inColor.ref4f()}.a, ${inColor.ref4f()}.a);")
    }
}

class PhongMaterialNode(graph: ShaderGraph) : ShaderNode("Phong Material", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inFragPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inCamPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    var inLightCount = ShaderNodeIoVar(ModelVar1iConst(1))
    var inFragToLight = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    var inRadiance = ShaderNodeIoVar(ModelVar3fConst(Vec3f(1f)))
    var inAmbient = ShaderNodeIoVar(ModelVar3fConst(Vec3f(0.22f)))
    var inShininess = ShaderNodeIoVar(ModelVar1fConst(20f))
    var inSpecularIntensity = ShaderNodeIoVar(ModelVar1fConst(1f))

    val outColor = ShaderNodeIoVar(ModelVar4f("phongMat_outColor"), this)

    var lightBacksides = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo, inNormal, inFragPos, inCamPos)
        dependsOn(inLightCount, inFragToLight, inRadiance, inAmbient, inShininess, inSpecularIntensity)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            vec3 phongMat_v = normalize(${inFragPos.ref3f()} - ${inCamPos.ref3f()});
            vec3 phongMat_n = normalize(${inNormal.ref3f()});
            
            vec3 phongMat_ambient = ${inAlbedo.ref3f()} * ${inAmbient.ref3f()};
            vec3 phongMat_diffuse = vec3(0);
            vec3 phongMat_specular = vec3(0);
            for (int i = 0; i < ${inLightCount.ref1i()}; i++) {
                vec3 phongMat_l = normalize(${inFragToLight.ref3f("i")});
                vec3 radiance = ${inRadiance.ref3f("i")};
                
                ${ if (lightBacksides) "if (dot(phongMat_n, phongMat_l) < 0.0) { phongMat_n *= -1.0; }" else "" }
        
                float phongMat_cosTheta = clamp(dot(phongMat_n, phongMat_l), 0.0, 1.0);
                vec3 phongMat_r = reflect(phongMat_l, phongMat_n);
                float phongMat_cosAlpha = clamp(dot(phongMat_v, phongMat_r), 0.0, 1.0);
                
                phongMat_diffuse += ${inAlbedo.ref4f()}.rgb * radiance * phongMat_cosTheta;
                phongMat_specular += ${inSpecularIntensity.ref1f()} * radiance *
                        pow(phongMat_cosAlpha, ${inShininess.ref1f()}) * ${inAlbedo.ref4f()}.a;
            } 
            
            vec3 phongMat_color = phongMat_ambient + phongMat_diffuse + phongMat_specular;
            ${outColor.declare()} = vec4(phongMat_color * ${inAlbedo.ref4f()}.a, ${inAlbedo.ref4f()}.a);
            """)
    }
}

/**
 * Physical Based Rendering Shader. Based on https://learnopengl.com/PBR/Lighting
 */
class PbrMaterialNode(val reflectionMap: TextureCubeNode?, val brdfLut: Texture2dNode?, graph: ShaderGraph) :
        ShaderNode("pbrMaterial", graph, ShaderStage.FRAGMENT_SHADER.mask) {

    var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inEmissive: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.BLACK))
    var inNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inFragPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inViewDir: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    var inMetallic = ShaderNodeIoVar(ModelVar1fConst(0.0f))
    var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0.1f))
    var inAmbientOccl = ShaderNodeIoVar(ModelVar1fConst(1f))

    var inLightCount = ShaderNodeIoVar(ModelVar1iConst(0))
    var inFragToLight = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    var inRadiance = ShaderNodeIoVar(ModelVar3fConst(Vec3f(0f)))
    var inIrradiance = ShaderNodeIoVar(ModelVar3fConst(Vec3f(0.03f)))
    var inReflectionStrength = ShaderNodeIoVar(ModelVar1fConst(1f))
    var inReflectionColor = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inReflectionWeight = ShaderNodeIoVar(ModelVar1fConst(0f))
    var inLightBacksides = ShaderNodeIoVar(ModelVar1iConst(0))

    val outColor = ShaderNodeIoVar(ModelVar4f("pbrMat_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo, inEmissive, inNormal, inFragPos, inViewDir)
        dependsOn(inMetallic, inRoughness, inAmbientOccl)
        dependsOn(inFragToLight, inRadiance, inIrradiance, inReflectionColor, inReflectionWeight)
        dependsOn(reflectionMap)
        dependsOn(brdfLut)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("fresnelSchlick", """
            vec3 fresnelSchlick(float cosTheta, vec3 F0) {
                return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
            }
        """)

        generator.appendFunction("fresnelSchlickRoughness", """
            vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness) {
                return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
            }
        """)

        generator.appendFunction("DistributionGGX", """
            float DistributionGGX(vec3 N, vec3 H, float roughness) {
                float a = roughness*roughness;
                float a2 = a*a;
                float NdotH = max(dot(N, H), 0.0);
                float NdotH2 = NdotH*NdotH;
            	
                float num = a2;
                float denom = (NdotH2 * (a2 - 1.0) + 1.0);
                denom = $PI * denom * denom;
            	
                return num / denom;
            }
        """)

        generator.appendFunction("GeometrySchlickGGX", """
            float GeometrySchlickGGX(float NdotV, float roughness) {
                float r = (roughness + 1.0);
                float k = (r*r) / 8.0;
                
                float num   = NdotV;
                float denom = NdotV * (1.0 - k) + k;
                
                return num / denom;
            }
        """)

        generator.appendFunction("GeometrySmith", """
            float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
                float NdotV = max(dot(N, V), 0.0);
                float NdotL = max(dot(N, L), 0.0);
                float ggx2  = GeometrySchlickGGX(NdotV, roughness);
                float ggx1  = GeometrySchlickGGX(NdotL, roughness);
                
                return ggx1 * ggx2;
            }
        """)

        generator.appendMain("""
            vec3 albedo = ${inAlbedo.ref3f()};
            vec3 V = -${inViewDir.ref3f()};
            vec3 N = ${inNormal.ref3f()};
            
            float rough = clamp(${inRoughness.ref1f()}, 0.05, 1.0);
            float metal = ${inMetallic.ref1f()};
            
            vec3 F0 = vec3(0.04); 
            F0 = mix(F0, albedo, metal);
    
            vec3 Lo = vec3(0.0);
            
            for (int i = 0; i < ${inLightCount.ref1i()}; i++) {
                // calculate per-light radiance
                vec3 L = normalize(${inFragToLight.ref3f("i")});
                vec3 H = normalize(V + L);
                vec3 radiance = ${inRadiance.ref3f("i")};
        
                vec3 lightN = N;
                if (${inLightBacksides.ref1i()} != 0) {
                    lightN *= sign(dot(lightN, L));
                }
        
                // cook-torrance BRDF
                float NDF = DistributionGGX(lightN, H, rough); 
                float G = GeometrySmith(lightN, V, L, rough);
                vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);
                
                vec3 kS = F;
                vec3 kD = vec3(1.0) - kS;
                kD *= 1.0 - metal;
                
                vec3 numerator = NDF * G * F;
                float denominator = 4.0 * max(dot(lightN, V), 0.0) * max(dot(lightN, L), 0.0);
                vec3 specular = numerator / max(denominator, 0.001);
                    
                // add to outgoing radiance Lo
                float NdotL = max(dot(lightN, L), 0.0);
                Lo += (kD * albedo / $PI + specular) * radiance * NdotL;
            }
            """)

        if (reflectionMap != null && brdfLut != null) {
            generateFinalIbl(generator, reflectionMap, brdfLut)
        } else {
            generateFinalNonIbl(generator)
        }
    }

    private fun generateFinalNonIbl(generator: CodeGenerator) {
        generator.appendMain("""
            vec3 kS = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, rough);
            vec3 kD = 1.0 - kS;
            vec3 diffuse = ${inIrradiance.ref3f()} * albedo;
            vec3 ambient = (kD * diffuse) * ${inAmbientOccl.ref1f()};

            vec3 color = (ambient + Lo + ${inEmissive.ref3f()}) * ${inAlbedo.ref4f()}.a;
            ${outColor.declare()} = vec4(color, ${inAlbedo.ref4f()}.a);
        """)
    }

    private fun generateFinalIbl(generator: CodeGenerator, reflectionMap: TextureCubeNode, brdfLut: Texture2dNode) {
        generator.appendMain("""
            vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, rough);
            vec3 kS = F;
            vec3 kD = 1.0 - kS;
            kD *= 1.0 - metal;
            vec3 diffuse = ${inIrradiance.ref3f()} * albedo;

            // sample reflection map
            vec3 R = reflect(-V, N);
            const float MAX_REFLECTION_LOD = 6.0;
            vec3 prefilteredColor = ${generator.sampleTexture2d(reflectionMap.name, "R", "rough * MAX_REFLECTION_LOD")}.rgb;
            prefilteredColor = mix(prefilteredColor, clamp(${inReflectionColor.ref3f()}, 0.0, 5.0), ${inReflectionWeight.ref1f()});

            vec2 brdfUv = vec2(max(dot(N, V), 0.0), rough);
            vec2 envBRDF = ${generator.sampleTexture2d(brdfLut.name, "brdfUv")}.rg;
            vec3 specular = prefilteredColor * (F * envBRDF.x + envBRDF.y);
            vec3 ambient = (kD * diffuse) * ${inAmbientOccl.ref1f()};
            vec3 reflection = specular * ${inAmbientOccl.ref1f()} * ${inReflectionStrength.ref1f()};
            vec3 color = (ambient + Lo + ${inEmissive.ref3f()}) * ${inAlbedo.ref4f()}.a + reflection;
            ${outColor.declare()} = vec4(color, ${inAlbedo.ref4f()}.a);
        """)
    }
}

/**
 * Physical Based Rendering Light Shader. Only computes color contribution of a single light for deferred lighting.
 */
class PbrLightNode(graph: ShaderGraph) :
        ShaderNode("pbrLightNode", graph, ShaderStage.FRAGMENT_SHADER.mask) {

    var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inFragPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inCamPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    var inMetallic = ShaderNodeIoVar(ModelVar1fConst(0.0f))
    var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0.1f))

    var inFragToLight = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    var inRadiance = ShaderNodeIoVar(ModelVar3fConst(Vec3f(1f)))
    var inIrradiance = ShaderNodeIoVar(ModelVar3fConst(Vec3f(0.03f)))

    val outColor = ShaderNodeIoVar(ModelVar4f("pbrLight_outColor"), this)

    var lightBacksides = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo, inNormal, inFragPos, inCamPos, inFragToLight, inRadiance, inIrradiance)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("fresnelSchlick", """
            vec3 fresnelSchlick(float cosTheta, vec3 F0) {
                return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
            }
        """)

        generator.appendFunction("DistributionGGX", """
            float DistributionGGX(vec3 N, vec3 H, float roughness) {
                float a = roughness*roughness;
                float a2 = a*a;
                float NdotH = max(dot(N, H), 0.0);
                float NdotH2 = NdotH*NdotH;
            	
                float num = a2;
                float denom = (NdotH2 * (a2 - 1.0) + 1.0);
                denom = $PI * denom * denom;
            	
                return num / denom;
            }
        """)

        generator.appendFunction("GeometrySchlickGGX", """
            float GeometrySchlickGGX(float NdotV, float roughness) {
                float r = (roughness + 1.0);
                float k = (r*r) / 8.0;
                
                float num   = NdotV;
                float denom = NdotV * (1.0 - k) + k;
                
                return num / denom;
            }
        """)

        generator.appendFunction("GeometrySmith", """
            float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
                float NdotV = max(dot(N, V), 0.0);
                float NdotL = max(dot(N, L), 0.0);
                float ggx2  = GeometrySchlickGGX(NdotV, roughness);
                float ggx1  = GeometrySchlickGGX(NdotL, roughness);
                
                return ggx1 * ggx2;
            }
        """)

        val normalCheck = if (!lightBacksides) "dot($inFragToLight, ${inNormal.ref3f()}) > 0.0" else "true"
        generator.appendMain("""
            vec3 radiance = vec3(0.0);
            bool normalOk = $normalCheck;
            if (normalOk) {
                radiance = ${inRadiance.ref3f()};
            }
            
            ${outColor.declare()} = vec4(0.0, 0.0, 0.0, 0.0);
            if (normalOk && dot(radiance, radiance) > 0.0) {
                vec3 albedo = ${inAlbedo.ref3f()};
                vec3 V = normalize(${inCamPos.ref3f()} - ${inFragPos.ref3f()});
                vec3 N = normalize(${inNormal.ref3f()});
                
                float rough = clamp(${inRoughness.ref1f()}, 0.05, 1.0);
                float metal = ${inMetallic.ref1f()};
                
                vec3 F0 = vec3(0.04); 
                F0 = mix(F0, albedo, metal);
        
                vec3 L = normalize($inFragToLight);
                vec3 H = normalize(V + L);
                ${ if (lightBacksides) "N *= sign(dot(N, L));" else "" }
        
                // cook-torrance BRDF
                float NDF = DistributionGGX(N, H, rough); 
                float G = GeometrySmith(N, V, L, rough);
                vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);
                
                vec3 kS = F;
                vec3 kD = vec3(1.0) - kS;
                kD *= 1.0 - metal;
                
                vec3 numerator = NDF * G * F;
                float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
                vec3 specular = numerator / max(denominator, 0.001);
                    
                // add to outgoing radiance Lo
                float NdotL = max(dot(N, L), 0.0);
                ${outColor.name} = vec4((kD * albedo / $PI + specular) * radiance * NdotL, 0.0);
            }
            """)
    }
}