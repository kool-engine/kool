# Kotlin + OpenGL = kool

A simple OpenGL based graphics engine, which works with Desktop Java as well as
in browsers with Javascript/WebGL. Android is in progress (quite far, see android branch...)

For now this is just an experiment. However, if you are curious
you can checkout the [javascript demo](https://fabmax.github.io/kool/kool-js/?demo=treeDemo).

The hamburger-button in the upper-left corner triggers the demo chooser menu. Code for
all demos is available in kool-demo sub-project. 

## Features / Noticeable stuff:
- Multi-touch support in Javascript / WebGL (works on mobile browsers)
- New procedural [Tree Demo](https://fabmax.github.io/kool/kool-js/?demo=treeDemo)
- Cascaded shadow maps
- Vertex shader mesh animation: [Model Demo](https://fabmax.github.io/kool/kool-js/?demo=modelDemo)
- Normal mapping
- OpenStreetMap tile loading: [OSM Earth Demo](https://fabmax.github.io/kool/kool-js/?demo=earthDemo)
- Synthie music: [Synthie Demo](https://fabmax.github.io/kool/kool-js/?demo=synthieDemo), quite CPU intense...
- Multi-scene / multi-viewport support
- Some simple UI stuff: Text-Fields, (Toggle-)Buttons, Sliders, Labels
- Meshes with shared geometry
- Lazy and delayed texture loading
- Ray picking on scene objects
- Mouse hover events for scene objects
- Simple animations 
- Text rendering using arbitrary fonts. For now character set is fixed but *theoretically* it has unicode support :)
- Mouse controlled camera
- Simple scene graph
- Mesh building functions for several primitive 3D shapes
- Shading with different light (Phong / Gouraud) and color models (vertex, texture or fixed)

## Hello World Example (spinning cube):
For more example code checkout kool-demo sub-project.
```kotlin
fun main(args: Array<String>) {
    // Initialize platform and kool context
    val ctx = createContext()
    
    // Set some background color
    ctx.clearColor = Color(0.05f, 0.15f, 0.25f, 1f)
    
    // Create scene contents
    ctx.scenes += scene {
        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(20f, -30f)
            // camera pans in y-plane (y is up-axis, camera can move along x and z axis)
            panMethod = yPlanePan()
            // panning / camera translation is limited to a certain area
            translationBounds = BoundingBox(Vec3f(-50f), Vec3f(50f))
            // Add camera to the transform group
            +camera
        }
    
        // Add a TransformGroup with a rotating cube
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
                // The generator function is called to initially generate the mesh geometry
                generator = {
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
    
    // Finally run the whole thing
    ctx.run()
}
```
