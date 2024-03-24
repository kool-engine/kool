package de.fabmax.kool.demo.tests

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.vertexAttribFloat3
import de.fabmax.kool.pipeline.vertexAttribFloat4
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class InifiniteDepthTest : DemoScene("Infinite Depth Test") {

    override fun Scene.setupMainScene(ctx: KoolContext) {
        tryEnableInfiniteDepth()
        camera.setup()

        addMesh(Attribute.POSITIONS, Attribute.COLORS) {
            val depthsToColors = mapOf(
                1e16f to MdColor.INDIGO,        // 10^16 m: ~1 light year away from camera
                1e9f to MdColor.BLUE,
                1.001e9f to MdColor.RED,        // very little behind blue rect -> won't be visible
                9.999e8f to MdColor.CYAN,       // very little in front of blue rect
                5e6f to MdColor.GREEN,
                2f to MdColor.LIME,
                0.11f to MdColor.AMBER,
                0.1f to Color.WHITE,
            )

            generate {
                var fac = 0.1f + 0.1f * depthsToColors.size
                depthsToColors.forEach { (z, color) ->
                    this.color = color
                    rect {
                        origin.set(0f, 0f, -z)
                        val sz = z * fac
                        size.set(sz, sz)
                    }
                    fac -= 0.1f
                }
            }

            // tilt mesh a bit to see if antialiasing works
            transform.rotate(5f.deg, Vec3f.Z_AXIS)

            shader = KslUnlitShader {
                color { vertexColor() }
            }
        }

        addMesh(Attribute.POSITIONS, Attribute.COLORS) {
            val depthsToColors = mapOf(
                1.01f to MdColor.RED,       // should never be visible
                1.0f to MdColor.ORANGE,
                0.5f to MdColor.GREEN,
                0f to MdColor.CYAN,
                -0.01f to MdColor.BLUE,     // negative depth should not be visible if clip space is zero to one (i.e. suited for reversed depth)
                -1f to MdColor.PINK,
                -1.01f to MdColor.PURPLE,   // should never be visible
            )

            generate {
                var y = -0.9f
                depthsToColors.forEach { (z, color) ->
                    this.color = color

                    rect {
                        origin.set(0.9f, y, z)
                        size.set(0.1f, 0.1f)
                        y += 0.15f
                    }
                }
            }
            shader = KslShader("Clip Test") {
                val vColor = interStageFloat4()
                vertexStage {
                    main {
                        vColor.input set vertexAttribFloat4(Attribute.COLORS)
                        outPosition set float4Value(vertexAttribFloat3(Attribute.POSITIONS), 1f.const)
                    }
                }
                fragmentStage {
                    main {
                        colorOutput(vColor.output)
                    }
                }
            }
        }
    }

    private fun Camera.setup() {
        // cam far value: we could even use Float.POSITIVE_INFINITY here, but some very large non-infinite value
        //  should do as well (and is numerically "better")
        val far = 1e16f
        setClipRange(0.1f, far)
        position.set(0f, 0f, 0f)
        lookAt.set(0f, 0f, -1f)
    }
}
