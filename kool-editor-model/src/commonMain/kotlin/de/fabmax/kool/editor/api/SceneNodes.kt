package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.Heightmap
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logT
import kotlin.math.abs

class SceneNodes(val scene: EditorScene) {

    private val sceneNode: Scene get() = scene.sceneComponent.drawNode
    private val sceneNodes = mutableMapOf<MeshKey, DrawNodeAndUsers>()

    suspend fun useNode(key: MeshKey, user: MeshComponent): Node? {
        var meshUsers = sceneNodes[key]
        if (meshUsers == null) {
            meshUsers = when {
                key.shapes.all { it.isPrimitiveShape } -> createPrimitiveMesh(key)
                key.shapes.any { it is ShapeData.Heightmap } -> createHeightmapMesh(key)
                key.shapes.any { it is ShapeData.Model } -> createModel(key)
                else -> null
            }
            meshUsers?.let {
                logD { "Created new scene mesh ${it.node.name}" }
                sceneNodes[key] = it
            }
        }

        return meshUsers?.let {
            it.users += user
            logT { "Added user to scene mesh ${it.node.name}, now has: ${it.users.size} users" }
            it.node
        }
    }

    fun removeUser(key: MeshKey, user: MeshComponent) {
        val meshUsers = sceneNodes[key]
        meshUsers?.let {
            it.users -= user
            logT { "Removed user from scene mesh ${it.node.name}, now has: ${it.users.size} users" }
            if (it.users.isEmpty()) {
                sceneNode.removeNode(it.node)
                it.node.release()
                sceneNodes -= key
                logD { "Released scene mesh ${it.node.name}: No more active users" }
            }
        }
    }

    fun updateInstances() {
        sceneNodes.values.forEach { it.updateInstances() }
    }

    private suspend fun createPrimitiveMesh(meshKey: MeshKey): DrawNodeAndUsers {
        return createMeshNode(meshKey).apply {
            mesh.name = "PrimitiveMesh[${(meshKey.hashCode()).toHexString()}]"
            mesh.generate {
                meshKey.shapes.forEach { generatePrimitiveShape(it) }
            }
            mesh.geometry.generateTangents()
        }
    }

    private suspend fun createHeightmapMesh(meshKey: MeshKey): DrawNodeAndUsers {
        return createMeshNode(meshKey).apply {
            val heightmapShape = meshKey.shapes.filterIsInstance<ShapeData.Heightmap>().first()
            mesh.name = "Heightmap:${heightmapShape.mapPath}[${(meshKey.hashCode()).toHexString()}]"
            mesh.createHeightmapMesh(heightmapShape)
            mesh.geometry.generateTangents()
        }
    }

    private suspend fun createModel(meshKey: MeshKey): DrawNodeAndUsers? {
        val modelShape = meshKey.shapes.filterIsInstance<ShapeData.Model>().first()
        val modelRef = modelShape.toAssetRef()
        if (modelRef.path == null) {
            return null
        }

        val gltfFile = AppAssets.loadModel(modelRef)
        val shaderData = scene.shaderData
        val shaderShadowMaps = shaderData.shadowMaps.toList()
        val ibl = shaderData.environmentMaps
        val ssao = shaderData.ssaoMap
        val material = scene.project.materialsById[meshKey.material]
        val modelCfg = GltfLoadConfig(
            materialConfig = GltfMaterialConfig(
                environmentMaps = ibl,
                shadowMaps = shaderShadowMaps,
                scrSpcAmbientOcclusionMap = ssao,
                maxNumberOfLights = shaderData.maxNumberOfLights
            ),
            applyMaterials = material == null,
            assetLoader = AppAssets.assetLoader,
            addInstanceAttributes = listOf(Attribute.INSTANCE_MODEL_MAT)
        )
        val isIblShaded = ibl != null
//        val isSsaoEnabled = ssao != null
//        val maxNumLights = shaderData.maxNumberOfLights

        val gltf = gltfFile ?: return null
        val loadScene = if (modelShape.sceneIndex in gltf.scenes.indices) modelShape.sceneIndex else 0
        val model = gltf.makeModel(modelCfg, loadScene)
        model.name = "Model:${modelShape.modelPath}[${abs(meshKey.hashCode()).toHexString()}]"

        if (material != null) {
            model.meshes.values.forEach { material.applyMaterialTo(scene, it) }
        }
        if (!isIblShaded) {
            val bgColor = shaderData.ambientColorLinear
            model.meshes.values.forEach { mesh ->
                (mesh.shader as? KslLitShader)?.ambientFactor = bgColor
            }
        }
        if (modelShape.animationIndex >= 0) {
            model.animations.getOrNull(modelShape.animationIndex)?.weight = 1f
            model.onUpdate {
                model.applyAnimation(Time.deltaT)
            }
        }

        sceneNode.addNode(model)
        model.meshes.values.forEach {
            it.isFrustumChecked = false
            it.rayTest = MeshRayTest.geometryTest(it)
            if (AppState.isInEditor) {
                it.rayTest = MeshRayTest.geometryTest(it)
            }
        }
        return DrawNodeAndUsers.ModelNode(model)
    }

    private suspend fun createMeshNode(meshKey: MeshKey): DrawNodeAndUsers.MeshNode {
        val isInstanced = meshKey.exclusiveEntity == EntityId.NULL
        val instances = if (isInstanced) MeshInstanceList(100, Attribute.INSTANCE_MODEL_MAT) else null
        val attributes = listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)

        val mesh = Mesh(attributes, instances).apply {
            sceneNode.addNode(this)
            isFrustumChecked = false

            val material = scene.project.materialsById[meshKey.material] ?: scene.project.defaultMaterial
            material?.applyMaterialTo(scene, this)
            if (AppState.isInEditor) {
                rayTest = MeshRayTest.geometryTest(this)
            }
        }
        return DrawNodeAndUsers.MeshNode(mesh)
    }

    private fun MeshBuilder.generatePrimitiveShape(shape: ShapeData) = withTransform {
        when (shape) {
            is ShapeData.Box -> generateBox(shape)
            is ShapeData.Sphere -> generateSphere(shape)
            is ShapeData.Cylinder -> generateCylinder(shape)
            is ShapeData.Capsule -> generateCapsule(shape)
            is ShapeData.Rect -> generateRect(shape)
            else -> error("${shape.name} is not a primitive shape")
        }
    }

    private fun MeshBuilder.generateBox(shape: ShapeData.Box) {
        applyCommon(shape.pose, shape.color, shape.uvScale)
        cube { size.set(shape.size.toVec3f()) }
    }

    private fun MeshBuilder.generateSphere(shape: ShapeData.Sphere) {
        applyCommon(shape.pose, shape.color, shape.uvScale)
        if (shape.sphereType == "ico") {
            icoSphere {
                radius = shape.radius.toFloat()
                steps = shape.steps
            }
        } else {
            uvSphere {
                radius = shape.radius.toFloat()
                steps = shape.steps
            }
        }
    }

    private fun MeshBuilder.generateCylinder(shape: ShapeData.Cylinder) {
        applyCommon(shape.pose, shape.color, shape.uvScale)
        // cylinder is generated in x-axis major orientation to make it align with physics geometry
        rotate(90f.deg, Vec3f.Z_AXIS)
        cylinder {
            height = shape.length.toFloat()
            topRadius = shape.topRadius.toFloat()
            bottomRadius = shape.bottomRadius.toFloat()
            steps = shape.steps
        }
    }

    private fun MeshBuilder.generateCapsule(shape: ShapeData.Capsule) {
        applyCommon(shape.pose, shape.color, shape.uvScale)
        profile {
            val r = shape.radius.toFloat()
            val h = shape.length.toFloat()
            val hh = h / 2f
            simpleShape(false) {
                xyArc(Vec2f(hh + r, 0f), Vec2f(hh, 0f), 90f.deg, shape.steps / 2, true)
                xyArc(Vec2f(-hh, r), Vec2f(-hh, 0f), 90f.deg, shape.steps / 2, true)
            }
            for (i in 0 .. shape.steps) {
                sample()
                rotate(360f.deg / shape.steps, 0f.deg, 0f.deg)
            }
        }
    }

    private fun MeshBuilder.generateRect(shape: ShapeData.Rect) {
        applyCommon(shape.pose, shape.color, shape.uvScale)
        grid {
            sizeX = shape.size.x.toFloat()
            sizeY = shape.size.y.toFloat()
        }
    }

    private fun MeshBuilder.applyCommon(pose: TransformData? = null, shapeColor: ColorData? = null, uvScale: Vec2Data? = null) {
        pose?.toMat4f(transform)
        shapeColor?.let { color = it.toColorLinear() }
        uvScale?.let { scale ->
            vertexModFun = {
                texCoord.x *= scale.x.toFloat()
                texCoord.y *= scale.y.toFloat()
            }
        }
    }

    private suspend fun Mesh.createHeightmapMesh(shape: ShapeData.Heightmap) {
        val heightmap: Heightmap? = if (shape.mapPath == null) null else AppAssets.loadHeightmap(shape.toAssetRef())
        val rows = heightmap?.rows ?: DEFAULT_HEIGHTMAP_ROWS
        val cols = heightmap?.columns ?: DEFAULT_HEIGHTMAP_COLS
        val szX = (cols - 1) * shape.colScale.toFloat()
        val szY = (rows - 1) * shape.rowScale.toFloat()

        generate {
            applyCommon(uvScale = shape.uvScale)
            translate(szX * 0.5f, 0f, szY * 0.5f)
            grid {
                sizeX = szX
                sizeY = szY
                stepsX = cols
                stepsY = rows
                heightmap?.let { useHeightMap(it) }
            }
        }
    }

    companion object {
        const val DEFAULT_HEIGHTMAP_ROWS = 129
        const val DEFAULT_HEIGHTMAP_COLS = 129
    }

    data class MeshKey(val shapes: List<ShapeData>, val material: EntityId, val drawGroupId: Int, val exclusiveEntity: EntityId = EntityId.NULL)

    private sealed class DrawNodeAndUsers(val node: Node) {
        val users: MutableList<MeshComponent> = mutableListOf()

        abstract fun updateInstances()

        class MeshNode(val mesh: Mesh) : DrawNodeAndUsers(mesh) {
            override fun updateInstances() {
                mesh.instances?.apply {
                    clear()
                    addInstances(users.size) { buf ->
                        for (i in users.indices) {
                            users[i].addInstanceData(buf)
                        }
                    }
                }
            }
        }

        class ModelNode(model: Model) : DrawNodeAndUsers(model) {
            val modelInstances = model.meshes.values.mapNotNull { it.instances }

            override fun updateInstances() {
                for (i in modelInstances.indices) {
                    modelInstances[i].apply {
                        clear()
                        addInstances(users.size) { buf ->
                            for (j in users.indices) {
                                users[j].addInstanceData(buf)
                            }
                        }
                    }
                }
            }
        }
    }
}