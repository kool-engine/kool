package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.api.CachedSceneComponents
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SceneObjectsOverlay : Node("Scene objects overlay"), EditorOverlay {

    private var lightComponentCache: CachedSceneComponents<DiscreteLightComponent>? = null
    private val lightComponents: List<DiscreteLightComponent> get() = lightComponentCache?.getComponents() ?: emptyList()
    private var cameraComponentCache: CachedSceneComponents<CameraComponent>? = null
    private val cameraComponents: List<CameraComponent> get() = cameraComponentCache?.getComponents() ?: emptyList()
    private var groupComponentCache: CachedSceneComponents<TransformComponent>? = null
    private val groupComponents: List<TransformComponent> get() = groupComponentCache?.getComponents() ?: emptyList()

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
        rayTest = MeshRayTest.geometryTest(this)
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
            colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
        }
    }

    private val spotLightMesh = Mesh(
        Attribute.POSITIONS, Attribute.NORMALS,
        instances = spotLightsInstances,
        name = "Spot lights"
    ).apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
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
            colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
        }
    }

    private val pointLightMesh = Mesh(
        Attribute.POSITIONS, Attribute.NORMALS,
        instances = pointLightsInstances,
        name = "Point lights"
    ).apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
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
            colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
        }
    }

    private val cameraMesh = Mesh(
        Attribute.POSITIONS, Attribute.NORMALS,
        instances = cameraInstances,
        name = "Cameras"
    ).apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
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
            colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
        }
    }

    private val groupMesh = Mesh(
        Attribute.POSITIONS, Attribute.COLORS, Attribute.NORMALS,
        instances = groupInstances,
        name = "Groups"
    ).apply {
        isCastingShadow = false
        rayTest = MeshRayTest.geometryTest(this)
        generate {
            color = MdColor.RED toneLin 200
            line3d(Vec3f.NEG_X_AXIS * 0.5f, Vec3f.X_AXIS * 0.5f, Vec3f.Y_AXIS, lineW)
            line3d(Vec3f.NEG_X_AXIS * 0.5f, Vec3f.X_AXIS * 0.5f, Vec3f.Z_AXIS, lineW)
            color = MdColor.GREEN toneLin 200
            line3d(Vec3f.NEG_Y_AXIS * 0.5f, Vec3f.Y_AXIS * 0.5f, Vec3f.X_AXIS, lineW)
            line3d(Vec3f.NEG_Y_AXIS * 0.5f, Vec3f.Y_AXIS * 0.5f, Vec3f.Z_AXIS, lineW)
            color = MdColor.BLUE toneLin 200
            line3d(Vec3f.NEG_Z_AXIS * 0.5f, Vec3f.Z_AXIS * 0.5f, Vec3f.Y_AXIS, lineW)
            line3d(Vec3f.NEG_Z_AXIS * 0.5f, Vec3f.Z_AXIS * 0.5f, Vec3f.X_AXIS, lineW)
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { instanceColor() }
            colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
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
        if (lightComponentCache?.isOutdated == true) {
            dirLights.clear()
            spotLights.clear()
            pointLights.clear()

            lightComponents
                .filter { it.gameEntity.isVisible }
                .forEach {
                    when (it.light) {
                        is Light.Directional -> dirLights += DirLightComponentInstance(it)
                        is Light.Point -> pointLights += PointLightComponentInstance(it)
                        is Light.Spot -> spotLights += SpotLightComponentInstance(it)
                    }
                }
        }
        if (cameraComponentCache?.isOutdated == true) {
            cameras.clear()
            cameras += cameraComponents.map { CameraComponentInstance(it) }
        }
        if (groupComponentCache?.isOutdated == true) {
            groups.clear()
            groups += groupComponents
                .filter {
                    it.gameEntity.isSceneChild && it.gameEntity.components.none { c ->
                        c is SceneNodeComponent || c is PhysicsComponent
                    }
                }
                .map { GroupNodeInstance(it.gameEntity) }
        }

        dirLightsInstances.addInstances(dirLights)
        spotLightsInstances.addInstances(spotLights)
        pointLightsInstances.addInstances(pointLights)
        cameraInstances.addInstances(cameras)
        groupInstances.addInstances(groups)
    }

    private fun MeshInstanceList.addInstances(objs: List<OverlayObject>) {
        clear()
        addInstancesUpTo(objs.size) { buf ->
            var addCount = 0
            for (i in objs.indices) {
                if (objs[i].gameEntity.isVisible) {
                    objs[i].addInstance(buf)
                    addCount++
                }
            }
            addCount
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

    override fun onEditorSceneChanged(scene: EditorScene) {
        lightComponentCache = CachedSceneComponents(scene, DiscreteLightComponent::class)
        cameraComponentCache = CachedSceneComponents(scene, CameraComponent::class)
        groupComponentCache = CachedSceneComponents(scene, TransformComponent::class)
    }

    override fun pick(rayTest: RayTest): GameEntity? {
        var closest: GameEntity? = null
        cameras.forEach { if (it.rayTest(rayTest)) { closest = it.gameEntity } }
        groups.forEach { if (it.rayTest(rayTest)) { closest = it.gameEntity } }
        dirLights.forEach { if (it.rayTest(rayTest)) { closest = it.gameEntity } }
        spotLights.forEach { if (it.rayTest(rayTest)) { closest = it.gameEntity } }
        pointLights.forEach { if (it.rayTest(rayTest)) { closest = it.gameEntity } }
        return closest
    }

    companion object {
        const val lineW = 0.06f
    }

    private inner class PointLightComponentInstance(val component: DiscreteLightComponent) : OverlayObject(component.gameEntity) {
        override val color: Color get() = component.light.color
        fun rayTest(rayTest: RayTest) = super.rayTest(rayTest, pointLightMesh)
    }

    private inner class SpotLightComponentInstance(val component: DiscreteLightComponent) : OverlayObject(component.gameEntity) {
        override val color: Color get() = component.light.color
        fun rayTest(rayTest: RayTest) = super.rayTest(rayTest, spotLightMesh)
    }

    private inner class DirLightComponentInstance(val component: DiscreteLightComponent) : OverlayObject(component.gameEntity) {
        override val color: Color get() = component.light.color
        fun rayTest(rayTest: RayTest) = super.rayTest(rayTest, dirLightMesh)
    }

    private inner class CameraComponentInstance(val component: CameraComponent) : OverlayObject(component.gameEntity) {
        private val activeColor = MdColor.GREY toneLin 300
        private val inactiveColor = MdColor.GREY toneLin 700

        override val color: Color get() {
            val isActive = component.sceneComponent.cameraComponent == component
            return if (isActive) activeColor else inactiveColor
        }
        fun rayTest(rayTest: RayTest) = super.rayTest(rayTest, cameraMesh)
    }

    private inner class GroupNodeInstance(gameEntity: GameEntity) : OverlayObject(gameEntity) {
        override val color: Color = Color.WHITE
        fun rayTest(rayTest: RayTest) = super.rayTest(rayTest, groupMesh)
    }
}