# kool - A Vulkan / WebGPU / OpenGL graphics engine written in Kotlin
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/kool-engine/kool/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/de.fabmax.kool/kool-core.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/de.fabmax.kool/kool-core)
![Build](https://github.com/kool-engine/kool/actions/workflows/build.yml/badge.svg?branch=main)

A multi-platform Vulkan / WebGPU / OpenGL game engine that works on Desktop Java, Android and browsers.

### Get In Touch
Feel free to join the [Discord Server](https://discord.gg/GvsJj2Pk3K)!

## Web-Demos
The following demos run directly in the browser. Chrome is highly recommended because it's still the only browser
with WebGPU support. Other browsers should work as well but use the WebGL backend, which doesn't support all
features / demos.
The code for all demos is available in
the [kool-demo](kool-demo/src/commonMain/kotlin/de/fabmax/kool/demo) subproject. You can also run them locally by
cloning this repo and running `./gradlew :kool-demo:runDesktop`

- [Island](https://kool-engine.github.io/live/demos/?demo=phys-terrain): Height-map based
  island incl. some wind-affected vegetation + a basic controllable character.
- [Physics - Vehicle](https://kool-engine.github.io/live/demos/?demo=phys-vehicle): A drivable vehicle (W, A, S, D /
  cursor keys, R to reset) based on the Nvidia PhysX vehicles SDK. **WebGPU only**
- [Physics - Ragdoll](https://kool-engine.github.io/live/demos/?demo=phys-ragdoll): Ragdoll physics demo.
- [Physics - Joints](https://kool-engine.github.io/live/demos/?demo=phys-joints): Physics demo consisting of a chain
  running over two gears. Uses a lot of multi shapes and revolute joints.
- [Physics - Collision](https://kool-engine.github.io/live/demos/?demo=physics): The obligatory collision physics demo with
  various different shapes.
- [2D Physics](https://kool-engine.github.io/live/demos/?demo=mixer2d): Simple 2D physics demo with quite many dynamic
  bodies and two kinematic rotors.
- [Bloom](https://kool-engine.github.io/live/demos/?demo=bloom): Decent looking bloom for (very) bright objects. **WebGPU only**
- [Pathtracing](https://kool-engine.github.io/live/demos/?demo=pathtracing): Compute shader based implementation of the
  [Ray Tracing in One Weekend](https://raytracing.github.io/books/RayTracingInOneWeekend.html) book. **WebGPU only**
- [Embedded UI](https://kool-engine.github.io/live/demos/?demo=ui): Integrated UI framework implemented completely within
  the engine. Fast, highly customizable and easy-to-use. Offers advanced features like compose-style `LaunchedEffect`,
  [Animations](https://kool-engine.github.io/live/demos/?demo=ui-easings) and more.
- [Particles](https://kool-engine.github.io/live/demos/?demo=bees): Two teams of bees fighting against each other.
  Simulation can be toggled between CPU and compute-shader (if available, i.e. on **WebGPU**).
- [Fluffy Bunny](https://kool-engine.github.io/live/demos/?demo=shell): Shell-shading based rendering of animated fur
  (based on this [video](https://www.youtube.com/watch?v=9dr-tRQzij4)).
- [Creative Coding](https://kool-engine.github.io/live/demos/?demo=creative-coding): A few relatively simple demos
  showcasing different techniques of generating procedural geometry.
- [Procedural Geometry](https://kool-engine.github.io/live/demos/?demo=procedural): Small test-case for
  procedural geometry; all geometry is generated in code (even the roses! Textures are regular images though). Also,
  some glass shading (shaft of the wine glass, the wine itself looks quite odd when shaded with refractions and is
  therefore opaque). **WebGPU only**
- [glTF Models](https://kool-engine.github.io/live/demos/?demo=gltf): Various demo models loaded from glTF / glb format
  - Flight Helmet from [glTF sample models](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0/FlightHelmet)
  - Polly from [Blender](https://github.com/KhronosGroup/glTF-Blender-Exporter/tree/master/polly)
  - Coffee Cart from [Poly Haven](https://polyhaven.com/a/CoffeeCart_01)
  - Camera Model also from [Poly Haven](https://polyhaven.com/a/CoffeeCart_01)
  - A few feature test models also from the [glTF sample model repository](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0)
- [Deferred Shading](https://kool-engine.github.io/live/demos/?demo=deferred): Thousands of dynamic
  light sources, bloom and ambient occlusion. **WebGPU only**
- [Screen-space Ambient Occlusion](https://kool-engine.github.io/live/demos/?demo=ao): Roughly based on
  [this](http://john-chapman-graphics.blogspot.com/2013/01/ssao-tutorial.html) article by John
  Chapman with slightly optimized sampling (also shamelessly recreated his demo scene).
- [Screen-space Reflections](https://kool-engine.github.io/live/demos/?demo=ssr): A simple PBR shaded
  model with screen-space reflections and up to four spot-lights with dynamic shadows.
- [Physical Based Rendering](https://kool-engine.github.io/live/demos/?demo=pbr): Interactive PBR demo 
  with image based lighting for various materials and environments (underlying PBR theory from
  [this](https://learnopengl.com/PBR/Theory) awesome article series).
- [Instanced / LOD Drawing](https://kool-engine.github.io/live/demos/?demo=instance): Instanced rendering
  demo of the Stanford Bunny. Uses six levels of detail to render up to 8000 instances.
- [Mesh Simplification](https://kool-engine.github.io/live/demos/?demo=simplification): Interactive mesh
  simplification demo (based on traditional [error-quadrics](https://www.cs.cmu.edu/~./garland/Papers/quadrics.pdf))

By default, the web demos use the WebGPU backend and fall back to WebGL if WebGPU is not supported by your browser. The
used backend is printed in the extended info-panel in the lower right corner (click on the little `+`), apart from
that there shouldn't be much visible difference in the WebGL and WebGPU backends. You can also force a certain backend
by appending `&backend=webgpu` or `&backend=webgl` to the URL.

I also made an actual game with this: [Blocks and Belts](https://fabmaxx.itch.io/blocks-and-belts).
Give it a try (it's free)!

## Scene Editor
Check out the [web editor](https://kool-engine.github.io/live/kool-editor/)! (Chrome recommended).

The engine used to be a code-only engine, but I recently made some progress in implementing a graphical scene editor.
The editor still is in an early state and lacks several rather essential features, but the basics already work quite
well. It is fully implemented within the engine itself (including the UI).

To unleash the full potential of the editor, you should run it locally. You can either download the editor
directly in the web-version by clicking the `Save Project` button in the upper right corner (this also preserves any
changes made in the editor) or you can clone the editor template project (see [usage](#usage) below).

More editor related documentation is available in [the editor docs](https://kool-engine.github.io/docs/editor).

## Platform Support

| Platform            | Backend     | Implementation Status                                  |
|---------------------|-------------|--------------------------------------------------------|
| Desktop (JVM)       | Vulkan      | :white_check_mark: Fully working                       |
| Desktop (JVM)       | OpenGL      | :white_check_mark: Fully working                       |
| Desktop (JVM)       | WebGPU      | :sparkles: Mostly working (using the `wgpu4k` backend) |
| Browser (JS + WASM) | WebGPU      | :white_check_mark: Fully working                       |
| Browser (JS + WASM) | WebGL 2     | :white_check_mark: Fully working                       |
| Android             | OpenGL ES 3 | :white_check_mark: Fully working                       |

### Supported desktop platforms
- Windows (x64): Vulkan, WebGPU and OpenGL
- Linux (x64): Vulkan, WebGPU and OpenGL
- macOS (ARM + x64): Vulkan and WebGPU (no OpenGL)

### JS vs. WASM Performance

Kool supports plain JavaScript as well as WebAssembly (WASM) as browser targets. However, the WASM backend seems to be
suffering from the many required JS-interop upcalls to various Web APIs. Therefore, in most cases, the WASM backend is
actually slower than the JS backend, and plain JS stays the recommended target for now.

### Java Version

On Desktop, Kool currently uses Java 17 as the minimum language level except the `kool-backend-wgpu4k` module, which
requires Java 22 (because it uses [Project Panama](https://openjdk.org/projects/panama/) features under the hood, which
became stable in Java 22). In case Java 22 is a problem for you, you can exclude `kool-backend-wgpu4k` from the project.

### Android Support

The Android target is disabled by default (to avoid having the Android SDK as a build requirement). You can
enable the Android target by running the gradle task `./gradlew enableAndroidPlatform`.

Moreover, the demos are currently not build for Android. However, there's a basic
[kool-android-template](https://github.com/kool-engine/kool-templates) project with a minimal kool Android app.

## Usage

You can use kool as a library in your own (multiplatform-) projects. The library is published
on maven central:

```
repositories {
    mavenCentral()
    // only needed in case you want to use a SNAPSHOT version:
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}
dependencies {
    implementation("de.fabmax.kool:kool-core:0.19.0")
    implementation("de.fabmax.kool:kool-physics:0.19.0")
}
```

There is also a separate repo containing minimal template projects to get you started:

[https://github.com/kool-engine/kool-templates](https://github.com/kool-engine/kool-templates)

The demos mentioned above and examples shown below should give you a rough idea on how to do stuff (documentation is
still a bit of a weak spot).

### Running the Demos on JVM

You can launch the desktop demo app directly from a terminal via gradle with `./gradlew :kool-demo:runDesktop`. 

Running the [main()](kool-demo/src/desktopMain/kotlin/de/fabmax/kool/demo/Main.kt) method from within IntelliJ
requires that the native libraries are located in a local folder and added as file dependencies (seems to be some kind
of dependency resolution bug in IntelliJ when importing multiplatform projects with JVM runtimeOnly libraries).

The required libs are copied automatically on build. So, in order to launch the demos from within IntelliJ you need
to build the project first (or manually run the `cacheRuntimeLibs` task) and then re-sync the gradle project, so that
the libs are resolved and added to the IntelliJ module classpath.

## Engine Features / Noticeable Stuff:
- [Compute shader](kool-demo/src/commonMain/kotlin/de/fabmax/kool/demo/helloworld/HelloComputeParticles.kt) support
- [Reversed-depth](https://developer.nvidia.com/content/depth-precision-visualized) rendering for vastly improved
  depth precision and range (more or less infinite)
- 3D Physics simulation (based on Nvidia PhysX 5.6, using [physx-jni](https://github.com/fabmax/physx-jni) on Java and [physx-js-webidl](https://github.com/fabmax/physx-js-webidl) on javascript)
- 2D Physics simulation (based on Box2D 3.1.1, using [box2d-jni](https://github.com/fabmax/box2d-jni) on Java and [kool-box2d-wasm](https://www.npmjs.com/package/kool-box2d-wasm) on javascript)
- Kotlin DSL based shader language (translates into GLSL and WGSL)
- Neat little integrated GUI framework. The API is heavily inspired by [Jetpack Compose](https://github.com/JetBrains/compose-jb) but the implementation is different, as it needs to run within the OpenGL context.
- [MSDF](https://github.com/Chlumsky/msdf-atlas-gen) Font support for text rendering in arbitrary font sizes
- Support for physical based rendering (with metallic workflow) and image-based lighting
- (Almost) complete support for [glTF 2.0](https://github.com/KhronosGroup/glTF) model format (including animations, morph targets and skins)
- Skin / armature mesh animation (vertex shader based)
- Deferred shading
- Various tone-mapping options:
  - ACES (default)
  - Khronos PBR Neutral
  - Uncharted 2
  - Modified Reinhard
- Decent looking Bloom
- Screen-space ambient occlusion
- Normal, roughness, metallic, ambient occlusion
- Vertex and parallax occlusion displacement mapping
- Lighting with multiple point, spot and directional lights
- Shadow mapping for multiple light sources (only spot and directional lights for now)
- Basic audio support
- Decoupled frontend (i.e., game-logic) and backend (i.e., rendering) threads

## A Hello World Example

Getting a basic scene on the screen is quite simple:
```kotlin
fun main() = KoolApplication {
    addScene {
        defaultOrbitCamera()

        addColorMesh {
            generate {
                cube {
                    colored()
                }
            }
            shader = KslPbrShader {
                color { vertexColor() }
                metallic(0f)
                roughness(0.25f)
            }
            onUpdate {
                transform.rotate(45f.deg * Time.deltaT, Vec3f.X_AXIS)
            }
        }

        lighting.singleDirectionalLight {
            setup(Vec3f(-1f, -1f, -1f))
            setColor(Color.WHITE, 5f)
        }
    }
}
```
The above example creates an application with a single scene and sets up a mouse-controlled camera
(with `defaultOrbitCamera()`).
As you might have guessed the `addColorMesh { ... }` block creates a colored cube and adds it to the scene.
In order to draw the mesh on the screen it needs a shader, which is assigned with
`shader = KslPbrShader { ... }`. This creates a simple PBR shader for a dielectric material
with a rather smooth surface. Color information is taken from the corresponding vertex attribute.
The `onUpdate`-block is called on each frame and modifies the cube transform to rotate it 45° per second around its
X-axis.
Finally, we set up a single directional scene light (of white color and an intensity of 5), so that our cube can shine
in its full glory. The resulting scene looks like [this](https://kool-engine.github.io/live/demos/?demo=helloWorld).

## Texture & Model Loading and Advanced Lighting

Asset loading, animation and more advanced lighting with shadow mapping and ambient occlusion require only a few more
lines of code:
```kotlin
fun main() = KoolApplication {
    addScene {
        defaultOrbitCamera()

        // Light setup
        lighting.singleSpotLight {
            setup(Vec3f(5f, 6.25f, 7.5f), Vec3f(-1f, -1.25f, -1.5f), 45f.deg)
            setColor(Color.WHITE, 300f)
        }
        val shadowMap = SimpleShadowMap(this, lighting.lights[0])
        val aoPipeline = AoPipeline.createForward(this)

        // Add a textured ground plane
        val texture = Assets.loadTexture2d("path/to/texture.png").getOrThrow()
        addTextureMesh {
            generate {
                grid { }
            }
            shader = KslPbrShader {
                color { textureColor(texture) }
                lighting { addShadowMap(shadowMap) }
                enableSsao(aoPipeline.aoMap)
            }
        }

        // Load a glTF 2.0 model
        val materialCfg = GltfMaterialConfig(
            shadowMaps = listOf(shadowMap),
            scrSpcAmbientOcclusionMap = aoPipeline.aoMap
        )
        val modelCfg = GltfLoadConfig(materialConfig = materialCfg)
        val model = Assets.loadGltfModel("path/to/model.glb", modelCfg).getOrThrow()

        model.transform.translate(0f, 0.5f, 0f)
        if (model.animations.isNotEmpty()) {
            model.enableAnimation(0)
            model.onUpdate {
                model.applyAnimation(Time.deltaT)
            }
        }

        // Add loaded model to scene
        addNode(model)
    }
}
```
First we set up the lighting. This is very similar to the previous example, but this time we use a spot-light, which
requires a position, direction and opening angle. Other than directional lights, point and spot-lights have a distinct
(point-) position and objects are affected less by them, the farther they are away. This usually results in a much
higher required light intensity: Here we use an intensity of 300.

Next we create a `SimpleShadowMap` which computes the shadows cast by the light source we defined before.
Moreover, the created `AoPipeline` computes an ambient occlusion map, which is later used by the shaders to
further improve the visual appearance of the scene.

After light setup we can add objects to our scene. First, we load a texture that we are going to use as a ground plane.
Textures and all other resources are loaded via the `Assets` API. Asset loading functions are suspending and return a
`Result` object, which can be used to check whether the loading was successful. In this case we use the `getOrThrow()`
function to throw an exception if the loading failed.

To draw the texture, we need to generate a mesh where the texture is put on. We add that by calling
`addTextureMesh { }` and generate a grid geometry in it. Default size and position of the generated grid are fine for
our ground plane, therefore `grid { }` does not need any more configuration. Similar to the
color cube from the previous example, the ground plane uses a PBR shader. However, this time we tell the shader to
use the ambient occlusion and shadow maps we created before. Moreover, the shader should not use the vertex color
attribute, but a simple pre-defined color (white in this case).

Notice we used add**Texture**Mesh here instead of the add**Color**Mesh from before. `addColorMesh`
and `addTextureMesh` are convenience functions which create and add meshes with all the mesh attributes needed for their
respective purpose. It is also possible to create completely custom meshes with arbitrary attributes, but that is
out-of-scope for this basic example.

Finally, we want to load a glTF 2.0 model. By default, the built-in glTF parser creates shaders for all models it loads.
The created shaders can be customized via a provided material configuration, which we use to pass the shadow and
ambient occlusion maps we created during light setup. After we created the custom model / material configuration
we can load the model with `Assets.loadGltfModel("path/to/model.glb", modelCfg)`. This suspending function returns the
loaded model, which can then be customized and inserted into the scene. Here we move the model 0.5 units along the
y-axis (up). If the model contains any animations, these can be easily activated. This example checks whether there
are any animations and if so activates the first one. The `model.onUpdate { }` block is executed on every frame and
updates the enabled animation. The model is inserted into the scene with `addNode(model)`.

The resulting scene looks like [this](https://kool-engine.github.io/live/demos/?demo=helloGltf). Here, the
[Animated Box](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0/BoxAnimated) from the glTF sample
repository is loaded.

## Kool UI

Kool comes with an embedded UI framework, which is heavily inspired by [Jetpack Compose](https://github.com/JetBrains/compose-jb)
but was implemented from scratch. Here is a small example:
```kotlin
fun main() = KoolApplication {
    addScene {
        setupUiScene(Scene.DEFAULT_CLEAR_COLOR)
      
        addPanelSurface(colors = Colors.singleColorLight(MdColor.LIGHT_GREEN)) {
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
Here, we create a new `Scene` and configure it to be a UI scene containing a single panel surface. Within
the `addPanelSurface`-block, we add a button and a text field. All appearance and layout-properties of the UI elements
are controlled by their `modifier`s.

Whenever the button is clicked we increment a `clickCount` which is then displayed by the text field. This works
because the `addPanelSurfcae`-block is executed each time any `remember`ed state (or `mutableStateOf()`) within the
block changes.

The resulting scene looks like [this](https://kool-engine.github.io/live/demos/?demo=hello-ui).

More complex layouts can be created by nesting `Row { }` and `Column { }` objects. The
[kool editor](https://kool-engine.github.io/live/kool-editor/) as well as the
[full UI demo](https://kool-engine.github.io/live/demos/?demo=ui) should give you an impression on what's possible.

## Kool Shader DSL

Kool comes with its own shader language (called ksl), which is implemented as a
[Kotlin Type-safe builder / DSL](https://kotlinlang.org/docs/type-safe-builders.html). The ksl shader code you write is
used to generate the actual GLSL / WGSL shader code. The benefit with this approach is that there is no hard-coded
platform-specific shader code in common code and all shaders work on OpenGL / GLSL as well as WebGPU / WGSL.
Moreover, it is relatively easy to add different generators which generate shader code for
different backends in the future (e.g. metal). 

Writing shaders in ksl is quite similar to GLSL, here's how a hello-world style shader looks like:

```kotlin
fun main() = KoolApplication {
    addScene {
        defaultOrbitCamera()

        addColorMesh {
            generate {
                cube {
                    colored()
                }
            }
            shader = KslShader("Hello world shader") {
                val interStageColor = interStageFloat4()
                vertexStage {
                    main {
                        val mvp = mvpMatrix()
                        val localPosition = float3Var(vertexAttribFloat3(Attribute.POSITIONS))
                        outPosition set mvp.matrix * float4Value(localPosition, 1f.const)
                        interStageColor.input set vertexAttribFloat4(Attribute.COLORS)
                    }
                }
                fragmentStage {
                    main {
                        colorOutput(interStageColor.output)
                    }
                }
            }
        }
    }
}
```
The interesting part starts at `shader = KslShader() = { ... }`. Here, a new shader is created and assigned to the mesh
created before. If you ever wrote a shader before, the structure should be familiar: The shader consists of a vertex
stage (responsible for projecting the individual mesh vertices onto the screen) and a fragment stage (responsible
for computing the output-color for each pixel covered by the mesh). This example shader is almost as simple as a valid
shader can be: It uses a pre-multiplied MVP matrix to project the vertex position attribute to the screen. Moreover,
the color attribute is taken from the vertex input and forwarded to the fragment shader via `interStageColor`. The
fragment stage then simply takes the color from `interStageColor` and writes it to the screen.

A little more complex example is available in [HelloKsl](kool-demo/src/commonMain/kotlin/de/fabmax/kool/demo/helloworld/HelloKsl.kt),
which looks like [this](https://kool-engine.github.io/live/demos/?demo=helloksl).
Of course, shaders can get more complex than that, you can dig further into the code. All shaders currently used in kool
are written in ksl.

## Physics Simulation

After playing around with various different engines on javascript and JVM I came to the
conclusion that all of them had some kind of flaw. So I decided to write my own bindings for
[Nvidia PhysX](https://github.com/NVIDIA-Omniverse/PhysX): [physx-jni](https://github.com/fabmax/physx-jni) for JVM, and
[physx-js-webidl](https://github.com/fabmax/physx-js-webidl) for JavaScript.

This was quite a bit of work, but I think it was worth it: By writing my own bindings
I get the features I need, and, even better, I get the same features for JavaScript and JVM, which makes the
multiplatform approach much easier.
