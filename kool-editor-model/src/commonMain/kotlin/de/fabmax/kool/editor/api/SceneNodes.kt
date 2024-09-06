package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.components.isDefaultMaterial
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.toMat4f
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.*
import kotlinx.atomicfu.atomic
import kotlin.math.abs

class SceneNodes(val scene: EditorScene) :
    EditorScene.SceneShaderDataListener,
    MaterialComponent.ListenerComponent
{
    private val sceneNode: Scene get() = scene.sceneComponent.sceneNode
    private val sceneNodes = mutableMapOf<MeshKey, DrawNodeAndUsers>()

    suspend fun useNode(key: MeshKey, user: MeshComponent): Node? {
        var meshUsers = sceneNodes[key]
        if (meshUsers == null) {
            meshUsers = when {
                key.shapes.all { it.isPrimitiveShape } -> MeshNode(key).apply { createNode() }
                key.shapes.any { it is ShapeData.Heightmap } -> MeshNode(key).apply { createNode() }
                key.shapes.any { it is ShapeData.Model } -> ModelNode(key).apply { createNode() }
                else -> null
            }
            meshUsers?.let {
                logT { "Created new scene mesh ${it.node?.name}" }
                sceneNodes[key] = it
            }
        }

        return meshUsers?.let {
            it.users += user
            logT { "Added user to scene mesh ${it.node?.name}, now has: ${it.users.size} users" }
            it.node
        }
    }

    fun removeUser(key: MeshKey, user: MeshComponent) {
        val meshUsers = sceneNodes[key]
        meshUsers?.let {
            it.users -= user
            logT { "Removed user from scene mesh ${it.node?.name}, now has: ${it.users.size} users" }
            if (it.users.isEmpty()) {
                it.node?.let { node ->
                    sceneNode.removeNode(node)
                    node.release()
                }
                sceneNodes -= key
                logT { "Released scene mesh ${it.node?.name}: No more active users" }
            }
        }
    }

    fun updateInstances() {
        sceneNodes.values.forEach { it.updateInstances() }
    }

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
        sceneNodes.values.forEach {
            it.onSceneShaderDataChanged(scene, sceneShaderData)
        }
    }

    override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
        sceneNodes.values
            .filter { it.meshKey.isUsingMaterial(component) }
            .forEach { it.onMaterialChanged(component, materialData) }
    }

    companion object {
        private const val DEFAULT_HEIGHTMAP_ROWS = 129
        private const val DEFAULT_HEIGHTMAP_COLS = 129

        private val modelMatsInstancedMesh = listOf(ModelMatrixComposition.INSTANCE_MODEL_MAT)
        private val modelMatsInstancedModel = listOf(ModelMatrixComposition.INSTANCE_MODEL_MAT, ModelMatrixComposition.UNIFORM_MODEL_MAT)
    }

    data class MeshKey(
        val shapes: List<ShapeData>,
        val material: EntityId,
        val drawGroupId: Int,
        val exclusiveEntity: EntityId = EntityId.NULL
    )

    fun MeshKey.isUsingMaterial(materialComponent: MaterialComponent): Boolean {
        return material == materialComponent.id || (material == EntityId.NULL && materialComponent.isDefaultMaterial())
    }

    private abstract class DrawNodeAndUsers(val meshKey: MeshKey) :
        EditorScene.SceneShaderDataListener,
        MaterialComponent.ListenerComponent
    {
        abstract val node: Node?
        val users: MutableList<MeshComponent> = mutableListOf()

        abstract fun updateInstances()

        abstract suspend fun createNode()
    }

    private inner class MeshNode(meshKey: MeshKey) : DrawNodeAndUsers(meshKey) {
        override var node: Mesh? = null

        override fun updateInstances() {
            node?.instances?.apply {
                clear()
                addInstancesUpTo(users.size) { buf ->
                    var added = 0
                    for (i in users.indices) {
                        val meshComponent = users[i]
                        if (meshComponent.gameEntity.isVisible) {
                            meshComponent.addInstanceData(buf)
                            added++
                        }
                    }
                    added
                }
            }
        }

        override suspend fun createNode() {
            val isInstanced = meshKey.exclusiveEntity == EntityId.NULL
            val instances = if (isInstanced) MeshInstanceList(100, Attribute.INSTANCE_MODEL_MAT) else null
            val attributes = listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)
            val isHeightmap = meshKey.shapes.any { it is ShapeData.Heightmap }

            node = Mesh(attributes, instances).apply {
                sceneNode.addNode(this)
                isFrustumChecked = false
                drawGroupId = meshKey.drawGroupId

                if (isHeightmap) {
                    createHeightmap()
                } else {
                    createPrimitive()
                }

                val material = scene.project.materialsById[meshKey.material] ?: scene.project.defaultMaterial
                material?.applyMaterialTo(this, scene.shaderData, modelMatsInstancedMesh)
                if (AppState.isInEditor) {
                    rayTest = MeshRayTest.geometryTest(this)
                }
            }
        }

        private fun Mesh.createPrimitive() {
            name = "PrimitiveMesh[${(meshKey.hashCode()).toHexString()}]"
            generate {
                meshKey.shapes.forEach { generatePrimitiveShape(it) }
            }
            geometry.generateTangents()
        }

        private suspend fun Mesh.createHeightmap() {
            val shape = meshKey.shapes.filterIsInstance<ShapeData.Heightmap>().first()
            name = "Heightmap:${shape.mapPath}[${(meshKey.hashCode()).toHexString()}]"

            val heightmap: Heightmap? = shape.toAssetRef()?.let { AppAssets.loadHeightmapOrNull(it) }
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
            geometry.generateTangents()
        }

        override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) { }

        override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
            node?.let { component.applyMaterialTo(it, scene.shaderData, modelMatsInstancedMesh) }
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

        private fun MeshBuilder.applyCommon(pose: PoseData? = null, shapeColor: ColorData? = null, uvScale: Vec2Data? = null) {
            pose?.toPoseF()?.toMat4f(transform)
            when (scene.upAxis) {
                SceneUpAxis.X_AXIS -> rotate(90f.deg, Vec3f.NEG_Z_AXIS)
                SceneUpAxis.Y_AXIS -> { }
                SceneUpAxis.Z_AXIS -> rotate(90f.deg, Vec3f.X_AXIS)
            }

            shapeColor?.let { color = it.toColorLinear() }
            uvScale?.let { scale ->
                vertexModFun = {
                    texCoord.x *= scale.x.toFloat()
                    texCoord.y *= scale.y.toFloat()
                }
            }
        }
    }

    private inner class ModelNode(meshKey: MeshKey) : DrawNodeAndUsers(meshKey) {
        override var node: Model? = null
        private val isRecreatingModel = atomic(false)

        private val isManagedMaterial = meshKey.material != EntityId.NULL
        private val modelInstances = mutableListOf<MeshInstanceList>()

        private var isIblShaded = false
        private var isSsaoEnabled = false
        private var maxNumLights = 4
        private var shaderShadowMaps: List<ShadowMap> = emptyList()

        override fun updateInstances() {
            for (i in modelInstances.indices) {
                modelInstances[i].apply {
                    clear()
                    addInstancesUpTo(users.size) { buf ->
                        var added = 0
                        for (j in users.indices) {
                            val meshComponent = users[j]
                            if (meshComponent.gameEntity.isVisible) {
                                meshComponent.addInstanceData(buf)
                                added++
                            }
                        }
                        added
                    }
                }
            }
        }

        override suspend fun createNode() {
            node?.let {
                sceneNode.removeNode(it)
                it.release()
            }

            val modelShape = meshKey.shapes.filterIsInstance<ShapeData.Model>().first()
            val modelRef = requireNotNull(modelShape.toAssetRef())

            val gltf = AppAssets.requireModel(modelRef)
            val shaderData = scene.shaderData
            shaderShadowMaps = shaderData.shadowMaps.toList()
            val ibl = shaderData.environmentMap
            val ssao = shaderData.ssaoMap
            val material = scene.project.materialsById[meshKey.material]
            val modelCfg = GltfLoadConfig(
                materialConfig = GltfMaterialConfig(
                    environmentMap = ibl,
                    shadowMaps = shaderShadowMaps,
                    scrSpcAmbientOcclusionMap = ssao,
                    maxNumberOfLights = shaderData.maxNumberOfLights,
                    modelMatrixComposition = modelMatsInstancedModel
                ),
                applyMaterials = material == null,
                assetLoader = AppAssets.assetLoader,
                addInstanceAttributes = listOf(Attribute.INSTANCE_MODEL_MAT)
            )
            isIblShaded = ibl != null
            isSsaoEnabled = ssao != null
            maxNumLights = shaderData.maxNumberOfLights

            val loadScene = if (modelShape.sceneIndex in gltf.scenes.indices) modelShape.sceneIndex else 0
            val model = gltf.makeModel(modelCfg, loadScene)
            model.name = "Model:${modelShape.modelPath}[${abs(meshKey.hashCode()).toHexString()}]"
            model.drawGroupId = meshKey.drawGroupId

            if (material != null) {
                model.meshes.values.forEach { material.applyMaterialTo(it, scene.shaderData, modelMatsInstancedModel) }
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
            modelInstances.clear()
            model.meshes.values.forEach { mesh ->
                mesh.isFrustumChecked = false
                if (AppState.isInEditor) {
                    mesh.rayTest = MeshRayTest.geometryTest(mesh)
                }
                mesh.instances?.let { modelInstances += it }
            }
            node = model
        }

        private fun recreateModelAsync() {
            if (!isRecreatingModel.getAndSet(true)) {
                launchOnMainThread {
                    createNode()
                    isRecreatingModel.lazySet(false)
                }
            }
        }

        override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
            if (isManagedMaterial) {
                return
            }

            val iblData = sceneShaderData.environmentMap
            val isIbl = iblData != null
            val isSsao = sceneShaderData.ssaoMap != null

            if (isIblShaded != isIbl ||
                maxNumLights != sceneShaderData.maxNumberOfLights ||
                shaderShadowMaps != sceneShaderData.shadowMaps ||
                isSsaoEnabled != isSsao
            ) {
                recreateModelAsync()

            } else {
                val meshes = node?.meshes ?: return
                meshes.values.forEach { mesh ->
                    (mesh.shader as? KslLitShader)?.apply {
                        ssaoMap = sceneShaderData.ssaoMap
                        if (iblData != null) {
                            ambientFactor = Color.WHITE
                            ambientMap = iblData.irradianceMap
                            (this as? KslPbrShader)?.reflectionMap = iblData.reflectionMap
                        } else {
                            ambientFactor = sceneShaderData.ambientColorLinear
                        }
                    }
                }
            }
        }

        override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
            val meshes = node?.meshes ?: return
            val updateFail = meshes.values.any { !component.applyMaterialTo(it, scene.shaderData, modelMatsInstancedModel) }
            if (updateFail) {
                createNode()
            }
        }
    }
}