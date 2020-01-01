package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Color

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

class PhongMaterialNode(graph: ShaderGraph) : ShaderNode("Phong Material", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inEyeDir: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.NEG_Z_AXIS))
    var inLightDir: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.NEG_Z_AXIS))

    var inAmbient = ShaderNodeIoVar(ModelVar3fConst(Vec3f(0.4f)))
    var inShininess = ShaderNodeIoVar(ModelVar1fConst(20f))
    var inSpecularIntensity = ShaderNodeIoVar(ModelVar1fConst(0.75f))
    var inLightColor = ShaderNodeIoVar(ModelVar3fConst(Vec3f(1f, 1f, 1f)))

    val outColor = ShaderNodeIoVar(ModelVar4f("phongMat_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo, inNormal, inEyeDir, inLightDir)

        shaderGraph as FragmentShaderGraph
        shaderGraph.colorOutput = outColor
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            vec3 phongMat_e = normalize(${inEyeDir.ref3f()});
            vec3 phongMat_l = normalize(${inLightDir.ref3f()});
            vec3 phongMat_n = normalize(${inNormal.ref3f()});
            
            float phongMat_cosTheta = clamp(dot(phongMat_n, phongMat_l), 0.0, 1.0);
            vec3 phongMat_r = reflect(-phongMat_l, phongMat_n);
            float phongMat_cosAlpha = clamp(dot(phongMat_e, phongMat_r), 0.0, 1.0);

            vec3 phongMat_ambient = ${inAlbedo.ref4f()}.rgb * ${inAmbient.ref3f()};
            vec3 phongMat_diffuse = ${inAlbedo.ref4f()}.rgb * ${inLightColor.ref3f()} * phongMat_cosTheta;
            vec3 phongMat_specular = ${inLightColor.ref3f()} * ${inSpecularIntensity.ref1f()} * pow(phongMat_cosAlpha, ${inShininess.ref1f()}) * ${inAlbedo.ref4f()}.a; 
            
            ${outColor.declare()} = vec4(phongMat_ambient + phongMat_diffuse + phongMat_specular, ${inAlbedo.ref4f()}.a);
            """)
    }
}

class PhongMaterialMultiLightNode(val lightNode: LightNode, graph: ShaderGraph) : ShaderNode("Phong Material", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inFragPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inCamPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    var inAmbient = ShaderNodeIoVar(ModelVar3fConst(Vec3f(0.3f)))
    var inShininess = ShaderNodeIoVar(ModelVar1fConst(20f))
    var inSpecularIntensity = ShaderNodeIoVar(ModelVar1fConst(0.75f))

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
            vec3 phongMat_e = normalize(${inFragPos.ref3f()} - ${inCamPos.ref3f()});
            vec3 phongMat_n = normalize(${inNormal.ref3f()});
            
            vec3 phongMat_ambient = ${inAlbedo.ref4f()}.rgb * ${inAmbient.ref3f()};
            vec3 phongMat_diffuse = vec3(0);
            vec3 phongMat_specular = vec3(0);
            for (int i = 0; i < ${lightNode.outLightCount.name}; i++) {
                vec3 phongMat_l = ${lightNode.generateGetDirection("i", inFragPos.ref3f())};
                float strength = ${lightNode.generateGetStrength("i", "phongMat_l", "0.95")};
                phongMat_l = normalize(phongMat_l);
                
                float phongMat_cosTheta = clamp(dot(phongMat_n, phongMat_l), 0.0, 1.0);
                vec3 phongMat_r = reflect(phongMat_l, phongMat_n);
                float phongMat_cosAlpha = clamp(dot(phongMat_e, phongMat_r), 0.0, 1.0);
                
                phongMat_diffuse += ${inAlbedo.ref4f()}.rgb * ${lightNode.outLightColor.name}[i].rgb * phongMat_cosTheta * strength;
                phongMat_specular += ${lightNode.outLightColor.name}[i].rgb * ${inSpecularIntensity.ref1f()} *
                                    pow(phongMat_cosAlpha, ${inShininess.ref1f()}) * ${inAlbedo.ref4f()}.a * strength;
            } 
            
            ${outColor.declare()} = vec4(phongMat_ambient + phongMat_diffuse + phongMat_specular, ${inAlbedo.ref4f()}.a);
            """)
    }
}