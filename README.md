# Kotlin + OpenGL = kool

A simple OpenGL based graphics engine, which works with Desktop Java as well as
in browsers with Javascript/WebGL (and also on Android once I add the platform
bindings)

For now this is just an experiment. However, if you are curious
you can checkout the [javascript demo](https://fabmax.lima-city.de/kool/index.html).

What's working:
- Mesh animation (see [Model Demo](https://fabmax.lima-city.de/kool/index.html?demo=modelDemo), vertex-shader based animation yet to come...)
- OpenStreetMap tile loading (see [OSM Earth Demo](https://fabmax.lima-city.de/kool/index.html?demo=earthDemo))
- Synthie music (see [Synthie Demo](https://fabmax.lima-city.de/kool/index.html?demo=synthieDemo), quite CPU intense...)
- Multi-scene / multi-viewport support
- Some simple UI stuff: Text-Fields, (Toggle-)Buttons, Sliders, Labels
- Meshes with shared geometry
- Instantiatable models (no model loading yet)
- Lazy and delayed texture loading
- Ray picking on scene objects
- Mouse hover events for scene objects
- Simple animations 
- Text rendering using arbitrary fonts. For now character set is fixed but *theoretically* it has unicode support :)
- Mouse controlled camera
- Simple scene graph
- Mesh building functions for several primitive 3D shapes
- Shading with different light (Phong / Gouraud) and color models (vertex, texture or fixed)

Below is the code for a demo scene

## Example:
```kotlin
fun main(args: Array<String>) {
    // Initialize platform and kool context
    val ctx = createContext()
    
    // Create scene contents
    ctx.scenes += scene {
        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform {
            // Set some initial rotation so that we look down on the scene
            setRotation(20f, -30f)
            // camera pans in y-plane (y is up-axis, camera can move along x and z axis)
            panMethod = yPlanePan()
            // panning / camera translation is limited to a certain area
            translationBounds = BoundingBox(Vec3f(-50f), Vec3f(50f))
            // Add camera to the transform group
            +camera
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
                    (shader as BasicShader).saturation = v
                }

                // Customize the shader to include the saturation property
                shader = basicShader {
                    colorModel = ColorModel.VERTEX_COLOR
                    lightModel = LightModel.PHONG_LIGHTING
                    // saturation property is needed to control the color intensity of the cube
                    isSaturation = true
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

                // Update the speed animator on every frame
                onRender += { ctx ->
                    speedAnimator.tick(ctx)
                }
                // By setting a positive speed the speed animator is started and animates it's value to 1. That value
                // is applied as animation speed of the rotation animation and as color intensity
                // --> The cube starts spinning and colors fade in.
                onHoverEnter += { ptr, rt, ctx ->
                    speedAnimator.speed = 1f
                }
                // By setting a positive speed the speed animator is started and animates it's value to 0.
                // --> The cube stops spinning and colors fade out.
                onHoverExit += { ptr, rt, ctx ->
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

            // Add the text, you can use any font you like. We us a font size of 72pts and characters will be 1.5
            // units tall
            val font = Font(FontProps(Font.SYSTEM_FONT, 72f, Font.PLAIN, 1.5f))
            +textMesh(font) {
                generator = {
                    color = Color.LIME
                    text(font) {
                        // Set the text to render, for now only characters defined in [Font.STD_CHARS] can be rendered
                        text = "kool Text!"
                        // Make the text centered
                        origin.set(-font.textWidth(text) / 2f, 0f, 0f)
                    }
                }
            }
        }
    }

    // Set background color
    ctx.clearColor = Color(0.05f, 0.15f, 0.25f, 1f)
    // Finally run the whole thing
    ctx.run()
}
```