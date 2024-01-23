package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.smoothStep
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.UnlitShaderConfig
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.LineMesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addLineMesh
import de.fabmax.kool.util.Color
import kotlin.math.abs
import kotlin.math.roundToInt

class GridOverlay : Node("Grid overlay") {

    private val yPlaneShader = GridShader()
    private val yPlaneGrid: LineMesh

    init {
        yPlaneGrid = addLineMesh("Y-Plane") {
            makeGrid()
            shader = yPlaneShader
        }

        onUpdate {
            updateShader(it.camera, yPlaneShader)
        }
    }

    private fun updateShader(cam: Camera, shader: GridShader) {
        val h = cam.globalPos.distance(cam.globalLookAt)

        val scale = abs(h) / 16
        var sDiscrete = 1f / 16f
        while (sDiscrete < scale) {
            sDiscrete *= 4f
        }
        val lowerDiscrete = sDiscrete / 4f

        val offsetX = (cam.globalPos.x / (32f * sDiscrete)).roundToInt() * 32f * sDiscrete
        val offsetZ = (cam.globalPos.z / (32f * sDiscrete)).roundToInt() * 32f * sDiscrete
        val wx = (scale - lowerDiscrete) / (sDiscrete - lowerDiscrete)

        shader.posOffset = Vec3f(offsetX, 0f, offsetZ)
        shader.scale = Vec2f(scale, sDiscrete)
        shader.majorWeight = smoothStep(0f, 0.1f, wx) * 0.75f + smoothStep(0.8f, 1f, wx) * 0.25f
        shader.superTickColor = KoolEditor.instance.ui.uiColors.value.primaryVariant.withAlpha(0.4f)
    }

    private fun LineMesh.makeGrid() {
        isFrustumChecked = false
        val superTick = Color.MAGENTA
        val majorColor = Color.RED
        val minorColor = Color.GREEN
        val n = 200

        for (x in -n..n) {
            val color = when {
                x % 32 == 0 -> superTick
                x % 4 == 0 -> majorColor
                else -> minorColor
            }
            addLine(Vec3f(x.toFloat(), 0f, -n.toFloat()), Vec3f(x.toFloat(), 0f, n.toFloat()), color)
        }
        for (z in -n..n) {
            val color = when {
                z % 32 == 0 -> superTick
                z % 4 == 0 -> majorColor
                else -> minorColor
            }
            addLine(Vec3f(-n.toFloat(), 0f, z.toFloat()), Vec3f(n.toFloat(), 0f, z.toFloat()), color)
        }
    }

    private class GridShader(
        cfg: UnlitShaderConfig = UnlitShaderConfig {
            color { vertexColor() }
            modelCustomizer = {
                val clipPos = interStageFloat4()
                val worldPos = interStageFloat3()
                vertexStage {
                    main {
                        val posPort = getFloat3Port("worldPos")
                        val scale = uniformFloat2("uScale")
                        val scaledPos = float3Var(posPort.input.input)
                        scaledPos set scaledPos * scale.y + uniformFloat3("uPosOffset")

                        val proj = float4Var(cameraData().viewProjMat * float4Value(scaledPos, 1f))
                        outPosition set proj
                        clipPos.input set proj
                        worldPos.input set scaledPos
                    }
                }
                fragmentStage {
                    main {
                        val colorPort = getFloat4Port("baseColor")
                        val inColor = float4Var(colorPort.input.input)
                        val outColor = float4Var()
                        val scale = uniformFloat2("uScale")

                        val camData = cameraData()
                        val camDist = float1Var(length(camData.position - worldPos.output))
                        val scaledDist = float1Var(camDist / (scale.x * 200f.const))
                        val aMod = float1Var(1f.const - smoothStep(0.5f.const, 1f.const, scaledDist))

                        val aLimit = float1Var(0.3f.const)
                        `if`(inColor.b gt 0.5f.const) {
                            aLimit set 0.5f.const
                            outColor set uniformFloat4("uSuperTickColor")

                        }.`else` {
                            val isMajor = bool1Var(inColor.r gt inColor.g)
                            val lineColor = float4Var(uniformFloat4("uLineColor"))
                            val majorWeight = uniformFloat1("uMajorWeight")

                            outColor set lineColor
                            outColor.a set lineColor.a * (1f.const - majorWeight)
                            `if`(isMajor) {
                                outColor.a set max(outColor.a + 0.15f.const, lineColor.a * majorWeight)
                            }
                        }

                        outColor.a set min(outColor.a, aLimit) * aMod
                        colorPort.input(outColor)

                        val clipDepth = float1Var(clipPos.output.z / clipPos.output.w) * 0.99999f.const
                        val near = 0f.const
                        val far = 1f.const
                        outDepth set (((far - near) * clipDepth) + near + far) / 2f.const
                    }
                }
            }
        }
    ) : KslUnlitShader(cfg) {
        var scale by uniform2f("uScale", Vec2f(1f, 1f))
        var posOffset by uniform3f("uPosOffset", Vec3f.ZERO)

        var lineColor by uniformColor("uLineColor", Color.LIGHT_GRAY.withAlpha(0.4f))
        var superTickColor by uniformColor("uSuperTickColor")
        var majorWeight by uniform1f("uMajorWeight", 0.5f)
    }
}