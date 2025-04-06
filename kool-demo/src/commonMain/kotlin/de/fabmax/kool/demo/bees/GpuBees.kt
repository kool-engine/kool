package de.fabmax.kool.demo.bees

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.KslBlinnPhongShaderConfig
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.blocks.noise11
import de.fabmax.kool.modules.ksl.blocks.noise13
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.geometry.RectUvs
import de.fabmax.kool.util.*
import kotlin.math.max
import kotlin.math.min

class GpuBees(beeScene: Scene) {

    private val maxGpuBees: Int get() = BeeConfig.maxBeesPerTeamGpu

    // Contains position, rotation and velocities for all bees of one team
    private val beeBufferA = StorageBuffer(BeeData().type, (maxGpuBees + 64))
    private val beeBufferB = StorageBuffer(BeeData().type, (maxGpuBees + 64))

    val beeUpdateTime = mutableStateOf(0.0)

    private val beeUpdateShader = KslComputeShader("Bee update") {
        optimizeExpressions = false
        computeStage(64) {
            val beeStruct = struct { BeeData() }
            val deltaT = uniformFloat1("deltaT")
            val randomSeed = uniformFloat1("randomSeed")
            val numBees = uniformInt1("numBees")
            val spawnPos = uniformFloat3("spawnPos")
            val beeBuffer = storage("beeBuffer", beeStruct)
            val enemyBeeBuffer = storage("enemyBeeBuffer", beeStruct)

            val speedJitter = uniformFloat1("speedJitter")
            val teamAttraction = uniformFloat1("teamAttraction")
            val teamRepulsion = uniformFloat1("teamRepulsion")
            val chaseForce = uniformFloat1("chaseForce")
            val attackForce = uniformFloat1("attackForce")

            val matrixToQuaternion = functionFloat4("matrixToQuaternion") {
                val mat = paramMat3("matrix")

                body {
                    val q = float4Var(QuatF.IDENTITY.const)

                    val r00 = mat[0].x; val r01 = mat[1].x; val r02 = mat[2].x
                    val r10 = mat[0].y; val r11 = mat[1].y; val r12 = mat[2].y
                    val r20 = mat[0].z; val r21 = mat[1].z; val r22 = mat[2].z

                    val trace = float1Var(r00 + r11 + r22)
                    `if`(trace gt 0f.const) {
                        val s = float1Var(0.5f.const / sqrt(trace + 1f.const))
                        q set float4Value((r21 - r12) * s, (r02 - r20) * s, (r10 - r01) * s, 0.25f.const / s)
                    }.`else` {
                        `if`(r00 lt r11) {
                            `if`(r11 lt r22) {
                                val s = float1Var(0.5f.const / sqrt(r22 - r00 - r11 + 1f.const))
                                `if`(r10 lt r01) { s set -s }   // ensure non-negative w
                                q set float4Value((r02 + r20) * s, (r12 + r21) * s, 0.25f.const / s, (r10 - r01) * s)
                            }.`else` {
                                val s = float1Var(0.5f.const / sqrt(r11 - r22 - r00 + 1f.const))
                                `if`(r02 lt r20) { s set -s }   // ensure non-negative w
                                q set float4Value((r01 + r10) * s, 0.25f.const / s, (r21 + r12) * s, (r02 - r20) * s)
                            }
                        }.`else` {
                            `if`(r00 lt r22) {
                                val s = float1Var(0.5f.const / sqrt(r22 - r00 - r11 + 1f.const))
                                `if`(r10 lt r01) { s set -s }   // ensure non-negative w
                                q set float4Value((r02 + r20) * s, (r12 + r21) * s, 0.25f.const / s, (r10 - r01) * s)
                            }.`else` {
                                val s = float1Var(0.5f.const / sqrt(r00 - r11 - r22 + 1f.const))
                                `if`(r21 lt r12) { s set -s }   // ensure non-negative w
                                q set float4Value(0.25f.const / s, (r10 + r01) * s, (r20 + r02) * s, (r21 - r12) * s)
                            }
                        }
                    }
                    normalize(q)
                }
            }

            val randomRotation = functionFloat4("randomRot") {
                val rand = paramFloat1("rand")

                body {
                    val ang = float1Var(noise11(rand) * PI_F.const)
                    val sin = float1Var(sin(ang))
                    val axis = float3Var(normalize(noise13(rand)) * sin)
                    float4Value(axis, cos(ang))
                }
            }

            val randomI = functionInt1("randomI") {
                val seed = paramInt1("seed")

                body {
                    val x = int1Var(seed)
                    x set (x xor (x shl 13.const))
                    x set (x xor (x shr 17.const))
                    x set (x xor (x shl 5.const))
                    x
                }
            }

            main {
                val beeIndex = int1Var(inGlobalInvocationId.x.toInt1())
                val rand = float1Var(randomSeed + beeIndex.toFloat1())

                val beeData = structVar(beeBuffer[beeIndex])
                val position = beeData.struct.position.ksl
                val rotation = beeData.struct.rotation.ksl
                val decay = beeData.struct.decay.ksl
                val velocity = beeData.struct.velocity.ksl
                val enemy = beeData.struct.enemyIndex.ksl

                fun randomBeeIndex(seed: Int) = abs(randomI(beeIndex + (rand * seed.toFloat().const).toInt1())).rem(numBees)

                `if`((beeIndex ge numBees) and (decay eq 0f.const)) {
                    decay set 0.01f.const
                }

                `if`(decay eq 0f.const) {
                    // bee is alive
                    val vJitter = float3Var((noise13(rand) - 0.5f.const) * 2f.const * speedJitter * deltaT)
                    velocity set (velocity + vJitter) * (1f.const - BeeConfig.speedDamping.const * deltaT)

                    // swarming
                    val attractiveFriend = structVar(beeBuffer[randomBeeIndex(73)]).struct
                    val friendPos = attractiveFriend.position.ksl
                    val delta = float3Var(friendPos - position)
                    val dist = float1Var(max(0.1f.const, length(delta)))
                    `if`(attractiveFriend.decay.ksl eq 0f.const) {
                        // only alive friends are attractive
                        velocity += delta * teamAttraction * deltaT / dist
                    }

                    val repellentFriend = structVar(beeBuffer[randomBeeIndex(31)]).struct
                    val repelPos = repellentFriend.position.ksl
                    delta set position - repelPos
                    dist set max(0.1f.const, length(delta))
                    `if`(repellentFriend.decay.ksl eq 0f.const) {
                        // only alive friends repel
                        velocity += delta * teamRepulsion * deltaT / dist
                    }

                    val enemyData = structVar(enemyBeeBuffer[enemy])
                    `if`((enemy lt 0.const) or (enemy gt numBees) or (enemyData.struct.decay.ksl ne 0f.const)) {
                        // choose a new enemy (old enemy is either dead or invalid)
                        enemy set randomBeeIndex(91)

                    }.`else` {
                        // attack enemy bee
                        delta set enemyData.struct.position.ksl - position
                        dist set max(0.1f.const, length(delta))

                        `if`(dist gt BeeConfig.attackDistance.const) {
                            velocity += delta * chaseForce * deltaT / dist

                        }.`else` {
                            velocity += delta * attackForce * deltaT / dist
                            `if`(dist lt BeeConfig.hitDistance.const) {
                                enemyData.struct.decay.ksl set 0.01f.const
                                enemyBeeBuffer[enemy] = enemyData
                            }
                        }
                    }

                }.`else` {
                    // bee is dead
                    velocity set velocity * (1f.const - 0.5f.const * deltaT)
                    velocity.y += BeeConfig.gravity.const * deltaT
                    decay += deltaT

                    `if`((decay gt BeeConfig.decayTime.const) and (beeIndex lt numBees)) {
                        // respawn bee
                        decay set 0f.const
                        rotation set randomRotation(rand + 51f.const)
                        position set spawnPos + normalize(noise13(rand) - 0.5f.const) * noise11(rand + 1337f.const) * (BeeConfig.worldSize.x * 0.05f).const
                        velocity set (noise13(rand + 19f.const) - 0.5f.const) * (2f * BeeConfig.maxSpawnSpeed).const
                        enemy set randomBeeIndex(53)
                    }
                }

                position += velocity * deltaT

                // update rotation by current velocity direction
                val speed = float1Var(length(velocity))
                `if`((speed gt 0.5f.const) and (abs(dot(velocity, Vec3f.Y_AXIS.const) / speed) lt 0.99f.const)) {
                    val right = float3Var(normalize(cross(Vec3f.Y_AXIS.const, velocity)))
                    val up = float3Var(normalize(cross(velocity, right)))
                    val front = float3Var(normalize(velocity))

                    val rotMat = mat3Var(mat3Value(right, up, front))
                    val quat = float4Var(matrixToQuaternion(rotMat))
                    val w0 = float1Var(deltaT * 5f.const)
                    rotation set normalize(mix(rotation, quat, w0))
                }

                // clamp bee positions to world bounds
                `if`(abs(position.x) gt BeeConfig.worldExtent.x.const) {
                    position.x set BeeConfig.worldExtent.x.const * sign(position.x)
                    velocity *= Vec3f(-0.5f, 0.8f, 0.8f).const
                }
                `if`(abs(position.y) gt BeeConfig.worldExtent.y.const) {
                    position.y set BeeConfig.worldExtent.y.const * sign(position.y)
                    velocity *= Vec3f(0.8f, -0.5f, 0.8f).const
                }
                `if`(abs(position.z) gt BeeConfig.worldExtent.z.const) {
                    position.z set BeeConfig.worldExtent.z.const * sign(position.z)
                    velocity *= Vec3f(0.8f, 0.8f, -0.5f).const
                }

                beeBuffer[beeIndex] = beeData
            }
        }
    }

    private val beeInstancesA = MeshInstanceList(emptyList())
    private val beeInstancesB = MeshInstanceList(emptyList())
    val beeMeshA = makeBeeMesh(beeInstancesA)
    val beeMeshB = makeBeeMesh(beeInstancesB)

    private val simulationPass = ComputePass("Bee update pass")

    init {
        initBeeBuffer(beeBufferA, Vec3f(BeeConfig.worldSize.x * 0.4f, 0f, 0f))
        initBeeBuffer(beeBufferB, Vec3f(BeeConfig.worldSize.x * -0.4f, 0f, 0f))

        beeScene.onRelease {
            beeBufferA.release()
            beeBufferB.release()
        }

        simulationPass.isProfileTimes = true
        beeScene.addComputePass(simulationPass)

        var deltaT by beeUpdateShader.uniform1f("deltaT")
        var randomSeed by beeUpdateShader.uniform1f("randomSeed")
        var numBees by beeUpdateShader.uniform1i("numBees")
        var spawnPos by beeUpdateShader.uniform3f("spawnPos")
        var beeBuffer by beeUpdateShader.storage("beeBuffer")
        var enemyBeeBuffer by beeUpdateShader.storage("enemyBeeBuffer")

        var speedJitter by beeUpdateShader.uniform1f("speedJitter")
        var teamAttraction by beeUpdateShader.uniform1f("teamAttraction")
        var teamRepulsion by beeUpdateShader.uniform1f("teamRepulsion")
        var chaseForce by beeUpdateShader.uniform1f("chaseForce")
        var attackForce by beeUpdateShader.uniform1f("attackForce")

        val taskA = simulationPass.addTask(beeUpdateShader, Vec3i.ZERO)
        val taskB = simulationPass.addTask(beeUpdateShader, Vec3i.ZERO)

        val bindGroupA: BindGroupData = taskA.pipeline.pipelineData
        val bindGroupB: BindGroupData = taskB.pipeline.pipelineDataLayout.createData()

        taskA.pipeline.swapPipelineData("a")
        spawnPos = Vec3f(BeeConfig.worldSize.x * -0.4f, 0f, 0f)
        beeBuffer = beeBufferA
        enemyBeeBuffer = beeBufferB
        taskB.pipeline.swapPipelineData("b")
        spawnPos = Vec3f(BeeConfig.worldSize.x * 0.4f, 0f, 0f)
        beeBuffer = beeBufferB
        enemyBeeBuffer = beeBufferA

        var numSimulatedBees = 0
        var prevSimulatedBees = 0
        var decreaseBeeCountdown = 0f

        beeScene.onUpdate {
            // use a multiple of compute shader workgroup-size (64) as instance count
            val currentInstances = (BeeConfig.beesPerTeam.value + 63) and 63.inv()

            // if number of bees is decreased, keep simulating the previous number until excess bees decayed
            if (prevSimulatedBees > currentInstances) {
                decreaseBeeCountdown = BeeConfig.decayTime
            }
            prevSimulatedBees = currentInstances
            if (decreaseBeeCountdown > 0f) {
                decreaseBeeCountdown -= Time.deltaT
                numSimulatedBees = max(numSimulatedBees, currentInstances)
            } else {
                numSimulatedBees = currentInstances
            }
            beeUpdateTime.set(simulationPass.tGpu.inWholeMicroseconds / 1000.0)
        }

        taskA.onBeforeDispatch {
            taskA.pipeline.swapPipelineData("a")
            deltaT = min(0.02f, Time.deltaT)
            randomSeed = 1000f + (Time.gameTime % 1000.0).toFloat()
            numBees = BeeConfig.beesPerTeam.value

            speedJitter = BeeConfig.speedJitter.value
            teamAttraction = BeeConfig.teamAttraction.value
            teamRepulsion = BeeConfig.teamRepulsion.value
            chaseForce = BeeConfig.chaseForce.value
            attackForce = BeeConfig.attackForce.value

            taskA.setNumGroupsByInvocations(numSimulatedBees)
            beeInstancesA.numInstances = numSimulatedBees
        }

        taskB.onBeforeDispatch {
            taskB.pipeline.swapPipelineData("b")
            deltaT = min(0.02f, Time.deltaT)
            randomSeed = -1000f - (Time.gameTime % 1000.0).toFloat()
            numBees = BeeConfig.beesPerTeam.value

            speedJitter = BeeConfig.speedJitter.value
            teamAttraction = BeeConfig.teamAttraction.value
            teamRepulsion = BeeConfig.teamRepulsion.value
            chaseForce = BeeConfig.chaseForce.value
            attackForce = BeeConfig.attackForce.value

            taskB.setNumGroupsByInvocations(numSimulatedBees)
            beeInstancesB.numInstances = numSimulatedBees
        }
    }

    fun setEnabled(isEnabled: Boolean) {
        beeMeshA.isVisible = isEnabled
        beeMeshB.isVisible = isEnabled
        simulationPass.isEnabled = isEnabled
    }

    private fun initBeeBuffer(beeBuffer: GpuBuffer, spawnPos: Vec3f) {
        val data = StructBuffer(beeBuffer.size, BeeData())
        repeat(data.capacity) {
            data.put {
                position.set(spawnPos + randomInUnitCube() * 5f)
                decay.set(BeeConfig.decayTime)
                velocity.set(Vec3f.ZERO)
                enemyIndex.set(-1)
                rotation.set(QuatF.IDENTITY.toVec4f())
            }
        }
        beeBuffer.uploadData(data)
    }

    fun setupShaders(beeTexture: Texture2d) {
        beeMeshA.shader = BeeDrawShader(MdColor.BLUE, MdColor.PURPLE).apply {
            colorMap = beeTexture
            storage("beeBuffer", beeBufferA)
        }
        beeMeshB.shader = BeeDrawShader(MdColor.AMBER, MdColor.DEEP_ORANGE).apply {
            colorMap = beeTexture
            storage("beeBuffer", beeBufferB)
        }
    }

    private fun makeBeeMesh(beeInstances: MeshInstanceList) = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, instances = beeInstances).apply {
        generate {
            cube {
                size.set(0.7f, 0.7f, 1f)
                val s = 1/32f
                uvs = listOf(
                    RectUvs(Vec2f(0*s, 0*s), Vec2f(7*s, 0*s), Vec2f(0*s, 10*s), Vec2f(7*s, 10*s)),      // top
                    RectUvs(Vec2f(21*s, 10*s), Vec2f(14*s, 10*s), Vec2f(21*s, 0*s), Vec2f(14*s, 0*s)),  // bottom
                    RectUvs(Vec2f(21*s, 0*s), Vec2f(28*s, 0*s), Vec2f(21*s, 10*s), Vec2f(28*s, 10*s)),  // left
                    RectUvs(Vec2f(14*s, 10*s), Vec2f(7*s, 10*s), Vec2f(14*s, 0*s), Vec2f(7*s, 0*s)),    // right
                    RectUvs(Vec2f(0*s, 10*s), Vec2f(7*s, 10*s), Vec2f(0*s, 17*s), Vec2f(7*s, 17*s)),    // front
                    RectUvs(Vec2f(14*s, 17*s), Vec2f(7*s, 17*s), Vec2f(14*s, 10*s), Vec2f(7*s, 10*s))   // back
                )
            }
        }
    }

    private class BeeDrawShader(
        aliveColor: Color,
        deadColor: Color,
        cfg: Config = KslBlinnPhongShaderConfig {
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { textureColor() }
            shininess(5f)
            specularStrength(0.25f)
            lightingCfg.ambientLight = AmbientLight.Uniform(BeeDemo.bgColor.toLinear())

            modelCustomizer = {
                val aliveness = interStageFloat1()
                vertexStage {
                    main {
                        val beeStruct = struct { BeeData() }
                        val beeBuffer = storage("beeBuffer", beeStruct)
                        val beeOffset = int1Var(inInstanceIndex.toInt1())

                        val bee = structVar(beeBuffer[beeOffset])
                        val rotQuat = float4Var(bee.struct.rotation.ksl)
                        val r = rotQuat.w
                        val i = rotQuat.x
                        val j = rotQuat.y
                        val k = rotQuat.z

                        val rotMat = mat3Var()
                        rotMat[0] set float3Value(
                            1f.const - 2f.const * (j*j + k*k),
                            2f.const * (i*j + k*r),
                            2f.const * (i*k - j*r)
                        )
                        rotMat[1] set float3Value(
                            2f.const * (i*j - k*r),
                            1f.const - 2f.const * (i*i + k*k),
                            2f.const * (j*k + i*r)
                        )
                        rotMat[2] set float3Value(
                            2f.const * (i*k + j*r),
                            2f.const * (j*k - i*r),
                            1f.const - 2f.const * (i*i + j*j)
                        )

                        val globalPos = float3Var(bee.struct.position.ksl)
                        val decay = float1Var(bee.struct.decay.ksl)
                        val vertexNormal = vertexAttribFloat3(Attribute.NORMALS.name)
                        val vertexPos = float3Var(vertexAttribFloat3(Attribute.POSITIONS.name))

                        val scale = float1Var(1f.const - clamp(decay - (BeeConfig.decayTime - 1f).const, 0f.const, 1f.const))

                        getFloat3Port("worldPos").input(rotMat * vertexPos * scale + globalPos)
                        getFloat3Port("worldNormal").input(rotMat * vertexNormal)

                        aliveness.input set decay
                    }
                }
                fragmentStage {
                    main {
                        val colorPort = getFloat4Port("baseColor")
                        val color = float4Var(colorPort.input.input)

                        val accentColor = float4Var(aliveColor.toLinear().const)
                        `if`(aliveness.output gt 0.01f.const) {
                            accentColor set deadColor.toLinear().const
                        }
                        `if`(color.r eq color.b) {
                            color.rgb set accentColor.rgb * color.r
                        }
                        colorPort.input(color)
                    }
                }
            }
        }
    ) : KslBlinnPhongShader(cfg)

    class BeeData : Struct("BeeData", MemoryLayout.Std430) {
        val position = float3("position")
        val decay = float1("decay")
        val rotation = float4("rotation")
        val velocity = float3("velocity")
        val enemyIndex = int1("enemyIndex")
    }
}