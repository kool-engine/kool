package de.fabmax.kool.demo

import android.os.Bundle
import de.fabmax.kool.KoolActivity
import de.fabmax.kool.assetTexture
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.*
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.*

class MainActivity : KoolActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // creates the Kool render context
        val ctx = createContext()

        ctx.scenes += simpleScene()

        /*var firstRender = true
        ctx.scenes += scene {
            onRender += {
                if (firstRender) {
                    firstRender = false
                    ctx.scenes += modelScene()
                }
            }
        }*/

        ctx.run()
    }

    private fun simpleScene() = scene {
        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(20f, -30f)
            // panning / camera translation is limited to a certain area
            translationBounds = BoundingBox(Vec3f(-50f), Vec3f(50f))
            // Add camera to the transform group
            +camera
        }

        +transformGroup {
            // Create an Animator to animate the cube rotation
            val cubeAnimator = LinearAnimator(InterpolatedFloat(0f, 360f))
            cubeAnimator.repeating = Animator.REPEAT
            cubeAnimator.duration = 20f

            // Update the rotation animation
            animation = { ctx ->
                val angle = cubeAnimator.tick(ctx)
                setIdentity()
                translate(3f, 0f, 0f)
                rotate(angle * 5, Vec3f.Y_AXIS)
                rotate(angle, Vec3f.X_AXIS)
            }

            // Add the color cube mesh
            +colorMesh("Cube") {
                // Customize the shader to include the saturation property
                shader = basicShader {
                    colorModel = ColorModel.VERTEX_COLOR
                    lightModel = LightModel.PHONG_LIGHTING
                }

                // The generator function is called to initially generate the mesh geometry
                generator = {
                    // Make the generated mesh twice as large
                    scale(2f, 2f, 2f)

                    // Generate cube mesh with every face set to a different color
                    cube {
                        // make it colorful
                        colorCube()
                        // set origin of cube to the center instead of lower left back corner
                        centerOrigin()
                    }
                }
            }
        }

        +transformGroup {
            // Create an Animator to animate the sphere Y position between -1 and 1
            val animator = CosAnimator(InterpolatedFloat(-1f, 1f))
            animator.repeating = Animator.REPEAT_TOGGLE_DIR

            // Animation function is called on every frame
            animation = { ctx ->
                // Clear transformation
                setIdentity()
                // Shift content 5 units left and let it bounce along Y-Axis
                translate(-3f, animator.tick(ctx), 0f)
                // Slowly rotate the sphere, so we can see all the colors
                rotate(ctx.time.toFloat() * 19, Vec3f.Y_AXIS)
            }

            // Add a sphere mesh, node name is optional but nice for debugging
            +textureMesh("Sphere") {
                // The generator function is called to initially generate the mesh geometry
                generator = {
                    // Generate the sphere mesh with a sphere radius of 1.5 units
                    sphere {
                        radius = 1.5f
                        // Make it really smooth
                        steps = 50
                    }
                }
                // load texture from assets
                (shader as BasicShader).texture = assetTexture("world.jpg")
            }
        }
    }
}
