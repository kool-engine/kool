package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.logW

class SceneMeshes(val scene: EditorScene) : Node("SceneMeshes") {

    private val meshes = mutableMapOf<MeshKey, DrawNodeAndUsers>()

    suspend fun usePrimitiveMesh(key: MeshKey, user: GameEntity): Mesh {
        val meshUsers = meshes.getOrPut(key) {
            createMesh(key, user)
        }
        return meshUsers.drawNode as Mesh
    }

    fun clearMesh(key: MeshKey, user: GameEntity) {
        val meshUsers = meshes[key]
        meshUsers?.let {
            it.users -= user
            if (it.users.isEmpty()) {
                removeNode(it.drawNode)
                it.drawNode.release()
                meshes -= key
            }
        }
    }

    private suspend fun createMesh(meshKey: MeshKey, user: GameEntity): DrawNodeAndUsers {
        val isInstanced = meshKey.exclusiveEntity == EntityId.NULL
        val instances = if (isInstanced) MeshInstanceList(100, Attribute.INSTANCE_MODEL_MAT) else null
        val attributes = listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)

        val users = mutableSetOf(user)
        val mesh = Mesh(attributes, instances).apply {
            this@SceneMeshes.addNode(this)

            generate {
                meshKey.shapes.forEach { generateShape(it) }
            }
            geometry.generateTangents()

            if (isInstanced) {
                onUpdate {
                    instances?.let { insts ->
                        insts.clear()
                        users.forEach {
                            it.getComponent<MeshComponent>()?.addInstance(insts)
                        }
                    }
                }
            }

            val material = scene.project.materialsById[meshKey.material] ?: scene.project.defaultMaterial
            material?.applyMaterialTo(user, this)
            if (AppState.isInEditor) {
                rayTest = MeshRayTest.geometryTest(this)
            }
        }
        return DrawNodeAndUsers(mesh, users)
    }

    fun MeshBuilder.generateShape(shape: ShapeData) = withTransform {
        when (shape) {
            is ShapeData.Box -> generateBox(shape)
            is ShapeData.Sphere -> generateSphere(shape)
            is ShapeData.Cylinder -> generateCylinder(shape)
            is ShapeData.Capsule -> generateCapsule(shape)
            is ShapeData.Rect -> generateRect(shape)
            else -> {
                logW { "Ignoring shape ${shape.name} while generating mesh" }
            }
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

    data class MeshKey(val shapes: List<ShapeData>, val material: EntityId, val drawGroupId: Int, val exclusiveEntity: EntityId = EntityId.NULL)

    private class DrawNodeAndUsers(val drawNode: Node, val users: MutableSet<GameEntity>)
}