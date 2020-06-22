package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.Uniform1f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.sqrt


/**
 * @author fabmax
 */

fun treeScene(ctx: KoolContext): List<Scene> {
    val scenes = mutableListOf<Scene>()

    // generate tree structure
    val w = 3f
    val h = 3.5f
    val dist = TreeTopPointDistribution(1f + h / 2f, w, h)
    //val dist = SphericalPointDistribution(2f, Vec3f(0f, 3f, 0f))
    //val dist = CubicPointDistribution(4f, Vec3f(0f, 3f, 0f))
    val treeGen = TreeGenerator(dist, primaryLightDir = MutableVec3f(-1f, -1.5f, -1f).norm())
    treeGen.generate()

    // meshes
    var trunkMesh: Mesh? = null
    var leafMesh: Mesh? = null

    var autoRotate = true
    var windSpeed = 2.5f
    var windAnimationPos = 0f
    var windStrength = 1f

    val treeScene = scene {
        val backLightDirection = Vec3f(1f, -1.5f, 1f)
        val sunLightDirection = Vec3f(-1f, -1.5f, -1f)
        lighting.lights.apply {
            clear()
            add(Light()
                    .setDirectional(sunLightDirection.norm(MutableVec3f()))
                    //.setColor(Color.RED, 3f))
                    .setColor(Color.MD_AMBER.mix(Color.WHITE, 0.6f).toLinear(), 3f))

            add(Light()
                    .setDirectional(backLightDirection.norm(MutableVec3f()))
                    //.setColor(Color.GREEN, 3f))
                    .setColor(Color.MD_AMBER.mix(Color.WHITE, 0.6f).toLinear(), 0.25f))
        }

        val shadowMaps = mutableListOf(CascadedShadowMap(this, 0, mapSize = 2048).apply { maxRange = 50f })

        +makeTreeGroundGrid(10, shadowMaps)

        // generate tree trunk mesh
        trunkMesh = textureMesh(isNormalMapped = true) {
            generate {
                timedMs({"Generated ${geometry.numIndices / 3} trunk triangles in"}) {
                    treeGen.buildTrunkMesh(this)
                }
            }

            var uWindSpeed: Uniform1f? = null
            var uWindStrength: Uniform1f? = null
            val pbrCfg = PbrShader.PbrConfig().apply {
                albedoSource = Albedo.TEXTURE_ALBEDO
                isNormalMapped = true
                isAmbientOcclusionMapped = true
                isRoughnessMapped = true
                maxLights = lighting.lights.size
                this.shadowMaps.addAll(shadowMaps)
            }
            pipelineLoader = PbrShader(pbrCfg, treePbrModel(pbrCfg)).apply {
                albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/bark_pine/Bark_Pine_baseColor.jpg") }
                ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/bark_pine/Bark_Pine_ambientOcclusion.jpg") }
                normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/bark_pine/Bark_Pine_normal.jpg") }
                roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/bark_pine/Bark_Pine_roughness.jpg") }

                onDispose += {
                    albedoMap?.dispose()
                    ambientOcclusionMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                }
                onCreated += {
                    uWindSpeed = model.findNode<PushConstantNode1f>("windAnim")?.uniform
                    uWindStrength = model.findNode<PushConstantNode1f>("windStrength")?.uniform
                }
            }
            onUpdate += { _, ctx ->
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
            val pbrCfg = PbrShader.PbrConfig().apply {
                albedoSource = Albedo.TEXTURE_ALBEDO
                maxLights = lighting.lights.size
                lightBacksides = true
                this.shadowMaps.addAll(shadowMaps)
            }
            pipelineLoader = PbrShader(pbrCfg, treePbrModel(pbrCfg)).apply {
                albedoMap = Texture { it.loadTextureData("leaf.png") }
                roughness = 0.5f

                onDispose += {
                    albedoMap!!.dispose()
                }
                onSetup += {
                    it.cullMethod = CullMethod.NO_CULLING
                }
                onCreated += {
                    uWindSpeed = model.findNode<PushConstantNode1f>("windAnim")?.uniform
                    uWindStrength = model.findNode<PushConstantNode1f>("windStrength")?.uniform
                }
            }
            onUpdate += { _, ctx ->
                uWindSpeed?.value = windAnimationPos
                uWindStrength?.value = windStrength
            }
        }

        +trunkMesh!!
        +leafMesh!!

        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +orbitInputTransform {
            +camera
            minZoom = 1.0
            maxZoom = 50.0
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 6.0

            setMouseRotation(0f, -10f)
            setMouseTranslation(0f, 2f, 0f)

            (camera as PerspectiveCamera).apply { clipFar = 50f }

            onUpdate += { _, ctx ->
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }
    }
    scenes += treeScene



    scenes += uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-450f), dps(-680f), zero())
            layoutSpec.setSize(dps(330f), dps(560f), full())

            var y = -40f
            +label("Generator Settings") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }

            y -= 35f
            +label("Grow Distance:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val growDistVal = label("growDistVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = treeGen.growDistance.toString(2)
            }
            +growDistVal
            y -= 25f
            +slider("growDist") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(0.1f, 0.4f, treeGen.growDistance)
                onValueChanged += { value ->
                    treeGen.growDistance = value
                    growDistVal.text = value.toString(2)
                }
            }

            y -= 35f
            +label("Kill Distance:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val killDistVal = label("killDistVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = treeGen.killDistance.toString(2)
            }
            +killDistVal
            y -= 25f
            +slider("killDist") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(1f, 4f, treeGen.killDistance)
                onValueChanged += { value ->
                    treeGen.killDistance = value
                    killDistVal.text = value.toString(2)
                }
            }

            y -= 35f
            +label("Attraction Points:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val attractPtsVal = label("attractPtsVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = "${treeGen.numberOfAttractionPoints}"
            }
            +attractPtsVal
            y -= 25f
            +slider("attractPts") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(100f, 10000f, treeGen.numberOfAttractionPoints.toFloat())
                onValueChanged += { value ->
                    treeGen.numberOfAttractionPoints = value.toInt()
                    attractPtsVal.text = "${value.toInt()}"
                }
            }

            y -= 35f
            +label("Radius of Influence:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val infRadiusVal = label("infRadiusVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = treeGen.radiusOfInfluence.toString(2)
            }
            +infRadiusVal
            y -= 25f
            +slider("killDist") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(0.25f, 10f, treeGen.radiusOfInfluence)
                onValueChanged += { value ->
                    treeGen.radiusOfInfluence = value
                    infRadiusVal.text = value.toString(2)
                }
            }

            y -= 45f
            +button("Generate Tree!") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onClick += { _,_,_ ->
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

            y -= 40
            +label("Scene") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }

            y -= 35f
            +label("Animation Speed:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val windSpeedVal = label("windSpeedVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = windSpeed.toString(1)
            }
            +windSpeedVal
            y -= 25f
            +slider("windSpeed") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(0.0f, 10f, windSpeed)
                onValueChanged += { value ->
                    windSpeed = value
                    windSpeedVal.text = value.toString(1)
                }
            }

            y -= 35f
            +label("Animation Strength:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val windStrengthVal = label("windStrengthVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = windStrength.toString(1)
            }
            +windStrengthVal
            y -= 25f
            +slider("windStrength") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(0.0f, 5f, treeGen.radiusOfInfluence)
                onValueChanged += { value ->
                    windStrength = value
                    windStrengthVal.text = value.toString(1)
                }
            }

            y -= 35
            +toggleButton("Toggle Leafs") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                isEnabled = true
                onStateChange += {
                    leafMesh?.isVisible = isEnabled
                }
            }
            y -= 35
            +toggleButton("Auto Rotate") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                isEnabled = autoRotate
                onStateChange += {
                    autoRotate = isEnabled
                }
            }
        }
    }

    return scenes
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

private fun treePbrModel(cfg: PbrShader.PbrConfig) = ShaderModel("treePbrModel()").apply {
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
            val tan = vec3TransformNode(attrTangents().output, mvpNode.outModelMat, 0f)
            stageInterfaceNode("ifTangents", tan.outVec3)
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
            inCamPos = mvpFrag.outCamPos

            inIrradiance = irrSampler?.outColor ?: pushConstantNodeColor("uAmbient").output

            inAlbedo = when (cfg.albedoSource) {
                Albedo.VERTEX_ALBEDO -> ifColors!!.output
                Albedo.STATIC_ALBEDO -> pushConstantNodeColor("uAlbedo").output
                Albedo.TEXTURE_ALBEDO -> {
                    val albedoSampler = textureSamplerNode(textureNode("tAlbedo"), ifTexCoords!!.output, false)
                    val albedoLin = gammaNode(albedoSampler.outColor)
                    albedoLin.outColor
                }
            }
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
            if (cfg.isAmbientOcclusionMapped) {
                val occlusion = rmoSamplers.getOrPut(cfg.ambientOcclusionTexName) { textureSamplerNode(textureNode(cfg.ambientOcclusionTexName), ifTexCoords!!.output).outColor }
                rmoSamplers[cfg.ambientOcclusionTexName] = occlusion
                inAmbientOccl = splitNode(occlusion, cfg.ambientOcclusionChannel).output
            }
        }
        val hdrToLdr = hdrToLdrNode(mat.outColor)
        colorOutput(hdrToLdr.outColor)
    }
}

private fun makeTreeGroundGrid(cells: Int, shadowMaps: List<CascadedShadowMap>): Node {
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

        val pbrCfg = PbrShader.PbrConfig().apply {
            albedoSource = Albedo.TEXTURE_ALBEDO
            isNormalMapped = true
            isAmbientOcclusionMapped = true
            isRoughnessMapped = true
            isDisplacementMapped = true
            maxLights = 2
            this.shadowMaps.addAll(shadowMaps)
        }
        pipelineLoader = PbrShader(pbrCfg).apply {
            albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg") }
            normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_Nor_2k.jpg") }
            roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_rough_2k.jpg") }
            ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_AO_2k.jpg") }
            displacementMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg") }

            onDispose += {
                albedoMap?.dispose()
                normalMap?.dispose()
                roughnessMap?.dispose()
                ambientOcclusionMap?.dispose()
                displacementMap?.dispose()
            }
        }
    }
}