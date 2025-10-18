package de.fabmax.kool.scene.geometry

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.math.spatial.InRadiusTraverser
import de.fabmax.kool.math.spatial.pointKdTree
import de.fabmax.kool.util.*

fun <T: Struct> IndexedVertexList<T>.computeBounds(
    result: BoundingBoxF = BoundingBoxF(),
): BoundingBoxF {
    val pos = MutableVec3f()
    val attr = checkNotNull(positionAttr) { "IndexedVertexList has no position attribute" }
    for (i in 0 until numVertices) {
        vertexData.get(i) {
            result.add(get(attr, pos))
        }
    }
    return result
}

fun <T: Struct> IndexedVertexList<T>.generateNormals() {
    check(primitiveType == PrimitiveType.TRIANGLES) { "Normal generation is only supported for triangle meshes" }
    if (positionAttr == null || normalAttr == null) {
        logW { "generateNormals() requires non-null positionAttr and normalAttr" }
        return
    }

    val n1 = MutableVec3f()
    val n2 = MutableVec3f()
    val n3 = MutableVec3f()
    val p1 = MutableVec3f()
    val p2 = MutableVec3f()
    val p3 = MutableVec3f()
    val e1 = MutableVec3f()
    val e2 = MutableVec3f()
    val nrm = MutableVec3f()

    for (i in 0 until numVertices) {
        vertexData.set(i) {
            set(normalAttr, Vec3f.ZERO)
        }
    }

    for (i in 0 until numIndices step 3) {
        vertexData.get(indices[i])     { positionAttr.get(p1); normalAttr.get(n1) }
        vertexData.get(indices[i + 1]) { positionAttr.get(p2); normalAttr.get(n2) }
        vertexData.get(indices[i + 2]) { positionAttr.get(p3); normalAttr.get(n3) }

        p2.subtract(p1, e1).norm()
        p3.subtract(p1, e2).norm()
        e1.cross(e2, nrm).norm().mul(triArea(p1, p2, p3))
        if (!nrm.x.isNaN() && !nrm.y.isNaN() && !nrm.z.isNaN()) {
            vertexData.set(indices[i])     { normalAttr.set(n1.add(nrm)) }
            vertexData.set(indices[i + 1]) { normalAttr.set(n2.add(nrm)) }
            vertexData.set(indices[i + 2]) { normalAttr.set(n3.add(nrm)) }
        }
    }

    for (i in 0 until numVertices) {
        vertexData.set(i) {
            normalAttr.get(nrm)
            normalAttr.set(nrm.norm())
        }
    }
    modCount.increment()
}

fun <T: Struct> IndexedVertexList<T>.generateTangents(tangentSign: Float = 1f) {
    check(primitiveType == PrimitiveType.TRIANGLES) { "Normal generation is only supported for triangle meshes" }
    if (positionAttr == null || texCoordAttr == null || tangentAttr == null) {
        logW { "generateTangents() requires non-null positionAttr, texCoordAttr and tangentAttr" }
        return
    }

    val pos1 = MutableVec3f()
    val pos2 = MutableVec3f()
    val pos3 = MutableVec3f()
    val tex1 = MutableVec2f()
    val tex2 = MutableVec2f()
    val tex3 = MutableVec2f()
    val tan1 = MutableVec4f()
    val tan2 = MutableVec4f()
    val tan3 = MutableVec4f()
    val e1 = MutableVec3f()
    val e2 = MutableVec3f()
    val tan = MutableVec4f()

    for (i in 0 until numVertices) {
        vertexData.set(i) {
            tangentAttr.set(Vec4f.ZERO)
        }
    }

    for (i in 0 until numIndices step 3) {
        vertexData.get(indices[i]) {
            positionAttr.get(pos1)
            tangentAttr.get(tan1)
            texCoordAttr.get(tex1)
        }
        vertexData.get(indices[i + 1]) {
            positionAttr.get(pos2)
            tangentAttr.get(tan2)
            texCoordAttr.get(tex2)
        }
        vertexData.get(indices[i + 2]) {
            positionAttr.get(pos3)
            tangentAttr.get(tan3)
            texCoordAttr.get(tex3)
        }

        pos2.subtract(pos1, e1).norm()
        pos3.subtract(pos1, e2).norm()

        val du1 = tex2.x - tex1.x
        val dv1 = tex2.y - tex1.y
        val du2 = tex3.x - tex1.x
        val dv2 = tex3.y - tex1.y
        val f = 1f / (du1 * dv2 - du2 * dv1)
        if (!f.isNaN()) {
            tan.x = f * (dv2 * e1.x - dv1 * e2.x)
            tan.y = f * (dv2 * e1.y - dv1 * e2.y)
            tan.z = f * (dv2 * e1.z - dv1 * e2.z)

            vertexData.set(indices[i])     { tangentAttr.set(tan1.add(tan)) }
            vertexData.set(indices[i + 1]) { tangentAttr.set(tan2.add(tan)) }
            vertexData.set(indices[i + 2]) { tangentAttr.set(tan3.add(tan)) }
        }
    }
    for (i in 0 until numVertices) {
        vertexData.set(i) {
            tangentAttr.get(tan)
            tan.w = 0f
            tan.norm()
            tan.w = tangentSign
            tangentAttr.set(tan)
        }
    }
    modCount.increment()
}

fun <T: Struct> IndexedVertexList<T>.splitVertices() {
    val splitData = StructBuffer(layout, numIndices)
    for (i in 0 until numIndices) {
        val ind = indices[i]
        splitData.set(i, ind, vertexData)
    }
    vertexData = splitData

    val n = numIndices
    indices.clear()
    for (i in 0 until n) {
        indices.put(i)
    }
    numVertices = numIndices
    modCount.increment()
}

fun <T: Struct> IndexedVertexList<T>.mergeCloseVertices(epsilon: Float = 0.001f) {
    val positions = mutableListOf<PointAndIndex>()
    forEach {
        positions += PointAndIndex(it, it.index)
    }

    val mergeMap = mutableMapOf<Int, Int>()

    val tree = pointKdTree(positions)
    val trav = InRadiusTraverser<PointAndIndex>()
    positions.forEach { pt ->
        trav.setup(pt, epsilon).traverse(tree)
        trav.result.removeAll { it.index in mergeMap.keys }
        trav.result.forEach { mergeMap[it.index] = pt.index }
    }

    val mergedData = StructBuffer(layout, vertexData.capacity)
    val indexMap = mutableMapOf<Int, Int>()
    var j = 0
    for (i in 0 until numVertices) {
        val mergedI = mergeMap[i] ?: i
        if (mergedI == i) {
            indexMap[mergedI] = j
            mergedData.set(j, i, vertexData)
            j++
        }
    }
    logD { "Removed ${numVertices - j} vertices" }
    numVertices = j
    vertexData = mergedData

    val mergeIndices = Uint32Buffer(indices.capacity)
    for (i in 0 until numIndices) {
        val ind = indices[i]
        mergeIndices.put(indexMap[mergeMap[ind]!!]!!)
    }
    indices = mergeIndices
    modCount.increment()
}

private class PointAndIndex(pos: Vec3f, val index: Int) : Vec3f(pos)