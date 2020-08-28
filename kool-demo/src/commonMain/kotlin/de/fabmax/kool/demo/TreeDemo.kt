package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.Uniform1f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.cos
import kotlin.math.sqrt


/**
 * @author fabmax
 */

class TreeDemo : DemoScene("Procedural Tree") {

    val treeGen: TreeGenerator
    var trunkMesh: Mesh? = null
    var leafMesh: Mesh? = null

    var autoRotate = true
    var windSpeed = 2.5f
    var windAnimationPos = 0f
    var windStrength = 1f

    val lightDirection = MutableVec3f(-1f, -1.5f, -1f).norm()

    init {
        val w = 3f
        val h = 3.5f
        val dist = TreeTopPointDistribution(1f + h / 2f, w, h)
        //val dist = SphericalPointDistribution(2f, Vec3f(0f, 3f, 0f))
        //val dist = CubicPointDistribution(4f, Vec3f(0f, 3f, 0f))
        treeGen = TreeGenerator(dist, primaryLightDir = lightDirection)
        treeGen.generate()
    }

    override fun setupMainScene(ctx: KoolContext) = scene {
        lighting.singleLight {
            setDirectional(lightDirection).setColor(Color.MD_AMBER.mix(Color.WHITE, 0.6f).toLinear(), 5f)
        }
        val shadowMaps = mutableListOf(CascadedShadowMap(this, 0).apply { maxRange = 50f })
        val bgGradient = ColorGradient(
                0.00f to Color.fromHex("B2D7FF").mix(Color.BLACK, 0.75f),
                0.35f to Color.fromHex("B2D7FF").mix(Color.BLACK, 0.75f),
                0.45f to Color.fromHex("B2D7FF").mix(Color.BLACK, 0.25f),
                0.90f to Color.fromHex("3295FF").mix(Color.BLACK, 0.45f),
                1.00f to Color.fromHex("3295FF").mix(Color.BLACK, 0.50f)
        )
        val envMaps = EnvironmentHelper.gradientColorEnvironment(this, bgGradient, ctx)

        +makeTreeGroundGrid(10, shadowMaps, envMaps)
        +Skybox.cube(envMaps.reflectionMap)

        // generate tree trunk mesh
        trunkMesh = textureMesh(isNormalMapped = true) {
            generate {
                timedMs({"Generated ${geometry.numIndices / 3} trunk triangles in"}) {
                    treeGen.buildTrunkMesh(this)
                }
            }

            var uWindSpeed: Uniform1f? = null
            var uWindStrength: Uniform1f? = null
            val pbrCfg = PbrMaterialConfig().apply {
                useAlbedoMap("${Demo.pbrBasePath}/bark_pine/Bark_Pine_baseColor.jpg")
                useOcclusionMap("${Demo.pbrBasePath}/bark_pine/Bark_Pine_ambientOcclusion.jpg")
                useNormalMap("${Demo.pbrBasePath}/bark_pine/Bark_Pine_normal.jpg")
                useRoughnessMap("${Demo.pbrBasePath}/bark_pine/Bark_Pine_roughness.jpg")
                useImageBasedLighting(envMaps)
                this.shadowMaps.addAll(shadowMaps)
                roughness = 1f
            }
            // custom tree shader model applies a (pretty crappy) vertex shader animation emulating wind
            shader = PbrShader(pbrCfg, treePbrModel(pbrCfg)).apply {
                onDispose += {
                    albedoMap?.dispose()
                    occlusionMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                }
                onPipelineCreated += { _, _, _ ->
                    uWindSpeed = model.findNode<PushConstantNode1f>("windAnim")?.uniform
                    uWindStrength = model.findNode<PushConstantNode1f>("windStrength")?.uniform
                }
            }
            onUpdate += {
                windAnimationPos += ctx.deltaT * windSpeed
                uWindSpeed?.value = windAnimationPos
                uWindStrength?.value = windStrength
            }
        }

        // generate tree leaf mesh
        leafMesh = textureMesh {
            generate {
                timedMs({"Generated ${geometry.numIndices / 3} leaf triangles in"}) {
                    treeGen.buildLeafMesh(this)
                }
            }

            var uWindSpeed: Uniform1f? = null
            var uWindStrength: Uniform1f? = null
            val pbrCfg = PbrMaterialConfig().apply {
                useAlbedoMap("${Demo.pbrBasePath}/leaf.png")
                useImageBasedLighting(envMaps)
                roughness = 1f
                alphaMode = AlphaModeMask(0.5f)
                cullMethod = CullMethod.NO_CULLING
                this.shadowMaps.addAll(shadowMaps)
            }
            // custom tree shader model applies a (pretty crappy) vertex shader animation emulating wind
            shader = PbrShader(pbrCfg, treePbrModel(pbrCfg)).apply {
                onDispose += {
                    albedoMap!!.dispose()
                }
                onPipelineCreated += { _, _, _ ->
                    uWindSpeed = model.findNode<PushConstantNode1f>("windAnim")?.uniform
                    uWindStrength = model.findNode<PushConstantNode1f>("windStrength")?.uniform
                }
            }
            onUpdate += {
                uWindSpeed?.value = windAnimationPos
                uWindStrength?.value = windStrength
            }
        }

        +trunkMesh!!
        +leafMesh!!

        +orbitInputTransform {
            +camera
            minZoom = 1.0
            maxZoom = 50.0
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 6.0

            setMouseRotation(0f, -10f)
            setMouseTranslation(0f, 2f, 0f)

            (camera as PerspectiveCamera).apply { clipFar = 50f }

            onUpdate += {
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        section("Generator Settings") {
            sliderWithValue("Grow Distance:", treeGen.growDistance, 0.1f, 0.4f) { treeGen.growDistance = value }
            sliderWithValue("Kill Distance:", treeGen.killDistance, 1f, 4f) { treeGen.killDistance = value }
            sliderWithValue("Attraction Points:", treeGen.numberOfAttractionPoints.toFloat(), 100f, 10000f) {
                treeGen.numberOfAttractionPoints = value.toInt()
            }
            sliderWithValue("Radius of Influence:", treeGen.radiusOfInfluence, 0.25f, 10f) {
                treeGen.radiusOfInfluence = value
            }
            button("Generate Tree") {
                treeGen.generate()

                trunkMesh?.apply {
                    geometry.batchUpdate {
                        clear()
                        val builder = MeshBuilder(this)
                        timedMs({"Generated ${numIndices / 3} trunk triangles in"}) {
                            treeGen.buildTrunkMesh(builder)
                            generateTangents()
                        }
                    }
                }
                leafMesh?.apply {
                    geometry.batchUpdate {
                        clear()
                        val builder = MeshBuilder(this)
                        timedMs({"Generated ${numIndices / 3} leaf triangles in"}) {
                            treeGen.buildLeafMesh(builder)
                        }
                    }
                }
            }
        }
        section("Scene") {
            sliderWithValue("Animation Speed", windSpeed, 0f, 10f) { windSpeed = value }
            sliderWithValue("Animation Strength", windStrength, 0f, 10f) { windStrength = value }
            toggleButton("Toggle Leafs", true) { leafMesh?.isVisible = isEnabled }
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled }
        }
    }

    private class WindNode(graph: ShaderGraph) : ShaderNode("windNode", graph) {
        var inputPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inputAnim = ShaderNodeIoVar(ModelVar1fConst(0f))
        var inputStrength = ShaderNodeIoVar(ModelVar1fConst(1f))
        val outputPos = ShaderNodeIoVar(ModelVar3f("windOutPos"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inputPos, inputAnim, inputStrength)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
            float r = clamp(sqrt(${inputPos.ref3f()}.x * ${inputPos.ref3f()}.x + ${inputPos.ref3f()}.z * ${inputPos.ref3f()}.z) - 0.25, 0.0, 2.0) + 
                        clamp(${inputPos.ref3f()}.z - 1.0, 0.0, 1.0);
            float windTx = cos(${inputPos.ref3f()}.x * 10.0 + ${inputPos.ref3f()}.y * 2.0 + ${inputAnim.ref1f()}) * r * 0.01 * ${inputStrength.ref1f()};
            float windTz = sin(${inputPos.ref3f()}.z * 10.0 - ${inputPos.ref3f()}.y * 2.0 + ${inputAnim.ref1f()} * 1.1f) * r * 0.01 * ${inputStrength.ref1f()};
            ${outputPos.declare()} = ${inputPos.ref3f()} + vec3(windTx, 0.0, windTz);
        """)
        }
    }

    private fun treePbrModel(cfg: PbrMaterialConfig) = ShaderModel("treePbrModel()").apply {
        val ifColors: StageInterfaceNode?
        val ifNormals: StageInterfaceNode
        val ifTangents: StageInterfaceNode?
        val ifFragPos: StageInterfaceNode
        val ifTexCoords: StageInterfaceNode?
        val mvpNode: UniformBufferMvp
        val shadowMapNodes = mutableListOf<ShadowMapNode>()

        vertexStage {
            mvpNode = mvpNode()
            val nrm = vec3TransformNode(attrNormals().output, mvpNode.outModelMat, 0f)
            ifNormals = stageInterfaceNode("ifNormals", nrm.outVec3)

            ifTexCoords = if (cfg.requiresTexCoords()) {
                stageInterfaceNode("ifTexCoords", attrTexCoords().output)
            } else {
                null
            }

            val staticLocalPos = if (cfg.isDisplacementMapped) {
                val dispTex = textureNode("tDisplacement")
                val dispNd = displacementMapNode(dispTex, ifTexCoords!!.input, attrPositions().output, attrNormals().output).apply {
                    inStrength = pushConstantNode1f("uDispStrength").output
                }
                dispNd.outPosition
            } else {
                attrPositions().output
            }
            val windNd = addNode(WindNode(vertexStageGraph)).apply {
                inputPos = staticLocalPos
                inputAnim = pushConstantNode1f("windAnim").output
                inputStrength = pushConstantNode1f("windStrength").output
            }
            val localPos = windNd.outputPos
            val worldPos = vec3TransformNode(localPos, mvpNode.outModelMat, 1f).outVec3
            ifFragPos = stageInterfaceNode("ifFragPos", worldPos)

            ifColors = if (cfg.albedoSource == Albedo.VERTEX_ALBEDO) {
                stageInterfaceNode("ifColors", attrColors().output)
            } else {
                null
            }
            ifTangents = if (cfg.isNormalMapped) {
                val tanAttr = attrTangents().output
                val tan = vec3TransformNode(splitNode(tanAttr, "xyz").output, mvpNode.outModelMat, 0f)
                val tan4 = combineXyzWNode(tan.outVec3, splitNode(tanAttr, "w").output)
                stageInterfaceNode("ifTangents", tan4.output)
            } else {
                null
            }

            val viewPos = vec4TransformNode(worldPos, mvpNode.outViewMat).outVec4

            cfg.shadowMaps.forEachIndexed { i, map ->
                when (map) {
                    is CascadedShadowMap -> shadowMapNodes += cascadedShadowMapNode(map, "depthMap_$i", viewPos, worldPos)
                    is SimpleShadowMap -> shadowMapNodes += simpleShadowMapNode(map, "depthMap_$i", worldPos)
                }
            }
            positionOutput = vec4TransformNode(localPos, mvpNode.outMvpMat).outVec4
        }
        fragmentStage {
            var albedo = when (cfg.albedoSource) {
                Albedo.VERTEX_ALBEDO -> ifColors!!.output
                Albedo.STATIC_ALBEDO -> pushConstantNodeColor("uAlbedo").output
                Albedo.TEXTURE_ALBEDO -> {
                    val albedoSampler = textureSamplerNode(textureNode("tAlbedo"), ifTexCoords!!.output)
                    gammaNode(albedoSampler.outColor).outColor
                }
                Albedo.CUBE_MAP_ALBEDO -> throw IllegalStateException("CUBE_MAP_ALBEDO is not allowed for PbrShader")
            }

            (cfg.alphaMode as? AlphaModeMask)?.let { mask ->
                discardAlpha(splitNode(albedo, "a").output, constFloat(mask.cutOff))
            }
            albedo = combineNode(GlslType.VEC_4F).apply {
                inX = splitNode(albedo, "r").output
                inY = splitNode(albedo, "g").output
                inZ = splitNode(albedo, "b").output
                inW = constFloat(1f)
            }.output

            val mvpFrag = mvpNode.addToStage(fragmentStageGraph)
            val lightNode = multiLightNode(cfg.maxLights)
            shadowMapNodes.forEach {
                lightNode.inShaodwFacs[it.lightIndex] = it.outShadowFac
            }

            val reflMap: CubeMapNode?
            val brdfLut: TextureNode?
            val irrSampler: CubeMapSamplerNode?

            if (cfg.isImageBasedLighting) {
                val irrMap = cubeMapNode("irradianceMap")
                irrSampler = cubeMapSamplerNode(irrMap, ifNormals.output, false)
                reflMap = cubeMapNode("reflectionMap")
                brdfLut = textureNode("brdfLut")
            } else {
                irrSampler = null
                reflMap = null
                brdfLut = null
            }

            val mat = pbrMaterialNode(lightNode, reflMap, brdfLut).apply {
                lightBacksides = cfg.lightBacksides
                inFragPos = ifFragPos.output
                inViewDir = viewDirNode(mvpFrag.outCamPos, ifFragPos.output).output

                inIrradiance = irrSampler?.outColor ?: pushConstantNodeColor("uAmbient").output

                inAlbedo = albedo
                inNormal = if (cfg.isNormalMapped && ifTangents != null) {
                    val bumpNormal = normalMapNode(textureNode("tNormal"), ifTexCoords!!.output, ifNormals.output, ifTangents.output)
                    bumpNormal.inStrength = ShaderNodeIoVar(ModelVar1fConst(cfg.normalStrength))
                    bumpNormal.outNormal
                } else {
                    ifNormals.output
                }


                val rmoSamplers = mutableMapOf<String, ShaderNodeIoVar>()
                if (cfg.isRoughnessMapped) {
                    val roughness = textureSamplerNode(textureNode(cfg.roughnessTexName), ifTexCoords!!.output).outColor
                    rmoSamplers[cfg.roughnessTexName] = roughness
                    inRoughness = splitNode(roughness, cfg.roughnessChannel).output
                } else {
                    inRoughness = pushConstantNode1f("uRoughness").output
                }
                if (cfg.isMetallicMapped) {
                    val metallic = rmoSamplers.getOrPut(cfg.metallicTexName) { textureSamplerNode(textureNode(cfg.metallicTexName), ifTexCoords!!.output).outColor }
                    rmoSamplers[cfg.metallicTexName] = metallic
                    inMetallic = splitNode(metallic, cfg.metallicChannel).output
                } else {
                    inMetallic = pushConstantNode1f("uMetallic").output
                }
                if (cfg.isOcclusionMapped) {
                    val occlusion = rmoSamplers.getOrPut(cfg.occlusionTexName) { textureSamplerNode(textureNode(cfg.occlusionTexName), ifTexCoords!!.output).outColor }
                    rmoSamplers[cfg.occlusionTexName] = occlusion
                    inAmbientOccl = splitNode(occlusion, cfg.occlusionChannel).output
                }
            }
            when (cfg.alphaMode) {
                is AlphaModeBlend -> colorOutput(hdrToLdrNode(mat.outColor).outColor)
                is AlphaModeMask -> colorOutput(hdrToLdrNode(mat.outColor).outColor, alpha = constFloat(1f))
                is AlphaModeOpaque -> colorOutput(hdrToLdrNode(mat.outColor).outColor, alpha = constFloat(1f))
            }
        }
    }

    private fun makeTreeGroundGrid(cells: Int, shadowMaps: List<CascadedShadowMap>, envMaps: EnvironmentMaps): Node {
        val groundExt = cells / 2

        return textureMesh(isNormalMapped = true) {
            isCastingShadow = false
            generate {
                withTransform {
                    color = Color.LIGHT_GRAY.withAlpha(0.2f)
                    vertexModFun = {
                        texCoord.set(position.x, position.z).scale(0.2f)
                    }
                    grid {
                        sizeX = groundExt * 2f
                        sizeY = groundExt * 2f
                        stepsX = 200
                        stepsY = 200

                        heightFun = { x, y ->
                            val fx = (x.toFloat() / stepsX - 0.5f) * 7f
                            val fy = (y.toFloat() / stepsY - 0.5f) * 7f
                            cos(sqrt(fx*fx + fy*fy)) * 0.2f - 0.2f
                        }
                    }
                }
                geometry.generateTangents()
            }
            shader = pbrShader {
                useAlbedoMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg")
                useNormalMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_Nor_2k.jpg")
                useRoughnessMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_rough_2k.jpg")
                useOcclusionMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_AO_2k.jpg")
                useDisplacementMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg")
                useImageBasedLighting(envMaps)
                this.shadowMaps.addAll(shadowMaps)

                onDispose += {
                    albedoMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                    occlusionMap?.dispose()
                    displacementMap?.dispose()
                }
            }
        }
    }
}