# kool - A OpenGL / Vulkan graphics engine written in Kotlin
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/fabmax/kool/blob/master/LICENSE)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.fabmax.kool/kool-core/badge.svg)](https://central.sonatype.com/artifact/de.fabmax.kool/kool-core)

A multi-platform OpenGL / Vulkan based game engine that works on Desktop Java and browsers with
WebGL2.

I started working on a graphical scene editor, which should make it much easier to create new projects with this engine.
However, the editor is still in a super early state and not very useful yet. So, for now, this is very much a code-only
game engine. I recommend taking a look at the demos listed below in case you are curious (all demo source code is
available in the `kool-demo` subproject). The library is also published on maven central, see [below](#usage).

I also made an actual game with this: [Blocks and Belts](https://fabmaxx.itch.io/blocks-and-belts).
Give it a try (it's free)!

## Demos
- [Island](https://fabmax.github.io/kool/kool-js/?demo=phys-terrain): Height-map based
  island incl. some wind-affected vegetation + a basic controllable character.
- [Physics - Ragdoll](https://fabmax.github.io/kool/kool-js/?demo=phys-ragdoll): Ragdoll physics demo.
- [Physics - Vehicle](https://fabmax.github.io/kool/kool-js/?demo=phys-vehicle): A drivable vehicle (W, A, S, D /
  cursor keys, R to reset) based on the Nvidia PhysX vehicles SDK.
- [Physics - Joints](https://fabmax.github.io/kool/kool-js/?demo=phys-joints): Physics demo consisting of a chain
  running over two gears. Uses a lot of multi shapes and revolute joints.
- [Physics - Collision](https://fabmax.github.io/kool/kool-js/?demo=physics): The obligatory collision physics demo with
  various different shapes.
- [Embedded UI](https://fabmax.github.io/kool/kool-js/?demo=ui): Integrated UI framework implemented completely within
  the engine. Fast, highly customizable and easy-to-use.
- [Creative Coding](https://fabmax.github.io/kool/kool-js/?demo=creative-coding): A few relatively simple demos
  showcasing different techniques of generating procedural geometry.
- [Procedural Geometry](https://fabmax.github.io/kool/kool-js/?demo=procedural): Small test-case for
  procedural geometry; all geometry is generated in code (even the roses! Textures are regular images though). Also, some glass
  shading (shaft of the wine glass, the wine itself looks quite odd when shaded with refractions and is therefore opaque).
- [glTF Models](https://fabmax.github.io/kool/kool-js/?demo=gltf): Various demo models loaded from glTF / glb format
  - Flight Helmet from [glTF sample models](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0/FlightHelmet)
  - Polly from [Blender](https://github.com/KhronosGroup/glTF-Blender-Exporter/tree/master/polly)
  - Coffee Cart from [3D Model Haven]((https://3dmodelhaven.com/model/?c=appliances&m=CoffeeCart_01))
  - Camera Model also from [3D Model Haven](https://3dmodelhaven.com/model/?c=appliances&m=CoffeeCart_01)
  - A few feature test models also from the [glTF sample model repository](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0)
- [Deferred Shading](https://fabmax.github.io/kool/kool-js/?demo=deferred): Thousands of dynamic
  light sources, bloom and ambient occlusion.
- [Screen-space Ambient Occlusion](https://fabmax.github.io/kool/kool-js/?demo=ao): Roughly based on
  [this](http://john-chapman-graphics.blogspot.com/2013/01/ssao-tutorial.html) article by John
  Chapman with slightly optimized sampling (also shamelessly recreated his demo scene).
- [Screen-space Reflections](https://fabmax.github.io/kool/kool-js/?demo=ssr): A simple PBR shaded
  model with screen-space reflections and up to four spot-lights with dynamic shadows.
- [Physical Based Rendering](https://fabmax.github.io/kool/kool-js/?demo=pbr): Interactive PBR demo 
  with image based lighting for various materials and environments (underlying PBR theory from
  [this](https://learnopengl.com/PBR/Theory) awesome article series).
- [Instanced / LOD Drawing](https://fabmax.github.io/kool/kool-js/?demo=instance): Instanced rendering
  demo of the Stanford Bunny. Uses six levels of detail to render up to 8000 instances.
- [Mesh Simplification](https://fabmax.github.io/kool/kool-js/?demo=simplification): Interactive mesh
  simplification demo (based on traditional [error-quadrics](https://www.cs.cmu.edu/~./garland/Papers/quadrics.pdf))

Code for all demos is available in kool-demo sub-project.

## Engine Features / Noticeable Stuff:
- Physics simulation (based on Nvidia PhysX 5.1, using [physx-jni](https://github.com/fabmax/physx-jni) on Java and [physx-js-webidl](https://github.com/fabmax/physx-js-webidl) on javascript)
- Kotlin DSL based shader language (translates into GLSL)
- Neat little integrated GUI framework. The API is heavily inspired by [Jetpack Compose](https://github.com/JetBrains/compose-jb) but the implementation is different, as it needs to run within the OpenGL context.
- [MSDF](https://github.com/Chlumsky/msdf-atlas-gen) Font support for text rendering in arbitrary font sizes
- Experimental Vulkan rendering backend (on JVM)
- Support for physical based rendering (with metallic workflow) and image-based lighting
- (Almost) complete support for [glTF 2.0](https://github.com/KhronosGroup/glTF) model format (including animations, morph targets and skins)
- Skin / armature mesh animation (vertex shader based)
- Deferred shading
  - HDR lighting with [Uncharted2 tone-mapping](http://filmicworlds.com/blog/filmic-tonemapping-operators/)
  - Optional Screen-space reflections
  - Optional Bloom
- Screen-space ambient occlusion
- Normal, roughness, metallic, ambient occlusion and displacement mapping
- Lighting with multiple point, spot and directional lights
- Shadow mapping for multiple light sources (only spot and directional lights for now)
- Basic audio support

## Usage

If you are adventurous, you can use kool as a library in your own (multiplatform-) projects. The library is published
on maven central, and there is a separate repo containing a minimal template project to get you started:

[https://github.com/fabmax/kool-templates](https://github.com/fabmax/kool-templates)

The demos mentioned above and examples shown below should give you a rough idea on how to do stuff (documentation is
still a bit of a weak spot).

## A Hello World Example

Getting a basic scene on the screen is quite simple:
```kotlin
fun main() = KoolApplication { ctx ->
    ctx.scenes += scene {
        defaultOrbitCamera()

        colorMesh {
            generate {
                cube(centered = true) {
                    colored()
                }
            }
            shader = KslPbrShader {
                color { vertexColor() }
                metallic(0f)
                roughness(0.25f)
            }
            onUpdate {
                transform.rotate(Time.deltaT * 45f, Vec3f.X_AXIS)
            }
        }

        lighting.singleLight {
            setDirectional(Vec3f(-1f, -1f, -1f))
            setColor(Color.WHITE, 5f)
        }
    }
}
```
The above example creates an application with a single scene and sets up a mouse-controlled camera
(with `defaultOrbitCamera()`).
As you might have guessed the `colorMesh { ... }` block creates a colored cube and adds it to the scene.
In order to draw the mesh on the screen it needs a shader, which is assigned with
`shader = KslPbrShader { ... }`. This creates a simple PBR shader for a dielectric material
with a rather smooth surface. Color information is taken from the corresponding vertex attribute.
The `onUpdate`-block is called on each frame and modifies the cube transform to rotate it 45° per second around its
X-axis.
Finally, we set up a single directional scene light (of white color and an intensity of 5), so that our cube can shine
in its full glory. The resulting scene looks like [this](https://fabmax.github.io/kool/kool-js/?demo=helloWorld).

## Model Loading and Advanced Lighting

Model loading, animation and more advanced lighting with shadow mapping and ambient occlusion requires only a few more
lines of code:
```kotlin
fun main() = KoolApplication { ctx ->
    ctx.scenes += scene {
        defaultOrbitCamera()

        // Light setup
        lighting.singleLight {
            setSpot(Vec3f(5f, 6.25f, 7.5f), Vec3f(-1f, -1.25f, -1.5f), 45f)
            setColor(Color.WHITE, 300f)
        }
        val shadowMap = SimpleShadowMap(this, lightIndex = 0)
        val aoPipeline = AoPipeline.createForward(this)

        // Add a ground plane
        colorMesh {
            generate {
                grid { }
            }
            shader = KslPbrShader {
                color { constColor(Color.WHITE) }
                shadow { addShadowMap(shadowMap) }
                enableSsao(aoPipeline.aoMap)
            }
        }

        // Load a glTF 2.0 model
        Assets.launch {
            val materialCfg = GltfFile.ModelMaterialConfig(
                shadowMaps = listOf(shadowMap),
                scrSpcAmbientOcclusionMap = aoPipeline.aoMap
            )
            val modelCfg = GltfFile.ModelGenerateConfig(materialConfig = materialCfg)
            val model = loadGltfModel("path/to/model.glb", modelCfg)

            model.transform.translate(0f, 0.5f, 0f)
            if (model.animations.isNotEmpty()) {
                model.enableAnimation(0)
                model.onUpdate {
                    model.applyAnimation(Time.deltaT)
                }
            }
            
            // Add model to scene, use RenderLoop coroutine context to make sure insertion happens at a safe time
            withContext(Dispatchers.RenderLoop) {
                addNode(model)
            }
        }
    }
}
```
First we set up the lighting. This is very similar to the previous example but this time we use a spot-light, which
requires a position, direction and opening angle. Other than directional lights, point and spot-lights have a distinct
(point-) position and objects are affected less by them, the farther they are away. This usually results in a much
higher required light intensity: Here we use an intensity of 300.

Next we create a `SimpleShadowMap` which computes the shadows cast by the light source we defined before.
Moreover, the created `AoPipeline` computes an ambient occlusion map, which is later used by the shaders to
further improve the visual appearance of the scene.

After light setup we can add objects to our scene. First we generate a grid mesh as ground plane. Default size and
position of the generated grid are fine, therefore `grid { }` does not need any more configuration. Similar to the
color cube from the previous example, the ground plane uses a PBR shader. However, this time we tell the shader to
use the ambient occlusion and shadow maps we created before. Moreover, the shader should not use the vertex color
attribute, but a simple pre-defined color (white in this case).

Finally, we want to load a glTF 2.0 model. Resources are loaded via the `Assets` object. Since resource loading is a
potentially long-running operation we do that from within a coroutine launched with the asset manager:
`Assets.launch { ... }`. By default, the built-in glTF parser creates shaders for all models it loads. The
created shaders can be customized via a provided material configuration, which we use to pass the shadow and
ambient occlusion maps we created during light setup. After we created the custom model / material configuration
we can load the model with `loadGltfModel("path/to/model.glb", modelCfg)`. This (suspending) function returns the
loaded model, which can then be customized and inserted into the scene. Here we move the model 0.5 units along the
y-axis (up). If the model contains any animations, these can be easily activated. This example checks whether there
are any animations and if so activates the first one. The `model.onUpdate { }` block is executed on every frame and
updates the enabled animation. The model is inserted into the scene with `addNode(model)`, however we do that from
the `Dispatchers.RenderLoop` context to avoid threading issues (on JVM the asset loader coroutines run on a different
thread than the render-loop).

The resulting scene looks like [this](https://fabmax.github.io/kool/kool-js/?demo=helloGltf). Here, the
[Animated Box](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0/BoxAnimated) from the glTF sample
repository is loaded.

## Kool UI

Kool comes with an embedded UI framework, which is heavily inspired by [Jetpack Compose](https://github.com/JetBrains/compose-jb)
but was implemented from scratch. Here is a small example:
```kotlin
fun main() = KoolApplication { ctx ->
    ctx.scenes += UiScene(clearScreen = true) {
        Panel(colors = Colors.singleColorLight(MdColor.LIGHT_GREEN)) {
            modifier
                .size(400.dp, 300.dp)
                .align(AlignmentX.Center, AlignmentY.Center)
                .background(RoundRectBackground(colors.background, 16.dp))

            var clickCount by remember(0)
            Button("Click me!") {
                modifier
                    .alignX(AlignmentX.Center)
                    .margin(sizes.largeGap * 4f)
                    .padding(horizontal = sizes.largeGap, vertical = sizes.gap)
                    .font(sizes.largeText)
                    .onClick { clickCount++ }
            }
            Text("Button clicked $clickCount times") {
                modifier
                    .alignX(AlignmentX.Center)
            }
        }
    }
}
```
Here, we create a new `UiScene` and add a `Panel` to it, which serves as top-level container for our UI content. Within
the `Panel`-block, we add a button and a text field. All appearance and layout-properties of the UI elements are
controlled by their `modifier`s.

Whenever the button is clicked we increment a `clickCount` which is then displayed by the text field. This works
because the `Panel`-block is executed each time any `remember`ed state (or `mutableStateOf()`) within the block changes.
The mechanics behind that are somewhat similar to how Jetpack-Compose works, although my implementation is much less
sophisticated. On the plus-side we don't need a dedicated compiler-plugin and there is a bit less magic involved.

The resulting scene looks like [this](https://fabmax.github.io/kool/kool-js/?demo=hello-ui).

More complex layouts can be created by nesting `Row { }` and `Column { }` objects. The
[full UI demo](https://fabmax.github.io/kool/kool-js/?demo=ui) should give you an impression on what's possible.

## Kool Shader Language

I'm currently working on my own shader language (called ksl), which is implemented as a
[Kotlin Type-safe builder / DSL](https://kotlinlang.org/docs/type-safe-builders.html). The ksl shader code you write is
used to generate the actual GLSL shader code. The benefit with this approach is that there is no hard-coded GLSL
code in common code, and it should be relatively easy to add different generators which generate shader code for
different backends in the future (e.g. WGSL, or metal). 

This is still work in progress. However, in case you are curious, you can take a look at
[KslLitShader](kool-core/src/commonMain/kotlin/de/fabmax/kool/modules/ksl/KslLitShader.kt), which 
uses the new ksl approach (the interesting stuff happens in the `LitShaderModel` inner class).

## Physics Simulation

After playing around with various different engines on javascript and JVM I came to the
conclusion that all of them had some kind of flaw. So I decided to write my own bindings for
[Nvidia PhysX](https://github.com/NVIDIA-Omniverse/PhysX): [physx-jni](https://github.com/fabmax/physx-jni) for JVM, and
[physx-js-webidl](https://github.com/fabmax/physx-js-webidl) for javascript.

This was quite a bit of work (and is an ongoing project), but I think it was worth it: By writing my own bindings
I get the features I need, and, even better, I get the same features for javascript and JVM, which makes the
multiplatform approach much easier.
