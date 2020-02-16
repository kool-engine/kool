# Kotlin + OpenGL = kool
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/fabmax/kool/blob/master/LICENSE)

A multi-platform Vulkan / OpenGL based graphics engine that works on Desktop Java and browsers with
WebGL2. Android support is currently suspended but it should be quite easy to get that going again.

This is just a personal pet-project. However, if you are curious
you can checkout the [javascript demo](https://fabmax.github.io/kool/kool-js/?demo=pbrDemo).
The hamburger-button in the upper-left corner triggers the demo chooser menu. Code for
all demos is available in kool-demo sub-project.

In order to add support for Vulkan, I had to drastically change some parts of the engine and this is an
ongoing process. Hence, stuff is a still a bit messy but things are getting better.

Together with Vulkan
support I implemented a new, much more flexible shader generator. Shaders are composed of nodes quite
similar to Unity's Shader Graph (however it's completely code-based, no fancy editor). Shader code is
generated and compiled from the node-based model on-the-fly for each backend.

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
            pipelineLoader = phongShader { albedoSource = AlbedoSource.VERTEX_ALBEDO }
        }
    }
    
    ctx.run()
}
```
The above example creates a new scene and sets up a mouse-controlled camera (with ```defaultCamTransform()```).
As you might have guessed the ```+colorMesh { ... }``` block creates a colored cube and adds it to the scene.
In order to draw the mesh on the screen it needs a shader, which is assigned with
```pipelineLoader = phongShader { ... }```. In this case we use a simple pre-defined Phong shader.
However, it's also possible to define custom shaders by combining a few shader nodes.

A Phong shader also needs a light source. Every newly created scene already comes with a single directional
light as it's default light source, so, for this example, we don't have to do any additional light setup.

## A Simple Custom Shader

As mentioned above shaders can be composed from a set of predefined nodes (and even additional custom nodes).
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

This example is obviously very simple, however more complex shaders can be defined in exactly the same
fashion. E.g. ```PhongShader``` and ```PbrShader``` use exactly the same mechanism.

## Features / Noticeable Stuff:
- Node based dynamic shader generation
- All new Vulkan rendering backend (for JVM, based on lwjgl3)
- Support for physical based rendering (with metallic workflow) and image-based lighting
- Normal, roughness, metallic, ambient occlusion and Displacement mapping
- HDR lighting with [Uncharted2 tone-mapping](http://filmicworlds.com/blog/filmic-tonemapping-operators/)
- Lighting with multiple point, spot and directional lights
- A small GUI framework for simple in-game menus / controls

## What's Next?
The new render pipeline still lacks a lot of features the old OpenGL-only one already included; hence 
first step should be to get those working again:
- (Cascaded) shadow mapping (possibly for multiple lights and all three light types)
- Support for Instanced rendering
- A vertex shader node for skeletal animations

There are also a few slightly more sophisticated features on my wish list:
- Screen-space ambient occlusion
- Screen-space reflections
- Optional deferred rendering
