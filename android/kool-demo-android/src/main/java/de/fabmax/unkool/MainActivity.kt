package de.fabmax.unkool

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import de.fabmax.kool.KoolActivity
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.transformGroup
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

        ctx.scenes += scene {
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
        }

        ctx.run()
    }
}
