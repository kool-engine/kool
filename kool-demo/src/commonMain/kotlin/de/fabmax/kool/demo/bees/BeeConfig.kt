package de.fabmax.kool.demo.bees

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.mutableStateOf

object BeeConfig {
    val maxBeesPerTeamCpu = 50_000
    val maxBeesPerTeamGpu = 500_000
    val beesPerTeam = mutableStateOf(if (KoolSystem.isJavascript) maxBeesPerTeamCpu / 5 else maxBeesPerTeamCpu)

    val maxSpawnSpeed = 20f

    val speedJitter = 200f
    val speedDamping = 0.9f
    val teamAttraction = 5f
    val teamRepulsion = -4f

    val chaseForce = 20f
    val attackDistance = 7.5f
    val attackForce = 500f
    val hitDistance = 2f

    val worldSize: Vec3f = Vec3f(200f, 100f, 100f)
    val worldExtent: Vec3f = worldSize.mul(0.5f, MutableVec3f())

    val gravity = -20f
    val decayTime = 6f
}