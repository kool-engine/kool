# kool - A Vulkan / OpenGL graphics engine written in Kotlin
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.fabmax.kool/kool-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.fabmax.kool/kool-core)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/fabmax/kool/blob/master/LICENSE)

A multi-platform Vulkan / OpenGL based graphics engine that works on Desktop Java and browsers with
WebGL2. Android support is currently suspended but it should be quite easy to get that going again.

I finally made my first actual game with this: [Blocks and Belts](https://fabmaxx.itch.io/blocks-and-belts).
Give it a try (it's free)!

If you are adventurous, you might be able to use this for your own projects
as well (look below for a very short usage guide - that's all the documentation there is)

I also have a few demos in place (roughly in order of creation; once loaded, you can also switch between them via the
hamburger button in the upper left corner):
- [Island](https://fabmax.github.io/kool/kool-js/?demo=phys-terrain): Height-map based
  island incl. some wind-affected vegetation + a basic controllable character.
- [Physics - Ragdoll](https://fabmax.github.io/kool/kool-js/?demo=phys-ragdoll): Ragdoll physics demo.
- [Physics - Vehicle](https://fabmax.github.io/kool/kool-js/?demo=phys-vehicle): A drivable vehicle (W, A, S, D /
  cursor keys, R to reset) based on the nVidia PhysX vehicles SDK.
- [Physics - Joints](https://fabmax.github.io/kool/kool-js/?demo=phys-joints): Physics demo consisting of a chain
  running over two gears. Uses a lot of multi shapes and revolute joints.
- [Physics - Collision](https://fabmax.github.io/kool/kool-js/?demo=physics): The obligatory collision physics demo with
  various different shapes.
- [Embedded UI](https://fabmax.github.io/kool/kool-js/?demo=ui): Integrated UI framework implemented completely within
  the engine. Highly customizable, easy-to-use and blazing fast. I guess next thing is an editor then... :smile:
- [Creative Coding](https://fabmax.github.io/kool/kool-js/?demo=creative-coding): A few relatively simple demos
  showcasing different techniques of generating procedural geometry.
- [Procedural Geometry](https://fabmax.github.io/kool/kool-js/?demo=procedural): Small test-case for
  procedural geometry; all geometry is generated in code (even the roses! Textures are regular images though). Also some glass
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
  model with screen-space reflections and up to four spot lights with dynamic shadows.
- [Physical Based Rendering](https://fabmax.github.io/kool/kool-js/?demo=pbr): Interactive PBR demo 
  with image based lighting for various materials and environments (underlying PBR theory from
  [this](https://learnopengl.com/PBR/Theory) awesome article series).
- [Instanced / LOD Drawing](https://fabmax.github.io/kool/kool-js/?demo=instance): Instanced rendering
  demo of the Stanford Bunny. Uses six levels of detail to render up to 8000 instances.
- [Mesh Simplification](https://fabmax.github.io/kool/kool-js/?demo=simplification): Interactive mesh
  simplification demo (based on traditional [error quadrics](https://www.cs.cmu.edu/~./garland/Papers/quadrics.pdf))

Code for all demos is available in kool-demo sub-project.

## Engine Features / Noticeable Stuff:

- Physics simulation (based on Nvidia PhysX 5, using [physx-jni](https://github.com/fabmax/physx-jni) on Java and [physx-js-webidl](https://github.com/fabmax/physx-js-webidl) on javascript)
- Kotlin DSL based shader language (translates into GLSL)
- Neat little integrated GUI framework (the API is heavily inspired by [Jetpack Compose](https://github.com/JetBrains/compose-jb) but the implementation is my own)
- Vulkan rendering backend (on JVM)
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

## A Hello World Example

Getting a basic scene on the screen is quite simple:
```kotlin
fun main() {
    val ctx = createDefaultContext()
    
    ctx.scenes += scene {
        defaultCamTransform()
    
        +colorMesh {
            generate {
                cube {
                    colored()
                    centered()
                }
            }
            shader = KslPbrShader {
                color { vertexColor() }
                metallic(0f)
                roughness(0.25f)
            }
        }
    
        lighting.singleLight {
            setDirectional(Vec3f(-1f, -1f, -1f))
            setColor(Color.WHITE, 5f)
        }
    }
    
    ctx.run()
}
```
The above example creates a new scene and sets up a mouse-controlled camera (with `defaultCamTransform()`).
As you might have guessed the `+colorMesh { ... }` block creates a colored cube and adds it to the scene.
In order to draw the mesh on the screen it needs a shader, which is assigned with
`shader = KslPbrShader { ... }`. This creates a simple PBR shader for a dielectric material
with a rather smooth surface. Color information is taken from the corresponding vertex attribute.
Finally, we set up a single directional scene light (of white color and an intensity of 5), so that our cube can shine
in its full glory. The resulting scene looks like [this](https://fabmax.github.io/kool/kool-js/?demo=helloWorld).

## Model Loading and Advanced Lighting

Model loading, animation and more advanced lighting with shadow mapping and ambient occlusion requires only a few more
lines of code:
```kotlin
fun main() {
    val ctx = createDefaultContext()
    
    ctx.scenes += scene {
        defaultCamTransform()
    
        // Light setup
        lighting.singleLight {
            setSpot(Vec3f(5f, 6.25f, 7.5f), Vec3f(-1f, -1.25f, -1.5f), 45f)
            setColor(Color.WHITE, 300f)
        }
        val shadows = listOf(SimpleShadowMap(this, lightIndex = 0))
        val aoPipeline = AoPipeline.createForward(this)
    
        // Add a ground plane
        +colorMesh {
            generate {
                grid { }
            }
            shader = pbrShader {
                useStaticAlbedo(Color.WHITE)
                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                shadowMaps += shadows
            }
        }

        // Load a glTF 2.0 model
        ctx.assetMgr.launch {
            val materialCfg = GltfFile.ModelMaterialConfig(
                    shadowMaps = shadows,
                    scrSpcAmbientOcclusionMap = aoPipeline.aoMap
            )
            val modelCfg = GltfFile.ModelGenerateConfig(materialConfig = materialCfg)
            loadGltfModel("path/to/model.glb", modelCfg)?.let { model ->
                +model
                model.translate(0f, 0.5f, 0f)
    
                if (model.animations.isNotEmpty()) {
                    model.enableAnimation(0)
                    model.onUpdate += { updateEvt ->
                        model.applyAnimation(updateEvt.deltaT)
                    }
                }
            }
        }
    }
    
    ctx.run()
}
```
First we set up the lighting. This is very similar to the previous example but this time we use a spot light, which
requires a position, direction and opening angle. Other than directional lights, point and spot lights have a distinct
(point-) position and objects are affected less by them, the farther they are away. This usually results in a much
higher required light intensity: Here we use an intensity of 300.

Next we create a `SimpleShadowMap` which computes the shadows casted by the light source we defined before.
Moreover, the created `AoPipeline` computes an ambient occlusion map, which is later used by the shaders to
further improve the visual appearance of the scene.

After light setup we can add objects to our scene. First we generate a grid mesh as ground plane. Default size and
position of the generated grid are fine, therefore `grid { }` does not need any more configuration. Similar to the
color cube from the previous example, the ground plane uses a PBR shader. However, this time we tell the shader to
use the ambient occlusion and shadow maps we created before. Moreover, the shader should not use the vertex color
attribute, but a simple pre-defined color (white in this case).

Finally, we want to load a glTF 2.0 model. Resources are loaded via the asset manager. Since resource loading is a
potentially long-running operation we do that from within a coroutine launched with the asset manager:
`ctx.assetMgr.launch { ... }`. By default, the built-in glTF parser creates shaders for all models it loads. The
created shaders can be customized via a provided material configuration, which we use to pass the shadow and
ambient occlusion maps we created during light setup. After we created the custom model / material configuration
we can load the model with `loadGltfModel("path/to/model.glb", modelCfg)`. This (suspending) function returns the
model or null in case of an error. If the model was successfully loaded the `let { ... }` block is executed and the
model is added to the scene (`+model`). The `Model` class derives from `TransformGroup`, hence it is
easy to manipulate the model. Here we move the model 0.5 units along the y-axis (up). If the model contains any
animations, these can be easily activated. This example checks whether there are any animations and if so activates
the first one. The `model.onUpdate { }` block is executed on every frame and updates the enabled animation.

The resulting scene looks like [this](https://fabmax.github.io/kool/kool-js/?demo=helloGltf). Here, the
[Animated Box](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0/BoxAnimated) from the glTF sample
respository is loaded.


## Kool UI

Kool comes with an embedded UI framework, which is heavily inspired by [Jetpack Compose](https://github.com/JetBrains/compose-jb)
but was implemented from scratch. Here is a small example:
```kotlin
fun main() {
    val ctx = createDefaultContext()
    
    ctx.scenes += UiScene(clearScreen = true) {
        +Panel(colors = Colors.singleColorLight(MdColor.LIGHT_GREEN)) {
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
    
    ctx.run()
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

This is still work in progress and most shaders in this project still use my old approach, which used blocks of
pre-defined GLSL code. However, in case you are curious, you can take a look at
[KslLitShader](kool-core/src/commonMain/kotlin/de/fabmax/kool/modules/ksl/KslLitShader.kt), which already
uses the new ksl approach (the interesting stuff happens in the `LitShaderModel` inner class).

## Physics Simulation

After playing around with various different engines on javascript and JVM I came to the
conclusion that all of them had some kind of flaw. So I decided to write my own bindings for
[Nvidia PhysX](https://github.com/NVIDIA-Omniverse/PhysX): [physx-jni](https://github.com/fabmax/physx-jni) for JVM, and
[physx-js-webidl](https://github.com/fabmax/physx-js-webidl) for javascript.

This was quite a bit of work (and is an ongoing project), but I think it was worth it: By writing my own bindings
I get the features I need, and, even better, I get the same features for javascript and JVM, which makes the
multiplatform approach much easier.

## Usage

If you are adventurous, you can use kool as a library in your own (multiplatform-)projects. I published a version on
maven central a while ago, however that's very much outdated. Here is a gradle snippet in case you want to try anyway
(but I would recommend to clone this project and use one of the demos as starting point):

Gradle setup:
```groovy
// JVM dependencies
dependencies {
    implementation "de.fabmax.kool:kool-core-jvm:0.8.0"

    // On JVM, lwjgl runtime dependencies have to be included as well
    def lwjglVersion = "3.3.0"
    def lwjglNatives = "natives-windows"    // alternatively: natives-linux or natives-macos, depending on your OS
    runtime "org.lwjgl:lwjgl:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-glfw:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-jemalloc:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-opengl:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-vma:${lwjglVersion}:$lwjglNatives"
    runtime "org.lwjgl:lwjgl-shaderc:${lwjglVersion}:$lwjglNatives"
}

// or alternatively for javascript
dependencies {
    implementation "de.fabmax.kool:kool-core-js:0.8.0"
}
```
