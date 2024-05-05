package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.components.ContentComponent
import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SceneObjectsOverlay : Node("Scene objects overlay") {

    private val lights = mutableListOf<LightComponentInstance>()
    private val cameras = mutableListOf<CameraComponentInstance>()
    private val groups = mutableListOf<GroupNodeInstance>()

    private val dirLightsInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS))
    private val spotLightsInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS))
    private val pointLightsInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS))
    private val cameraInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS))
    private val groupInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS))

    private val dirLightMesh = Mesh(
        Attribute.POSITIONS, Attribute.NORMALS,
        instances = dirLightsInstances,
        name = "Directional lights"
    ).apply {
        isCastingShadow = false
        generate {
            uvSphere {
                radius = 0.15f
            }
            generateArrow()

            for (i in 0 until 63) {
                val a1 = i / 63f * 2f * PI.toFloat()
                val z1 = cos(a1)
                val y1 = sin(a1)
                val a2 = (i + 1) / 63f * 2f * PI.toFloat()
                val z2 = cos(a2)
                val y2 = sin(a2)

                val nrm = Vec3f(0f, y1, z1)
                line3d(Vec3f(0f, y1, z1), Vec3f(0f, y2, z2), nrm, lineW)
                line3d(Vec3f(0f, y1, z1), Vec3f(0f, y2, z2), Vec3f.X_AXIS, lineW)

                val nrm2 = nrm.rotate(90f.deg, Vec3f.X_AXIS, MutableVec3f())
                if (i % 8 == 0) {
                    line3d(Vec3f(0f, y1, z1), Vec3f(0.7f, y1, z1), nrm, lineW)
                    line3d(Vec3f(0f, y1, z1), Vec3f(0.7f, y1, z1), nrm2, lineW)
                }
                if ((i + 4) % 8 == 0) {
                    val r = 0.5f
                    line3d(Vec3f(0f, y1 * r, z1 * r), Vec3f(0.7f, y1 * r, z1 * r), nrm, lineW)
                    line3d(Vec3f(0f, y1 * r, z1 * r), Vec3f(0.7f, y1 * r, z1 * r), nrm2, lineW)
                }
            }
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { instanceColor(Attribute.COLORS) }
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB
        }
    }

    private val spotLightMesh = Mesh(
        Attribute.POSITIONS, Attribute.NORMALS,
        instances = spotLightsInstances,
        name = "Spot lights"
    ).apply {
        isCastingShadow = false
        generate {
            uvSphere {
                radius = 0.15f
            }
            generateArrow()

            for (i in 0 until 63) {
                val a1 = i / 63f * 2f * PI.toFloat()
                val z1 = cos(a1) * 0.85f
                val y1 = sin(a1) * 0.85f
                val a2 = (i + 1) / 63f * 2f * PI.toFloat()
                val z2 = cos(a2) * 0.85f
                val y2 = sin(a2) * 0.85f

                val nrm = Vec3f(0f, y1, z1)
                line3d(Vec3f(1f, y1, z1), Vec3f(1f, y2, z2), nrm, lineW)
                line3d(Vec3f(1f, y1, z1), Vec3f(1f, y2, z2), Vec3f.X_AXIS, lineW)

                if (i % 16 == 0){
                    line3d(Vec3f.ZERO, Vec3f(1f, y1, z1), nrm, lineW)
                    val nrm2 = nrm.rotate(90f.deg, Vec3f.X_AXIS, MutableVec3f())
                    line3d(Vec3f.ZERO, Vec3f(1f, y1, z1), nrm2, lineW)
                }
            }
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { instanceColor(Attribute.COLORS) }
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB
        }
    }

    private val pointLightMesh = Mesh(
        Attribute.POSITIONS, Attribute.NORMALS,
        instances = pointLightsInstances,
        name = "Point lights"
    ).apply {
        isCastingShadow = false
        generate {
            uvSphere {
                radius = 0.15f
            }

            val ico = MeshBuilder.IcoGenerator()
            ico.subdivide(1)

            ico.verts.forEachIndexed { i, pt ->
                val o = if (pt.dot(Vec3f.Y_AXIS) > 0.9f) Vec3f.X_AXIS else Vec3f.Y_AXIS
                val n1 = pt.cross(o, MutableVec3f())
                val n2 = n1.rotate(90f.deg, pt, MutableVec3f())

                val l0 = if (i % 2 == 0) 0f else 0.45f
                val l1 = if (i % 2 == 0) 0.9f else 0.9f

                line3d(MutableVec3f(pt).mul(l0), MutableVec3f(pt).mul(l1), n1, 0.03f)
                line3d(MutableVec3f(pt).mul(l0), MutableVec3f(pt).mul(l1), n2, 0.03f)
            }
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { instanceColor(Attribute.COLORS) }
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB
        }
    }

    private val cameraMesh = Mesh(
        Attribute.POSITIONS, Attribute.NORMALS,
        instances = cameraInstances,
        name = "Cameras"
    ).apply {
        isCastingShadow = false
        generate {
            rotate(90f.deg, Vec3f.Y_AXIS)
            generateArrow()

            cube {
                size.set(0.4f, 0.3f, 0.2f)
            }

            val pts = listOf(
                Vec3f(1f, 0.6f, 1f),
                Vec3f(1f, 0.6f, -1f),
                Vec3f(1f, -0.6f, -1f),
                Vec3f(1f, -0.6f, 1f),
            )

            pts.forEachIndexed { i, pt ->
                line3d(Vec3f.ZERO, pt, Vec3f.Y_AXIS, lineW)
                line3d(Vec3f.ZERO, pt, Vec3f.Z_AXIS, lineW)

                val p2 = pts[(i + 1) % 4]
                line3d(pt, p2, Vec3f.X_AXIS, lineW)
                line3d(pt, p2, if (pt.y == p2.y) Vec3f.Y_AXIS else Vec3f.Z_AXIS, lineW)
            }
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { instanceColor(Attribute.COLORS) }
        }
    }

    private val groupMesh = Mesh(
        Attribute.POSITIONS, Attribute.COLORS, Attribute.NORMALS,
        instances = groupInstances,
        name = "Groups"
    ).apply {
        isCastingShadow = false
        generate {
            color = MdColor.RED
            line3d(Vec3f.ZERO, Vec3f.X_AXIS, Vec3f.Y_AXIS, lineW)
            line3d(Vec3f.ZERO, Vec3f.X_AXIS, Vec3f.Z_AXIS, lineW)
            color = MdColor.GREEN
            line3d(Vec3f.ZERO, Vec3f.Y_AXIS, Vec3f.X_AXIS, lineW)
            line3d(Vec3f.ZERO, Vec3f.Y_AXIS, Vec3f.Z_AXIS, lineW)
            color = MdColor.BLUE
            line3d(Vec3f.ZERO, Vec3f.Z_AXIS, Vec3f.Y_AXIS, lineW)
            line3d(Vec3f.ZERO, Vec3f.Z_AXIS, Vec3f.X_AXIS, lineW)
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color {
                vertexColor()
            }
        }
    }

    init {
        addNode(spotLightMesh)
        addNode(pointLightMesh)
        addNode(dirLightMesh)

        addNode(cameraMesh)
        addNode(groupMesh)

        onUpdate {
            updateLightInstances()
            updateCameraInstances()
            updateGroupInstances()
        }
    }

    private fun updateLightInstances() {
        dirLightsInstances.clear()
        spotLightsInstances.clear()
        pointLightsInstances.clear()

        lights.forEach { lightInst ->
            val light = lightInst.component.light
            val instances = when (light) {
                is Light.Directional -> dirLightsInstances
                is Light.Point -> pointLightsInstances
                is Light.Spot -> spotLightsInstances
            }

            instances.addInstance {
                light.modelMatF.putTo(this)
                light.color.putTo(this)
            }
        }
    }

    private fun updateCameraInstances() {
        cameraInstances.clear()
        cameraInstances.addInstances(cameras.size) { buf ->
            cameras.forEach { camInst ->
                val cam = camInst.component
                val isActive = cam.sceneModel.cameraState.value == cam
                val color = if (isActive) MdColor.GREY tone 300 else MdColor.GREY tone 700

                cam.nodeModel.drawNode.modelMatF.putTo(buf)
                color.putTo(buf)
            }
        }
    }

    private fun updateGroupInstances() {
        groupInstances.clear()
        groupInstances.addInstances(groups.size) { buf ->
            groups.forEach {
                it.nodeModel.drawNode.modelMatF.putTo(buf)
                Color.WHITE.putTo(buf)
            }
        }
    }

    private fun MeshBuilder.generateArrow() {
        line3d(Vec3f.ZERO, Vec3f(1.5f, 0f, 0f), Vec3f.Z_AXIS, lineW)
        line3d(Vec3f.ZERO, Vec3f(1.5f, 0f, 0f), Vec3f.Y_AXIS, lineW)
        withTransform {
            rotate((-90f).deg, Vec3f.Z_AXIS)
            translate(0f, 1.6f, 0f)
            cylinder {
                bottomRadius = 0.15f
                topRadius = 0f
                height = 0.2f
                topFill = false
            }
        }
    }

    fun updateOverlayInstances() {
        cameras.clear()
        lights.clear()
        groups.clear()

        val sceneModel = KoolEditor.instance.activeScene.value ?: return
        sceneModel.project.getComponentsInScene<CameraComponent>(sceneModel)
            .filter { it.nodeModel.isVisibleState.value }
            .forEach { cameras += CameraComponentInstance(it) }
        sceneModel.project.getComponentsInScene<DiscreteLightComponent>(sceneModel)
            .filter { it.nodeModel.isVisibleState.value }
            .forEach { lights += LightComponentInstance(it) }
        sceneModel.nodeModels.values.filter { it.components.none { c -> c is ContentComponent } }
            .filter { it.isVisibleState.value }
            .forEach { groups += GroupNodeInstance(it) }
    }

    fun pick(rayTest: RayTest): SceneNodeModel? {
        return null
    }

    companion object {
        const val lineW = 0.06f
    }

    private data class LightComponentInstance(val component: DiscreteLightComponent)
    private data class CameraComponentInstance(val component: CameraComponent)
    private data class GroupNodeInstance(val nodeModel: SceneNodeModel)
}