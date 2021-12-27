package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.Texture2dInput
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.atmosphere.AtmosphereNode
import de.fabmax.kool.util.atmosphere.RaySphereIntersectionNode
import de.fabmax.kool.util.deferred.DeferredPassSwapListener
import de.fabmax.kool.util.deferred.DeferredPasses
import kotlin.math.pow

class AtmosphericScatteringShader : ModeledShader(atmosphereModel()), DeferredPassSwapListener {

    private var atmosphereNode: AtmosphereNode? = null

    val opticalDepthLut = Texture2dInput("tOpticalDepthLut")
    val sceneColor = Texture2dInput("tSceneColor")
    val scenePos = Texture2dInput("tScenePos")
    val skyColor = Texture2dInput("tSkyColor")

    var dirToSun = Vec3f(0f, 0f, 1f)
        set(value) {
            field = value
            atmosphereNode?.uDirToSun?.value?.set(value)
        }
    var sunColor = Color.WHITE
        set(value) {
            field = value
            atmosphereNode?.uSunColor?.value?.set(value)
        }

    var scatteringCoeffPow = 4.5f
        set(value) {
            field = value
            updateScatteringCoeffs()
        }
    var scatteringCoeffStrength = 1f
        set(value) {
            field = value
            updateScatteringCoeffs()
        }
    var scatteringCoeffs = Vec3f(0.5f, 0.8f, 1.0f)
        set(value) {
            field = value
            updateScatteringCoeffs()
        }

    var rayleighColor = Color(0.75f, 0.75f, 0.75f, 1f)
        set(value) {
            field = value
            atmosphereNode?.uRayleighColor?.value?.set(value)
        }

    var mieColor = Color(1f, 0.9f, 0.8f, 0.3f)
        set(value) {
            field = value
            atmosphereNode?.uMieColor?.value?.set(value)
        }
    var mieG = 0.99f
        set(value) {
            field = value
            atmosphereNode?.uMieG?.value = value
        }

    var planetCenter = Vec3f.ZERO
        set(value) {
            field = value
            atmosphereNode?.uPlanetCenter?.value?.set(value)
        }
    var surfaceRadius = 600f
        set(value) {
            field = value
            atmosphereNode?.uSurfaceRadius?.value = value
        }
    var atmosphereRadius = 609f
        set(value) {
            field = value
            atmosphereNode?.uAtmosphereRadius?.value = value
        }

    init {
        onPipelineSetup += { builder, _, _ ->
            builder.depthTest = DepthCompareOp.DISABLED
        }
        onPipelineCreated += { _, _, _ ->
            opticalDepthLut.connect(model)
            sceneColor.connect(model)
            scenePos.connect(model)
            skyColor.connect(model)

            atmosphereNode = model.findNodeByType()
            atmosphereNode?.apply {
                uDirToSun.value.set(dirToSun)
                uPlanetCenter.value.set(planetCenter)
                uSurfaceRadius.value = surfaceRadius
                uAtmosphereRadius.value = atmosphereRadius
                uRayleighColor.value.set(rayleighColor)
                uMieColor.value.set(mieColor)
                uMieG.value = mieG
                uSunColor.value.set(sunColor)
                updateScatteringCoeffs()
            }
        }
    }

    private fun updateScatteringCoeffs() {
        atmosphereNode?.uScatteringCoeffs?.value?.let {
            it.x = scatteringCoeffs.x.pow(scatteringCoeffPow) * scatteringCoeffStrength
            it.y = scatteringCoeffs.y.pow(scatteringCoeffPow) * scatteringCoeffStrength
            it.z = scatteringCoeffs.z.pow(scatteringCoeffPow) * scatteringCoeffStrength
        }
    }

    companion object {
        private fun atmosphereModel() = ShaderModel().apply {
            val mvp: UniformBufferMvp
            val ifQuadPos: StageInterfaceNode
            val ifViewDir: StageInterfaceNode

            vertexStage {
                mvp = mvpNode()
                val quadPos = attrTexCoords().output
                val viewDir = addNode(XyToViewDirNode(stage)).apply {
                    inScreenPos = quadPos
                }
                ifViewDir = stageInterfaceNode("ifViewDir", viewDir.outViewDir)
                ifQuadPos = stageInterfaceNode("ifTexCoords", quadPos)
                positionOutput = fullScreenQuadPositionNode(quadPos).outQuadPos
            }
            fragmentStage {
                addNode(RaySphereIntersectionNode(stage))

                val fragMvp = mvp.addToStage(stage)
                val opticalDepthLut = texture2dNode("tOpticalDepthLut")
                val sceneColor = texture2dSamplerNode(texture2dNode("tSceneColor"), ifQuadPos.output).outColor
                val skyColor = texture2dSamplerNode(texture2dNode("tSkyColor"), ifQuadPos.output).outColor
                val viewPos = texture2dSamplerNode(texture2dNode("tScenePos"), ifQuadPos.output).outColor
                val view2world = addNode(ViewToWorldPosNode(stage)).apply {
                    inViewPos = viewPos
                }

                val atmoNd = addNode(AtmosphereNode(opticalDepthLut, stage)).apply {
                    inSceneColor = sceneColor
                    inSceneDepth = splitNode(viewPos, "z").output
                    inScenePos = view2world.outWorldPos
                    inSkyColor = skyColor
                    inViewDepth = splitNode(viewPos, "z").output
                    inCamPos = fragMvp.outCamPos
                    inLookDir = ifViewDir.output
                }

                colorOutput(hdrToLdrNode(atmoNd.outColor).outColor)
            }
        }
    }

    private class XyToViewDirNode(graph: ShaderGraph) : ShaderNode("xyToViewDir", graph) {
        private val uInvViewProjMat = UniformMat4f("uInvViewProjMat")

        var inScreenPos = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
        val outViewDir = ShaderNodeIoVar(ModelVar3f("${name}_outWorldPos"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inScreenPos)
            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uInvViewProjMat }
                    onUpdate = { _, cmd ->
                        uInvViewProjMat.value.set(cmd.renderPass.camera.invViewProj)
                    }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                vec4 ${name}_near = $uInvViewProjMat * vec4(${inScreenPos.ref2f()} * 2.0 - 1.0, 0.0, 1.0);
                vec4 ${name}_far = $uInvViewProjMat * vec4(${inScreenPos.ref2f()} * 2.0 - 1.0, 1.0, 1.0);
                ${outViewDir.declare()} = (${name}_far.xyz / ${name}_far.w) - (${name}_near.xyz / ${name}_near.w);
            """)
        }
    }

    private class ViewToWorldPosNode(graph: ShaderGraph) : ShaderNode("view2worldPos", graph) {
        private val uInvViewMat = UniformMat4f("uInvViewMat")

        var inViewPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        val outWorldPos = ShaderNodeIoVar(ModelVar4f("${name}_outWorldPos"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inViewPos)
            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uInvViewMat }
                    onUpdate = { _, cmd ->
                        uInvViewMat.value.set(cmd.renderPass.camera.invView)
                    }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outWorldPos.declare()} = $uInvViewMat * vec4(${inViewPos.ref3f()}, 1.0);")
        }
    }

    override fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses) {
        sceneColor(currentPasses.lightingPass.colorTexture)
        scenePos(currentPasses.materialPass.positionFlags)
    }
}