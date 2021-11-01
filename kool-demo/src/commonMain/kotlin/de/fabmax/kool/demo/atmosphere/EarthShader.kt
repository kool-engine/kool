package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.util.deferred.DeferredPbrShader

class EarthShader(textures: Map<String, Texture2d>, cfg: PbrMaterialConfig = shaderConfig(textures)) : DeferredPbrShader(cfg, model(cfg)) {

    private var tHeightMap: TextureSampler2d? = null
    var heightMap: Texture2d? = null
        set(value) {
            field = value
            tHeightMap?.texture = value
        }
    private var tOcean: TextureSampler2d? = null
    var oceanNrmTex: Texture2d? = null
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
            tOcean = model.findNode<Texture2dNode>("tOceanNrm1")?.sampler
            tOcean?.texture = oceanNrmTex

            tHeightMap = model.findNode<Texture2dNode>("tHeightMap")?.sampler
            tHeightMap?.texture = heightMap
        }
    }

    companion object {
        const val texEarthDay = "Earth by Day"
        const val texEarthNight = "Earth by Night"
        const val texEarthNrm = "Earth Normal Map"
        const val texEarthHeight = "Earth Height Map"
        const val texOceanNrm = "Ocean Normal Map"
        const val texLightGradient = "Light Gradient"

        fun shaderConfig(textures: Map<String, Texture2d>): PbrMaterialConfig {
            return PbrMaterialConfig().apply {
                useAlbedoMap(textures[texEarthDay])
                useEmissiveMap(textures[texEarthNight])
                useNormalMap(textures[texEarthNrm])

                isInstanced = true
            }
        }

        fun model(cfg: PbrMaterialConfig): ShaderModel{
            return defaultMrtPbrModel(cfg).apply {
                val ifSunDirViewSpace: StageInterfaceNode
                val ifNormalViewSpaceNoBump: StageInterfaceNode
                val ifNormalLocalNoBump: StageInterfaceNode

                vertexStage {
                    val mvp = findNodeByType<UniformBufferMvp>()!!
                    val modelViewMat = multiplyNode(mvp.outViewMat, mvp.outModelMat).output
                    val modelViewInstMat = findNode<NamedVariableNode>("modelViewMat")!!.output
                    val dirToSun = pushConstantNode3f("uDirToSun").output
                    val viewSunDir = vec3TransformNode(dirToSun, modelViewMat, 0f).outVec3
                    ifSunDirViewSpace = stageInterfaceNode("ifSunDirViewSpace", viewSunDir)

                    val instModelMat = instanceAttrModelMat().output
                    val spherePos = addNode(SpherePosNode(stage)).apply {
                        inModelMat = instModelMat
                        inTileName = instanceAttributeNode(SphereGridSystem.ATTRIB_TILE_NAME).output
                        inXyPos = attrTexCoords().output
                    }

                    val heightTex = texture2dNode("tHeightMap")
                    val heightMap = addNode(HeightMapNode(heightTex, stage)).apply {
                        inTileName = instanceAttributeNode(SphereGridSystem.ATTRIB_TILE_NAME).output
                        inEdgeFlag = attributeNode(SphereGridSystem.ATTRIB_EDGE_FLAG).output
                        inEdgeMask = instanceAttributeNode(SphereGridSystem.ATTRIB_EDGE_MASK).output
                        inRawTexCoord = attrTexCoords().output
                        inModelMat = instModelMat

                        inPosition = spherePos.outPos
                        inNormal = spherePos.outNrm
                        inTangent = spherePos.outTan
                        inTexCoord = spherePos.outTex
                        inStrength = constFloat(0.005f)
                        inNrmStrength = constFloat(3f)
                    }

                    findNode<NamedVariableNode>("texCoordInput")?.input = spherePos.outTex
                    findNode<NamedVariableNode>("localTangentInput")?.input = spherePos.outTan

                    findNode<NamedVariableNode>("localPosDisplaced")?.input = heightMap.outPosition
                    findNode<NamedVariableNode>("localNormalInput")?.input = heightMap.outNormal

                    ifNormalLocalNoBump = stageInterfaceNode("ifNormalLocalNoBump", spherePos.outNrm)
                    ifNormalViewSpaceNoBump = stageInterfaceNode("ifNormalNoBumpViewSpace", vec3TransformNode(spherePos.outNrm, modelViewInstMat, 0f).outVec3)
                }
                fragmentStage {
                    val mrtOutput = findNodeByType<MrtMultiplexNode>()!!
                    val ifTangent = findNode<StageInterfaceNode.OutputNode>("ifTangents")!!
                    val dayAlbedo = mrtOutput.inAlbedo

                    val roughnessNd = addNode(EarthRoughnessNode(dayAlbedo, stage))
                    mrtOutput.inRoughness = roughnessNd.outRoughness

                    val oceanNd = addNode(EarthOceanNode(stage)).apply {
                        inOceanTex1 = texture2dNode("tOceanNrm1")
                        inAlbedo = dayAlbedo
                        inIsOcean = roughnessNd.outIsOcean
                        inNormal = ifNormalLocalNoBump.output
                        inBumpNormal = mrtOutput.inViewNormal
                        inTangent = ifTangent.output

                        mrtOutput.inViewNormal = outBumpNormal
                    }

                    addNode(EarthDayNightMixNode(stage)).apply {
                        inNormal = normalizeNode(ifNormalViewSpaceNoBump.output).output
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
        val outEmissive = ShaderNodeIoVar(ModelVar3f("${name}_outEmissive"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inNormal, inSunDir, inAlbedo, inEmissive)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
            float ${name}_mix = (clamp(dot($inNormal, $inSunDir), -0.05, 0.05) + 0.05) * 10.0;
            ${outAlbedo.declare()} = ${inAlbedo.ref4f()} * ${name}_mix;
            ${outEmissive.declare()} = ${inEmissive.ref3f()} * (1.0 - ${name}_mix) * 0.6;
        """)
        }
    }
}