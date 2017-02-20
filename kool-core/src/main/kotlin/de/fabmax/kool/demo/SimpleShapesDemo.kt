package de.fabmax.kool.demo

import de.fabmax.kool.assetTexture
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.group
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.transformGroup
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.*

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

        // Add a TransformGroup with a bouncing textured sphere
        +transformGroup {
            // Create an Animator to animate the sphere Y position between -1 and 1
            val animator = CosAnimator(InterpolatedFloat(-1f, 1f))
            animator.repeating = Animator.REPEAT_TOGGLE_DIR

            // Animation function is called on every frame, animator.tick is called to update the animated value
            animation = { ctx ->
                // Clear transformation
                setIdentity()
                // Shift content 5 units left and let it bounce along Y-Axis
                translate(-5f, animator.tick(ctx), 0f)
                // Slowly rotate the sphere, so we can see all the colors
                rotate(ctx.time.toFloat() * 19, Vec3f.Y_AXIS)
            }

            // Add a sphere mesh, node name is optional but nice for debugging
            +textureMesh("Sphere") {
                // Generate the sphere mesh with a sphere radius of 1.5 units
                sphere {
                    radius = 1.5f
                    // Make it really smooth
                    steps = 50
                }
                // load texture from assets
                shader?.texture = assetTexture("world.jpg")
            }
        }

        // Add a TransformGroup with a rotating cube
        // The cube has a mouse hover listener: normally it's gray and rotation is paused, colors fade in and
        // rotation starts when the mouse hovers over the cube
        +transformGroup {
            // Create an Animator to animate the cube rotation
            val cubeAnimator = LinearAnimator(InterpolatedFloat(0f, 360f))
            cubeAnimator.repeating = Animator.REPEAT
            cubeAnimator.duration = 20f

            // Update the rotation animation
            animation = { ctx ->
                val angle = cubeAnimator.tick(ctx)
                setIdentity()
                translate(5f, 0f, 0f)
                rotate(angle * 5, Vec3f.Y_AXIS)
                rotate(angle, Vec3f.X_AXIS)
            }

            // Add the color cube mesh
            +colorMesh("Cube") {
                // Create an animator which is triggered by the mouse hover events
                // The animator controls the rotation speed and color intensity of the cube
                val speedAnimator = CosAnimator(InterpolatedFloat(0f, 1f))
                // By setting an initial negative speed, the animation is updated exactly once and then pauses
                speedAnimator.speed = -1f
                speedAnimator.duration = 0.5f
                speedAnimator.value.onUpdate = { v ->
                    // Update rotation animation speed and color intensity
                    cubeAnimator.speed = v
                    shader?.saturation = v
                }

                // Customize the shader to include the saturation property
                shader = basicShader {
                    colorModel = ColorModel.VERTEX_COLOR
                    lightModel = LightModel.PHONG_LIGHTING
                    // saturation property is needed to control the color intensity of the cube
                    isSaturation = true
                }

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

                // Update the speed animator on every frame
                mesh.onRender = { ctx ->
                    speedAnimator.tick(ctx)
                }
                // By setting a positive speed the speed animator is started and animates it's value to 1. That value
                // is applied as animation speed of the rotation animation and as color intensity
                // --> The cube starts spinning and colors fade in.
                mesh.onHoverEnter = {
                    speedAnimator.speed = 1f
                }
                // By setting a positive speed the speed animator is started and animates it's value to 0.
                // --> The cube stops spinning and colors fade out.
                mesh.onHoverExit = {
                    speedAnimator.speed = -1f
                }
            }
        }

        // Add another TransformGroup with a size-changing text string
        +transformGroup {
            // Animate text size between 0.75 and 1.25
            val animator = CosAnimator(InterpolatedFloat(0.75f, 1.25f))
            animator.repeating = Animator.REPEAT_TOGGLE_DIR
            animator.duration = 0.75f

            // Content is shifted to the back and scaled depending on time
            animation = { ctx ->
                val s = animator.tick(ctx)
                setIdentity()
                translate(0f, 0f, -5f)
                scale(s, s, s)
            }

            // Add the text, you can use any font you like
            val textFont = Font("sans-serif", 72.0f)
            +textMesh(textFont) {
                color = Color.LIME
                text {
                    // Set the text to be rendered, for now only characters defined in [Font.STD_CHARS] can be rendered
                    text = "kool Text!"
                    font = textFont
                    // Make the text centered
                    position.set(-font.stringWidth(text) / 2f, 0f, 0f)
                    // generated text mesh size is based on the font size, without scaling, a single character would
                    // be 48 units tall, hence we have to scale it down a lot. This could also be done by calling
                    // scale(0.03f, 0.03f, 0.03f) on an outer level, but let's take the shortcut
                    scale = 0.02f
                }
            }
        }
    }

    // Set background color
    ctx.clearColor = Color(0.05f, 0.15f, 0.25f, 1f)
    // Finally run the whole thing
    ctx.run()
}
