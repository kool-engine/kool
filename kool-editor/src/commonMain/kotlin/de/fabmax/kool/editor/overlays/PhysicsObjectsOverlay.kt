package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.api.CachedSceneComponents
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.JointComponent
import de.fabmax.kool.editor.components.PhysicsComponent
import de.fabmax.kool.editor.data.JointData
import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.physics.joints.D6JointMotion
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.roundToInt

class PhysicsObjectsOverlay : Node("Physics objects overlay"), EditorOverlay {

    private var physicsComponentCache: CachedSceneComponents<PhysicsComponent>? = null
    private val physicsComponents: List<PhysicsComponent> get() = physicsComponentCache?.getComponents() ?: emptyList()

    private val joints = mutableListOf<JointInstance>()

    private val centerInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val xPrismaticInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val yPrismaticInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val zPrismaticInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val twistInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val ySwingInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val zSwingInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))

    private val jointCenterMesh = Mesh(meshAttrs, centerInstances, "centers").apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
        shader = objectShader
        generate {
            icoSphere {
                steps = 2
                radius = 0.05f
            }
        }
    }

    private val xPrismaticJointMesh = Mesh(meshAttrs, xPrismaticInstances, "x-prismatics").apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
        shader = objectShader
        generate {
            rotate(90f.deg, Vec3f.Z_AXIS)
            prismaticArrow()
        }
    }

    private val yPrismaticJointMesh = Mesh(meshAttrs, yPrismaticInstances, "y-prismatics").apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
        shader = objectShader
        generate {
            prismaticArrow()
        }
    }

    private val zPrismaticJointMesh = Mesh(meshAttrs, zPrismaticInstances, "z-prismatics").apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
        shader = objectShader
        generate {
            rotate(90f.deg, Vec3f.X_AXIS)
            prismaticArrow()
        }
    }

    private val twistJointMesh = Mesh(meshAttrs, twistInstances, "twists").apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
        shader = objectShader
        generate {
            withTransform {
                rotate(90f.deg, Vec3f.Z_AXIS)
                translate(0f, 0.3f, 0f)
                cylinder {
                    radius = 0.015f
                    height = 0.4f
                }
                translate(0f, -0.6f, 0f)
                cylinder {
                    radius = 0.015f
                    height = 0.4f
                }
            }

            rotationArrow(270f.deg)
        }
    }

    private val ySwingJointMesh = Mesh(meshAttrs, ySwingInstances, "y-swing").apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
        shader = objectShader
        generate {
            rotate(90f.deg, Vec3f.NEG_Z_AXIS)
            rotationArrow(120f.deg)
        }
    }

    private val zSwingJointMesh = Mesh(meshAttrs, zSwingInstances, "z-swing").apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
        shader = objectShader
        generate {
            rotate(90f.deg, Vec3f.NEG_Z_AXIS)
            rotate(90f.deg, Vec3f.Y_AXIS)
            rotationArrow(120f.deg)
        }
    }

    init {
        addNode(jointCenterMesh)
        addNode(twistJointMesh)
        addNode(ySwingJointMesh)
        addNode(zSwingJointMesh)
        addNode(xPrismaticJointMesh)
        addNode(yPrismaticJointMesh)
        addNode(zPrismaticJointMesh)

        onUpdate {
            updateOverlayInstances()
        }
    }

    private fun updateOverlayInstances() {
        if (physicsComponentCache?.isOutdated == true) {
            joints.clear()
            joints += physicsComponents
                .filterIsInstance<JointComponent>()
                .map { JointInstance(it) }
        }

        centerInstances.addInstances(joints, Color.WHITE) { true }
        xPrismaticInstances.addInstances(joints, MdColor.RED.toLinear()) { it.isPrismaticX }
        yPrismaticInstances.addInstances(joints, MdColor.LIGHT_GREEN.toLinear()) { it.isPrismaticY }
        zPrismaticInstances.addInstances(joints, MdColor.BLUE.toLinear()) { it.isPrismaticZ }
        twistInstances.addInstances(joints, MdColor.PURPLE.toLinear()) { it.isTwist }
        ySwingInstances.addInstances(joints, MdColor.CYAN.toLinear()) { it.isSwingY }
        zSwingInstances.addInstances(joints, MdColor.CYAN.toLinear()) { it.isSwingZ }
    }

    private fun MeshInstanceList.addInstances(objs: List<JointInstance>, color: Color, filter: (JointInstance) -> Boolean) {
        clear()
        addInstancesUpTo(objs.size) { buf ->
            var addCount = 0
            for (i in objs.indices) {
                val j = objs[i]
                if (j.gameEntity.isVisible && filter(j)) {
                    j.addInstance(buf, color)
                    addCount++
                }
            }
            addCount
        }
    }

    private fun MeshBuilder.prismaticArrow() {
        withTransform {
            translate(0f, 0.3f, 0f)
            cylinder {
                radius = 0.015f
                height = 0.4f
            }
            translate(0f, -0.6f, 0f)
            cylinder {
                radius = 0.015f
                height = 0.4f
            }
        }

        withTransform {
            translate(0f, 0.5f, 0f)
            cylinder {
                topRadius = 0f
                topFill = false
                bottomRadius = 0.05f
                height = 0.1f
            }
            translate(0f, -1f, 0f)
            cylinder {
                topRadius = 0.05f
                bottomFill = false
                bottomRadius = 0f
                height = 0.1f
            }
        }
    }

    private fun MeshBuilder.rotationArrow(sweep: AngleF) {
        val p = profile {
            circleShape(0.015f, 8)
        }
        val steps = (sweep.deg / 8).roundToInt()
        for (i in 0..steps) {
            withTransform {
                rotate(sweep * (i / steps.toFloat()) - sweep * 0.5f, Vec3f.X_AXIS)
                translate(0f, 0.3f, 0f)
                p.sample()
            }
        }
        withTransform {
            rotate(sweep * -0.5f, Vec3f.X_AXIS)
            translate(0f, 0.3f, 0f)
            rotate(90f.deg, Vec3f.NEG_X_AXIS)
            cylinder {
                topRadius = 0f
                topFill = false
                bottomRadius = 0.05f
                height = 0.1f
            }
        }
        withTransform {
            rotate(sweep * 0.5f, Vec3f.X_AXIS)
            translate(0f, 0.3f, 0f)
            rotate(90f.deg, Vec3f.X_AXIS)
            cylinder {
                topRadius = 0f
                topFill = false
                bottomRadius = 0.05f
                height = 0.1f
            }
        }
    }

    override fun onEditorSceneChanged(scene: EditorScene) {
        physicsComponentCache = CachedSceneComponents(scene, PhysicsComponent::class)
    }

    override fun pick(rayTest: RayTest): GameEntity? {
        var closest: GameEntity? = null
        joints.forEach {
            if (it.rayTest(rayTest)) {
                closest = it.gameEntity
            }
        }
        return closest
    }

    companion object {
        private val meshAttrs = listOf(Attribute.POSITIONS)
        private val objectShader = KslUnlitShader {
            pipeline {
                depthTest = DepthCompareOp.ALWAYS
                isWriteDepth = false
            }
            vertices { isInstanced = true }
            color { instanceColor() }
            colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
        }
    }

    private val JointData.isPrismaticX: Boolean get() =
        this is JointData.Prismatic || this is JointData.Distance || (this is JointData.D6 && motionX != D6JointMotion.Locked)
    private val JointData.isPrismaticY: Boolean get() =
        this is JointData.Distance || (this is JointData.D6 && motionY != D6JointMotion.Locked)
    private val JointData.isPrismaticZ: Boolean get() =
        this is JointData.Distance || (this is JointData.D6 && motionZ != D6JointMotion.Locked)
    private val JointData.isTwist: Boolean get() =
        this is JointData.Revolute || this is JointData.Spherical || (this is JointData.D6 && motionTwist != D6JointMotion.Locked)
    private val JointData.isSwingY: Boolean get() =
        this is JointData.Spherical || (this is JointData.D6 && motionSwingY != D6JointMotion.Locked)
    private val JointData.isSwingZ: Boolean get() =
        this is JointData.Spherical || (this is JointData.D6 && motionSwingZ != D6JointMotion.Locked)

    private inner class JointInstance(val jointComponent: JointComponent) : OverlayObject(jointComponent.gameEntity) {
        override val color: Color = Color.WHITE

        val isPrismaticX: Boolean get() = jointComponent.data.jointData.isPrismaticX
        val isPrismaticY: Boolean get() = jointComponent.data.jointData.isPrismaticY
        val isPrismaticZ: Boolean get() = jointComponent.data.jointData.isPrismaticZ
        val isTwist: Boolean get() = jointComponent.data.jointData.isTwist
        val isSwingY: Boolean get() = jointComponent.data.jointData.isSwingY
        val isSwingZ: Boolean get() = jointComponent.data.jointData.isSwingZ

        fun rayTest(rayTest: RayTest): Boolean {
            var isHit = rayTest(rayTest, jointCenterMesh)
            if (isPrismaticX) isHit = isHit || rayTest(rayTest, xPrismaticJointMesh)
            if (isPrismaticY) isHit = isHit || rayTest(rayTest, yPrismaticJointMesh)
            if (isPrismaticZ) isHit = isHit || rayTest(rayTest, zPrismaticJointMesh)
            if (isTwist) isHit = isHit || rayTest(rayTest, twistJointMesh)
            if (isSwingY) isHit = isHit || rayTest(rayTest, ySwingJointMesh)
            if (isSwingZ) isHit = isHit || rayTest(rayTest, zSwingJointMesh)
            return isHit
        }
    }
}