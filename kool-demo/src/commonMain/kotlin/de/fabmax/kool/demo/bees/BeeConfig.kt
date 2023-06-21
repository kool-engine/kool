package de.fabmax.kool.demo.bees

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.mutableStateOf

object BeeConfig {
    val maxBeesPerTeam = 50_000
    val beesPerTeam = mutableStateOf(if (KoolSystem.isJavascript) maxBeesPerTeam / 10 else maxBeesPerTeam)

    var maxSpawnSpeed = 20f

    var speedJitter = 200f
    var speedDamping = 0.9f
    var teamAttraction = 5f
    var teamRepulsion = -4f

    var chaseForce = 50f
    var attackDistance = 4f
    var attackForce = 500f
    var hitDistance = 0.5f

    val worldSize: Vec3f = Vec3f(100f, 20f, 30f)
    val worldExtent: Vec3f = worldSize.scale(0.5f, MutableVec3f())

    val gravity = -20f
    val decayTime = 4f
}