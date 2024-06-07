package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.Heightmap
import de.fabmax.kool.util.launchOnMainThread

class MeshComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<MeshComponentData> = ComponentInfo(MeshComponentData(ShapeData.Box()))
) :
    GameEntityDataComponent<MeshComponentData>(gameEntity, componentInfo),
    DrawNodeComponent,
    MaterialComponent.ListenerComponent,
    MaterialReferenceComponent.ListenerComponent
{
    override var drawNode: Mesh? = null
        private set

    init {
        dependsOn(MaterialReferenceComponent::class, isOptional = true)

        data.shapes
            .filterIsInstance<ShapeData.Heightmap>()
            .filter{ it.mapPath.isNotBlank() }
            .forEach { requiredAssets += it.toAssetReference() }
    }

    override fun onDataChanged(oldData: MeshComponentData, newData: MeshComponentData) {
        launchOnMainThread {
            updateGeometry()
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        drawNode = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS).apply {
            name = gameEntity.name
            isVisible = gameEntity.isVisibleState.value

            if (AppState.isInEditor) {
                rayTest = MeshRayTest.geometryTest(this)
            }
            gameEntity.replaceDrawNode(this)
        }

        updateGeometry()
        createMeshShader()
    }

    override fun destroyComponent() {
        super.destroyComponent()
        drawNode?.release()
        drawNode = null
        gameEntity.replaceDrawNode(Node(gameEntity.name))
    }

    suspend fun updateGeometry() {
        val mesh = drawNode ?: return

        requiredAssets.clear()
        mesh.generate {
            data.shapes.forEach { shape -> generateShape(shape) }
            geometry.generateTangents()
        }

        // force ray test mesh update
        mesh.rayTest.onMeshDataChanged(mesh)

        if (isApplied) {
            gameEntity.getComponents<UpdateMeshComponent>().forEach { it.updateMesh(data) }
        }
    }

    private suspend fun MeshBuilder.generateShape(shape: ShapeData) = withTransform {
        shape.common.pose.toMat4f(transform)
        color = shape.common.vertexColor.toColorLinear()
        vertexModFun = {
            texCoord.x *= shape.common.uvScale.x.toFloat()
            texCoord.y *= shape.common.uvScale.y.toFloat()
        }

        when (shape) {
            is ShapeData.Box -> cube { size.set(shape.size.toVec3f()) }
            is ShapeData.Sphere -> generateSphere(shape)
            is ShapeData.Cylinder -> generateCylinder(shape)
            is ShapeData.Capsule -> generateCapsule(shape)
            is ShapeData.Heightmap -> generateHeightmap(shape)
            is ShapeData.Rect -> generateRect(shape)
            is ShapeData.Custom -> { }
            is ShapeData.Plane -> error("Plane shape is not supported as mesh shape")
        }
    }

    private fun MeshBuilder.generateSphere(shape: ShapeData.Sphere) {
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
        // generate cylinder in x-axis major orientation to make it align with physics geometry
        rotate(90f.deg, Vec3f.Z_AXIS)
        cylinder {
            height = shape.length.toFloat()
            topRadius = shape.topRadius.toFloat()
            bottomRadius = shape.bottomRadius.toFloat()
            steps = shape.steps
        }
    }

    private fun MeshBuilder.generateCapsule(shape: ShapeData.Capsule) {
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
        grid {
            sizeX = shape.size.x.toFloat()
            sizeY = shape.size.y.toFloat()
        }
    }

    private suspend fun MeshBuilder.generateHeightmap(shape: ShapeData.Heightmap) {
        var heightmap: Heightmap? = null
        if (shape.mapPath.isNotBlank()) {
            heightmap = AppAssets.loadHeightmap(shape.toAssetReference())
        }

        val rows = heightmap?.rows ?: DEFAULT_HEIGHTMAP_ROWS
        val cols = heightmap?.columns ?: DEFAULT_HEIGHTMAP_COLS

        val szX = (cols - 1) * shape.colScale.toFloat()
        val szY = (rows - 1) * shape.rowScale.toFloat()
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

    override fun onMaterialReferenceChanged(component: MaterialReferenceComponent, material: MaterialComponent?) {
        launchOnMainThread {
            createMeshShader()
        }
    }

    override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
        val mesh = drawNode ?: return
        val holder = gameEntity.getComponent<MaterialReferenceComponent>() ?: return
        if (holder.isHoldingMaterial(component)) {
            component.applyMaterialTo(gameEntity, mesh)
        }
    }

    private suspend fun createMeshShader() {
        val mesh = drawNode ?: return
        val material = gameEntity.getComponent<MaterialReferenceComponent>()?.material ?: project.defaultMaterial
        material?.applyMaterialTo(gameEntity, mesh)
    }

    companion object {
        const val DEFAULT_HEIGHTMAP_ROWS = 129
        const val DEFAULT_HEIGHTMAP_COLS = 129
    }
}

interface UpdateMeshComponent {
    fun updateMesh(mesh: MeshComponentData)
}

fun ShapeData.Heightmap.toAssetReference() = AssetReference.Heightmap(
    mapPath, heightScale.toFloat(), heightOffset.toFloat()
)