# Kotlin + OpenGL = kool

A simple OpenGL based graphics engine that works on Desktop Java, Android and
in browsers with Javascript / WebGL.

Android version is now merged into main branch, but in IntelliJ the kotlin-platform-android plugin still
behaves a bit odd (IntelliJ 2018.1 EAP).
Android build can be disabled by removing the two android modules from settings.gradle...

For now this is just an experiment. However, if you are curious
you can checkout the [javascript demo](https://fabmax.github.io/kool/kool-js/?demo=modelDemo).
The hamburger-button in the upper-left corner triggers the demo chooser menu. Code for
all demos is available in kool-demo sub-project.

I started porting a few parts of [Bullet Physics](https://github.com/bulletphysics/bullet3). However, it's work in
progress and still in a super early state: Only supported shape are boxes, no joints and there is no real broadphase
yet. That's fine for [a few boxes](https://fabmax.github.io/kool/kool-js/?demo=boxDemo) but not much more (Actually
it's surprisingly fast given the fact that there is no intelligent broadphase - JVM implementation handles
200 bodies, js is slower...).

## Features / Noticeable stuff:
- Some super-primitive [Physics Simulation](https://fabmax.github.io/kool/kool-js/?demo=boxDemo)
- Full support of all features on all platforms
- Multi-touch support (on Android and Javascript / WebGL)
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
- View frustum culling of scene objects
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
            onPreRender += { ctx ->
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

## References
- http://ogldev.atspace.co.uk/index.html
  
  Best OpenGL tutorial page I came across so far. [Cascaded shadow maps](http://ogldev.atspace.co.uk/www/tutorial49/tutorial49.html),
  [vertex shader mesh animation](http://ogldev.atspace.co.uk/www/tutorial38/tutorial38.html) and
  [normal mapping](http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html) implementations are based on tutorials
  from this page.

- http://www.lighthouse3d.com

  Also nice OpenGL tutorials. [View frustum culling](http://www.lighthouse3d.com/tutorials/view-frustum-culling/)
  is based on one of them (I use the Radar Approach).

- [Modeling Trees with a Space Colonization Algorithm](http://algorithmicbotany.org/papers/colonization.egwnp2007.large.pdf)
  
  A nice paper about procedural generation of trees. Tree demo is based on that one.

- http://wavepot.com
  
  Javascript audio synthesizer. Melody of synthie demo is taken from here ("unexpected-token"). My implementation is
  quite different though...

- [Bullet Physics](https://github.com/bulletphysics/bullet3)

  Awesome open-source physics engine. Ported some parts of it.
