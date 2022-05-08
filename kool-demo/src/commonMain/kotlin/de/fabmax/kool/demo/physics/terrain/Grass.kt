package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.logD

class Grass(val terrain: Terrain, val trees: Trees) {

    val grassQuads: Mesh

    init {
        grassQuads = Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, TreeShader.WIND_SENSITIVITY)).apply {
            generate {
                val pos = MutableVec3f()
                val step = MutableVec3f()
                val topOffset = MutableVec3f()
                val midOffset = MutableVec3f()

                val pt = PerfTimer()
                var genCnt = 0

                val rand = Random(1337)
                for (i in 0..750_000) {
                    val x = rand.randomF(-250f, 250f)
                    val z = rand.randomF(-250f, 250f)

                    val weights = terrain.getSplatWeightsAt(x, z)
                    if (weights.y > 0.5f && rand.randomF() < weights.y) {
                        genCnt++

                        pos.set(x, terrain.getTerrainHeightAt(x, z), z)
                        step.set(rand.randomF(-1f, 1f), 0f, rand.randomF(-1f, 1f)).norm().add(pos)
                        step.y = terrain.getTerrainHeightAt(step.x, step.z)
                        step.subtract(pos).scale(0.333f)
                        midOffset.set(step).rotate(90f, Vec3f.Y_AXIS)
                        topOffset.set(rand.randomF(-0.25f, 0.25f), rand.randomF(0.75f, 1.25f) * weights.y, rand.randomF(-0.25f, 0.25f))
                        grassSprite(pos, step, topOffset, midOffset)
                    }
                }
                geometry.generateNormals()

                logD { "Generated $genCnt grass patches in ${pt.takeMs()} ms" }
            }
            isCastingShadow = false
        }
    }

    fun setupShader(grassColor: Texture2d, ibl: EnvironmentMaps, shadowMap: ShadowMap) {
        val grassShader = GrassShader(grassColor, ibl, shadowMap, trees.windDensity, false)
        grassQuads.shader = grassShader

        grassQuads.onUpdate += {
            grassShader.windOffset = trees.windOffset
            grassShader.windStrength = trees.windStrength
            grassShader.windScale = 1f / trees.windScale
        }
    }

    companion object {
        private fun MeshBuilder.makeGrassVertex(pos: Vec3f, u: Float, v: Float, wind: Float): Int = vertex {
            set(pos)
            texCoord.set(u, v)
            getFloatAttribute(TreeShader.WIND_SENSITIVITY)?.f = wind
        }

        fun MeshBuilder.grassSprite(pos: Vec3f, step: Vec3f, topOffset: Vec3f, midOffset: Vec3f) {
            val pl = MutableVec3f()
            val ph = MutableVec3f()

            pl.set(pos)
            ph.set(pos).add(topOffset)

            var u = 0f
            var i1 = makeGrassVertex(pl, u, 1f, 0f)
            var i2 = makeGrassVertex(ph, u, 0f, topOffset.y)

            for (i in 1..3) {
                u += 0.333f
                pl.add(step)
                ph.add(step)

                if (i == 1) {
                    pl.add(midOffset)
                    ph.add(midOffset)
                } else if (i == 3) {
                    pl.subtract(midOffset)
                    ph.subtract(midOffset)
                }

                val i3 = makeGrassVertex(pl, u, 1f, 0f)
                val i4 = makeGrassVertex(ph, u, 0f, topOffset.y)
                geometry.addTriIndices(i1, i2, i4)
                geometry.addTriIndices(i1, i4, i3)
                i1 = i3
                i2 = i4
            }
        }
    }
}