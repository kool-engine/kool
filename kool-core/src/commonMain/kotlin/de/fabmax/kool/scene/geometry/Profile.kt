package de.fabmax.kool.scene.geometry

import de.fabmax.kool.math.*
import de.fabmax.kool.util.*

interface ShapeContainer {
    val shapes: MutableList<Shape>
}

inline fun ShapeContainer.multiShape(block: MultiShape.() -> Unit): MultiShape {
    val shape = MultiShape()
    shapes += shape
    shape.block()
    return shape
}

inline fun ShapeContainer.simpleShape(isClosed: Boolean, block: SimpleShape.() -> Unit): SimpleShape {
    val shape = SimpleShape(isClosed)
    shapes += shape
    shape.block()
    return shape
}

class Profile : ShapeContainer {
    override val shapes = mutableListOf<Shape>()

    fun sample(meshBuilder: MeshBuilder<*>, connect: Boolean, inverseOrientation: Boolean) {
        shapes.forEach { it.sample(meshBuilder, connect, inverseOrientation) }
    }

    fun fillTop(meshBuilder: MeshBuilder<*>) {
        shapes.forEach { it.fillTop(meshBuilder) }
    }

    fun fillBottom(meshBuilder: MeshBuilder<*>) {
        shapes.forEach { it.fillBottom(meshBuilder) }
    }
}

abstract class Shape {
    abstract val positions: List<Vec3f>
    abstract val sampledVertIndices: List<Int>

    abstract fun <Layout: Struct> sample(meshBuilder: MeshBuilder<Layout>, connect: Boolean, inverseOrientation: Boolean)
    abstract fun <Layout: Struct> fillTop(meshBuilder: MeshBuilder<Layout>)
    abstract fun <Layout: Struct> fillBottom(meshBuilder: MeshBuilder<Layout>)
}

class SimpleShape(val isClosed: Boolean) : Shape() {
    override val positions = mutableListOf<MutableVec3f>()
    val normals = mutableListOf<MutableVec3f>()
    val texCoords = mutableListOf<MutableVec2f>()
    val colors = mutableListOf<Color>()
    val emissionColors = mutableListOf<Color>()
    val metallicRoughs = mutableListOf<Vec2f>()
    val customAttribs = mutableListOf<(MutableStructBufferView<*>) -> Unit>()

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

    fun getColor(i: Int): Color? {
        return if (i < colors.size) colors[i] else null
    }

    fun getEmissionColor(i: Int): Color? {
        return if (i < emissionColors.size) emissionColors[i] else null
    }

    fun getMetallicRoughness(i: Int): Vec2f? {
        return if (i < metallicRoughs.size) metallicRoughs[i] else null
    }

    private fun applyCustomAttribs(v: MutableStructBufferView<*>, i: Int) {
        if (i < customAttribs.size) {
            customAttribs[i].invoke(v)
        }
    }

    fun xy(x: Float, y: Float) {
        positions += MutableVec3f(x, y, 0f)
    }

    fun xyArc(start: Vec2f, center: Vec2f, angle: AngleF, steps: Int, generateNormals: Boolean = false) {
        val angStep = angle / steps
        val v = MutableVec2f(start.x - center.x, start.y - center.y)
        for (i in 0 .. steps) {
            xy(center.x + v.x, center.y + v.y)
            if (generateNormals) {
                normals += MutableVec3f(v.x, v.y, 0f).norm()
            }
            v.rotate(angStep)
        }
    }

    fun xz(x: Float, z: Float) {
        positions += MutableVec3f(x, 0f, z)
    }

    fun xzArc(x: Float, z: Float, center: Vec2f, angle: AngleF, steps: Int, generateNormals: Boolean = false) {
        val angStep = angle / steps
        val v = MutableVec2f(x - center.x, z - center.y)
        for (i in 0 .. steps) {
            xz(center.x + v.x, center.y + v.y)
            if (generateNormals) {
                normals += MutableVec3f(v.x, 0f, v.y).norm()
            }
            v.rotate(angStep)
        }
    }

    fun yz(y: Float, z: Float) {
        positions += MutableVec3f(0f, y, z)
    }

    fun yzArc(y: Float, z: Float, center: Vec2f, angle: AngleF, steps: Int, generateNormals: Boolean = false) {
        val angStep = angle / steps
        val v = MutableVec2f(y - center.x, z - center.y)
        for (i in 0 .. steps) {
            yz(center.x + v.x, center.y + v.y)
            if (generateNormals) {
                normals += MutableVec3f(0f, v.x, v.y).norm()
            }
            v.rotate(angStep)
        }
    }

    fun uv(x: Float, y: Float) {
        texCoords += MutableVec2f(x, y)
    }

    fun normal(x: Float, y: Float, z: Float) {
        normals += MutableVec3f(x, y, z)
    }

    fun color(color: Color) {
        colors += color
    }

    fun emissionColor(color: Color) {
        emissionColors += color
    }

    fun metallicRoughness(metallic: Float, roughness: Float) {
        metallicRoughs += Vec2f(metallic, roughness)
    }

    fun setTexCoordsX(x: Float) {
        texCoords.forEach { it.x = x }
    }

    fun setTexCoordsY(y: Float) {
        texCoords.forEach { it.y = y }
    }

    override fun <Layout: Struct> sample(meshBuilder: MeshBuilder<Layout>, connect: Boolean, inverseOrientation: Boolean) {
        prevIndices.clear()
        prevIndices.addAll(vertIndices)
        vertIndices.clear()

        positions.forEachIndexed { i, pos ->
            vertIndices += meshBuilder.vertex {
                meshBuilder.positionAttr?.set(pos)
                meshBuilder.normalAttr?.set(getNormal(i))
                meshBuilder.texCoordAttr?.set(getTexCoord(i))
                getColor(i)?.let { meshBuilder.colorAttr?.set(it) }
                getEmissionColor(i)?.let { meshBuilder.emissiveColorAttr?.set(it.r, it.g, it.b) }
                getMetallicRoughness(i)?.let {
                    meshBuilder.metallicAttr?.set(it.x)
                    meshBuilder.roughnessAttr?.set(it.y)
                }
                applyCustomAttribs(this, i)
            }
        }

        if (connect) {
            connect(meshBuilder, prevIndices, inverseOrientation)
        }
    }

    fun connect(meshBuilder: MeshBuilder<*>, otherVertIndices: List<Int>, inverseOrientation: Boolean) {
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

    override fun <Layout: Struct> fillTop(meshBuilder: MeshBuilder<Layout>) {
        val triangulated = PolyUtil.fillPolygon(positions)
        for (i in triangulated.indices.indices step 3) {
            val i1 = triangulated.indices[i]
            val i2 = triangulated.indices[i+1]
            val i3 = triangulated.indices[i+2]
            meshBuilder.geometry.addTriIndices(vertIndices[i1], vertIndices[i2], vertIndices[i3])
        }
    }

    override fun <Layout: Struct> fillBottom(meshBuilder: MeshBuilder<Layout>) {
        val triangulated = PolyUtil.fillPolygon(positions)
        for (i in triangulated.indices.indices step 3) {
            val i1 = triangulated.indices[i]
            val i2 = triangulated.indices[i+1]
            val i3 = triangulated.indices[i+2]
            meshBuilder.geometry.addTriIndices(vertIndices[i1], vertIndices[i3], vertIndices[i2])
        }
    }
}

class MultiShape : Shape(), ShapeContainer {
    override val shapes = mutableListOf<Shape>()
    override val positions: List<Vec3f>
        get() = shapes.flatMap { it.positions }
    override val sampledVertIndices: List<Int>
        get() = shapes.flatMap { it.sampledVertIndices }

    override fun <Layout: Struct> sample(meshBuilder: MeshBuilder<Layout>, connect: Boolean, inverseOrientation: Boolean) {
        shapes.forEach { it.sample(meshBuilder, connect, inverseOrientation) }
    }

    override fun <Layout: Struct> fillTop(meshBuilder: MeshBuilder<Layout>) {
        val joinedInds = sampledVertIndices
        val triangulated = PolyUtil.fillPolygon(positions)
        for (i in triangulated.indices.indices step 3) {
            val i1 = triangulated.indices[i]
            val i2 = triangulated.indices[i+1]
            val i3 = triangulated.indices[i+2]
            meshBuilder.geometry.addTriIndices(joinedInds[i1], joinedInds[i2], joinedInds[i3])
        }
    }

    override fun <Layout: Struct> fillBottom(meshBuilder: MeshBuilder<Layout>) {
        val joinedInds = sampledVertIndices
        val triangulated = PolyUtil.fillPolygon(positions)
        for (i in triangulated.indices.indices step 3) {
            val i1 = triangulated.indices[i]
            val i2 = triangulated.indices[i+1]
            val i3 = triangulated.indices[i+2]
            meshBuilder.geometry.addTriIndices(joinedInds[i1], joinedInds[i3], joinedInds[i2])
        }
    }
}
