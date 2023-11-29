package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.ComputeRenderPass
import de.fabmax.kool.pipeline.StorageTexture2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time

class HelloCompute : DemoScene("Hello Compute") {
    override fun Scene.setupMainScene(ctx: KoolContext) {

        // create a simple compute shader
        val computeShader = KslComputeShader("Compute shader test") {
            // a compute shader always has a single compute stage
            computeStage(16, 16, 1) {
                // storage uniforms map to a special kind of 1D / 2D / 3D texture with various possible
                // int and float formats
                val storage = storage2d<KslFloat4>("storageTex")

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
                    storageWrite(storage, texelCoord, rgba)
                }
            }
        }

        val outputWidth = 256
        val outputHeight = 256

        // compute shaders are executed by ComputeRenderPasses, which are added as a scene offscreen pass
        addOffscreenPass(ComputeRenderPass(computeShader, outputWidth, outputHeight))

        // create and bind the storage texture used as compute shader output
        val storageTexture = StorageTexture2d(outputWidth, outputHeight, TexFormat.RGBA_F32)
        computeShader.storage2d("storageTex", storageTexture)

        // animate offset position to change the colors over time
        var offsetPos by computeShader.uniform2f("uOffset")
        onUpdate {
            offsetPos = Vec2f((Time.gameTime * 0.1f).toFloat(), (Time.gameTime * 0.17f).toFloat())
        }

        // draw storage texture on a simple quad mesh
        defaultOrbitCamera(0f, 0f)
        addTextureMesh {
            generate {
                centeredRect {
                    size.set(8f, 8f)
                }
            }
            shader = KslUnlitShader {
                color { textureColor(storageTexture) }
            }
        }

        // release storage texture when done
        storageTexture.releaseWith(this)
    }
}