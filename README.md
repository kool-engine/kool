# kool - A Vulkan / OpenGL graphics engine written in Kotlin
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/fabmax/kool/blob/master/LICENSE)

A multi-platform Vulkan / OpenGL based graphics engine that works on Desktop Java and browsers with
WebGL2. Android support is currently suspended but it should be quite easy to get that going again.

This is my personal pet-project. I have a few demos in place (once loaded, you can also switch between
them via the hamburger button in the upper left corner):
- [Deferred Shading](https://fabmax.github.io/kool/kool-js/?demo=deferredDemo): Handles thousands of dynamic
  light sources - also includes PBR shading and ambient occlusion.
- [glTF Models](https://fabmax.github.io/kool/kool-js/?demo=gltfDemo): Two fancy models: Camera Model
  from [3dModeHaven](https://3dmodelhaven.com/model/?m=Camera_01) and Flight Helmet from 
  [glTF sample models](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0/FlightHelmet). Moreover, a
  few feature test models.
- [Physical Based Rendering](https://fabmax.github.io/kool/kool-js/?demo=pbrDemo): Interactive PBR demo 
  with image based lighting for various materials and environments (underlying PBR theory from
  [this](https://learnopengl.com/PBR/Theory) awesome article series).
- [Screen-space Ambient Occlusion](https://fabmax.github.io/kool/kool-js/?demo=aoDemo): Roughly based on
  [this](http://john-chapman-graphics.blogspot.com/2013/01/ssao-tutorial.html) article by John
  Chapman with slightly optimized sampling (also shamelessly recreated his demo scene).
- [Multi Light Shadow Mapping](https://fabmax.github.io/kool/kool-js/?demo=multiShadowDemo): A simple PBR shaded
  model with up to four spot lights and dynamic shadows.
- [Mesh Simplification](https://fabmax.github.io/kool/kool-js/?demo=simplificationDemo): Interactive mesh
  simplification demo (based on traditional [error quadrics](https://www.cs.cmu.edu/~./garland/Papers/quadrics.pdf))
- [Procedural Tree](https://fabmax.github.io/kool/kool-js/?demo=treeDemo): A simple procedural tree generator
  based on a [space colonization algorithm](http://algorithmicbotany.org/papers/colonization.egwnp2007.large.pdf)
- [Instanced / LOD Drawing](https://fabmax.github.io/kool/kool-js/?demo=instanceDemo): Instanced rendering
  demo of the Stanford Bunny. Uses six levels of detail to render up to 8000 instances.

Code for all demos is available in kool-demo sub-project.

Support for Vulkan based rendering is quite recent. Together with Vulkan support I implemented a new, much more
flexible shader generator. Shaders are composed of nodes quite similar to Unity's Shader Graph (however it's completely 
code-based, no fancy editor). Shader code is generated and compiled from the node-based model on-the-fly for each backend.

In order to add support for Vulkan, I had to drastically change some parts of the engine and this is an
ongoing process. Hence, stuff is a still a bit messy but things are getting better.

## Features / Noticeable Stuff:

- Node based dynamic shader generation
- Vulkan rendering backend (on JVM)
- Support for physical based rendering (with metallic workflow) and image-based lighting
- (Almost) complete support for [glTF 2.0](https://github.com/KhronosGroup/glTF) model format (including animations, morph targets and skins)
- Skin / armature mesh animation (vertex shader based)
- Deferred shading
- Screen-space ambient occlusion
- Normal, roughness, metallic, ambient occlusion and displacement mapping
- HDR lighting with [Uncharted2 tone-mapping](http://filmicworlds.com/blog/filmic-tonemapping-operators/)
- Lighting with multiple point, spot and directional lights
- Shadow mapping for multiple light sources (only spot and directional lights for now)
- A small GUI framework for simple in-game menus / controls

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
            shader = pbrShader {
                albedoSource = Albedo.VERTEX_ALBEDO
                metallic = 0.0f
                roughness = 0.25f
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
The above example creates a new scene and sets up a mouse-controlled camera (with ```defaultCamTransform()```).
As you might have guessed the ```+colorMesh { ... }``` block creates a colored cube and adds it to the scene.
In order to draw the mesh on the screen it needs a shader, which is assigned with
```shader = pbrShader { ... }```. This creates a simple PBR shader for a dielectric material
with a rather smooth surface. Color information is taken from the corresponding vertex attribute.
Finally, we set up a single directional scene light (of white color and an intensity of 5), so that our cube can shine
in its full glory. The resulting scene looks like [this](https://fabmax.github.io/kool/kool-js/?demo=helloWorldDemo).

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
                    model.onUpdate += { _, ctx ->
                        model.applyAnimation(ctx.time)
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

Next we create a ```SimpleShadowMap``` which computes the shadows casted by the light source we defined before.
Moreover, the created ```AoPipeline``` computes an ambient occlusion map, which is later used by the shaders to
further improve the visual appearance of the scene.

After light setup we can add objects to our scene. First we generate a grid mesh as ground plane. Default size and
position of the generated grid are fine, therefore ```grid { }``` does not need any more configuration. Similar to the
color cube from the previous example, the ground plane uses a PBR shader. However, this time we tell the shader to
use the ambient occlusion and shadow maps we created before. Moreover, the shader should not use the vertex color
attribute, but a simple pre-defined color (white in this case).

Finally, we want to load a glTF 2.0 model. Resources are loaded via the asset manager. Since resource loading is a
potentially long-running operation we do that from within a coroutine launched with the asset manager:
```ctx.assetMgr.launch { ... }```. By default, the built-in glTF parser creates shaders for all models it loads. The
created shaders can be customized via a provided material configuration, which we use to pass the shadow and
ambient occlusion maps we created during light setup. After we created the custom model / material configuration
we can load the model with ```loadGltfModel("path/to/model.glb", modelCfg)```. This (suspending) function returns the
model or null in case of an error. If the model was successfully loaded the ```let { ... }``` block is executed and the
model is added to the scene (```+model```). The ```Model``` class derives from ```TransformGroup```, hence it is
easy to manipulate the model. Here we move the model 0.5 units along the y-axis (up). If the model contains any
animations, these can be easily activated. This example checks whether there are any animations and if so activates
the first one. The ```model.onUpdate { }``` block is executed on every frame and updates the enabled animation.

The resulting scene looks like [this](https://fabmax.github.io/kool/kool-js/?demo=helloGltfDemo). Here, the
[Animated Box](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0/BoxAnimated) from the glTF sample
respository is loaded.


## A Simple Custom Shader

As mentioned above shaders can be composed of a set of predefined nodes (and even additional custom nodes).
Shader nodes are combined to a ```ShaderModel``` which is then used to generate the shader code for the
selected rendering backend (currently Vulkan, OpenGL or WebGL2). A very simple shader model could look like this:
```kotlin
val superSimpleModel = ShaderModel().apply {
    val ifColors: StageInterfaceNode
    vertexStage {
        val mvpMat = mvpNode().outMvpMat
        val vertexPos = attrPositions().output
        val vertexColor = attrColors().output

        ifColors = stageInterfaceNode("ifColors", vertexColor)

        positionOutput = vec4TransformNode(vertexPos, mvpMat).outVec4
    }
    fragmentStage {
        colorOutput(unlitMaterialNode(ifColors.output).outColor)
    }
}
mesh.shader = ModeledShader(superSimpleModel)
```
The shader model includes the definitions for the vertex and fragment shaders.

The vertex shader uses a MVP matrix (provided by the ```mvpNode()```) and the position and color
attributes of the input vertices (provided by ```attrPositions()``` and ```attrColors()```). The
vertex color is forwarded to the fragment shader via a ```StageInterfaceNode``` named ifColors.
Then the vertex position and MVP matrix are used to compute the output position of the vertex shader.

The fragment shader simply takes the forwarded vertex color and plugs it into an ```unlitMaterialNode()```
which more or less directly feeds that color into the fragment shader output.

Finally, the shader model can be used to create a ```ModeledShader``` which is then assigned to a mesh.

This example is obviously very simple, but it shows the working principle: Nodes contain basic building blocks
which can be composed to complete shaders. Nodes have inputs and outputs which are used to connect them.
The shader generator uses the connectivity information to build a dependency graph and call the code generator
functions of the individual nodes in the correct order.

More complex shaders can be defined in exactly the same fashion. E.g. ```PhongShader``` and
```PbrShader``` use exactly the same mechanism.

## Usage

If you are adventurous, you can use kool as a library in your own projects.

Gradle setup:
```groovy
repositories {
    maven {
        url = "https://dl.bintray.com/fabmax/kool"
    }
}

// JVM dependencies
dependencies {
    implementation "de.fabmax.kool:kool-core-jvm:0.4.0"

    // On JVM, lwjgl runtime dependencies have to be included as well
    def lwjglVersion = "3.2.3"
    def lwjglNatives = "natives-windows"    // alternatively: natives-linux or natives-macos, depending on your OS
    runtime "org.lwjgl:lwjgl:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-glfw:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-assimp:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-jemalloc:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-opengl:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-stb:${lwjglVersion}:${lwjglNatives}"
    runtime "org.lwjgl:lwjgl-vma:${lwjglVersion}:$lwjglNatives"
    runtime "org.lwjgl:lwjgl-shaderc:${lwjglVersion}:$lwjglNatives"
}

// or alternatively for javascript
dependencies {
    implementation "de.fabmax.kool:kool-core-js:0.4.0"
}
```

## What's Next?

I have a few features on my wishlist, which I may (or may not) implement in the future (in no particular order):
- Screen-space reflections
- Shadow mapping for point lights
- Rendering backend for WebGPU

Apart from that there are about one million things I could (and maybe will) optimize further (especially in the Vulkan code)
