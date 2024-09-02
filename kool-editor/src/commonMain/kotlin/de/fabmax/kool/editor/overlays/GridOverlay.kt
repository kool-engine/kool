package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.BasicVertexConfig
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.vertexTransformBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.vertexAttribFloat4
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import kotlin.math.roundToLong

class GridOverlay(val overlay: OverlayScene) : Node("Grid overlay"), EditorOverlay {
    private val editor: KoolEditor get() = overlay.editor

    private val yPlaneShader = GridShader()
    private val gridTransform = TrsTransformD()

    private val yPlaneGrid: LineMesh
    private val camPos = MutableVec3d()
    private val camLookAt = MutableVec3d()

    init {
        yPlaneGrid = addLineMesh("Y-Plane") {
            makeGrid()
            shader = yPlaneShader
            transform = gridTransform
        }

        onUpdate {
            updateShader(it.camera, yPlaneShader)
        }
    }

    private fun updateShader(cam: Camera, shader: GridShader) {
        val sceneOrigin = editor.activeScene.value?.sceneOrigin?.translation ?: Vec3d.ZERO

        camPos.set(cam.globalPos)
        camLookAt.set(cam.globalLookAt)
        val scale = camPos.distance(camLookAt) / 32
        var sDiscrete = 1.0 / 32.0
        while (sDiscrete < scale) {
            sDiscrete *= 4.0
        }
        val lowerDiscrete = sDiscrete / 4f
        val wx = ((scale - lowerDiscrete) / (sDiscrete - lowerDiscrete)).toFloat()

        val mod = 32.0 * sDiscrete
        val offsetX = (camPos.x / (32.0 * sDiscrete)).roundToLong() * mod + sceneOrigin.x % mod
        val offsetZ = (camPos.z / (32.0 * sDiscrete)).roundToLong() * mod + sceneOrigin.z % mod
        val offsetY = sceneOrigin.y

        shader.posOffset = Vec3f(offsetX.toFloat(), offsetY.toFloat(), offsetZ.toFloat())
        shader.scale = Vec2f(scale.toFloat(), sDiscrete.toFloat())
        shader.majorWeight = smoothStep(0f, 0.1f, wx) * 0.75f + smoothStep(0.8f, 1f, wx) * 0.25f
        shader.superTickColor = editor.ui.uiColors.value.primaryVariant.withAlpha(0.4f)
    }

    private fun LineMesh.makeGrid() {
        isFrustumChecked = false
        val superTick = Color.MAGENTA
        val majorColor = Color.RED
        val minorColor = Color.GREEN
        val n = GRID_N

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

    companion object {
        private const val GRID_N = 400
    }

    private class GridShader : KslShader("grid-shader") {
        var scale by uniform2f("uScale", Vec2f(1f, 1f))
        var posOffset by uniform3f("uPosOffset", Vec3f.ZERO)

        var lineColor by uniformColor("uLineColor", Color.LIGHT_GRAY.withAlpha(0.4f))
        var superTickColor by uniformColor("uSuperTickColor")
        var majorWeight by uniform1f("uMajorWeight", 0.5f)

        init {
            program.gridProgram()
        }

        private fun KslProgram.gridProgram() {
            val clipPos = interStageFloat4()
            val fragPos = interStageFloat3()
            val fragColor = interStageFloat4()
            vertexStage {
                main {
                    val scale = uniformFloat2("uScale")
                    val viewProj = mat4Var(cameraData().viewProjMat)

                    val localPos = float3Var(vertexAttribFloat3(Attribute.POSITIONS.name) * scale.y)
                    val vertexBlock = vertexTransformBlock(BasicVertexConfig.Builder().build()) {
                        inLocalPos(localPos)
                    }
                    val worldPos = float3Var(vertexBlock.outWorldPos)
                    worldPos set worldPos + uniformFloat3("uPosOffset")

                    val proj = float4Var(viewProj * float4Value(worldPos, 1f))
                    outPosition set proj
                    clipPos.input set proj
                    fragPos.input set worldPos
                    fragColor.input set vertexAttribFloat4(Attribute.COLORS)
                }
            }
            fragmentStage {
                main {
                    val inColor = float4Var(fragColor.output)
                    val outColor = float4Var()
                    val scale = uniformFloat2("uScale")

                    val camData = cameraData()
                    val camDist = float1Var(length(camData.position - fragPos.output))
                    val scaledDist = float1Var(camDist / (scale.x * (GRID_N.toFloat()).const))
                    val aMod = float1Var(1f.const - smoothStep(0.25f.const, 1f.const, scaledDist))

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
                    colorOutput(outColor)

                    outDepth set (clipPos.output.z / (clipPos.output.w * 0.99999f.const))
                }
            }
        }
    }
}