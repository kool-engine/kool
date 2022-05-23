package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.atmosphere.AtmosphereNode
import de.fabmax.kool.modules.atmosphere.OpticalDepthLutPass
import de.fabmax.kool.modules.atmosphere.RaySphereIntersectionNode
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MutableColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SkyCubePass(opticalDepthLut: Texture2d, size: Int = 256) :
        OffscreenRenderPassCube(Group(), renderPassConfig {
            name = "SkyCubePass"
            setSize(size, size)
            addColorTexture(TexFormat.RGBA_F16)
            clearDepthTexture()
        }) {

    val syncLights = mutableListOf<Light>()

    var azimuth = 0f
        set(value) {
            field = value
            isEnabled = true
        }

    var elevation = 60f
        set(value) {
            field = value.clamp(-90f, 90f)
            isEnabled = true
        }

    private val lightGradient = ColorGradient(
            -90f to Color.BLACK,
            -2f to Color.BLACK,
            0f to MdColor.ORANGE.mix(Color.WHITE, 0.6f).toLinear().scale(0.7f),
            5f to MdColor.AMBER.mix(Color.WHITE, 0.6f).toLinear(),
            10f to Color.WHITE,
            90f to Color.WHITE,
    )
    private val sunLight = Light()
            .setDirectional(Vec3f(-1f, -1f, -1f))
            .setColor(Color.WHITE, 3f)

    val groundShader: PbrShader
    private val skyShader = SkyShader(opticalDepthLut)

    init {
        lighting = Lighting().apply {
            lights.clear()
            lights += sunLight
        }

        groundShader = pbrShader {
            isHdrOutput = true
            useStaticAlbedo(MdColor.BROWN toneLin 500)
            roughness = 0.8f
        }.apply {
            ambient(nightSkyColor)
        }

        (drawNode as Group).apply {
            // sky
            +textureMesh {
                generate {
                    icoSphere {
                        steps = 2
                    }
                }
                shader = skyShader
            }

            // ground
            +colorMesh {
                generate {
                    translate(0f, -0.011f, 0f)
                    rotate(-90f, Vec3f.X_AXIS)
                    circle {
                        radius = 9f
                        steps = 100
                    }
                }
                shader = groundShader
            }
        }

        onBeforeCollectDrawCommands += {
            updateSunLight()
            skyShader.atmoNode?.let {
                it.uDirToSun.value.set(sunLight.direction).scale(-1f)
                it.uSunColor.value.set(sunLight.color)
                it.uSunColor.value.a /= 3f
            }
        }

        onAfterDraw += {
            isEnabled = false
        }
    }

    private fun updateSunLight() {
        val phi = -azimuth.toRad()
        val theta = (PI.toFloat() / 2f) - elevation.toRad()
        sunLight.direction.z = -sin(theta) * cos(phi)
        sunLight.direction.x = -sin(theta) * sin(phi)
        sunLight.direction.y = -cos(theta)

        val syncColor = lightGradient.getColorInterpolated(elevation, MutableColor(), -90f, 90f)
        val strength = sqrt(syncColor.r * syncColor.r + syncColor.g * syncColor.g + syncColor.b * syncColor.b)
        for (light in syncLights) {
            light.setDirectional(sunLight.direction)
            light.setColor(syncColor, strength * sunLight.color.a)
        }
    }

    companion object {
        private val nightSkyColor = Color(0.02f, 0.07f, 0.15f).scaleRgb(1.5f, MutableColor()).toLinear()
    }

    private class SkyShader(opticalDepthLut: Texture2d) : ModeledShader(model()) {

        var atmoNode: AtmosphereNode? = null

        init {
            onPipelineSetup += { builder, _, _ ->
                builder.depthTest = DepthCompareOp.DISABLED
                builder.cullMethod = CullMethod.NO_CULLING
            }
            onPipelineCreated += { _, _, _ ->
                model.findNode<Texture2dNode>("tOpticalDepthLut")!!.sampler.texture = opticalDepthLut

                atmoNode = model.findNodeByType<AtmosphereNode>()!!.apply {
                    uPlanetCenter.value.set(0f, -60f, 0f)
                    uSurfaceRadius.value = 60f
                    uAtmosphereRadius.value = 65f

                    uScatteringCoeffs.value.set(0.274f, 1.536f, 3.859f)
                    uRayleighColor.value.set(0.5f, 0.5f, 1.0f, 1.0f)
                    uMieColor.value.set(1f, 0.35f, 0.35f, 0.5f)
                    uMieG.value = 0.8f
                }
            }
        }

        companion object {
            fun model() = ShaderModel("sky-cube-shader").apply {
                val mvp: UniformBufferMvp
                val ifWorldPos: StageInterfaceNode

                vertexStage {
                    mvp = mvpNode()
                    val localPos = attrPositions().output
                    val worldPos = vec3TransformNode(localPos, mvp.outModelMat, 1f).outVec3
                    ifWorldPos = stageInterfaceNode("ifFragPos", worldPos)
                    positionOutput = vec4TransformNode(localPos, mvp.outMvpMat).outVec4
                }
                fragmentStage {
                    addNode(RaySphereIntersectionNode(stage))

                    val fragMvp = mvp.addToStage(stage)
                    val opticalDepthLut = texture2dNode("tOpticalDepthLut")
                    val viewDir = viewDirNode(fragMvp.outCamPos, ifWorldPos.output).output

                    val atmoNd = addNode(AtmosphereNode(opticalDepthLut, stage)).apply {
                        inSceneColor = constVec4f(nightSkyColor)
                        inSkyColor = constVec4f(nightSkyColor)
                        inScenePos = ifWorldPos.output
                        inCamPos = fragMvp.outCamPos
                        inLookDir = viewDir
                        randomizeStartOffsets = false
                    }
                    colorOutput(atmoNd.outColor)
                }
            }
        }
    }
}

class SkyCubeIblSystem(val parentScene: Scene) {
    val opticalDepthLutPass = OpticalDepthLutPass()
    val skyPass = SkyCubePass(opticalDepthLutPass.colorTexture!!)

    val irradianceMapPass = IrradianceMapPass.irradianceMap(parentScene, skyPass.colorTexture!!, 8)
    val reflectionMapPass = ReflectionMapPass.reflectionMap(parentScene, skyPass.colorTexture!!, 128)

    val envMaps = EnvironmentMaps(irradianceMapPass.colorTexture!!, reflectionMapPass.colorTexture!!)

    var isAutoUpdateIblMaps = true

    init {
        irradianceMapPass.isAutoRemove = false
        reflectionMapPass.isAutoRemove = false

        skyPass.onAfterDraw += {
            if (isAutoUpdateIblMaps) {
                updateIblMaps()
            }
        }
    }

    fun updateIblMaps() {
        irradianceMapPass.isEnabled = true
        reflectionMapPass.isEnabled = true
    }

    fun setupOffscreenPasses() {
        parentScene.addOffscreenPass(opticalDepthLutPass)

        parentScene.addOffscreenPass(skyPass)
        parentScene.addOffscreenPass(irradianceMapPass)
        parentScene.addOffscreenPass(reflectionMapPass)
    }
}
