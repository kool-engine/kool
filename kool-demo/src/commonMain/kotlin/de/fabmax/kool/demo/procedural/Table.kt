package de.fabmax.kool.demo.procedural

import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Table : Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)) {

    init {
        isCastingShadow = false
        generate {
            makeGeometry()
            geometry.removeDegeneratedTriangles()
            geometry.generateNormals()
            geometry.generateTangents()
        }

        shader = deferredKslPbrShader {
            color {
                textureColor(Texture2d("${DemoLoader.materialPath}/granitesmooth1/granitesmooth1-albedo4.jpg"))
            }
            normalMapping {
                setNormalMap(Texture2d("${DemoLoader.materialPath}/granitesmooth1/granitesmooth1-normal2.jpg"))
            }
            roughness {
                textureProperty(Texture2d("${DemoLoader.materialPath}/granitesmooth1/granitesmooth1-roughness3.jpg"))
            }
        }
    }

    private fun MeshBuilder.makeGeometry() {
        val tableR = 30f
        val r = 1f

        translate(0f, -r, 0f)
        rotate(90f, Vec3f.X_AXIS)

        profile {
            val shape = simpleShape(true) {
                for (a in 0..100) {
                    val rad = 2f * PI.toFloat() * a / 100
                    xy(cos(rad) * tableR, sin(rad) * tableR)
                    uv(0f, 0f)
                }
            }

            for (i in 0..15) {
                val p = i / 15f
                withTransform {
                    val h = cos((1 - p) * PI.toFloat()) * r
                    val e = sin(p * PI.toFloat()) * r
                    val s = (tableR + e) / tableR
                    val uvS = (tableR + r * p * PI.toFloat()) * 0.04f

                    shape.texCoords.forEachIndexed { i, uv -> uv.set(shape.positions[i].x, shape.positions[i].y).norm().scale(uvS) }
                    translate(0f, 0f, h)
                    scale(s, s, 1f)
                    if (i == 0) {
                        sampleAndFillBottom()
                    } else {
                        sample()
                    }
                }
            }
            fillTop()
        }
    }
}