package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Color
import kotlin.math.PI

class UnlitMaterialNode(graph: ShaderGraph) : ShaderNode("Unlit Material", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    val outColor = ShaderNodeIoVar(ModelVar4f("unlitMat_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo)

        shaderGraph as FragmentShaderGraph
        shaderGraph.colorOutput = outColor
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outColor.declare()} = ${inAlbedo.ref4f()};")
    }
}

class PhongMaterialNode(val lightNode: LightNode, graph: ShaderGraph) : ShaderNode("Phong Material", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inFragPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inCamPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    var inSpotInnerAngle = ShaderNodeIoVar(ModelVar1fConst(0.8f))
    var inAmbient = ShaderNodeIoVar(ModelVar3fConst(Vec3f(0.03f)))
    var inShininess = ShaderNodeIoVar(ModelVar1fConst(20f))
    var inSpecularIntensity = ShaderNodeIoVar(ModelVar1fConst(1f))

    val outColor = ShaderNodeIoVar(ModelVar4f("phongMat_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo, inNormal, inFragPos, inCamPos)
        dependsOn(lightNode)

        shaderGraph as FragmentShaderGraph
        shaderGraph.colorOutput = outColor
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            vec3 phongMat_v = normalize(${inFragPos.ref3f()} - ${inCamPos.ref3f()});
            vec3 phongMat_n = normalize(${inNormal.ref3f()});
            
            vec3 phongMat_ambient = ${inAlbedo.ref3f()} * ${inAmbient.ref3f()};
            vec3 phongMat_diffuse = vec3(0);
            vec3 phongMat_specular = vec3(0);
            for (int i = 0; i < ${lightNode.outLightCount.name}; i++) {
                vec3 phongMat_l = ${lightNode.generateGetFragToLight("i", inFragPos.ref3f())};
                vec3 radiance = ${lightNode.generateGetRadiance("i", "phongMat_l", inSpotInnerAngle.ref1f())};
                phongMat_l = normalize(phongMat_l);
                
                float phongMat_cosTheta = clamp(dot(phongMat_n, phongMat_l), 0.0, 1.0);
                vec3 phongMat_r = reflect(phongMat_l, phongMat_n);
                float phongMat_cosAlpha = clamp(dot(phongMat_v, phongMat_r), 0.0, 1.0);
                
                phongMat_diffuse += ${inAlbedo.ref4f()}.rgb * radiance * phongMat_cosTheta;
                phongMat_specular += ${inSpecularIntensity.ref1f()} * radiance *
                        pow(phongMat_cosAlpha, ${inShininess.ref1f()}) * ${inAlbedo.ref4f()}.a;
            } 
            
            vec3 phongMat_color = phongMat_ambient + phongMat_diffuse + phongMat_specular;
            phongMat_color = phongMat_color / (phongMat_color + vec3(1.0));
            phongMat_color = pow(phongMat_color, vec3(1.0/2.2));  
            ${outColor.declare()} = vec4(phongMat_color, ${inAlbedo.ref4f()}.a);
            """)
    }
}

/**
 * Physical Based Rendering Shader. Based on https://learnopengl.com/PBR/Lighting
 */
class PbrMaterialNode(val lightNode: LightNode, graph: ShaderGraph) : ShaderNode("Phong Material", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inFragPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inCamPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    var inSpotInnerAngle = ShaderNodeIoVar(ModelVar1fConst(0.8f))
    var inAmbient = ShaderNodeIoVar(ModelVar3fConst(Vec3f(0.03f)))
    var inMetallic = ShaderNodeIoVar(ModelVar1fConst(0.0f))
    var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0.1f))

    val outColor = ShaderNodeIoVar(ModelVar4f("pbrMat_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo, inNormal, inFragPos, inCamPos)
        dependsOn(lightNode)

        shaderGraph as FragmentShaderGraph
        shaderGraph.colorOutput = outColor
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("fresnelSchlick", """
            vec3 fresnelSchlick(float cosTheta, vec3 F0) {
                return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
            }
        """)

        generator.appendFunction("DistributionGGX", """
            float DistributionGGX(vec3 N, vec3 H, float roughness) {
                float a      = roughness*roughness;
                float a2     = a*a;
                float NdotH  = max(dot(N, H), 0.0);
                float NdotH2 = NdotH*NdotH;
            	
                float num   = a2;
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
            vec3 V = normalize(${inCamPos.ref3f()} - ${inFragPos.ref3f()});
            vec3 N = normalize(${inNormal.ref3f()});
            
            vec3 F0 = vec3(0.04); 
            F0 = mix(F0, albedo, $inMetallic);
    
            vec3 Lo = vec3(0.0);
            for (int i = 0; i < ${lightNode.outLightCount}; i++) {
                // calculate per-light radiance
                vec3 fragToLight = ${lightNode.generateGetFragToLight("i", inFragPos.ref3f())};
                vec3 L = normalize(fragToLight);
                vec3 H = normalize(V + L);
                vec3 radiance = ${lightNode.generateGetRadiance("i", "fragToLight", inSpotInnerAngle.ref1f())};
        
                // cook-torrance BRDF
                float NDF = DistributionGGX(N, H, $inRoughness); 
                float G = GeometrySmith(N, V, L, $inRoughness);
                vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);
                
                vec3 kS = F;
                vec3 kD = vec3(1.0) - kS;
                kD *= 1.0 - $inMetallic;
                
                vec3 numerator = NDF * G * F;
                float denominator = 1;//4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
                vec3 specular = numerator / max(denominator, 0.001);
                    
                // add to outgoing radiance Lo
                float NdotL = max(dot(N, L), 0.0);
                Lo += (kD * albedo / $PI + specular) * radiance * NdotL;
            }
  
            vec3 ambient = $inAmbient * albedo;
            vec3 color = ambient + Lo;
            color = color / (color + vec3(1.0));
            color = pow(color, vec3(1.0/2.2));  
            ${outColor.declare()} = vec4(color, ${inAlbedo.ref4f()}.a);
            """)
    }
}