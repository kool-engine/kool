package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.util.deferred.DeferredPbrShader

class EarthShader(textures: Map<String, Texture>, cfg: PbrMaterialConfig = shaderConfig(textures)) : DeferredPbrShader(cfg, model(cfg)) {

    private var tOcean: TextureSampler? = null
    var oceanNrmTex: Texture? = null
        set(value) {
            field = value
            tOcean?.texture = value
        }

    var uDirToSun: Uniform3f? = null
    var uNormalShift: Uniform4f? = null
    var uWaterColor: UniformColor? = null

    init {
        onPipelineCreated += { _, _, _ ->
            uDirToSun = model.findNode<PushConstantNode3f>("uDirToSun")?.uniform
            model.findNodeByType<EarthOceanNode>()?.let {
                uNormalShift = it.uNormalShift
                uWaterColor = it.uWaterColor
            }
            tOcean = model.findNode<TextureNode>("tOceanNrm1")?.sampler
            tOcean?.texture = oceanNrmTex
        }
    }

    companion object {
        const val texEarthDay = "Earth by Day"
        const val texEarthNight = "Earth by Night"
        const val texEarthNrm = "Earth Normal Map"
        const val texEarthHeight = "Earth Height Map"
        const val texOceanNrm = "Ocean Normal Map"

        fun shaderConfig(textures: Map<String, Texture>): PbrMaterialConfig {
            return PbrMaterialConfig().apply {
                useAlbedoMap(textures[texEarthDay])
                useDisplacementMap(textures[texEarthHeight])
                useEmissiveMap(textures[texEarthNight])
                useNormalMap(textures[texEarthNrm])

                displacementStrength = 1f
            }
        }

        fun model(cfg: PbrMaterialConfig): ShaderModel{
            return defaultMrtPbrModel(cfg).apply {
                val ifSunDirViewSpace: StageInterfaceNode
                val ifLocalNormal: StageInterfaceNode
                vertexStage {
                    val mvp = findNodeByType<UniformBufferMvp>()!!
                    val modelViewMat = multiplyNode(mvp.outViewMat, mvp.outModelMat).output
                    val dirToSun = pushConstantNode3f("uDirToSun").output
                    val viewSunDir = vec3TransformNode(dirToSun, modelViewMat, 0f).outVec3
                    ifSunDirViewSpace = stageInterfaceNode("ifSunDirViewSpace", viewSunDir)
                    ifLocalNormal = stageInterfaceNode("ifLocalNormal", attrNormals().output)
                }
                fragmentStage {
                    val mrtOutput = findNodeByType<MrtMultiplexNode>()!!
                    val ifTangent = findNode<StageInterfaceNode.OutputNode>("ifTangents")!!
                    val dayAlbedo = mrtOutput.inAlbedo

                    val roughnessNd = addNode(EarthRoughnessNode(dayAlbedo, stage))
                    mrtOutput.inRoughness = roughnessNd.outRoughness

                    val oceanNd = addNode(EarthOceanNode(stage)).apply {
                        inOceanTex1 = textureNode("tOceanNrm1")
                        inAlbedo = dayAlbedo
                        inIsOcean = roughnessNd.outIsOcean
                        inNormal = ifLocalNormal.output
                        inBumpNormal = mrtOutput.inViewNormal
                        inTangent = ifTangent.output

                        mrtOutput.inViewNormal = outBumpNormal
                    }

                    addNode(EarthDayNightMixNode(stage)).apply {
                        inNormal = normalizeNode(mrtOutput.inViewNormal).output
                        inSunDir = normalizeNode(ifSunDirViewSpace.output).output
                        inAlbedo = oceanNd.outColor
                        inEmissive = mrtOutput.inEmissive
                        mrtOutput.inAlbedo = outAlbedo
                        mrtOutput.inEmissive = outEmissive
                    }
                }
            }
        }
    }

    private class EarthDayNightMixNode(graph: ShaderGraph) : ShaderNode("earthDayNightMix", graph) {
        lateinit var inNormal: ShaderNodeIoVar
        lateinit var inSunDir: ShaderNodeIoVar
        lateinit var inAlbedo: ShaderNodeIoVar
        lateinit var inEmissive: ShaderNodeIoVar

        val outAlbedo = ShaderNodeIoVar(ModelVar4f("${name}_outAlbedo"), this)
        val outEmissive = ShaderNodeIoVar(ModelVar4f("${name}_outEmissive"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inNormal, inSunDir, inAlbedo, inEmissive)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
            float ${name}_mix = (clamp(dot($inNormal, $inSunDir), -0.05, 0.05) + 0.05) * 10.0;
            ${outAlbedo.declare()} = $inAlbedo * ${name}_mix;
            ${outEmissive.declare()} = $inEmissive * (1.0 - ${name}_mix) * 0.6;
        """)
        }
    }

    private class EarthRoughnessNode(val inAlbedo: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("earthRoughness", graph) {
        val outRoughness = ShaderNodeIoVar(ModelVar1f("earth_outRoughness"), this)
        val outIsOcean = ShaderNodeIoVar(ModelVar1i("earth_outIsOcean"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inAlbedo)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
            float ${name}_relBlue = min($inAlbedo.b - $inAlbedo.r, $inAlbedo.b - $inAlbedo.g) / $inAlbedo.b;
            float ${name}_brightness = dot($inAlbedo.rgb, vec3(0.333));
            
            ${outRoughness.declare()} = 1.0 - (0.3 + ${name}_brightness * 0.4);
            bool $outIsOcean = ${name}_relBlue > 0.2 || ${name}_brightness < 0.002;
            if ($outIsOcean) {
                $outRoughness = 0.22;
            }
        """)
        }
    }

    private class EarthOceanNode(graph: ShaderGraph) : ShaderNode("earthOcean", graph) {
        lateinit var inOceanTex1: TextureNode

        lateinit var inAlbedo: ShaderNodeIoVar
        lateinit var inIsOcean: ShaderNodeIoVar
        lateinit var inNormal: ShaderNodeIoVar
        lateinit var inBumpNormal: ShaderNodeIoVar
        lateinit var inTangent: ShaderNodeIoVar

        val outColor = ShaderNodeIoVar(ModelVar4f("ocean_outColor"), this)
        val outBumpNormal = ShaderNodeIoVar(ModelVar3f("ocean_outBump"), this)

        val uNormalShift = Uniform4f("uNormalShift")
        val uWaterColor = UniformColor("uWaterColor")

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inAlbedo, inIsOcean, inNormal, inBumpNormal, inTangent)

            shaderGraph.pushConstants.apply {
                +{ uNormalShift }
                +{ uWaterColor }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendFunction("cubeUv", """
                vec2 cubeUv(vec3 normal) {
                    vec3 absN = abs(normal);
                    bvec3 isPos = bvec3(normal.x > 0.0, normal.y > 0.0, normal.z > 0.0);
                
                    float maxAxis = 1.0;
                    vec2 uvc = vec2(1.0, 1.0);
                    
                    if (isPos.x && absN.x > absN.y && absN.x > absN.z) {
                        maxAxis = absN.x;
                        uvc = vec2(-normal.z, -normal.y);
                    } else if (!isPos.x && absN.x > absN.y && absN.x > absN.z) {
                        maxAxis = absN.x;
                        uvc = vec2(normal.z, -normal.y);
                        
                    } else if (isPos.y && absN.y > absN.x && absN.y > absN.z) {
                        maxAxis = absN.y;
                        uvc = vec2(normal.x, normal.z);
                    } else if (!isPos.y && absN.y > absN.x && absN.y > absN.z) {
                        maxAxis = absN.y;
                        uvc = vec2(normal.x, -normal.z);
                        
                    } else if (isPos.z && absN.z > absN.x && absN.z > absN.y) {
                        maxAxis = absN.z;
                        uvc = vec2(normal.x, -normal.y);
                    } else if (!isPos.z && absN.z > absN.x && absN.z > absN.y) {
                        maxAxis = absN.z;
                        uvc = vec2(-normal.x, -normal.y);
                    }
                    
                    return 0.5 * (uvc / maxAxis + 1.0);
                }
            """)

            generator.appendMain("""
                ${outColor.declare()} = ${inAlbedo.ref4f()};
                ${outBumpNormal.declare()} = ${inBumpNormal.ref3f()};
                if ($inIsOcean) {
                    vec2 oceanUvBase = cubeUv(normalize(${inNormal.ref3f()}));
                    float s = 400.0;
                    vec2 oceanUv1 = oceanUvBase * vec2(1.0 * s, 1.5 * s) + $uNormalShift.xy;
                    vec2 oceanUv2 = oceanUvBase * vec2(1.69751 * s, 1.27841 * s) + $uNormalShift.zw;
                    vec2 oceanUvStrength = oceanUvBase;
                    
                    vec3 oceanNrm1 = ${generator.sampleTexture2d(inOceanTex1.name, "oceanUv1")}.rgb * 2.0 - 1.0;
                    vec3 oceanNrm2 = ${generator.sampleTexture2d(inOceanTex1.name, "oceanUv2")}.rgb * 2.0 - 1.0;
                    vec3 oceanNrm = oceanNrm1 + oceanNrm2;
                    float nrmStrength = smoothstep(0.3, 0.6, ${generator.sampleTexture2d(inOceanTex1.name, "oceanUvStrength")}.r) * 0.7 + 0.3;
                    oceanNrm.xy *= nrmStrength;
                    
                    //$outColor = vec4(vec3(nrmStrength), 1.0);
                    $outColor = $uWaterColor;
                    $outBumpNormal = calcBumpedNormal(${inBumpNormal.ref3f()}, ${inTangent.ref4f()}, normalize(oceanNrm), 0.4);
                }
            """)
        }
    }
}