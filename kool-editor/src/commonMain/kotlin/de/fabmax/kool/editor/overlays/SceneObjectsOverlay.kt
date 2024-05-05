package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.components.ContentComponent
import de.fabmax.kool.editor.components.DiscreteLightComponent
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.MdColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SceneObjectsOverlay : Node("Scene objects overlay") {

    private val dirLights = mutableListOf<DirLightComponentInstance>()
    private val spotLights = mutableListOf<SpotLightComponentInstance>()
    private val pointLights = mutableListOf<PointLightComponentInstance>()
    private val cameras = mutableListOf<CameraComponentInstance>()
    private val groups = mutableListOf<GroupNodeInstance>()

    private val dirLightsInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val spotLightsInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val pointLightsInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val cameraInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))
    private val groupInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.INSTANCE_COLOR))

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
            color { instanceColor() }
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
            color { instanceColor() }
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
            color { instanceColor() }
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
            color { instanceColor() }
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB
        }
    }

    private val groupMesh = Mesh(
        Attribute.POSITIONS, Attribute.COLORS, Attribute.NORMALS,
        instances = groupInstances,
        name = "Groups"
    ).apply {
        isCastingShadow = false
        generate {
            color = MdColor.RED toneLin 200
            line3d(Vec3f.ZERO, Vec3f.X_AXIS, Vec3f.Y_AXIS, lineW)
            line3d(Vec3f.ZERO, Vec3f.X_AXIS, Vec3f.Z_AXIS, lineW)
            color = MdColor.GREEN toneLin 200
            line3d(Vec3f.ZERO, Vec3f.Y_AXIS, Vec3f.X_AXIS, lineW)
            line3d(Vec3f.ZERO, Vec3f.Y_AXIS, Vec3f.Z_AXIS, lineW)
            color = MdColor.BLUE toneLin 200
            line3d(Vec3f.ZERO, Vec3f.Z_AXIS, Vec3f.Y_AXIS, lineW)
            line3d(Vec3f.ZERO, Vec3f.Z_AXIS, Vec3f.X_AXIS, lineW)
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color {
                instanceColor()
                vertexColor(blendMode = ColorBlockConfig.BlendMode.Multiply)
            }
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB
        }
    }

    init {
        addNode(pointLightMesh)
        addNode(spotLightMesh)
        addNode(dirLightMesh)
        addNode(cameraMesh)
        addNode(groupMesh)

        onUpdate {
            updateOverlayInstances()
        }
    }

    private fun updateOverlayInstances() {
        groupInstances.clear()
        cameraInstances.clear()
        dirLightsInstances.clear()
        spotLightsInstances.clear()
        pointLightsInstances.clear()

        groupInstances.addInstances(groups.size) { buf -> groups.forEach { it.addInstance(buf) } }
        cameraInstances.addInstances(cameras.size) { buf -> cameras.forEach { it.addInstance(buf) } }
        dirLightsInstances.addInstances(dirLights.size) { buf -> dirLights.forEach { it.addInstance(buf) } }
        spotLightsInstances.addInstances(spotLights.size) { buf -> spotLights.forEach { it.addInstance(buf) } }
        pointLightsInstances.addInstances(pointLights.size) { buf -> pointLights.forEach { it.addInstance(buf) } }
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

    fun updateOverlayObjects() {
        cameras.clear()
        groups.clear()
        dirLights.clear()
        spotLights.clear()
        pointLights.clear()

        val sceneModel = KoolEditor.instance.activeScene.value ?: return
        sceneModel.project.getComponentsInScene<CameraComponent>(sceneModel)
            .filter { it.nodeModel.isVisibleState.value }
            .forEach { cameras += CameraComponentInstance(it) }
        sceneModel.nodeModels.values.filter { it.components.none { c -> c is ContentComponent } }
            .filter { it.isVisibleState.value }
            .forEach { groups += GroupNodeInstance(it) }
        sceneModel.project.getComponentsInScene<DiscreteLightComponent>(sceneModel)
            .filter { it.nodeModel.isVisibleState.value }
            .forEach {
                when (it.light) {
                    is Light.Directional -> dirLights += DirLightComponentInstance(it)
                    is Light.Point -> pointLights += PointLightComponentInstance(it)
                    is Light.Spot -> spotLights += SpotLightComponentInstance(it)
                }
            }
    }

    fun pick(rayTest: RayTest): SceneNodeModel? {
        var closest: SceneNodeModel? = null
        cameras.forEach { if (it.rayTest(rayTest)) { closest = it.nodeModel } }
        groups.forEach { if (it.rayTest(rayTest)) { closest = it.nodeModel } }
        dirLights.forEach { if (it.rayTest(rayTest)) { closest = it.nodeModel } }
        spotLights.forEach { if (it.rayTest(rayTest)) { closest = it.nodeModel } }
        pointLights.forEach { if (it.rayTest(rayTest)) { closest = it.nodeModel } }
        return closest
    }

    companion object {
        const val lineW = 0.06f
    }

    private abstract class OverlayObject(val nodeModel: SceneNodeModel, val mesh: Mesh) {

        abstract val modelMat: Mat4f
        abstract val color: Color

        val radius = mesh.geometry.bounds.size.length() * 0.5f

        fun addInstance(target: Float32Buffer) {
            val selectionOv = KoolEditor.instance.selectionOverlay
            val color = if (selectionOv.isSelected(nodeModel)) selectionOv.selectionColor.toLinear() else color
            modelMat.putTo(target)
            color.putTo(target)
        }

        fun rayTest(rayTest: RayTest): Boolean {
            val pos = modelMat.getTranslation()
            val n = pos.nearestPointOnRay(rayTest.ray.origin, rayTest.ray.direction, MutableVec3f())
            if (n.distance(pos) < radius) {
                val d = n.sqrDistance(rayTest.ray.origin)
                if (d < rayTest.hitDistanceSqr) {
                    rayTest.setHit(mesh, d)
                    return true
                }
            }
            return false
        }
    }

    private inner class PointLightComponentInstance(val component: DiscreteLightComponent) :
        OverlayObject(component.nodeModel, pointLightMesh)
    {
        override val modelMat: Mat4f get() = component.light.modelMatF
        override val color: Color get() = component.light.color
    }

    private inner class SpotLightComponentInstance(val component: DiscreteLightComponent) :
        OverlayObject(component.nodeModel, spotLightMesh)
    {
        override val modelMat: Mat4f get() = component.light.modelMatF
        override val color: Color get() = component.light.color
    }

    private inner class DirLightComponentInstance(val component: DiscreteLightComponent) :
        OverlayObject(component.nodeModel, dirLightMesh)
    {
        override val modelMat: Mat4f get() = component.light.modelMatF
        override val color: Color get() = component.light.color
    }

    private inner class CameraComponentInstance(val component: CameraComponent) :
        OverlayObject(component.nodeModel, cameraMesh)
    {
        private val activeColor = MdColor.GREY toneLin 300
        private val inactiveColor = MdColor.GREY toneLin 700

        override val modelMat: Mat4f get() = nodeModel.drawNode.modelMatF
        override val color: Color get() {
            val isActive = component.sceneModel.cameraState.value == component
            return if (isActive) activeColor else inactiveColor
        }
    }

    private inner class GroupNodeInstance(nodeModel: SceneNodeModel) :
        OverlayObject(nodeModel, groupMesh)
    {
        override val modelMat: Mat4f get() = nodeModel.drawNode.modelMatF
        override val color: Color = Color.WHITE
    }
}