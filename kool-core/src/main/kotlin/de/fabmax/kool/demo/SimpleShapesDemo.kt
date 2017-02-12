package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.group
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.transformGroup
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.colorMesh

/**
 * A simple demo scene with mouse-controlled camera and a few animated shapes.
 *
 * @author fabmax
 */
fun simpleShapesDemo(ctx: RenderContext) {
    // Create scene contents
    ctx.scene.root = group {
        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform {
            // Set some initial rotation so that we look down on the scene
            setRotation(20f, -30f)
            // Add camera to the transform group
            +ctx.scene.camera
        }

        // Add a TransformGroup with a bouncing sphere
        +transformGroup {
            // Animation function is called on every frame
            animation = { ctx ->
                // Clear transformation
                setIdentity()
                // Shift content 5 units left and let it bounce along Y-Axis
                translate(-5f, Math.sin(ctx.time * 5).toFloat(), 0f)
                // Slowly rotate the sphere, so we can see all the colors
                rotate(ctx.time.toFloat() * 19, Vec3f.X_AXIS)
            }

            // Add a sphere mesh
            +colorMesh {
                // vertexModFun is called for every generated vertex, overwrite the vertex color depending on the
                // normal orientation, this will make the sphere nicely colorful
                vertexModFun = { color.set(Color((normal.x + 1) / 2, (normal.y + 1) / 2, (normal.z + 1) / 2, 1f)) }
                // Generate the sphere mesh with a sphere radius of 1.5 units
                sphere { radius = 1.5f }
            }
        }

        // Add a TransformGroup with a rotating cube
        +transformGroup {
            // Similar to above, but this time content is shifted to the right
            animation = { ctx ->
                setIdentity()
                translate(5f, 0f, 0f)
                rotate(ctx.time.toFloat() * 90, Vec3f.Y_AXIS)
                rotate(ctx.time.toFloat() * 19, Vec3f.X_AXIS)
            }

            // Add a cube mesh
            +colorMesh {
                // Make the generated mesh twice as large
                scale(2f, 2f, 2f)
                // Shift cube origin to center instead of lower, left, back corner
                translate(-.5f, -.5f, -.5f)

                // Generate cube mesh with every face set to a different color
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

        // Add another TransformGroup with a size-changing cylinder
        +transformGroup("back") {
            // Content is shifted to the back and scaled depending on time
            animation = { ctx ->
                setIdentity()
                translate(0f, 0f, -5f)
                val s = 1f + Math.sin(ctx.time * 3).toFloat() * 0.5f
                scale(s, s, s)
            }

            // Add the cylinder mesh
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
    }

    // Set background color
    ctx.clearColor = Color(0.05f, 0.15f, 0.25f, 1f)
    // Finally run the whole thing
    ctx.run()
}
