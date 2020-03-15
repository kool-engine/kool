# kool - A Vulkan / OpenGL graphics engine written in Kotlin
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/fabmax/kool/blob/master/LICENSE)

A multi-platform Vulkan / OpenGL based graphics engine that works on Desktop Java and browsers with
WebGL2. Android support is currently suspended but it should be quite easy to get that going again.

This is my personal pet-project. I have a few demos in place (once loaded, you can also switch between
them via the hamburger button in the upper left corner):
- [Physical Based Rendering](https://fabmax.github.io/kool/kool-js/?demo=pbrDemo): Interactive PBR demo 
  with image based lighting for various materials and environments (underlying PBR theory from
  [this](https://learnopengl.com/PBR/Theory) awesome article series).
- [Multi Light Shadow Mapping](https://fabmax.github.io/kool/kool-js/?demo=multiLightDemo): A simple PBR shaded
  model with up to four spot lights and dynamic shadows.
- [Mesh Simplification](https://fabmax.github.io/kool/kool-js/?demo=simplificationDemo): Interactive mesh
  simplification demo (based on traditional [error quadrics](https://www.cs.cmu.edu/~./garland/Papers/quadrics.pdf))
- [Procedural Tree](https://fabmax.github.io/kool/kool-js/?demo=treeDemo): A simple procedural tree generator
  based on a [space colonization algorithm](http://algorithmicbotany.org/papers/colonization.egwnp2007.large.pdf)
- [Instanced / LOD Drawing](https://fabmax.github.io/kool/kool-js/?demo=instancedDemo): Instanced rendering
  demo of the Stanford Bunny. Uses six levels of detail to render up to 8000 instances.

Code for all demos is available in kool-demo sub-project.

Together with Vulkan support I implemented a new, much more flexible shader generator. Shaders are composed of nodes
quite similar to Unity's Shader Graph (however it's completely code-based, no fancy editor). Shader code is
generated and compiled from the node-based model on-the-fly for each backend.

In order to add support for Vulkan, I had to drastically change some parts of the engine and this is an
ongoing process. Hence, stuff is a still a bit messy but things are getting better.

## Features / Noticeable Stuff:

- Node based dynamic shader generation
- All new Vulkan rendering backend (on JVM)
- Support for physical based rendering (with metallic workflow) and image-based lighting
- Normal, roughness, metallic, ambient occlusion and Displacement mapping
- HDR lighting with [Uncharted2 tone-mapping](http://filmicworlds.com/blog/filmic-tonemapping-operators/)
- Lighting with multiple point, spot and directional lights
- Shadow mapping for multiple light sources (spot lights only for now)
- A small GUI framework for simple in-game menus / controls

## A Hello World Example

Getting a simple scene on the screen is quite simple:
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
            pipelineLoader = pbrShader {
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
```pipelineLoader = pbrShader { ... }```. This creates a simple PBR shader for a dielectric material
with a rather smooth surface. Color information is taken from the corresponding vertex attribute.
Finally we set up a single directional scene light (of white color and an intensity of 5), so that our cube can shine in its full glory. The
resulting scene looks like [this](https://fabmax.github.io/kool/kool-js/?demo=helloWorldDemo).

## A Simple Custom Shader

As mentioned above shaders can also be composed from a set of predefined nodes (and even additional custom nodes).
Shader nodes are combined to a ```ShaderModel``` which is then used to generate the shader code for the
rendering backend (currently Vulkan or WebGL2). A very simple shader model could look like this:
```kotlin
val superSimpleModel = ShaderModel().apply {
    val ifColors: StageInterfaceNode
    vertexStage {
        val mvpMat = mvpNode().outMvpMat
        val vertexPos = attrPositions().output
        val vertexColor = attrColors().output

        ifColors = stageInterfaceNode("ifColors", vertexColor)

        positionOutput = vertexPositionNode(vertexPos, mvpMat).outPosition
    }
    fragmentStage {
        colorOutput = unlitMaterialNode(ifColors.output).outColor
    }
}
mesh.pipelineLoader = ModeledShader(superSimpleModel)
```
The shader model includes the definitions for the vertex and fragment shaders.

The vertex shader uses a MVP matrix (provided by the ```mvpNode()```) and the position and color
attributes of the input vertices (provided by ```attrPositions()``` and ```attrColors()```). The
vertex color is forwarded to the fragment shader via a ```StageInterfaceNode``` named ifColors.
Then the vertex position and MVP matrix are used to compute the output position of the vertex shader.

The fragment shader simply takes the forwarded vertex color and plugs it into an ```unlitMaterialNode()```
which more or less directly feeds that color into the fragment shader output.

Finally the shader model can be used to create a ```ModeledShader``` which can then be assigned to a mesh.

This example is obviously very simple but it shows the working principle: Nodes contain basic building blocks
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
    implementation "de.fabmax.kool:kool-core-jvm:0.2.0"

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
    implementation "de.fabmax.kool:kool-core-js:0.2.0"
}
```

## What's Next?

The new render pipeline still lacks a lot of features the old OpenGL-only one already included; hence 
first step should be to get these working again:
- (Cascaded) shadow mapping for non spot light types
- A vertex shader node for skeletal animations

There are also a few new features on my wish list:
- New rendering backend for WebGPU (should actually be quite simple)
- Loading of glTF models
- Screen-space ambient occlusion
- Screen-space reflections
- Optional deferred rendering
