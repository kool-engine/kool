package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.AlphaModeBlend
import de.fabmax.kool.pipeline.shading.UnlitMaterialConfig
import de.fabmax.kool.pipeline.shading.UnlitShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.IndexedVertexList
import kotlin.math.max
import kotlin.math.pow

class SkyPass(val atmosphereDemo: AtmosphereDemo) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "SkyPass"
            setDynamicSize()
            addColorTexture {
                colorFormat = TexFormat.RGB_F16
                minFilter = FilterMethod.NEAREST
                magFilter = FilterMethod.NEAREST
            }
            clearDepthTexture()
        }) {

    val content = drawNode as Group

    init {
        val scene = atmosphereDemo.mainScene
        val proxyCamera = PerspectiveCamera.Proxy(scene.camera as PerspectiveCamera)
        camera = proxyCamera
        onBeforeCollectDrawCommands += { ctx ->
            proxyCamera.sync(scene.mainRenderPass.viewport, ctx)
            proxyCamera.clipNear = 5f
            proxyCamera.clipFar = 5000f
        }

        lighting = scene.lighting

        scene.addOffscreenPass(this)
        //clearColor = Color.RED

        scene.onRenderScene += { ctx ->
            val vpW = mainRenderPass.viewport.width
            val vpH = mainRenderPass.viewport.height
            if (vpW > 0 && vpH > 0 && (vpW != width || vpH != height)) {
                resize(vpW, vpH, ctx)
            }
        }

        content.apply {
            isFrustumChecked = false
            setupSky()
        }
    }

    private fun Group.setupSky() {
        val textures = atmosphereDemo.textures
        var milkyWayShader: UnlitShader? = null
        var starShader: StarShader? = null
        var sunBgShader: UnlitShader? = null

        val milkyWay = textureMesh {
            isFrustumChecked = false
            generate {
                vertexModFun = {
                    texCoord.x = 1f - texCoord.x
                }
                uvSphere {
                    steps = 10
                }
            }
            milkyWayShader = skyboxShader(textures[AtmosphereDemo.texMilkyway])
            shader = milkyWayShader
        }
        val stars = pointMesh {
            isFrustumChecked = false
            generate {
                val rand = Random(56498561)
                val gradient = ColorGradient(Color.WHITE, Color.MD_BLUE.mix(Color.WHITE, 0.5f), Color.MD_PINK.mix(Color.WHITE, 0.5f), Color.MD_YELLOW.mix(Color.WHITE, 0.5f))
                for (i in 0..30000) {
                    vertex {
                        var x = 1f
                        var y = 1f
                        while (x*x + y*y > 1) {
                            x = rand.randomF(-1f, 1f)
                            y = rand.randomF(-1f, 1f)
                        }
                        // increase star density towards milky way center and ecliptic
                        position.set(x * 1.5f, rand.randomF(-1f, 1f), y * 1.5f)
                        position.x -= 0.25f
                        position.norm()

                        val brightness = rand.randomF(0.1f, 1f)
                        color.set(gradient.getColor(rand.randomF()).scale(brightness, MutableVec4f()))
                        getFloatAttribute(PointMesh.ATTRIB_POINT_SIZE)!!.f = 1f + brightness.pow(3) * 0.75f
                    }
                    geometry.addIndex(i)
                }
            }
            starShader = StarShader()
            shader = starShader
        }
        val sunBg = textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    size.set(3f, 3f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    origin.z = -10f
                }
            }
            sunBgShader = skyboxShader(textures[AtmosphereDemo.texSunBg])
            shader = sunBgShader
        }
        val sun = textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    size.set(1f, 1f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    origin.z = -10f
                }

            }
            shader = skyboxShader(textures[AtmosphereDemo.texSun])
        }

        +group {
            isFrustumChecked = false
            +milkyWay
            // milky way is wildly tilted (no idea in which direction...)
            rotate(-60f, Vec3f.X_AXIS)

            onUpdate += {
                val atmoThickness = atmosphereDemo.atmoShader.atmosphereRadius - atmosphereDemo.atmoShader.surfaceRadius
                val skyHeightAlpha = ((atmosphereDemo.cameraHeight / atmoThickness).clamp(0.2f, 1f) - 0.2f) * (1f / 0.8f)

                val posNormal = MutableVec3f(atmosphereDemo.mainScene.camera.globalPos).norm()
                val dayNightAlpha = smoothStep(0.1f, 0.3f, posNormal * atmosphereDemo.sun.direction)

                val w = 1f - ((1f - skyHeightAlpha) * (1f - dayNightAlpha))
                val mulColor = Color(w, w, w, 1f)
                starShader!!.color = mulColor
                milkyWayShader!!.color = mulColor
                sunBgShader!!.color = mulColor.withAlpha(0.75f)

                starShader?.uPtSizeMod?.value = max(0.6f, width / 1920f)
            }
            +stars
        }
        +sunBg
        +sun

        +group("moonGroup") {
            isFrustumChecked = false
            +Moon()

            onUpdate += {
                setIdentity()
                rotate(AtmosphereDemo.moonInclination, Vec3f.X_AXIS)
                rotate(360f * atmosphereDemo.moonTime, Vec3f.Y_AXIS)
                translate(0f, 0f, AtmosphereDemo.moonDist)
            }
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private fun skyboxShader(texture: Texture?): UnlitShader {
        val unlitCfg = UnlitMaterialConfig().apply {
            alphaMode = AlphaModeBlend()
            useColorMap(texture, true)
            color = Color.WHITE
        }
        val unlitModel = UnlitShader.defaultUnlitModel(unlitCfg).apply {
            vertexStage {
                val mvp = findNodeByType<UniformBufferMvp>()!!
                positionOutput = addNode(Skybox.SkyboxPosNode(mvp, attrPositions().output, stage)).outPosition
            }
            fragmentStage {
                val unlitMat = findNodeByType<UnlitMaterialNode>()!!
                colorOutput(gammaNode(unlitMat.outColor).outColor)
            }
        }
        return UnlitShader(unlitCfg, unlitModel).apply {
            onPipelineSetup += { builder, _, _ ->
                builder.cullMethod = CullMethod.NO_CULLING
                builder.depthTest = DepthCompareOp.DISABLED
            }
        }
    }

    class StarShader : UnlitShader(cfg, model) {
        var uPtSizeMod: Uniform1f? = null

        init {
            onPipelineSetup += { builder, _, _ ->
                builder.depthTest = DepthCompareOp.DISABLED
            }
            onPipelineCreated += { _, _, _ ->
                uPtSizeMod = model.findNode<PushConstantNode1f>("uPtSizeMod")?.uniform
            }
        }

        companion object {
            private val cfg = UnlitMaterialConfig()
            private val model = defaultUnlitModel(cfg).apply {
                vertexStage {
                    val mvp = findNodeByType<UniformBufferMvp>()!!
                    val ptSizeMod = pushConstantNode1f("uPtSizeMod").output
                    pointSize(multiplyNode(attributeNode(PointMesh.ATTRIB_POINT_SIZE).output, ptSizeMod).output)
                    positionOutput = addNode(Skybox.SkyboxPosNode(mvp, attrPositions().output, stage)).outPosition
                }
                fragmentStage {
                    val unlitMat = findNodeByType<UnlitMaterialNode>()!!
                    val mulColor = pushConstantNodeColor("uColor").output
                    val color = multiplyNode(unlitMat.outColor, mulColor).output
                    colorOutput(gammaNode(color).outColor)
                }
            }
        }
    }

    private inner class Moon : Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS), "moon") {
        init {
            isFrustumChecked = false
            generate {
                rotate(180f, Vec3f.Y_AXIS)
                icoSphere {
                    steps = 4
                    radius = AtmosphereDemo.moonRadius
                }
            }
            shader = pbrShader {
                useAlbedoMap(atmosphereDemo.textures[AtmosphereDemo.texMoon])
                roughness = 0.7f
                isHdrOutput = true
            }.apply {
                ambient = Color(0.05f, 0.05f, 0.05f).toLinear()
            }
        }
    }
}