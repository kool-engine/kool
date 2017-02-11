package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.group
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.transformGroup
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.colorMesh

/**
 * @author fabmax
 */
fun simpleShapesDemo(ctx: RenderContext) {
    ctx.scene.root = group {

        +transformGroup("left") {
            animation = { ctx ->
                setIdentity()
                translate(-5f, Math.sin(ctx.time * 5).toFloat(), 0f)
                rotate(ctx.time.toFloat() * 19, Vec3f.X_AXIS)
            }

            +colorMesh("sphere") {
                vertexModFun = { color.set(Color((normal.x + 1) / 2, (normal.y + 1) / 2, (normal.z + 1) / 2, 1f)) }
                sphere { radius = 1.5f }
            }
        }

        +transformGroup("right") {
            animation = { ctx ->
                setIdentity()
                translate(5f, 0f, 0f)
                rotate(ctx.time.toFloat() * 90, Vec3f.Y_AXIS)
                rotate(ctx.time.toFloat() * 19, Vec3f.X_AXIS)
            }

            +colorMesh("cube") {
                scale(2f, 2f, 2f)
                translate(-.5f, -.5f, -.5f)

                cube {
                    frontColor = Color.RED
                    rightColor = Color.GREEN
                    backColor = Color.BLUE
                    leftColor = Color.YELLOW
                    topColor = Color.MAGENTA
                    bottomColor = Color.CYAN
                }
            }
        }

        +transformGroup("back") {
            animation = { ctx ->
                setIdentity()
                translate(0f, 0f, -5f)
                val s = 1f + Math.sin(ctx.time * 3).toFloat() * 0.5f
                scale(s, s, s)
            }

            +colorMesh {
                color = Color.LIME
                cylinder {
                    origin.set(0f, -1.5f, 0f)
                    height = 3f
                    topRadius = .5f
                    bottomRadius = 1f
                }
            }

        }

        +sphericalInputTransform("camRig") {
            +ctx.scene.camera
            setRotation(0f, -30f)
        }
    }

    ctx.clearColor = Color(0.05f, 0.15f, 0.25f, 1f)
    ctx.run()
}
