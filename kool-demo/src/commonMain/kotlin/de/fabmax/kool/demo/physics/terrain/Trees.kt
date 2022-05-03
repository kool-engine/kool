package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.math.spatial.NearestTraverser
import de.fabmax.kool.math.spatial.OcTree
import de.fabmax.kool.math.spatial.Vec3fAdapter
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import kotlin.math.sqrt

class Trees(val terrain: Terrain, nTrees: Int) {

    val treeGroup = Group().apply { isFrustumChecked = false }

    private val treeAreas = listOf(
        Vec3f(-32f, 0f, 64f) to 50f,
        Vec3f(-61f, 0f, 109f) to 50f,
        Vec3f(29f, 0f, 52f) to 50f,
        Vec3f(25f, 0f, -143f) to 25f
    )

    private val random = Random(17)

    private val treeTree = OcTree(Vec3fAdapter, bounds = BoundingBox(Vec3f(-200f), Vec3f(200f)))

    init {
        instancedTrees(20, nTrees)
        //singleMeshTrees(nTrees)
    }

    private fun instancedTrees(nMeshes: Int, nInstances: Int) {
        val treeGenerator = LowPolyTree(0x1deadb0b)
        val meshes = mutableListOf<Mesh>()

        for (i in 0 until nMeshes) {
            val treeData = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, LowPolyTree.WIND_SENSITIVITY)
            val meshBuilder = MeshBuilder(treeData)

            val root = treeGenerator.generateNodes(Mat4f())
            treeGenerator.trunkMesh(root, random.randomF(0.25f, 0.75f), meshBuilder)
            treeGenerator.leafMesh(root, random.randomF(0.25f, 0.75f), meshBuilder)

            treeData.splitVertices()
            treeData.generateNormals()
            meshes += Mesh(treeData).apply {
                isFrustumChecked = false
                instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
            }
        }
        meshes.forEach { treeGroup += it }
        for (i in 0 until nInstances) {
            val (pos, likelihood) = pickTreePosition()
            if (likelihood > 0f) {
                treeTree.add(pos)
                val mesh = meshes[random.randomI(meshes.indices)]
                val size = 0.6f + likelihood.clamp(0f, 0.4f)
                val pose = Mat4f().translate(pos).rotate(random.randomF(0f, 360f), Vec3f.Y_AXIS).scale(size)
                mesh.instances!!.addInstance {
                    put(pose.matrix)
                }
            }
        }
    }

    private fun singleMeshTrees(nTrees: Int) {
        val treeGenerator = LowPolyTree(0x1deadb0b)
        val treeData = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, LowPolyTree.WIND_SENSITIVITY)
        val meshBuilder = MeshBuilder(treeData)

        val pose = Mat4f()
        for (i in 0 until nTrees) {
            val (pos, likelihood) = pickTreePosition()
            if (likelihood > 0f) {
                treeTree.add(pos)
                pose.setIdentity().translate(pos)
                val root = treeGenerator.generateNodes(pose)
                treeGenerator.trunkMesh(root, likelihood, meshBuilder)
                treeGenerator.leafMesh(root, likelihood, meshBuilder)
            }
        }

        treeData.splitVertices()
        treeData.generateNormals()
        treeGroup += Mesh(treeData)
    }

    private fun pickTreePosition(): Pair<Vec3f, Float> {
        val trav = NearestTraverser<Vec3f>()
        val position = MutableVec3f()
        var tries = 0
        while (tries < 500) {
            tries++

            position.x = random.randomF(-200f, 200f)
            position.z = random.randomF(-200f, 200f)
            position.y = 0f
            val areaDistance = treeAreas.minOf { it.first.distance(position) / it.second }
            if (areaDistance > 1f) {
                // position is not in tree area
                continue
            }

            val minGreen = minOf(
                terrain.getSplatWeightsAt(position.x, position.z).y,
                terrain.getSplatWeightsAt(position.x - 3f, position.z - 3f).y,
                terrain.getSplatWeightsAt(position.x - 3f, position.z + 3f).y,
                terrain.getSplatWeightsAt(position.x + 3f, position.z - 3f).y,
                terrain.getSplatWeightsAt(position.x + 3f, position.z + 3f).y
            )
            if (minGreen < 0.9f) {
                // trees only grow on green stuff
                continue
            }

            position.y = terrain.getTerrainHeightAt(position.x, position.z) - 0.25f
            trav.setup(position, 10f).traverse(treeTree)
            val nearestTreeDistance = sqrt(trav.sqrDist).clamp(0f, 7f)
            if (nearestTreeDistance < 2f) {
                // too close to next tree
                continue
            }

            val growLikelihood = nearestTreeDistance / 7f * (1f - areaDistance)
            if (random.randomF() < growLikelihood) {
                return position to growLikelihood
            }
        }
        return Vec3f.ZERO to 0f
    }

    fun setupTreeShaders(ibl: EnvironmentMaps, shadowMap: ShadowMap) {
        treeGroup.children.filterIsInstance<Mesh>().forEach {
            it.shader = blinnPhongShader {
                color { addVertexColor() }
                shadow { addShadowMap(shadowMap) }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                specularStrength = 0.05f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
                isInstanced = it.instances != null
            }
        }
    }

}