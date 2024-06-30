package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.*
import kotlinx.atomicfu.atomic

class MeshComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<MeshComponentData> = ComponentInfo(MeshComponentData(ShapeData.Box()))
) :
    GameEntityDataComponent<MeshComponentData>(gameEntity, componentInfo),
    DrawNodeComponent,
    MaterialComponent.ListenerComponent,
    MaterialReferenceComponent.ListenerComponent,
    EditorScene.SceneShaderDataListener
{
    private val meshHolder = MeshHolder()
    private val modelHolder = ModelHolder()

    override var drawNode: Node? = null
        private set

    val drawNodeMesh: Mesh? get() = meshHolder.mesh
    val drawNodeModel: Model? get() = modelHolder.model

    val gltfFile: GltfFile? get() = modelHolder.gltfFile

    private val listeners by cachedEntityComponents<ListenerComponent>()

    init {
        dependsOn(MaterialReferenceComponent::class, isOptional = true)

        val modelShape = data.shapes.find { it is ShapeData.Model } as ShapeData.Model?
        val heightMapShape = data.shapes.find { it is ShapeData.Heightmap } as ShapeData.Heightmap?
        if (modelShape != null) {
            requiredAssets += modelShape.toAssetReference()
        } else if (heightMapShape != null) {
            requiredAssets += heightMapShape.toAssetReference()
        }
    }

    override fun onDataChanged(oldData: MeshComponentData, newData: MeshComponentData) {
        launchOnMainThread {
            if (oldData.shapes != newData.shapes) {
                updateDrawNode(newData)
            }
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        updateDrawNode(data)
    }

    override fun destroyComponent() {
        gameEntity.replaceDrawNode(Node(gameEntity.name))
        meshHolder.release()
        modelHolder.release()
        drawNode = null
        super.destroyComponent()
    }

    private suspend fun updateDrawNode(data: MeshComponentData) {
        requiredAssets.clear()

        val modelShape = data.shapes.find { it is ShapeData.Model } as ShapeData.Model?
        val heightMapShape = data.shapes.find { it is ShapeData.Heightmap } as ShapeData.Heightmap?
        val customShape = data.shapes.find { it is ShapeData.Custom } as ShapeData.Custom?

        val node = when {
            modelShape != null -> modelHolder.updateModel(modelShape)
            heightMapShape != null -> meshHolder.updateHeightMap(heightMapShape)
            customShape != null -> meshHolder.updateCustom()
            else -> meshHolder.updatePrimitive(data)
        }

        if (node == modelHolder.model) {
            meshHolder.release()
        } else if (node == meshHolder.mesh) {
            modelHolder.release()
        }

        if (node != drawNode) {
            drawNode?.release()
            drawNode = node
            gameEntity.replaceDrawNode(node ?: Node())
        }
        listeners.forEach { it.onMeshGeometryChanged(this, data) }
    }

    override fun onMaterialReferenceChanged(component: MaterialReferenceComponent, material: MaterialComponent?) {
        meshHolder.onMaterialReferenceChanged(component, material)
        modelHolder.onMaterialReferenceChanged(component, material)
    }

    override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
        val holder = gameEntity.getComponent<MaterialReferenceComponent>() ?: return
        if (holder.isHoldingMaterial(component)) {
            meshHolder.onMaterialChanged(component, materialData)
            modelHolder.onMaterialChanged(component, materialData)
        }
    }

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
        modelHolder.onSceneShaderDataChanged(scene, sceneShaderData)
    }

    companion object {
        const val DEFAULT_HEIGHTMAP_ROWS = 129
        const val DEFAULT_HEIGHTMAP_COLS = 129
    }

    interface ListenerComponent {
        suspend fun onMeshGeometryChanged(component: MeshComponent, newData: MeshComponentData)
    }

    private inner class MeshHolder :
        MaterialComponent.ListenerComponent,
        MaterialReferenceComponent.ListenerComponent
    {
        var mesh: Mesh? = null

        fun release() {
            mesh?.release()
            mesh = null
        }

        suspend fun updatePrimitive(data: MeshComponentData): Mesh {
            val mesh = getOrCreateMeshNode()
            mesh.generate {
                data.shapes.forEach { shape -> generateShape(shape) }
                geometry.generateTangents()
            }
            mesh.rayTest.onMeshDataChanged(mesh)
            return mesh
        }

        suspend fun updateHeightMap(data: ShapeData.Heightmap): Mesh {
            requiredAssets += data.toAssetReference()

            val mesh = getOrCreateMeshNode()
            mesh.generateHeightmap(data)
            return mesh
        }

        suspend fun updateCustom(): Mesh {
            return getOrCreateMeshNode()
        }

        suspend fun getOrCreateMeshNode(): Mesh {
            if (mesh == null) {
                mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS).apply {
                    val material = gameEntity.getComponent<MaterialReferenceComponent>()?.material ?: project.defaultMaterial
                    material?.applyMaterialTo(gameEntity, this)
                    if (AppState.isInEditor) {
                        rayTest = MeshRayTest.geometryTest(this)
                    }
                }
            }
            return mesh!!
        }

        fun MeshBuilder.generateShape(shape: ShapeData) = withTransform {
            applyCommon(shape.common)

            when (shape) {
                is ShapeData.Box -> cube { size.set(shape.size.toVec3f()) }
                is ShapeData.Sphere -> generateSphere(shape)
                is ShapeData.Cylinder -> generateCylinder(shape)
                is ShapeData.Capsule -> generateCapsule(shape)
                is ShapeData.Rect -> generateRect(shape)
                else -> {
                    logW { "Ignoring shape ${shape.name} while generating mesh ${gameEntity.name}" }
                }
            }
        }

        fun MeshBuilder.generateSphere(shape: ShapeData.Sphere) {
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

        fun MeshBuilder.generateCylinder(shape: ShapeData.Cylinder) {
            // cylinder is generated in x-axis major orientation to make it align with physics geometry
            rotate(90f.deg, Vec3f.Z_AXIS)
            cylinder {
                height = shape.length.toFloat()
                topRadius = shape.topRadius.toFloat()
                bottomRadius = shape.bottomRadius.toFloat()
                steps = shape.steps
            }
        }

        fun MeshBuilder.generateCapsule(shape: ShapeData.Capsule) {
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

        fun MeshBuilder.generateRect(shape: ShapeData.Rect) {
            grid {
                sizeX = shape.size.x.toFloat()
                sizeY = shape.size.y.toFloat()
            }
        }

        suspend fun Mesh.generateHeightmap(shape: ShapeData.Heightmap) {
            var heightmap: Heightmap? = null
            if (shape.mapPath.isNotBlank()) {
                heightmap = AppAssets.loadHeightmap(shape.toAssetReference())
            }

            val rows = heightmap?.rows ?: DEFAULT_HEIGHTMAP_ROWS
            val cols = heightmap?.columns ?: DEFAULT_HEIGHTMAP_COLS

            val szX = (cols - 1) * shape.colScale.toFloat()
            val szY = (rows - 1) * shape.rowScale.toFloat()

            generate {
                applyCommon(shape.common)

                translate(szX * 0.5f, 0f, szY * 0.5f)
                grid {
                    sizeX = szX
                    sizeY = szY
                    if (heightmap != null) {
                        useHeightMap(heightmap)
                    } else {
                        stepsX = cols
                        stepsY = rows
                    }
                }
            }
        }

        fun MeshBuilder.applyCommon(common: ShapeData.CommonShapeData) {
            common.pose.toMat4f(transform)
            color = common.vertexColor.toColorLinear()
            vertexModFun = {
                texCoord.x *= common.uvScale.x.toFloat()
                texCoord.y *= common.uvScale.y.toFloat()
            }
        }

        override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
            mesh?.let { component.applyMaterialTo(gameEntity, it) }
        }

        override fun onMaterialReferenceChanged(component: MaterialReferenceComponent, material: MaterialComponent?) {
            val mesh = this.mesh ?: return
            launchOnMainThread {
                val applyMat = material ?: project.defaultMaterial
                applyMat?.applyMaterialTo(gameEntity, mesh)
            }
        }
    }

    private inner class ModelHolder :
        MaterialComponent.ListenerComponent,
        MaterialReferenceComponent.ListenerComponent,
        EditorScene.SceneShaderDataListener
    {
        var gltfPath: String? = null
        var gltfFile: GltfFile? = null

        var isIblShaded = false
        var isSsaoEnabled = false
        var maxNumLights = 4
        var shaderShadowMaps: List<ShadowMap> = emptyList()

        var model: Model? = null
        val isRecreatingModel = atomic(false)

        fun release() {
            model?.release()
            model = null
        }

        suspend fun updateModel(data: ShapeData.Model): Model? {
            logD { "${gameEntity.name}: (re-)loading model ${data.modelPath}" }
            release()

            if (data.modelPath.isBlank()) {
                return null
            }
            requiredAssets += data.toAssetReference()
            if (gltfPath != data.modelPath) {
                gltfPath = data.modelPath
                gltfFile = AppAssets.loadModel(data.modelPath)
            }

            val shaderData = scene.shaderData
            val shaderShadowMaps = shaderData.shadowMaps.toList()
            val ibl = shaderData.environmentMaps
            val ssao = shaderData.ssaoMap
            val materialRef = gameEntity.getComponent<MaterialReferenceComponent>()?.material
            val modelCfg = GltfLoadConfig(
                materialConfig = GltfMaterialConfig(
                    environmentMaps = ibl,
                    shadowMaps = shaderShadowMaps,
                    scrSpcAmbientOcclusionMap = ssao,
                    maxNumberOfLights = shaderData.maxNumberOfLights
                ),
                applyMaterials = materialRef == null,
                assetLoader = AppAssets.assetLoader
            )
            isIblShaded = ibl != null
            isSsaoEnabled = ssao != null
            maxNumLights = shaderData.maxNumberOfLights

            val gltf = gltfFile ?: return null
            val loadScene = if (data.sceneIndex in gltf.scenes.indices) data.sceneIndex else 0
            val model = gltf.makeModel(modelCfg, loadScene).also { this.model = it }

            if (materialRef != null) {
                model.meshes.values.forEach { materialRef.applyMaterialTo(gameEntity, it) }
            }
            if (!isIblShaded) {
                val bgColor = shaderData.ambientColorLinear
                model.meshes.values.forEach { mesh ->
                    (mesh.shader as? KslLitShader)?.ambientFactor = bgColor
                }
            }
            if (data.animationIndex >= 0) {
                model.animations.getOrNull(data.animationIndex)?.weight = 1f
                model.onUpdate {
                    model.applyAnimation(Time.deltaT)
                }
            }

            if (AppState.isInEditor) {
                model.meshes.values.forEach { it.rayTest = MeshRayTest.geometryTest(it) }
            }
            return model
        }

        override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
            val meshes = model?.meshes ?: return
            val updateFail = meshes.values.any { !component.applyMaterialTo(gameEntity, it) }
            if (updateFail) {
                updateDrawNode(data)
            }
        }

        override fun onMaterialReferenceChanged(component: MaterialReferenceComponent, material: MaterialComponent?) {
            val meshes = model?.meshes ?: return
            launchOnMainThread {
                if (material != null) {
                    val updateFail = meshes.values.any { !material.applyMaterialTo(gameEntity, it) }
                    if (updateFail) {
                        updateDrawNode(data)
                    }
                } else {
                    updateDrawNode(data)
                }
            }
        }

        override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
            val meshes = model?.meshes ?: return
            if (gameEntity.hasComponent<MaterialReferenceComponent>()) {
                return
            }

            val iblData = sceneShaderData.environmentMaps
            val isIbl = iblData != null
            val isSsao = sceneShaderData.ssaoMap != null

            if (isIblShaded != isIbl ||
                maxNumLights != sceneShaderData.maxNumberOfLights ||
                shaderShadowMaps != sceneShaderData.shadowMaps ||
                isSsaoEnabled != isSsao
            ) {
                recreateModelAsync()
            }

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

        private fun recreateModelAsync() {
            if (!isRecreatingModel.getAndSet(true)) {
                launchOnMainThread {
                    updateDrawNode(data)
                    isRecreatingModel.lazySet(false)
                }
            }
        }
    }
}

fun ShapeData.Heightmap.toAssetReference() = AssetReference.Heightmap(
    mapPath, heightScale.toFloat(), heightOffset.toFloat()
)

fun ShapeData.Model.toAssetReference() = AssetReference.Model(modelPath)