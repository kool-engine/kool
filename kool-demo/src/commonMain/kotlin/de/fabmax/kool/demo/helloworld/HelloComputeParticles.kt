package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.PI_F
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.blocks.noise11
import de.fabmax.kool.modules.ksl.blocks.noise13
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.GradientTexture
import de.fabmax.kool.pipeline.StorageBuffer
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.Time

class HelloComputeParticles : DemoScene("Hello Compute Particles") {

    class ParticleAppearance : Struct("particleAppearance", MemoryLayout.Std430) {
        val color = float4()
        val size = float1()
    }

    class Particle : Struct("particleData", MemoryLayout.Std430) {
        // particle color and size are stored in a nested struct, we could of course also store these in simple
        // primitive members, the nested struct is only used for an example of using nested structs
        val appearance = struct(ParticleAppearance())

        val position = float3()
        val velocity = float3()
        val lifeTime = float1()
    }

    fun particleComputeShader() = KslComputeShader("particle compute shader") {
        // register the struct as a ksl type, so we can use it in the shader
        val particleStruct = struct { Particle() }
        // the particle buffer will hold the actual particle data
        val particleBuffer = storage("particleBuffer", particleStruct)

        val gradient = texture1d("gradient")
        val deltaT = uniformFloat1("deltaT")
        val globalSeed = uniformFloat1("seed")

        computeStage(workGroupSizeX = 64) {
            main {
                val particleIdx = inGlobalInvocationId.x.toInt1()

                // get particle data from the storage buffer
                // particle attributes can be accessed via particle.struct.[attribute name].ksl (see below)
                val particleVar = structVar(particleBuffer[particleIdx])
                val particle = particleVar.struct
                val lifeTime = float1Var(particle.lifeTime.ksl - deltaT)

                // simulate the particle
                `if`(lifeTime le 0f.const) {
                    // particle expired -> respawn it at some random location
                    val seed = float1Var(globalSeed + particleIdx.toFloat1() * deltaT * 1000f.const)

                    val a = float1Var(noise11(seed + 17f.const) * 2f.const)
                    val r = float1Var(1.5f.const + noise11(seed + 1234f.const) * 0.1f.const)
                    particle.appearance.color.ksl set gradient.load((abs(a - 1f.const) * 255f.const).toInt1(), 0.const)
                    particle.position.ksl set float3Value(cos(a * PI_F.const), 0f.const, sin(a * PI_F.const)) * r
                    particle.velocity.ksl set (noise13(seed + 4711f.const) + float3Value(-0.5f, 1f, -0.5f)) * 2f.const
                    lifeTime set 0.75f.const + noise11(seed)
                }

                // update particle attributes
                particle.velocity.ksl.y -= 10f.const * deltaT
                particle.position.ksl += particle.velocity.ksl * deltaT
                particle.appearance.size.ksl set 5f.const * sqrt(lifeTime)
                particle.lifeTime.ksl set lifeTime

                // write updated particle data back to storage buffer
                particleBuffer[particleIdx] = particleVar
            }
        }
    }

    fun particleDrawShader() = KslShader("particle draw shader") {
        val particleStruct = struct { Particle() }
        val particleBuffer = storage("particleBuffer", particleStruct)
        val index = interStageInt1()

        vertexStage {
            main {
                // produce a point vertex for the current particle position
                val particleData = structVar(particleBuffer[inVertexIndex.toInt1()]).struct
                val cam = cameraData()
                val modelMat = modelMatrix()

                outPosition set cam.viewProjMat * modelMat.matrix * float4Value(particleData.position.ksl, 1f.const)
                outPointSize set particleData.appearance.size.ksl

                // forward vertex index to fragment shader so that we can use it there to access the particle data
                // again. notice that it would probably be more efficient to read the particle color here and forward
                // that to the fragment shader instead of reading the buffer there again. but again, it's an example.
                index.input set inVertexIndex.toInt1()
            }
        }
        fragmentStage {
            main {
                // use the forwarded vertex index to read the particle color from the buffer and output it
                colorOutput(particleBuffer[index.output].struct.appearance.color.ksl)
            }
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val particleColor = GradientTexture(ColorGradient.ROCKET)

        val particleBuffer = StorageBuffer(Particle().type, N_PARTICLES)

        val particleComputeShader = particleComputeShader()
        particleComputeShader.storage("particleBuffer", particleBuffer)
        particleComputeShader.texture1d("gradient", particleColor)
        var simulationDeltaT by particleComputeShader.uniform1f("deltaT")
        var simulationSeed by particleComputeShader.uniform1f("seed")

        val particleDrawShader = particleDrawShader()
        particleDrawShader.storage("particleBuffer", particleBuffer)

        // add a compute pass, which runs the particle simulation
        addComputePass(ComputePass(particleComputeShader, N_PARTICLES))

        // add a point mesh for rendering the particles. the mesh has no vertex attributes since all the data is
        // taken from the particle storage buffer instead of the usual vertex buffer
        addMesh(attributes = emptyList(), primitiveType = PrimitiveType.POINTS) {
            // we still need to add an (empty) vertex per particle. to do so we simply add a vertex index per particle
            generate { repeat(N_PARTICLES) { geometry.addIndex(vertex { }) } }
            shader = particleDrawShader
        }

        defaultOrbitCamera()

        onUpdate {
            simulationDeltaT = Time.deltaT.coerceAtMost(0.1f)
            simulationSeed = 1337f + (Time.gameTime % 1000.0).toFloat()
        }
        onRelease {
            particleColor.release()
            particleBuffer.release()
        }
    }

    companion object {
        // 256k particles
        const val N_PARTICLES = 1 shl 18
    }
}