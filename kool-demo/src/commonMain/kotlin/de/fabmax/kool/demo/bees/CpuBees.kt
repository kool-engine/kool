package de.fabmax.kool.demo.bees

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.RectUvs
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.Time
import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.random.Random

class CpuBees(val team: Int) {
    val beeInstances = MeshInstanceList(BeeConfig.maxBeesPerTeamCpu, BeeDemo.ATTR_POSITION, BeeDemo.ATTR_ROTATION)
    val beeMesh: Mesh

    val positions = Array(BeeConfig.maxBeesPerTeamCpu + 1) { MutableVec4f(0f, 0f, 0f, BeeConfig.decayTime) }
    val rotations = Array(BeeConfig.maxBeesPerTeamCpu) { MutableQuatF(QuatF.IDENTITY) }
    val velocities = Array(BeeConfig.maxBeesPerTeamCpu) { MutableVec3f() }
    val enemies = IntArray(BeeConfig.maxBeesPerTeamCpu) { BeeConfig.maxBeesPerTeamCpu }

    private var numSimulatedBees = 0
    private var prevSimulatedBees = 0
    private var decreaseBeeCountdown = 0f

    lateinit var enemyBees: CpuBees

    private val random = Random(17 + team * 31)

    private val tmpVec4 = MutableVec4f()
    private val tmpVec3a = MutableVec3f()
    private val tmpVec3b = MutableVec3f()
    private val tmpMat3 = MutableMat3f()
    private val tmpQuat = MutableQuatF()

    val beeUpdateTime = mutableStateOf(0.0)
    val instanceUpdateTime = mutableStateOf(0.0)

    private var speedJitter = BeeConfig.speedJitter.value
    private var teamAttraction = BeeConfig.teamAttraction.value
    private var teamRepulsion = BeeConfig.teamRepulsion.value
    private var chaseForce = BeeConfig.chaseForce.value
    private var attackForce = BeeConfig.attackForce.value

    init {
        beeMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, instances = beeInstances).apply {
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
    }

    private fun updateBeeCount() {
        // if number of bees is decreased, keep simulating the previous number until excess bees decayed
        if (prevSimulatedBees > BeeConfig.beesPerTeam.value) {
            decreaseBeeCountdown = BeeConfig.decayTime
        }
        prevSimulatedBees = BeeConfig.beesPerTeam.value
        if (decreaseBeeCountdown > 0f) {
            decreaseBeeCountdown -= Time.deltaT
            numSimulatedBees = max(numSimulatedBees, BeeConfig.beesPerTeam.value)
        } else {
            numSimulatedBees = BeeConfig.beesPerTeam.value
        }
    }

    fun updateBees() {
        updateBeeCount()

        val numBees = BeeConfig.beesPerTeam.value
        val dt = min(0.02f, Time.deltaT)
        val pt = PerfTimer()

        speedJitter = BeeConfig.speedJitter.value
        teamAttraction = BeeConfig.teamAttraction.value
        teamRepulsion = BeeConfig.teamRepulsion.value
        chaseForce = BeeConfig.chaseForce.value
        attackForce = BeeConfig.attackForce.value

        // update alive bees
        repeat(numSimulatedBees) { i ->
            val bee = Bee(i)

            if (i > numBees) {
                if (bee.isAlive) {
                    bee.kill()
                }
            } else if (bee.isDecayed) {
                bee.spawn()
            }

            bee.update(dt)
        }
        beeUpdateTime.set(pt.takeMs())

        // copy mesh instance data
        pt.reset()
        updateInstances()
        instanceUpdateTime.set(pt.takeMs())
    }

    private fun updateInstances() {
        beeInstances.clear()
        beeInstances.addInstances(numSimulatedBees) { buf ->
            repeat(numSimulatedBees) { i ->
                positions[i].putTo(buf)
                rotations[i].putTo(buf)
            }
        }
    }

    private fun Bee.spawn() {
        rotation.set(randomF(0f, 360f).deg, random.randomInUnitSphere(tmpVec3a).norm())
        position.set(random.randomInUnitSphere(tmpVec3a).mul(BeeConfig.worldSize.x * 0.05f))
        position.x += -BeeConfig.worldSize.x * 0.4f + BeeConfig.worldSize.x * 0.8f * team
        enemy = EnemyBee(BeeConfig.maxBeesPerTeamCpu)
        random.randomInUnitSphere(velocity).mul(BeeConfig.maxSpawnSpeed)
    }

    private fun Bee.kill() {
        if (position.w == 0f) {
            position.w = 0.01f
        }
    }

    private fun getRandomBee() = Bee(random.randomI(0, max(1, BeeConfig.beesPerTeam.value - 1)))

    @JvmInline
    value class Bee(val index: Int) {
        init {
            if (index < 0) {
                throw IllegalArgumentException("negative index!")
            }
        }
    }

    @JvmInline
    value class EnemyBee(val index: Int)

    val Bee.position: MutableVec4f get() = positions[index]
    val Bee.rotation: MutableQuatF get() = rotations[index]
    val Bee.velocity: MutableVec3f get() = velocities[index]
    val Bee.isAlive: Boolean get() = position.w == 0f
    val Bee.isDecayed: Boolean get() = position.w >= BeeConfig.decayTime
    var Bee.enemy: EnemyBee
        get() = EnemyBee(enemies[index])
        set(value) {
            enemies[index] = value.index
        }

    val EnemyBee.position: MutableVec4f get() = enemyBees.positions[index]
    val EnemyBee.isAlive: Boolean get() = position.w == 0f

    fun Bee.update(dt: Float) {
        if (isAlive) {
            updateAlive(dt)
        } else {
            updateDead(dt)
        }

        // update position by current velocity and time step
        val rot = rotation
        val pos = position
        val vel = velocity

        pos.add(tmpVec3a.set(vel).mul(dt))

        // update rotation by current velocity direction
        val speed = vel.length()
        if (speed > 1f && abs(vel.dot(Vec3f.Y_AXIS) / speed) < 0.99f) {
            val right = Vec3f.Y_AXIS.cross(vel, tmpVec3a).norm()
            val up = vel.cross(right, tmpVec3b).norm()
            val front = tmpVec3a.set(vel).norm()

            tmpMat3.set(
                right.x, up.x, front.x,
                right.y, up.y, front.y,
                right.z, up.z, front.z
            )
            tmpMat3.getRotation(tmpQuat)

            val w0 = dt * 5f
            val w1 = 1f - w0
            rot.x = rot.x * w1 + tmpQuat.x * w0
            rot.y = rot.y * w1 + tmpQuat.y * w0
            rot.z = rot.z * w1 + tmpQuat.z * w0
            rot.w = rot.w * w1 + tmpQuat.w * w0
            rot.norm()
        }

        // clamp bee positions to world bounds
        if (abs(pos.x) > BeeConfig.worldExtent.x) {
            pos.x = BeeConfig.worldExtent.x * sign(pos.x)
            vel.x *= -0.5f
            vel.y *= 0.8f
            vel.z *= 0.8f
        }
        if (abs(pos.y) > BeeConfig.worldExtent.y) {
            pos.y = BeeConfig.worldExtent.y * sign(pos.y)
            vel.x *= 0.8f
            vel.y *= -0.5f
            vel.z *= 0.8f
        }
        if (abs(pos.z) > BeeConfig.worldExtent.z) {
            pos.z = BeeConfig.worldExtent.z * sign(pos.z)
            vel.x *= 0.8f
            vel.y *= 0.8f
            vel.z *= -0.5f
        }
    }

    fun Bee.updateAlive(dt: Float) {
        val pos = position
        val vel = velocity
        val target = enemy

        val v = random.randomInUnitCube(tmpVec3a).mul(speedJitter * dt)
        vel.add(v).mul(1f - BeeConfig.speedDamping * dt)

        // swarming
        val attractiveFriend = getRandomBee()
        var delta = attractiveFriend.position.subtract(pos, tmpVec4)
        var dist = max(0.1f, delta.length())
        if (attractiveFriend.isAlive) {
            // only alive friends are attractive
            vel.add(delta.mul(teamAttraction * dt / dist))
        }

        val repellentFriend = getRandomBee()
        delta = pos.subtract(repellentFriend.position, tmpVec4)
        dist = max(0.1f, delta.length())
        if (repellentFriend.isAlive) {
            // only alive friends repel
            vel.add(delta.mul(teamRepulsion * dt / dist))
        }

        // attack enemy bee
        if (!target.isAlive) {
            enemy = EnemyBee(enemyBees.getRandomBee().index)
        } else {
            delta = target.position.subtract(pos, tmpVec4)
            dist = delta.length()

            if (dist > BeeConfig.attackDistance) {
                vel.add(delta.mul(chaseForce * dt / dist))

            } else {
                vel.add(delta.mul(attackForce * dt / dist))
                if (dist < BeeConfig.hitDistance) {
                    target.kill()
                }
            }
        }
    }

    fun Bee.updateDead(dt: Float) {
        val pos = position
        val vel = velocity

        vel.mul(1f - 0.5f * dt)
        vel.y += BeeConfig.gravity * dt

        pos.w += dt
    }

    fun EnemyBee.kill() {
        enemyBees.positions[index].w = 0.01f
    }

    private fun MutableVec3f.add(vec4: Vec4f): MutableVec3f {
        x += vec4.x
        y += vec4.y
        z += vec4.z
        return this
    }

    private fun MutableVec4f.add(vec3: Vec3f): MutableVec4f {
        x += vec3.x
        y += vec3.y
        z += vec3.z
        return this
    }
}