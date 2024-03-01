package de.fabmax.kool.demo.bees

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.mutableStateOf

object BeeConfig {
    val maxBeesPerTeamCpu = 50_000
    val maxBeesPerTeamGpu = 500_000
    val beesPerTeam = mutableStateOf(5_000)

    val maxSpawnSpeed = 20f

    val speedJitter = mutableStateOf(200f)
    val speedDamping = 0.9f
    val teamAttraction = mutableStateOf(5f)
    val teamRepulsion = mutableStateOf(4f)

    val chaseForce = mutableStateOf(20f)
    val attackDistance = 5f
    val attackForce = mutableStateOf(500f)
    val hitDistance = 2f

    val worldSize: Vec3f = Vec3f(200f, 100f, 100f)
    val worldExtent: Vec3f = worldSize.mul(0.5f, MutableVec3f())

    val gravity = -30f
    val decayTime = 6f
}