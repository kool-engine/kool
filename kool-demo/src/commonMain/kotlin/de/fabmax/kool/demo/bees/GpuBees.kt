package de.fabmax.kool.demo.bees

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.KslBlinnPhongShaderConfig
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.blocks.noise11
import de.fabmax.kool.modules.ksl.blocks.noise31
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.geometry.RectUvs
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import kotlin.math.min

class GpuBees(beeScene: Scene) {

    private val maxGpuBees: Int get() = BeeConfig.maxBeesPerTeamGpu

    // Contains position, rotation and velocities for all bees of one team
    private val beeBufferA = StorageBuffer1d((maxGpuBees + 64) * 3, GpuType.FLOAT4)
    private val beeBufferB = StorageBuffer1d((maxGpuBees + 64) * 3, GpuType.FLOAT4)

    private val beeUpdateShader = KslComputeShader("Bee update") {
        computeStage(64) {
            val deltaT = uniformFloat1("deltaT")
            val randomSeed = uniformFloat1("randomSeed")
            val numBees = uniformInt1("numBees")
            val spawnPos = uniformFloat3("spawnPos")
            val beeBuffer = storage1d<KslFloat4>("beeBuffer")
            val enemyBeeBuffer = storage1d<KslFloat4>("enemyBeeBuffer")

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

            main {
                val beeIndex = int1Var(inGlobalInvocationId.x.toInt1())
                val beeOffset = int1Var(beeIndex * 3.const)
                val rand = float1Var(randomSeed + beeIndex.toFloat1())

                val positionDeadAlive = float4Var(beeBuffer[beeOffset])
                val rotation = float4Var(beeBuffer[beeOffset + 1.const])
                val velocityEnemy = float4Var(beeBuffer[beeOffset + 2.const])

                val position = float3Var(positionDeadAlive.xyz)
                val deadAlive = float1Var(positionDeadAlive.w)
                val velocity = float3Var(velocityEnemy.xyz)
                val enemy = int1Var(velocityEnemy.w.toInt1())

                `if`((beeIndex ge numBees) and (deadAlive eq 0f.const)) {
                    deadAlive set 0.01f.const
                }

                `if`(deadAlive eq 0f.const) {
                    // bee is alive
                    val vJitter = float3Var((noise31(rand) - 0.5f.const) * 2f.const * BeeConfig.speedJitter.const * deltaT)
                    velocity set (velocity + vJitter) * (1f.const - BeeConfig.speedDamping.const * deltaT)

                    // swarming
                    val attractiveFriend = int1Var((noise11(rand + 17f.const) * numBees.toFloat1()).toInt1() * 3.const)
                    val friendPos = float3Var(beeBuffer[attractiveFriend].xyz)
                    val delta = float3Var(friendPos - position)
                    val dist = float1Var(max(0.1f.const, length(delta)))
                    velocity += delta * BeeConfig.teamAttraction.const * deltaT / dist

                    val repellentFriend = int1Var((noise11(rand + 31f.const) * numBees.toFloat1()).toInt1() * 3.const)
                    friendPos set float3Var(beeBuffer[repellentFriend].xyz)
                    delta set position - friendPos
                    dist set max(0.1f.const, length(delta))
                    velocity += delta * BeeConfig.teamRepulsion.const * deltaT / dist

                    // attack enemy bee
                    `if`((enemy lt 0.const) or (enemy gt numBees) or (enemyBeeBuffer[enemy * 3.const].w ne 0f.const)) {
                        enemy set (noise11(rand + 11f.const) * numBees.toFloat1()).toInt1()

                    }.`else` {
                        val enemyState = float4Var(enemyBeeBuffer[enemy * 3.const])
                        delta set enemyState.xyz - position
                        dist set max(0.1f.const, length(delta))

                        `if`(dist gt BeeConfig.attackDistance.const) {
                            velocity += delta * BeeConfig.chaseForce.const * deltaT / dist

                        }.`else` {
                            velocity += delta * BeeConfig.attackForce.const * deltaT / dist
                            `if`(dist lt BeeConfig.hitDistance.const) {
                                enemyState.w set 0.01f.const
                                enemyBeeBuffer[enemy * 3.const] = enemyState
                            }
                        }
                    }

                }.`else` {
                    // bee is dead
                    velocity set velocity * (1f.const - 0.5f.const * deltaT)
                    velocity.y += BeeConfig.gravity.const * deltaT
                    deadAlive += deltaT

                    `if`((deadAlive gt BeeConfig.decayTime.const) and (beeIndex lt numBees)) {
                        deadAlive set 0f.const
                        position set spawnPos + (noise31(rand) - 0.5f.const) * 10f.const
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

                beeBuffer[beeOffset] = float4Value(position, deadAlive)
                beeBuffer[beeOffset + 1.const] = rotation
                beeBuffer[beeOffset + 2.const] = float4Value(velocity, enemy.toFloat1())
            }
        }
    }

    private val beeInstancesA = MeshInstanceList(emptyList())
    private val beeInstancesB = MeshInstanceList(emptyList())
    val beeMeshA = makeBeeMesh(beeInstancesA)
    val beeMeshB = makeBeeMesh(beeInstancesB)

    val simulationPass = ComputeRenderPass("Bee update pass")

    init {
        initBeeBuffer(beeBufferA, Vec3f(BeeConfig.worldSize.x * 0.4f, 0f, 0f), Vec3f(-10f, 0f, 0f))
        initBeeBuffer(beeBufferB, Vec3f(BeeConfig.worldSize.x * -0.4f, 0f, 0f), Vec3f(10f, 0f, 0f))

        beeScene.addOffscreenPass(simulationPass)

        var deltaT by beeUpdateShader.uniform1f("deltaT")
        var randomSeed by beeUpdateShader.uniform1f("randomSeed")
        var numBees by beeUpdateShader.uniform1i("numBees")
        var spawnPos by beeUpdateShader.uniform3f("spawnPos")
        var beeBuffer by beeUpdateShader.storage1d("beeBuffer")
        var enemyBeeBuffer by beeUpdateShader.storage1d("enemyBeeBuffer")

        val taskA = simulationPass.addTask(beeUpdateShader, Vec3i.ZERO)
        val taskB = simulationPass.addTask(beeUpdateShader, Vec3i.ZERO)

        val bindGroupA: BindGroupData = taskA.pipeline.pipelineData
        val bindGroupB: BindGroupData = taskB.pipeline.pipelineDataLayout.createData()

        taskA.pipeline.pipelineData = bindGroupA
        spawnPos = Vec3f(BeeConfig.worldSize.x * 0.4f, 0f, 0f)
        beeBuffer = beeBufferA
        enemyBeeBuffer = beeBufferB
        taskB.pipeline.pipelineData = bindGroupB
        spawnPos = Vec3f(BeeConfig.worldSize.x * -0.4f, 0f, 0f)
        beeBuffer = beeBufferB
        enemyBeeBuffer = beeBufferA

        taskA.onBeforeDispatch {
            taskA.pipeline.pipelineData = bindGroupA
            deltaT = min(0.02f, Time.deltaT)
            randomSeed = 1000f + (Time.gameTime % 1000.0).toFloat()
            numBees = BeeConfig.beesPerTeam.value

            taskA.setNumGroupsByInvocations(BeeConfig.beesPerTeam.value)
            beeInstancesA.numInstances = BeeConfig.beesPerTeam.value
        }
        taskB.onBeforeDispatch {
            taskB.pipeline.pipelineData = bindGroupB
            deltaT = min(0.02f, Time.deltaT)
            randomSeed = -1000f - (Time.gameTime % 1000.0).toFloat()
            numBees = BeeConfig.beesPerTeam.value

            taskB.setNumGroupsByInvocations(BeeConfig.beesPerTeam.value)
            beeInstancesB   .numInstances = BeeConfig.beesPerTeam.value
        }
    }

    fun setEnabled(isEnabled: Boolean) {
        beeMeshA.isVisible = isEnabled
        beeMeshB.isVisible = isEnabled
        simulationPass.isEnabled = isEnabled
    }

    private fun initBeeBuffer(beeBuffer: StorageBuffer1d, spawnPos: Vec3f, velocity: Vec3f) {
        for (i in 0 until maxGpuBees) {
            // position and dead / alive state
            beeBuffer[i * 3 + 0] = Vec4f(spawnPos + randomInUnitCube() * 5f, 0f)
            // rotation
            beeBuffer[i * 3 + 1] = QuatF.IDENTITY.toVec4f()
            // velocity and enemy index
            beeBuffer[i * 3 + 2] = Vec4f(velocity, 0f)
        }
    }

    fun setupShaders(beeTexture: Texture2d) {
        beeMeshA.shader = BeeDrawShader(MdColor.BLUE, MdColor.PURPLE).apply {
            colorMap = beeTexture
            storage1d("beeBuffer", beeBufferA)
        }
        beeMeshB.shader = BeeDrawShader(MdColor.AMBER, MdColor.DEEP_ORANGE).apply {
            colorMap = beeTexture
            storage1d("beeBuffer", beeBufferB)
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
            ambientColor = AmbientColor.Uniform(BeeDemo.bgColor.toLinear())

            modelCustomizer = {
                val aliveness = interStageFloat1()
                vertexStage {
                    main {
                        val beeBuffer = storage1d<KslFloat4>("beeBuffer")
                        val beeOffset = int1Var(inInstanceIndex.toInt1() * 3.const)

                        val rotQuat = float4Var(beeBuffer[beeOffset + 1.const])
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

                        val globalPos = float4Var(beeBuffer[beeOffset])
                        val vertexNormal = vertexAttribFloat3(Attribute.NORMALS.name)
                        val vertexPos = float3Var(vertexAttribFloat3(Attribute.POSITIONS.name))

                        val scale = float1Var(1f.const - clamp(globalPos.w - (BeeConfig.decayTime - 1f).const, 0f.const, 1f.const))

                        getFloat3Port("worldPos").input(rotMat * vertexPos * scale + globalPos.xyz)
                        getFloat3Port("worldNormal").input(rotMat * vertexNormal)

                        aliveness.input set globalPos.w
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

}