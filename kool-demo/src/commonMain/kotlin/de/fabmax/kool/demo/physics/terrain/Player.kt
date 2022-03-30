package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.character.CharacterController
import de.fabmax.kool.physics.character.CharacterControllerManager
import de.fabmax.kool.physics.util.CharacterTrackingCamRig
import de.fabmax.kool.pipeline.shading.unlitShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.LineMesh
import de.fabmax.kool.scene.Model
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.WalkAxes
import kotlin.math.*

class Player(val playerModel: Model, world: PhysicsWorld, ctx: KoolContext) : Group("player"), CharacterController.OnHitActor {

    val controller: CharacterController
    private val charManager: CharacterControllerManager

    var camRig: CharacterTrackingCamRig? = null
    val debugLineMesh = LineMesh().apply {
        isVisible = false
        shader = unlitShader {
            lineWidth = 2f
        }
    }
    private val controllerShapeOutline: LineMesh
    var isDrawShapeOutline: Boolean
        get() = controllerShapeOutline.isVisible
        set(value) { controllerShapeOutline.isVisible = value }


    var pushForceFac = 1f
    private val axes: WalkAxes
    private var moveHeading = 0f
    private var moveSpeed = 0f
    private val tmpForce = MutableVec3f()

    init {
        // set correct player model position (relative to player controller origin)
        playerModel.translate(0f, -0.9f, 0f)
        playerModel.rotate(180f, Vec3f.Y_AXIS)
        +playerModel

        charManager = CharacterControllerManager(world)
        controller = charManager.createController()
        controller.onHitActorListeners += this
        controllerShapeOutline = makeShapeOutline()
        +controllerShapeOutline

        axes = WalkAxes(ctx)

        onUpdate += {
            updateMovement()
            updateAnimation(it.deltaT)
        }
    }

    private fun updateMovement() {
        val lookDir = camRig?.lookDirection ?: Vec3f.NEG_Z_AXIS
        moveHeading = atan2(lookDir.x, -lookDir.z).toDeg()
        val walkDir = Vec2f(-axes.leftRight, axes.forwardBackward)
        if (walkDir.length() > 0f) {
            moveHeading += atan2(walkDir.x, walkDir.y).toDeg()
        }

        val speedFactor = max(abs(axes.forwardBackward), abs(axes.leftRight))
        moveSpeed = walkSpeed * speedFactor
        if (axes.runFactor > 0f) {
            moveSpeed = moveSpeed * (1f - axes.runFactor) + runSpeed * speedFactor * axes.runFactor
            controller.jumpSpeed = 6f
        } else {
            controller.jumpSpeed = 4f
        }
        if (axes.crouchFactor > 0f) {
            moveSpeed = moveSpeed * (1f - axes.crouchFactor) + crouchSpeed * speedFactor * axes.crouchFactor
        }

        // set controller.movement according to user input
        controller.movement.set(0f, 0f, -moveSpeed)
        controller.movement.rotate(moveHeading, Vec3f.Y_AXIS)
        controller.jump = axes.isJump
    }

    private fun updateAnimation(timeStep: Float) {
        // set transform group / model transform according to character position
        setIdentity()
        translate(controller.position)
        rotate(moveHeading, Vec3f.Y_AXIS)

        // determine which animation to use based on speed
        if (abs(moveSpeed) <= walkSpeed) {
            val w = (abs(moveSpeed) / walkSpeed).clamp(0f, 1f)
            playerModel.setAnimationWeight(walkAnimation, w)
            playerModel.setAnimationWeight(idleAnimation, 1f - w)
            playerModel.setAnimationWeight(runAnimation, 0f)

        } else {
            val w = ((abs(moveSpeed) - walkSpeed) / (runSpeed - walkSpeed)).clamp(0f, 1f)
            playerModel.setAnimationWeight(runAnimation, w)
            playerModel.setAnimationWeight(walkAnimation, 1f - w)
            playerModel.setAnimationWeight(idleAnimation, 0f)
        }
        playerModel.applyAnimation(timeStep * sign(moveSpeed))
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        axes.dispose(ctx)
    }

    fun releasePhysicsObjects() {
        controller.release()
        charManager.release()
    }

    private fun makeShapeOutline() = LineMesh().apply {
        isCastingShadow = false
        val cr = MdColor.RED
        val cg = MdColor.GREEN
        val cb = MdColor.BLUE
        val r = 0.3f // controller.radius
        val h = 0.6f // controller.height / 2f
        for (i in 0 until 40) {
            val a0 = (i / 40f) * 2 * PI.toFloat()
            val a1 = ((i + 1) / 40f) * 2 * PI.toFloat()
            addLine(Vec3f(cos(a0) * r, h, sin(a0) * r), Vec3f(cos(a1) * r, h, sin(a1) * r), cg)
            addLine(Vec3f(cos(a0) * r, -h, sin(a0) * r), Vec3f(cos(a1) * r, -h, sin(a1) * r), cg)
        }

        for (i in 0 until 20) {
            val a0 = (i / 40f) * 2 * PI.toFloat()
            val a1 = ((i + 1) / 40f) * 2 * PI.toFloat()
            addLine(Vec3f(cos(a0) * r, sin(a0) * r + h, 0f), Vec3f(cos(a1) * r, sin(a1) * r + h, 0f), cr)
            addLine(Vec3f(cos(a0) * r, -sin(a0) * r - h, 0f), Vec3f(cos(a1) * r, -sin(a1) * r - h, 0f), cr)

            addLine(Vec3f(0f, sin(a0) * r + h, cos(a0) * r), Vec3f(0f, sin(a1) * r + h, cos(a1) * r), cb)
            addLine(Vec3f(0f, -sin(a0) * r - h, cos(a0) * r), Vec3f(0f, -sin(a1) * r - h, cos(a1) * r), cb)
        }

        addLine(Vec3f(-r, h, 0f), Vec3f(-r, -h, 0f), cr)
        addLine(Vec3f(r, h, 0f), Vec3f(r, -h, 0f), cr)
        addLine(Vec3f(0f, h, -r), Vec3f(0f, -h, -r), cb)
        addLine(Vec3f(0f, h, r), Vec3f(0f, -h, r), cb)

        shader = unlitShader {
            lineWidth = 2f
        }
    }

    companion object {
        // move animation indices, depend on the actual model
        private const val idleAnimation = 0
        private const val runAnimation = 1
        private const val walkAnimation = 2

        // movement speeds, tuned to roughly match the animation speed
        private const val walkSpeed = 1.3f
        private const val crouchSpeed = 0.5f
        private const val runSpeed = 5f
    }

    override fun onHitActor(actor: RigidActor, hitWorldPos: Vec3f, hitWorldNormal: Vec3f) {
        if (actor is RigidDynamic) {
            // apply some fixed force to the hit actor
            val force = if (axes.isRun) -8000f else -2000f
            tmpForce.set(hitWorldNormal).scale(force * pushForceFac)
            actor.addForceAtPos(tmpForce, hitWorldPos, isLocalForce = false, isLocalPos = false)

            if (debugLineMesh.isVisible) {
                debugLineMesh.clear()
                val p0 = MutableVec3f(hitWorldPos).apply { x -= 0.25f }
                val p1 = MutableVec3f(hitWorldPos).apply { x += 0.25f }
                debugLineMesh.addLine(p0, p1, MdColor.RED)
                p0.set(hitWorldPos).apply { y -= 0.25f }
                p1.set(hitWorldPos).apply { y += 0.25f }
                debugLineMesh.addLine(p0, p1, MdColor.GREEN)
                p0.set(hitWorldPos).apply { z -= 0.25f }
                p1.set(hitWorldPos).apply { z += 0.25f }
                debugLineMesh.addLine(p0, p1, MdColor.BLUE)

                p0.set(hitWorldPos)
                p1.set(hitWorldPos).add(hitWorldNormal)
                debugLineMesh.addLine(p0, p1, MdColor.AMBER)
            }
        }
    }
}