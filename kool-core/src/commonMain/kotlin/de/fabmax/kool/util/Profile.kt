package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f

interface ShapeContainer {
    val shapes: MutableList<Shape>
}

class Profile : ShapeContainer {
    override val shapes = mutableListOf<Shape>()

    fun sample(meshBuilder: MeshBuilder, connect: Boolean, inverseOrientation: Boolean) {
        shapes.forEach { it.sample(meshBuilder, connect, inverseOrientation) }
    }

    fun fillTop(meshBuilder: MeshBuilder) {
        shapes.forEach { it.fillTop(meshBuilder) }
    }

    fun fillBottom(meshBuilder: MeshBuilder) {
        shapes.forEach { it.fillBottom(meshBuilder) }
    }
}

abstract class Shape {
    abstract val sampledVertIndices: List<Int>

    abstract fun sample(meshBuilder: MeshBuilder, connect: Boolean, inverseOrientation: Boolean)

    abstract fun fillTop(meshBuilder: MeshBuilder)
    abstract fun fillBottom(meshBuilder: MeshBuilder)
}

class SimpleShape(val isClosed: Boolean) : Shape() {
    val positions = mutableListOf<MutableVec3f>()
    val normals = mutableListOf<MutableVec3f>()
    val texCoords = mutableListOf<MutableVec2f>()

    var color: Color? = null

    private val prevIndices = mutableListOf<Int>()
    private val vertIndices = mutableListOf<Int>()
    override val sampledVertIndices: List<Int>
        get() = vertIndices

    val nVerts: Int
        get() = positions.size

    fun getNormal(i: Int): Vec3f {
        return if (i < normals.size) normals[i] else Vec3f.ZERO
    }

    fun getTexCoord(i: Int): Vec2f {
        return if (i < texCoords.size) texCoords[i] else Vec2f.ZERO
    }

    fun xy(x: Float, y: Float) {
        positions += MutableVec3f(x, y, 0f)
    }

    fun xz(x: Float, z: Float) {
        positions += MutableVec3f(x, 0f, z)
    }

    fun uv(x: Float, y: Float) {
        texCoords += MutableVec2f(x, y)
    }

    fun normal(x: Float, y: Float, z: Float) {
        normals += MutableVec3f(x, y, z)
    }

    fun setTexCoordsX(x: Float) {
        texCoords.forEach { it.x = x }
    }

    fun setTexCoordsY(y: Float) {
        texCoords.forEach { it.y = y }
    }

    override fun sample(meshBuilder: MeshBuilder, connect: Boolean, inverseOrientation: Boolean) {
        prevIndices.clear()
        prevIndices.addAll(vertIndices)
        vertIndices.clear()

        color?.let { meshBuilder.color = it }

        positions.forEachIndexed { i, pos ->
            vertIndices += meshBuilder.vertex(pos, getNormal(i), getTexCoord(i))
        }

        if (connect) {
            connect(meshBuilder, prevIndices, inverseOrientation)
        }
    }

    fun connect(meshBuilder: MeshBuilder, otherVertIndices: List<Int>, inverseOrientation: Boolean) {
        if (otherVertIndices.isNotEmpty()) {
            for (i in 1 until otherVertIndices.size) {
                if (inverseOrientation) {
                    meshBuilder.geometry.addTriIndices(otherVertIndices[i - 1], vertIndices[i], otherVertIndices[i])
                    meshBuilder.geometry.addTriIndices(otherVertIndices[i - 1], vertIndices[i - 1], vertIndices[i])
                } else {
                    meshBuilder.geometry.addTriIndices(otherVertIndices[i - 1], otherVertIndices[i], vertIndices[i])
                    meshBuilder.geometry.addTriIndices(otherVertIndices[i - 1], vertIndices[i], vertIndices[i - 1])
                }
            }
            if (isClosed && prevIndices.isNotEmpty()) {
                if (inverseOrientation) {
                    meshBuilder.geometry.addTriIndices(otherVertIndices.last(), vertIndices.first(), otherVertIndices.first())
                    meshBuilder.geometry.addTriIndices(otherVertIndices.last(), vertIndices.last(), vertIndices.first())
                } else {
                    meshBuilder.geometry.addTriIndices(otherVertIndices.last(), otherVertIndices.first(), vertIndices.first())
                    meshBuilder.geometry.addTriIndices(otherVertIndices.last(), vertIndices.first(), vertIndices.last())
                }
            }
        }
    }

    /**
     * fixme: For now this only works for convex profiles
     */
    override fun fillTop(meshBuilder: MeshBuilder) {
        val s = vertIndices.size
        for (i in 1 until vertIndices.size / 2) {
            meshBuilder.geometry.addTriIndices(vertIndices[s-i], vertIndices[i-1], vertIndices[i])
            meshBuilder.geometry.addTriIndices(vertIndices[s-i-1], vertIndices[s-i], vertIndices[i])
        }
        if (vertIndices.size % 2 != 0) {
            val m = vertIndices.size / 2
            meshBuilder.geometry.addTriIndices(vertIndices[m-1], vertIndices[m], vertIndices[m+1])
        }
    }

    /**
     * fixme: For now this only works for convex profiles
     */
    override fun fillBottom(meshBuilder: MeshBuilder) {
        val s = vertIndices.size
        for (i in 1 until vertIndices.size / 2) {
            meshBuilder.geometry.addTriIndices(vertIndices[s-i], vertIndices[i], vertIndices[i-1])
            meshBuilder.geometry.addTriIndices(vertIndices[s-i-1], vertIndices[i], vertIndices[s-i])
        }
        if (vertIndices.size % 2 != 0) {
            val m = vertIndices.size / 2
            meshBuilder.geometry.addTriIndices(vertIndices[m-1], vertIndices[m+1], vertIndices[m])
        }
    }
}

class MultiShape : Shape(), ShapeContainer {
    override val shapes = mutableListOf<Shape>()
    override val sampledVertIndices: List<Int>
        get() = shapes.flatMap { it.sampledVertIndices }

    override fun sample(meshBuilder: MeshBuilder, connect: Boolean, inverseOrientation: Boolean) {
        shapes.forEach { it.sample(meshBuilder, connect, inverseOrientation) }
    }

    override fun fillTop(meshBuilder: MeshBuilder) {
        val joinedInds = sampledVertIndices
        for (i in 2 until joinedInds.size) {
            meshBuilder.geometry.addTriIndices(joinedInds[0], joinedInds[i-1], joinedInds[i])
        }
    }

    override fun fillBottom(meshBuilder: MeshBuilder) {
        val joinedInds = sampledVertIndices
        for (i in 2 until joinedInds.size) {
            meshBuilder.geometry.addTriIndices(joinedInds[0], joinedInds[i], joinedInds[i-1])
        }
    }
}
