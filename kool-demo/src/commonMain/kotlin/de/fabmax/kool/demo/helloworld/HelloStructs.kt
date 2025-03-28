package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.vertexAttribFloat3
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.Time
import kotlin.math.sin

/**
 * A simple example that uses two structs inside a custom ksl shader for various purposes (regular variables,
 * uniform variables, function parameters and return types).
 * For examples how to use structs together with (storage-) buffers, take a look at HelloComputeParticles or
 * GpuBees.
 */
class HelloStructs : DemoScene("Hello Structs") {

    /**
     * A struct for storing 4 color values as 4 separate members. Std140 memory layout is needed in order
     * to use this struct as a shader uniform.
     */
    class FlatColorStruct : Struct("FlatColorStruct", MemoryLayout.Std140) {
        val color1 = float4()
        val color2 = float4()
        val color3 = float4()
        val color4 = float4()
    }

    /**
     * Another struct for storing 4 color values in an array. This is only for demo purposes as it is kind of duplicate
     * to the struct defined before. Memory layout does not matter for this struct as it will only be used inside
     * the shader.
     */
    class ColorArrayStruct : Struct("ColorArrayStruct", MemoryLayout.DontCare) {
        val colors = float4Array(4)
    }

    /**
     * Creates a simple demo shader which will use the structs defined above in various places.
     */
    fun demoShader() = KslShader("StructTestShader") {
        // print the shader code to the console once it is generated
        dumpCode = true

        // register struct types as ksl types so that we can use them as variables later on.
        val flatColorsType = struct { FlatColorStruct() }
        val colorArrayType = struct { ColorArrayStruct() }

        // uniform struct can be set from outside the shader. We will use it to supply input colors to the shader
        // later on.
        val uniformColors = uniformStruct("colors") { FlatColorStruct() }

        // inter stage (aka varying) variable forwards color from vertex shader to fragment shader.
        val interStageColor = interStageFloat4()

        vertexStage {
            // a function within the shader that takes colors from one struct type and copies them to the other
            // struct type. Kind of ridiculous but again, it's an example.
            // the 'colorArrayType' defines the return type of the function.
            val colorTransformFun = functionStruct("colorTransform", colorArrayType) {
                // the function expects one parameter of type 'flatColorsType'
                val flatColors = paramStruct(flatColorsType)

                body {
                    // function body: takes the flatColors parameter and copies it's values to a colorArray struct
                    // which is then returned
                    val colorArray = structVar(colorArrayType)
                    colorArray.struct.colors.ksl[0] set flatColors.struct.color1.ksl
                    colorArray.struct.colors.ksl[1] set flatColors.struct.color2.ksl
                    colorArray.struct.colors.ksl[2] set flatColors.struct.color3.ksl
                    colorArray.struct.colors.ksl[3] set flatColors.struct.color4.ksl

                    // return colorArray
                    colorArray
                }
            }

            // main function: vertex shader entry point
            main {
                // compute vertex position. nothing special here...
                val cam = cameraData()
                val pos = modelMatrix().matrix * float4Value(vertexAttribFloat3(Attribute.POSITIONS), 1f.const)
                outPosition set cam.viewProjMat * pos

                // take the flat color uniform defined at the top and call colorTransformFun() to get colors as
                // an array. Of course this doesn't necessarily have to be a function, it's only to demonstrate
                // how to use structs as function parameters / return types.
                val colorsAsArray = structVar(colorTransformFun(uniformColors))
                // set vertex color from color array based on vertex index
                interStageColor.input set colorsAsArray.struct.colors.ksl[inVertexIndex.toInt1() % 4.const]
            }
        }

        // minimal fragment shader: just output the (interpolated) color that was forwarded by the vertex shader
        fragmentStage {
            main {
                colorOutput(interStageColor.output)
            }
        }
    }

    /**
     * Demo scene setup: A single quad which uses the shader written above.
     */
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera()

        // create shader
        val demoShader = demoShader()

        // bind the uniform struct defined in the shader. Binding is done only based on the name. Make sure
        // to use the correct struct type here.
        val structBinding = demoShader.uniformStruct("colors") { FlatColorStruct() }

        onUpdate {
            // set shader input colors (updated on each frame).
            val gradient = ColorGradient.ROCKET
            structBinding.set {
                color1.set(gradient.getColor(sin(Time.gameTime / 3.0).toFloat(), -1f, 1f))
                color2.set(gradient.getColor(sin(Time.gameTime / 5.0).toFloat(), -1f, 1f))
                color3.set(gradient.getColor(sin(Time.gameTime / 7.0).toFloat(), -1f, 1f))
                color4.set(gradient.getColor(sin(Time.gameTime / 9.0).toFloat(), -1f, 1f))
            }
        }

        // add a simple mesh that uses the shader
        addMesh(Attribute.POSITIONS) {
            generate {
                icoSphere {
                    steps = 2
                    radius = 3f
                }
            }
            shader = demoShader
        }
    }
}