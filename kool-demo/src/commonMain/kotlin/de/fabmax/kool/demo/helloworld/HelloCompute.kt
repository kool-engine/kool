package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.*

class HelloCompute : DemoScene("Hello Compute") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        val storageSizeX = 256
        val storageSizeY = 256

        // create a simple compute shader
        val computeShader = KslComputeShader("Compute shader test") {
            // a compute shader always has a single compute stage
            computeStage(16, 16, 1) {
                // storage maps to a 1d / 2d / 3d array of 1d / 2d / 4d float or (u)int vectors
                // here we use a 2d array of 4d floats
                val pixelStorage = storage2d<KslFloat4>("pixelStorage", storageSizeX, storageSizeY)

                // regular uniforms...
                val offsetPos = uniformFloat2("uOffset")

                // compute program
                main {
                    // builtin globals provide information about local / global invocation IDs, work
                    // group ID, count and size
                    val numInvocationsXy = float2Var((inNumWorkGroups.xy * inWorkGroupSize.xy).toFloat2())
                    val texelCoord = int2Var(inGlobalInvocationId.xy.toInt2())

                    // for this demo we simply generate a position / workgroup dependent color
                    val pos = float2Var((texelCoord.toFloat2() / numInvocationsXy + sin(offsetPos)) * 0.5f.const)
                    val rgba = float4Var(Color.BLACK.const)
                    rgba.r set pos.x
                    rgba.g set pos.y
                    rgba.b set 1f.const - pos.x
                    `if` (inWorkGroupId.x.toInt1() % 2.const eq inWorkGroupId.y.toInt1() % 2.const) {
                        rgba.r set 1f.const - rgba.r
                        rgba.g set 1f.const - rgba.g
                        rgba.b set 1f.const - rgba.b
                    }

                    // storage textures can be randomly read and written
                    pixelStorage[texelCoord] = rgba
                }
            }
        }

        // compute shaders are executed by ComputeRenderPasses, which are added as a scene offscreen pass
        addOffscreenPass(ComputeRenderPass(computeShader, storageSizeX, storageSizeY))

        // create and bind the storage texture used as compute shader output
        val storageBuffer = StorageBuffer2d(storageSizeX, storageSizeY, GpuType.FLOAT4)
        computeShader.storage2d("pixelStorage", storageBuffer)

        // animate offset position to change the colors over time
        var offsetPos by computeShader.uniform2f("uOffset")
        onUpdate {
            offsetPos = Vec2f((Time.gameTime * 0.1f).toFloat(), (Time.gameTime * 0.17f).toFloat())
        }

        // draw storage texture on a simple quad mesh
        defaultOrbitCamera(0f, 0f)
        addTextureMesh {
            generate {
                rect {
                    size.set(8f, 8f)
                }
            }
            shader = KslShader("Buffer render shader") {
                val uv = interStageFloat2()
                vertexStage {
                    main {
                        uv.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS)
                        outPosition set mvpMatrix().matrix * float4Value(vertexAttribFloat3(Attribute.POSITIONS), 1f.const)
                    }
                }
                fragmentStage {
                    main {
                        val storage = storage2d<KslFloat4>("pixelStorage", storageSizeX, storageSizeY)
                        val color = float4Var(storage[(Vec2f(256f).const * uv.output).toInt2()])
                        colorOutput(color.rgb, 1f.const)
                    }
                }
            }.apply {
                storage2d("pixelStorage", storageBuffer)
            }
        }

        storageBuffer.releaseWith(this)

        var counter = 0
        onUpdate {
            if (counter++ % 1000 == 0) {
                // for the purpose of this example, we read back the buffer from time to time and print the color of
                // the first pixel to the console
                launchOnMainThread {
                    logI { "${Time.frameCount}: Read back storage buffer from GPU memory..." }
                    // buffer readback is an asynchronous operation and will take some time to complete (which is why
                    // it is a suspending function)
                    storageBuffer.readbackBuffer()
                    val color = storageBuffer.getF4(0)
                    logI { "${Time.frameCount}: Got buffer, rgba[0] = ${color.x}, ${color.y}, ${color.z}, ${color.w}" }
                }
            }
        }
    }
}