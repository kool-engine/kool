package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.transformGroup
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.colorMesh

/**
 * @author fabmax
 */
fun simpleShapesDemo(ctx: RenderContext) {

    ctx.scene.root = transformGroup {
        animation = { ctx ->
            transform.rotate(45f * ctx.deltaT, Vec3f.Y_AXIS)
        }

        +transformGroup {
            animation = { ctx ->
                transform.setIdentity()
                transform.translate(-5f, Math.sin(ctx.time * 5).toFloat(), 0f)
                transform.rotate(ctx.time.toFloat() * 19, Vec3f.X_AXIS)
            }

            +colorMesh {
                vertexModFun = { color.set(Color((normal.x + 1) / 2, (normal.y + 1) / 2, (normal.z + 1) / 2, 1f)) }
                sphere { radius = 1.5f }
            }
        }

        +transformGroup {
            animation = { ctx ->
                transform.setIdentity()
                transform.translate(5f, 0f, 0f)
                transform.rotate(ctx.time.toFloat() * 90, Vec3f.Y_AXIS)
                transform.rotate(ctx.time.toFloat() * 19, Vec3f.X_AXIS)
            }

            +colorMesh {
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
    }

    ctx.clearColor = Color(0.05f, 0.15f, 0.25f, 1f)
    ctx.scene.camera.position.set(0f, 5f, 15f)
    ctx.run()
}
