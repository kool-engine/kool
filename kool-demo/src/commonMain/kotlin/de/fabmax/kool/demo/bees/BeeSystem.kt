package de.fabmax.kool.demo.bees

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.Time
import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.random.Random

class BeeSystem(val team: Int) {
    val beeInstances = MeshInstanceList(BeeConfig.maxBeesPerTeam, BeeDemo.ATTR_POSITION, BeeDemo.ATTR_ROTATION)

    val positions = Array(BeeConfig.maxBeesPerTeam + 1) { MutableVec4f(Vec4f.W_AXIS) }
    val rotations = Array(BeeConfig.maxBeesPerTeam) { MutableVec4f(Vec4f.W_AXIS) }
    val velocities = Array(BeeConfig.maxBeesPerTeam) { MutableVec3f() }
    val enemies = IntArray(BeeConfig.maxBeesPerTeam) { BeeConfig.maxBeesPerTeam }

    var aliveBees = 0

    lateinit var enemyBees: BeeSystem

    private val random = Random(17 + team * 31)

    private val tmpVec4 = MutableVec4f()
    private val tmpVec3a = MutableVec3f()
    private val tmpVec3b = MutableVec3f()
    private val tmpMat3 = Mat3f()

    val beeUpdateTime = mutableStateOf(0.0)
    val instanceUpdateTime = mutableStateOf(0.0)

    init {
        spawnBees()
    }

    fun updateBees() {
        val dt = min(0.02f, Time.deltaT)
        val pt = PerfTimer()

        // update alive bees
        var newAliveCnt = 0
        repeat(aliveBees) { i ->
            val bee = Bee(i)
            if (!bee.isDecayed) {
                bee.update(dt)
                if (i != newAliveCnt) {
                    // keep the particle attribute arrays packed
                    rotations[newAliveCnt].set(rotations[i])
                    positions[newAliveCnt].set(positions[i])
                    velocities[newAliveCnt].set(velocities[i])
                    enemies[newAliveCnt] = enemies[i]
                }
                newAliveCnt++
            }
        }
        aliveBees = newAliveCnt

        // spawn new bees if there are too few
        spawnBees()
        // kill bees if there are to many
        killBees()
        beeUpdateTime.set(pt.takeMs())

        // copy mesh instance data
        pt.reset()
        updateInstances()
        instanceUpdateTime.set(pt.takeMs())
    }

    private fun updateInstances() {
        beeInstances.clear()
        beeInstances.addInstances(aliveBees) { buf ->
            repeat(aliveBees) { i ->
                buf.put(positions[i].array)
                buf.put(rotations[i].array)
            }
        }
    }

    private fun spawnBees() {
        val n = BeeConfig.beesPerTeam.value
        while (aliveBees < n) {
            val spawned = Bee(aliveBees++)

            spawned.rotation.setRotation(randomF(0f, 360f), random.randomInUnitSphere(tmpVec3a).norm())
            spawned.position.set(random.randomInUnitSphere(tmpVec3a).scale(BeeConfig.worldSize.x * 0.05f))
            spawned.position.x += -BeeConfig.worldSize.x * 0.4f + BeeConfig.worldSize.x * 0.8f * team
            spawned.enemy = EnemyBee(BeeConfig.maxBeesPerTeam)
            random.randomInUnitSphere(spawned.velocity).scale(BeeConfig.maxSpawnSpeed)
        }
    }

    private fun killBees() {
        if (aliveBees > BeeConfig.beesPerTeam.value) {
            for (i in BeeConfig.beesPerTeam.value until aliveBees) {
                if (positions[i].w == 0f) {
                    positions[i].w = 0.01f
                }
            }
        }
    }

    fun getRandomBee() = Bee(random.randomI(0, max(1, aliveBees - 1)))

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
    val Bee.rotation: MutableVec4f get() = rotations[index]
    val Bee.velocity: MutableVec3f get() = velocities[index]
    val Bee.isAlive: Boolean get() = position.w == 0f
    val Bee.isDecayed: Boolean get() = position.w > BeeConfig.decayTime
    var Bee.enemy: EnemyBee
        get() = EnemyBee(enemies[index])
        set(value) { enemies[index] = value.index }

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

        pos.add(tmpVec3a.set(vel).scale(dt))

        // update rotation by current velocity direction
        val speed = vel.length()
        if (speed > 1f && abs(vel * Vec3f.Y_AXIS / speed) < 0.99f) {
            val right = Vec3f.Y_AXIS.cross(vel, tmpVec3a).norm()
            val up = vel.cross(right, tmpVec3b).norm()
            tmpMat3.setColVec(0, right)
            tmpMat3.setColVec(1, up)
            tmpMat3.setColVec(2, tmpVec3a.set(vel).norm())
            tmpMat3.getRotation(tmpVec4)

            val w0 = dt * 5f
            val w1 = 1f - w0
            rot.x = rot.x * w1 + tmpVec4.x * w0
            rot.y = rot.y * w1 + tmpVec4.y * w0
            rot.z = rot.z * w1 + tmpVec4.z * w0
            rot.w = rot.w * w1 + tmpVec4.w * w0
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

        val v = random.randomInUnitCube(tmpVec3a).scale(BeeConfig.speedJitter * dt)
        vel.add(v).scale(1f - BeeConfig.speedDamping * dt)

        // swarming
        val attractiveFriend = getRandomBee()
        var delta = attractiveFriend.position.subtract(pos, tmpVec4)
        var dist = delta.length()
        if (dist > 0f) {
            vel.add(delta.scale(BeeConfig.teamAttraction * dt / dist))
        }
        val repellentFriend = getRandomBee()
        delta = repellentFriend.position.subtract(pos, tmpVec4)
        dist = delta.length()
        if (dist > 0f) {
            vel.add(delta.scale(BeeConfig.teamRepulsion * dt / dist))
        }

        // attack enemy bee
        if (!target.isAlive) {
            enemy = EnemyBee(enemyBees.getRandomBee().index)
        } else {
            delta = target.position.subtract(pos, tmpVec4)
            dist = delta.length()

            if (dist > BeeConfig.attackDistance) {
                vel.add(delta.scale(BeeConfig.chaseForce * dt / dist))

            } else {
                vel.add(delta.scale(BeeConfig.attackForce * dt / dist))
                if (dist < BeeConfig.hitDistance) {
                    target.kill()
                }
            }
        }
    }

    fun Bee.updateDead(dt: Float) {
        val pos = position
        val vel = velocity

        vel.scale(1f - 0.5f * dt)
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