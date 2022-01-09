package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import kotlin.math.*

/**
 * @author fabmax
 */
open class MeshBuilder(val geometry: IndexedVertexList) {

    val transform = Mat4fStack()

    var color = Color.GRAY
    var emissiveColor = Color.BLACK
    var metallic = 0f
    var roughness = 0.5f
    var vertexModFun: (VertexView.() -> Unit)? = null

    val hasNormals = geometry.hasAttribute(Attribute.NORMALS)

    inline fun vertex(block: VertexView.() -> Unit): Int {
        return geometry.addVertex {
            color.set(this@MeshBuilder.color)
            setEmissiveColor(this@MeshBuilder.emissiveColor)
            setMetallic(this@MeshBuilder.metallic)
            setRoughness(this@MeshBuilder.roughness)
            block()

            transform.transform(position)
            if (hasNormals && normal.sqrLength() != 0f) {
                transform.transform(normal, 0f)
                normal.norm()
            }
            vertexModFun?.invoke(this)
        }
    }

    open fun vertex(pos: Vec3f, nrm: Vec3f, uv: Vec2f = Vec2f.ZERO) = vertex {
        position.set(pos)
        normal.set(nrm)
        texCoord.set(uv)
    }

    inline fun withTransform(block: MeshBuilder.() -> Unit) {
        transform.push()
        this.block()
        transform.pop()
    }

    inline fun withColor(color: Color, block: MeshBuilder.() -> Unit) {
        val c = this.color
        this.color = color
        this.block()
        this.color = c
    }

    inline fun withEmissiveColor(emissiveColor: Color, block: MeshBuilder.() -> Unit) {
        val c = this.emissiveColor
        this.emissiveColor = emissiveColor
        this.block()
        this.emissiveColor = c
    }

    inline fun withMetallicRoughness(metallic: Float, roughness: Float, block: MeshBuilder.() -> Unit) {
        val m = this.metallic
        val r = this.roughness
        this.metallic = metallic
        this.roughness = roughness
        this.block()
        this.metallic = m
        this.roughness = r
    }

    fun clear() {
        geometry.clear()
        identity()
    }

    fun identity() = transform.setIdentity()

    fun translate(t: Vec3f) = transform.translate(t.x, t.y, t.z)

    fun translate(x: Float, y: Float, z: Float) = transform.translate(x, y, z)

    fun rotate(angleDeg: Float, axis: Vec3f) = transform.rotate(angleDeg, axis)

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float) = transform.rotate(angleDeg, axX, axY, axZ)

    fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float) = transform.rotate(eulerX, eulerY, eulerZ)

    fun scale(s: Float) = transform.scale(s, s, s)

    fun scale(x: Float, y: Float, z: Float) = transform.scale(x, y, z)

    fun setCoordSystem(origin: Vec3f, right: Vec3f, up: Vec3f, top: Vec3f? = null) {
        var topV = top
        if (topV == null) {
            topV = cross(right, up)
        }

        transform.setIdentity()
        transform[0, 0] = right.x
        transform[1, 0] = right.y
        transform[2, 0] = right.z

        transform[0, 1] = up.x
        transform[1, 1] = up.y
        transform[2, 1] = up.z

        transform[0, 2] = topV.x
        transform[1, 2] = topV.y
        transform[2, 2] = topV.z

        transform[0, 3] = origin.x
        transform[1, 3] = origin.y
        transform[2, 3] = origin.z
    }

    inline fun profile(block: Profile.() -> Unit): Profile {
        val profile = Profile()
        profile.block()
        return profile
    }

    fun ShapeContainer.circleShape(radius: Float = 1f, steps: Int = 40): SimpleShape {
        return simpleShape(true) {
            for (a in 0 until steps) {
                val rad = 2f * PI.toFloat() * a / steps
                xy(cos(rad) * radius, sin(rad) * radius)
            }
        }
    }

    fun Profile.sample(connect: Boolean = true, inverseOrientation: Boolean = false) {
        sample(this@MeshBuilder, connect, inverseOrientation)
    }

    fun Profile.sampleAndFillBottom(connect: Boolean = false, inverseOrientation: Boolean = false) {
        sample(this@MeshBuilder, connect, inverseOrientation)
        fillBottom(this@MeshBuilder)
    }

    fun Profile.fillBottom() {
        fillBottom(this@MeshBuilder)
    }

    fun Profile.sampleAndFillTop(connect: Boolean = false, inverseOrientation: Boolean = false) {
        sample(this@MeshBuilder, connect, inverseOrientation)
        fillTop(this@MeshBuilder)
    }

    fun Profile.fillTop() {
        fillTop(this@MeshBuilder)
    }

    inline fun circle(block: CircleProps.() -> Unit) {
        val props = CircleProps()
        props.block()
        circle(props)
    }

    fun fillPolygon(poly: PolyUtil.TriangulatedPolygon) {
        val meshIndices = IntArray(poly.vertices.size)
        poly.vertices.forEachIndexed { i, v ->
            meshIndices[i] = vertex {
                set(v)
                normal.set(poly.normal)
            }
        }
        for (i in poly.indices.indices step 3) {
            geometry.addTriIndices(meshIndices[poly.indices[i]], meshIndices[poly.indices[i + 1]], meshIndices[poly.indices[i + 2]])
        }
    }

    fun fillPolygon(points: List<Vec3f>, normal: Vec3f? = null) {
        val indices = points.map { pt ->
            vertex {
                position.set(pt)
                color.set(this@MeshBuilder.color)
                normal?.let { this.normal.set(it) }
            }
        }
        fillPolygon(indices)
    }

    fun fillPolygon(indices: List<Int>) {
        val points = indices.map { Vec3f(geometry.vertexIt.apply { index = it }.position) }
        val poly = PolyUtil.fillPolygon(points)
        for (i in 0 until poly.numTriangles) {
            val i0 = indices[poly.indices[i * 3]]
            val i1 = indices[poly.indices[i * 3 + 1]]
            val i2 = indices[poly.indices[i * 3 + 2]]
            geometry.addTriIndices(i0, i1, i2)
        }
    }

    fun circle(props: CircleProps) {
        var i1 = 0
        val iCenter = vertex(props.center, Vec3f.Z_AXIS, props.uvCenter)
        for (i in 0..props.steps) {
            val ang = (props.startDeg + props.sweepDeg * i / props.steps).toRad()
            val cos = cos(ang)
            val sin = sin(ang)
            val px = props.center.x + props.radius * cos
            val py = props.center.y + props.radius * sin

            val idx = vertex {
                position.set(px, py, props.center.z)
                normal.set(Vec3f.Z_AXIS)
                texCoord.set(cos, -sin).scale(props.uvRadius).add(props.uvCenter)
            }

            if (i > 0) {
                geometry.addTriIndices(iCenter, i1, idx)
            }
            i1 = idx
        }
    }

    inline fun uvSphere(block: SphereProps.() -> Unit) {
        val props = SphereProps().uvDefaults()
        props.block()
        uvSphere(props)
    }

    fun uvSphere(props: SphereProps) {
        val steps = max(props.steps / 2, 4)
        var prevIndices = IntArray(steps * 2 + 1)
        var rowIndices = IntArray(steps * 2 + 1)

        // bottom cap
        var theta = PI * (steps - 1) / steps
        var r = sin(theta).toFloat() * props.radius
        var y = cos(theta).toFloat() * props.radius
        for (i in 0..(steps * 2)) {
            val phi = PI * i / steps
            val x = cos(-phi).toFloat() * r
            val z = sin(-phi).toFloat() * r

            rowIndices[i] = vertex {
                position.set(x, y, z).add(props.center)
                normal.set(x, y, z).scale(1f / props.radius)
                texCoord.set(props.texCoordGenerator(theta.toFloat(), phi.toFloat()))
            }

            if (i > 0) {
                val iCenter = vertex {
                    position.set(props.center.x, props.center.y-props.radius, props.center.z)
                    normal.set(Vec3f.NEG_Y_AXIS)
                    texCoord.set(props.texCoordGenerator(PI.toFloat(), phi.toFloat()))
                }
                geometry.addTriIndices(iCenter, rowIndices[i], rowIndices[i - 1])
            }
        }

        // belt
        for (row in 2..steps-1) {
            val tmp = prevIndices
            prevIndices = rowIndices
            rowIndices = tmp

            theta = PI * (steps - row) / steps
            r = sin(theta).toFloat() * props.radius
            y = cos(theta).toFloat() * props.radius
            for (i in 0..(steps * 2)) {
                val phi = PI * i / steps
                val x = cos(-phi).toFloat() * r
                val z = sin(-phi).toFloat() * r
                rowIndices[i] = vertex {
                    position.set(x, y, z).add(props.center)
                    normal.set(x, y, z).scale(1f / props.radius)
                    texCoord.set(props.texCoordGenerator(theta.toFloat(), phi.toFloat()))
                }

                if (i > 0) {
                    geometry.addTriIndices(prevIndices[i - 1], rowIndices[i], rowIndices[i - 1])
                    geometry.addTriIndices(prevIndices[i - 1], prevIndices[i], rowIndices[i])
                }
            }
        }

        // top cap
        for (i in 1..(steps * 2)) {
            val iCenter = vertex {
                position.set(props.center.x, props.center.y + props.radius, props.center.z)
                normal.set(Vec3f.Y_AXIS)
                texCoord.set(props.texCoordGenerator(0f, (PI * i / steps).toFloat()))
            }
            geometry.addTriIndices(iCenter, rowIndices[i - 1], rowIndices[i])
        }
    }

    inline fun icoSphere(block: SphereProps.() -> Unit) {
        val props = SphereProps().icoDefaults()
        props.block()
        icoSphere(props)
    }

    /*
     * Based on https://schneide.blog/2016/07/15/generating-an-icosphere-in-c/
     */
    private class IcoGenerator {
        val x = 0.525731112f
        val z = 0.850650808f
        val n = 0f
        val verts = mutableListOf(
                Vec3f(-x, n, z), Vec3f(x, n, z), Vec3f(-x, n, -z), Vec3f(x, n, -z),
                Vec3f(n, z, x), Vec3f(n, z, -x), Vec3f(n, -z, x), Vec3f(n, -z, -x),
                Vec3f(z, x, n), Vec3f(-z, x, n), Vec3f(z, -x, n), Vec3f(-z, -x, n)
        )
        var uvVerts = mutableListOf<Pair<Vec3f, Vec2f>>()

        var faces = mutableListOf(
                4,0,1, 9,0,4, 5,9,4, 5,4,8, 8,4,1,
                10,8,1, 3,8,10, 3,5,8, 2,5,3, 7,2,3,
                10,7,3, 6,7,10, 11,7,6, 0,11,6, 1,0,6,
                1,6,10, 0,9,11, 11,9,2, 2,9,5, 2,7,11
        )

        fun subdivide(steps: Int) {
            val its = if (steps <= 8) { steps } else {
                logW { "clamping too large number of iterations for ico-sphere (${steps}) to 8" }
                8
            }
            for (i in 0 until its) {
                Subdivide.subdivideTris(verts, faces) { a, b -> MutableVec3f(a).add(b).norm() }
            }
        }

        fun generateUvs() {
            val pif = PI.toFloat()
            val uvVerts = verts.map { v -> v to Vec2f((atan2(v.x, v.z) + pif) / (2 * pif), acos(v.y) / pif) }.toMutableList()
            this.uvVerts = uvVerts

            // duplicate vertices at texture border
            for (i in faces.indices step 3) {
                // check if triangle stretches across texture border and duplicate vertex with adjusted uv if it does
                for (j in 0..2) {
                    val i1 = i + j
                    val i2 = i + (j+1) % 3
                    val i3 = i + (j+2) % 3

                    val u1 = uvVerts[faces[i1]].second.x
                    val u2 = uvVerts[faces[i2]].second.x
                    val u3 = uvVerts[faces[i3]].second.x

                    if (u1 - u2 > 0.5f && u1 - u3 > 0.5f) {
                        val dv1 = Vec3f(uvVerts[faces[i1]].first)
                        val du1 = MutableVec2f(uvVerts[faces[i1]].second).apply { this.x -= 1f }
                        faces[i1] = uvVerts.size
                        uvVerts += dv1 to du1
                    } else if (u2 - u1 > 0.5f && u3 - u1 > 0.5f) {
                        val dv1 = Vec3f(uvVerts[faces[i1]].first)
                        val du1 = MutableVec2f(uvVerts[faces[i1]].second).apply { this.x += 1f }
                        faces[i1] = uvVerts.size
                        uvVerts += dv1 to du1
                    }
                }
            }
        }
    }

    fun icoSphere(props: SphereProps) {
        val icoGenerator = IcoGenerator()
        icoGenerator.subdivide(props.steps)
        icoGenerator.generateUvs()

        // insert geometry
        val pif = PI.toFloat()
        val i0 = geometry.numVertices
        for (v in icoGenerator.uvVerts) {
            vertex {
                normal.set(v.first).norm()
                position.set(v.first).scale(props.radius).add(props.center)
                texCoord.set(props.texCoordGenerator(v.second.y * pif, v.second.x * 2 * pif))
            }
        }
        for (i in icoGenerator.faces.indices step 3) {
            geometry.addTriIndices(i0 + icoGenerator.faces[i], i0 + icoGenerator.faces[1 + i], i0 + icoGenerator.faces[2 + i])
        }
    }

    inline fun rect(block: RectProps.() -> Unit) {
        val props = RectProps()
        props.block()
        rect(props)
    }

    fun rect(props: RectProps) {
        props.fixNegativeSize()

        if (props.cornerRadius == 0f) {
            val i0 = vertex {
                position.set(props.origin.x, props.origin.y, props.origin.z)
                normal.set(Vec3f.Z_AXIS)
                texCoord.set(props.texCoordLowerLeft)
            }
            val i1 = vertex {
                position.set(props.origin.x + props.size.x, props.origin.y, props.origin.z)
                normal.set(Vec3f.Z_AXIS)
                texCoord.set(props.texCoordLowerRight)
            }
            val i2 = vertex {
                position.set(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z)
                normal.set(Vec3f.Z_AXIS)
                texCoord.set(props.texCoordUpperRight)
            }
            val i3 = vertex {
                position.set(props.origin.x, props.origin.y + props.size.y, props.origin.z)
                normal.set(Vec3f.Z_AXIS)
                texCoord.set(props.texCoordUpperLeft)
            }
            geometry.addTriIndices(i0, i1, i2)
            geometry.addTriIndices(i0, i2, i3)

        } else {
            val x = props.origin.x
            val y = props.origin.y
            val z = props.origin.z
            val w = props.size.x
            val h = props.size.y
            val xI = x + props.cornerRadius
            val yI = y + props.cornerRadius
            val wI = w - props.cornerRadius * 2
            val hI = h - props.cornerRadius * 2
            val nrm = Vec3f.Z_AXIS

            // compute tex coord insets, this only works for axis aligned tex coords...
            val uI = (props.texCoordUpperRight.x - props.texCoordUpperLeft.x) * props.cornerRadius / w
            val vI = (props.texCoordUpperRight.y - props.texCoordLowerRight.y) * props.cornerRadius / h

            val tmpPos = MutableVec3f()
            val tmpUv = MutableVec2f()

            if (hI > 0) {
                val i0 = vertex(tmpPos.set(x, yI, z), nrm, tmpUv.set(0f, vI).add(props.texCoordLowerLeft))
                val i1 = vertex(tmpPos.set(x + w, yI, z), nrm, tmpUv.set(0f, vI).add(props.texCoordLowerRight))
                val i2 = vertex(tmpPos.set(x + w, yI + hI, z), nrm, tmpUv.set(0f, -vI).add(props.texCoordUpperRight))
                val i3 = vertex(tmpPos.set(x, yI + hI, z), nrm, tmpUv.set(0f, -vI).add(props.texCoordUpperLeft))
                geometry.addTriIndices(i0, i1, i2)
                geometry.addTriIndices(i0, i2, i3)
            }

            if (wI > 0) {
                var i0 = vertex(tmpPos.set(xI, y, z), nrm, tmpUv.set(uI, 0f).add(props.texCoordLowerLeft))
                var i1 = vertex(tmpPos.set(xI + wI, y, z), nrm, tmpUv.set(-uI, 0f).add(props.texCoordLowerRight))
                var i2 = vertex(tmpPos.set(xI + wI, yI, z), nrm, tmpUv.set(-uI, vI).add(props.texCoordLowerRight))
                var i3 = vertex(tmpPos.set(xI, yI, z), nrm, tmpUv.set(uI, vI).add(props.texCoordLowerLeft))
                geometry.addTriIndices(i0, i1, i2)
                geometry.addTriIndices(i0, i2, i3)

                i0 = vertex(tmpPos.set(xI, yI + hI, z), nrm, tmpUv.set(uI, -vI).add(props.texCoordUpperLeft))
                i1 = vertex(tmpPos.set(xI + wI, yI + hI, z), nrm, tmpUv.set(-uI, -vI).add(props.texCoordUpperRight))
                i2 = vertex(tmpPos.set(xI + wI, y + h, z), nrm, tmpUv.set(-uI, 0f).add(props.texCoordUpperRight))
                i3 = vertex(tmpPos.set(xI, y + h, z), nrm, tmpUv.set(uI, 0f).add(props.texCoordUpperLeft))
                geometry.addTriIndices(i0, i1, i2)
                geometry.addTriIndices(i0, i2, i3)
            }

            circle {
                center.set(xI + wI, yI + hI, z)
                startDeg = 0f
                sweepDeg = 90f
                radius = props.cornerRadius
                steps = props.cornerSteps
                uvCenter.set(-uI, -vI).add(props.texCoordUpperRight)
                uvRadius = uI
            }
            circle {
                center.set(xI, yI + hI, z)
                startDeg = 90f
                sweepDeg = 90f
                radius = props.cornerRadius
                steps = props.cornerSteps
                uvCenter.set(uI, -vI).add(props.texCoordUpperLeft)
                uvRadius = uI
            }
            circle {
                center.set(xI, yI, z)
                startDeg = 180f
                sweepDeg = 90f
                radius = props.cornerRadius
                steps = props.cornerSteps
                uvCenter.set(uI, vI).add(props.texCoordLowerLeft)
                uvRadius = uI
            }
            circle {
                center.set(xI + wI, yI, z)
                startDeg = 270f
                sweepDeg = 90f
                radius = props.cornerRadius
                steps = props.cornerSteps
                uvCenter.set(-uI, vI).add(props.texCoordLowerRight)
                uvRadius = uI
            }
        }
    }

    fun line(pt1: Vec2f, pt2: Vec2f, width: Float) {
        line(pt1.x, pt1.y, pt2.x, pt2.y, width)
    }

    fun line(x1: Float, y1: Float, x2: Float, y2: Float, width: Float) {
        var dx = x2 - x1
        var dy = y2 - y1
        var len = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

        val addX = width * 0.25f * dx / len
        val addY = width * 0.25f * dy / len
        dx += addX + addX
        dy += addY + addY
        len += width * 0.5f

        val dxu = dx / len * width / 2
        val dyu = dy / len * width / 2

        val qx0 = x1 - addX + dyu
        val qy0 = y1 - addY - dxu

        val qx1 = x2 + addX + dyu
        val qy1 = y2 + addY - dxu

        val qx2 = x2 + addX - dyu
        val qy2 = y2 + addY + dxu

        val qx3 = x1 - addX - dyu
        val qy3 = y1 - addY + dxu

        val i0 = vertex { position.set(qx0, qy0, 0f); normal.set(Vec3f.Z_AXIS) }
        val i1 = vertex { position.set(qx1, qy1, 0f); normal.set(Vec3f.Z_AXIS) }
        val i2 = vertex { position.set(qx2, qy2, 0f); normal.set(Vec3f.Z_AXIS) }
        val i3 = vertex { position.set(qx3, qy3, 0f); normal.set(Vec3f.Z_AXIS) }
        geometry.addTriIndices(i0, i1, i2)
        geometry.addTriIndices(i0, i2, i3)
    }

    fun lineArc(centerX: Float, centerY: Float, radius: Float, startDeg: Float, sweepDeg: Float, width: Float, resolution: Float = 3f) {
        val steps = max(1, round(abs(sweepDeg) / resolution).toInt())
        val step = sweepDeg / steps

        val startRad = startDeg.toRad()
        val stepRad = step.toRad()

        for (i in 0 until steps) {
            val a0 = startRad + stepRad * i
            val a1 = a0 + stepRad
            val x0 = centerX + cos(a0) * radius
            val y0 = centerY + sin(a0) * radius
            val x1 = centerX + cos(a1) * radius
            val y1 = centerY + sin(a1) * radius
            line(x0, y0, x1, y1, width)
        }
    }

    inline fun cube(block: CubeProps.() -> Unit) {
        val props = CubeProps()
        props.block()
        cube(props)
    }

    fun cube(props: CubeProps) {
        val tmpPos = MutableVec3f()
        props.fixNegativeSize()

        // front
        withColor(props.frontColor ?: color) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z + props.size.z), Vec3f.Z_AXIS, Vec2f(0f, 1f))
            val i1 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y, props.origin.z + props.size.z), Vec3f.Z_AXIS, Vec2f(1f, 1f))
            val i2 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f.Z_AXIS, Vec2f(1f, 0f))
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f.Z_AXIS, Vec2f(0f, 0f))
            geometry.addTriIndices(i0, i1, i2)
            geometry.addTriIndices(i0, i2, i3)
        }

        // right
        withColor(props.rightColor ?: color) {
            val i0 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y, props.origin.z), Vec3f.X_AXIS, Vec2f(1f, 1f))
            val i1 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z), Vec3f.X_AXIS, Vec2f(1f, 0f))
            val i2 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f.X_AXIS, Vec2f(0f, 0f))
            val i3 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y, props.origin.z + props.size.z), Vec3f.X_AXIS, Vec2f(0f, 1f))
            geometry.addTriIndices(i0, i1, i2)
            geometry.addTriIndices(i0, i2, i3)
        }

        // back
        withColor(props.backColor ?: color) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.size.y, props.origin.z), Vec3f.NEG_Z_AXIS, Vec2f(1f, 0f))
            val i1 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z), Vec3f.NEG_Z_AXIS, Vec2f(0f, 0f))
            val i2 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y, props.origin.z), Vec3f.NEG_Z_AXIS, Vec2f(0f, 1f))
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z), Vec3f.NEG_Z_AXIS, Vec2f(1f, 1f))
            geometry.addTriIndices(i0, i1, i2)
            geometry.addTriIndices(i0, i2, i3)
        }

        // left
        withColor(props.leftColor ?: color) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z + props.size.z), Vec3f.NEG_X_AXIS, Vec2f(1f, 1f))
            val i1 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f.NEG_X_AXIS, Vec2f(1f, 0f))
            val i2 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.size.y, props.origin.z), Vec3f.NEG_X_AXIS, Vec2f(0f, 0f))
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z), Vec3f.NEG_X_AXIS, Vec2f(0f, 1f))
            geometry.addTriIndices(i0, i1, i2)
            geometry.addTriIndices(i0, i2, i3)
        }

        // top
        withColor(props.topColor ?: color) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f.Y_AXIS, Vec2f(0f, 1f))
            val i1 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z + props.size.z), Vec3f.Y_AXIS, Vec2f(1f, 1f))
            val i2 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y + props.size.y, props.origin.z), Vec3f.Y_AXIS, Vec2f(1f, 0f))
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.size.y, props.origin.z), Vec3f.Y_AXIS, Vec2f(0f, 0f))
            geometry.addTriIndices(i0, i1, i2)
            geometry.addTriIndices(i0, i2, i3)
        }

        // bottom
        withColor(props.bottomColor ?: color) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z), Vec3f.NEG_Y_AXIS, Vec2f(0f, 1f))
            val i1 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y, props.origin.z), Vec3f.NEG_Y_AXIS, Vec2f(1f, 1f))
            val i2 = vertex(tmpPos.set(props.origin.x + props.size.x, props.origin.y, props.origin.z + props.size.z), Vec3f.NEG_Y_AXIS, Vec2f(1f, 0f))
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z + props.size.z), Vec3f.NEG_Y_AXIS, Vec2f(0f, 0f))
            geometry.addTriIndices(i0, i1, i2)
            geometry.addTriIndices(i0, i2, i3)
        }
    }

    inline fun cylinder(block: CylinderProps.() -> Unit) {
        val props = CylinderProps()
        props.block()
        cylinder(props)
    }

    fun cylinder(props: CylinderProps) {
        val tmpNrm = MutableVec3f()
        props.fixNegativeSize()

        // bottom
        if (props.bottomFill) {
            withTransform {
                translate(props.origin)
                rotate(90f, Vec3f.X_AXIS)
                circle {
                    steps = props.steps
                    radius = props.bottomRadius
                }
            }
        }
        // top
        if (props.topFill) {
            withTransform {
                translate(props.origin.x, props.origin.y + props.height, props.origin.z)
                rotate(-90f, Vec3f.X_AXIS)
                circle {
                    steps = props.steps
                    radius = props.topRadius
                }
            }
        }

        val dr = props.bottomRadius - props.topRadius
        val nrmAng = 90f - acos(dr / sqrt(dr * dr + props.height * props.height)).toDeg()
        var i0 = 0
        var i1 = 0
        for (i in 0..props.steps) {
            val c = cos(i * PI * 2 / props.steps).toFloat()
            val s = sin(i * PI * 2 / props.steps).toFloat()

            val px2 = props.origin.x + props.bottomRadius * c
            val pz2 = props.origin.z + props.bottomRadius * s
            val px3 = props.origin.x + props.topRadius * c
            val pz3 = props.origin.z + props.topRadius * s

            tmpNrm.set(c, 0f, s).rotate(nrmAng, s, 0f, c)
            val i2 = vertex {
                position.set(px2, props.origin.y, pz2)
                normal.set(tmpNrm)
            }
            val i3 = vertex {
                position.set(px3, props.origin.y + props.height, pz3)
                normal.set(tmpNrm)
            }

            if (i > 0) {
                geometry.addTriIndices(i0, i1, i2)
                geometry.addTriIndices(i1, i3, i2)
            }
            i0 = i2
            i1 = i3
        }
    }

    inline fun grid(block: GridProps.() -> Unit) {
        val props = GridProps()
        props.block()
        grid(props)
    }

    fun grid(props: GridProps) {
        val gridNormal = MutableVec3f()

        val bx = -props.sizeX / 2
        val by = -props.sizeY / 2
        val sx = props.sizeX / props.stepsX
        val sy = props.sizeY / props.stepsY
        val nx = props.stepsX + 1
        props.xDir.cross(props.yDir, gridNormal).norm()

        for (y in 0 .. props.stepsY) {
            for (x in 0 .. props.stepsX) {
                val px = bx + x * sx
                val py = by + y * sy
                val h = props.heightFun(x, y)

                val idx = vertex {
                    position.set(props.center)
                    position.x += props.xDir.x * px + props.yDir.x * py + gridNormal.x * h
                    position.y += props.xDir.y * px + props.yDir.y * py + gridNormal.y * h
                    position.z += props.xDir.z * px + props.yDir.z * py + gridNormal.z * h
                    texCoord.set(x / props.stepsX.toFloat() * props.texCoordScale.x, y / props.stepsY.toFloat() * props.texCoordScale.y)
                }

                if (x > 0 && y > 0) {
                    if (x % 2 == y % 2) {
                        geometry.addTriIndices(idx - nx - 1, idx, idx - 1)
                        geometry.addTriIndices(idx - nx, idx, idx - nx - 1)
                    } else {
                        geometry.addTriIndices(idx - nx, idx, idx - 1)
                        geometry.addTriIndices(idx - nx, idx - 1, idx - nx - 1)
                    }
                }
            }
        }

        val iTri = geometry.numIndices - props.stepsX * props.stepsY * 6
        val e1 = MutableVec3f()
        val e2 = MutableVec3f()
        val v1 = geometry[0]
        val v2 = geometry[0]
        val v3 = geometry[0]
        for (i in iTri until geometry.numIndices step 3) {
            v1.index = geometry.indices[i]
            v2.index = geometry.indices[i+1]
            v3.index = geometry.indices[i+2]
            v2.position.subtract(v1.position, e1).norm()
            v3.position.subtract(v1.position, e2).norm()
            e1.cross(e2, gridNormal).norm()
            v1.normal.add(gridNormal)
            v2.normal.add(gridNormal)
            v3.normal.add(gridNormal)
        }

        val iVert = geometry.numVertices - (props.stepsX + 1) * (props.stepsY + 1)
        for (i in iVert until geometry.numVertices) {
            v1.index = i
            v1.normal.norm()
        }
    }

    fun geometry(geometry: IndexedVertexList, keepVertexColor: Boolean = false) {
        if (geometry.primitiveType == PrimitiveType.TRIANGLES) {
            val i0 = this.geometry.numVertices
            val beforeColor = color
            geometry.forEach {
                if (keepVertexColor) {
                    color = it.color
                }
                vertex(it.position, it.normal, it.texCoord)
            }
            for (i in 0 until geometry.numIndices) {
                this.geometry.addIndex(i0 + geometry.indices[i])
            }
            color = beforeColor
        } else {
            throw KoolException("Only triangle geometry can be added")
        }
    }

    inline fun text(font: Font, fontSizeUnits: Float = 0f, block: TextProps.() -> Unit) {
        val props = TextProps(font)
        props.block()
        text(props, fontSizeUnits)
    }

    fun text(props: TextProps, fontSizeUnits: Float = 0f) {
        val charMap = props.font.charMap
        if (charMap == null) {
            logE { "Font char map has not yet been initialized" }
            return
        }

        withTransform {
            if (fontSizeUnits != 0f) {
                val s = fontSizeUnits / charMap.fontProps.sizePts
                scale(s, s, s)
            }

            if (props.roundOriginToUnits) {
                translate(round(props.origin.x), round(props.origin.y), props.origin.z)
            } else {
                translate(props.origin)
            }

            val ct = props.charTransform
            var advanced = 0f
            val txt = if (props.autoWrapWidth > 0f) wrapText(props) else props.text
            for (c in txt) {
                if (c == '\n') {
                    translate(0f, -round(props.font.lineSpace), 0f)
                    advanced = 0f
                }

                val metrics = charMap[c]
                if (metrics != null) {
                    withTransform {
                        var advOffset = 0f
                        if (ct == null) {
                            advOffset = advanced
                        } else {
                            ct(advanced)
                        }
                        rect {
                            if (props.roundOriginToUnits) {
                                origin.set(round(advOffset - metrics.xOffset), round(metrics.yBaseline - metrics.height), 0f)
                            } else {
                                origin.set(advOffset - metrics.xOffset, metrics.yBaseline - metrics.height, 0f)
                            }
                            size.set(metrics.width, metrics.height)

                            texCoordUpperLeft.set(metrics.uvMin)
                            texCoordUpperRight.set(metrics.uvMax.x, metrics.uvMin.y)
                            texCoordLowerLeft.set(metrics.uvMin.x, metrics.uvMax.y)
                            texCoordLowerRight.set(metrics.uvMax)
                        }
                    }
                    advanced += metrics.advance
                }
            }
        }
    }

    private fun wrapText(txtProps: TextProps): String {
        val words = txtProps.text.split(' ')
        val outTxt = StringBuilder()

        var pos = 0f
        words.forEach { w ->
            val ww = txtProps.font.textWidth(w) + txtProps.font.charWidth(' ')
            val advPos = pos + ww
            pos = if (pos > 0f && advPos > txtProps.autoWrapWidth) {
                outTxt.append('\n')
                ww
            } else {
                advPos
            }
            outTxt.append(w).append(' ')
        }
        return outTxt.toString()
    }
}

class CircleProps {
    var radius = 1f
    var steps = 20
    val center = MutableVec3f()
    var startDeg = 0f
    var sweepDeg = 360f

    val uvCenter = MutableVec2f()
    var uvRadius = 0f

    init {
        fullTexCoords()
    }

    fun zeroTexCoords() {
        uvCenter.set(Vec2f.ZERO)
        uvRadius = 0f
    }

    fun fullTexCoords() {
        uvCenter.set(0.5f, 0.5f)
        uvRadius = 0.5f
    }
}

class GridProps {
    val center = MutableVec3f()
    val xDir = MutableVec3f(Vec3f.X_AXIS)
    val yDir = MutableVec3f(Vec3f.NEG_Z_AXIS)
    val texCoordScale = MutableVec2f(1f, 1f)
    var sizeX = 10f
    var sizeY = 10f
    var stepsX = 10
    var stepsY = 10
    var heightFun: (Int, Int) -> Float = ZERO_HEIGHT

    companion object {
        val ZERO_HEIGHT: (Int, Int) -> Float = { _, _ -> 0f }
    }
}

class SphereProps {
    var radius = 1f
    var steps = 5
    val center = MutableVec3f()

    private val uv = MutableVec2f()

    var texCoordGenerator: (Float, Float) -> Vec2f = { t, p -> defaultTexCoordGenerator(t, p) }

    private fun defaultTexCoordGenerator(theta: Float, phi: Float): Vec2f {
        return uv.set(phi / (PI.toFloat() * 2f), theta / PI.toFloat())
    }

    fun icoDefaults(): SphereProps {
        radius = 1f
        steps = 2
        center.set(Vec3f.ZERO)
        texCoordGenerator = { t, p -> defaultTexCoordGenerator(t, p) }
        return this
    }

    fun uvDefaults(): SphereProps {
        radius = 1f
        steps = 20
        center.set(Vec3f.ZERO)
        texCoordGenerator = { t, p -> defaultTexCoordGenerator(t, p) }
        return this
    }
}

class RectProps {
    var cornerRadius = 0f
    var cornerSteps = 8
    val origin = MutableVec3f()
    val size = MutableVec2f(1f, 1f)

    var width: Float
        get() = size.x
        set(value) { size.x = value }
    var height: Float
        get() = size.y
        set(value) { size.y = value }

    val texCoordUpperLeft = MutableVec2f()
    val texCoordUpperRight = MutableVec2f()
    val texCoordLowerLeft = MutableVec2f()
    val texCoordLowerRight = MutableVec2f()

    init {
        generateTexCoords(1f)
    }

    fun fixNegativeSize() {
        if (size.x < 0) {
            origin.x += size.x
            size.x = -size.x
        }
        if (size.y < 0) {
            origin.y += size.y
            size.y = -size.y
        }
    }

    fun zeroTexCoords() = generateTexCoords(0f)

    fun generateTexCoords(scale: Float = 1f) {
        texCoordUpperLeft.set(0f, 0f)
        texCoordUpperRight.set(scale, 0f)
        texCoordLowerLeft.set(0f, scale)
        texCoordLowerRight.set(scale, scale)
    }

    fun mirrorTexCoordsX() {
        val xul = texCoordUpperLeft.x
        val xll = texCoordLowerLeft.x
        texCoordUpperLeft.x = texCoordUpperRight.x
        texCoordUpperRight.x = xul
        texCoordLowerLeft.x = texCoordLowerRight.x
        texCoordLowerRight.x = xll
    }

    fun mirrorTexCoordsY() {
        val yul = texCoordUpperLeft.y
        val yur = texCoordUpperRight.y
        texCoordUpperLeft.y = texCoordLowerLeft.y
        texCoordLowerLeft.y = yul
        texCoordUpperRight.y = texCoordLowerRight.y
        texCoordLowerRight.y = yur
    }
}

class CubeProps {
    val origin = MutableVec3f()
    val size = MutableVec3f(1f, 1f, 1f)

    var width: Float
        get() = size.x
        set(value) { size.x = value }
    var height: Float
        get() = size.y
        set(value) { size.y = value }
    var depth: Float
        get() = size.z
        set(value) { size.z = value }

    var topColor: Color? = null
    var bottomColor: Color? = null
    var leftColor: Color? = null
    var rightColor: Color? = null
    var frontColor: Color? = null
    var backColor: Color? = null

    fun fixNegativeSize() {
        if (size.x < 0) {
            origin.x += size.x
            size.x = -size.x
        }
        if (size.y < 0) {
            origin.y += size.y
            size.y = -size.y
        }
        if (size.z < 0) {
            origin.z += size.z
            size.z = -size.z
        }
    }

    fun centered() {
        origin.x -= size.x / 2f
        origin.y -= size.y / 2f
        origin.z -= size.z / 2f
    }

    fun colored(linearSpace: Boolean = true) {
        if (linearSpace) {
            frontColor = MdColor.RED.toLinear()
            rightColor = MdColor.AMBER.toLinear()
            backColor = MdColor.INDIGO.toLinear()
            leftColor = MdColor.CYAN.toLinear()
            topColor = MdColor.PURPLE.toLinear()
            bottomColor = MdColor.GREEN.toLinear()
        } else {
            frontColor = MdColor.RED
            rightColor = MdColor.AMBER
            backColor = MdColor.INDIGO
            leftColor = MdColor.CYAN
            topColor = MdColor.PURPLE
            bottomColor = MdColor.GREEN
        }
    }
}

class CylinderProps {
    var bottomRadius = 1f
    var topRadius = 1f
    var steps = 20
    var height = 1f
    var topFill = true
    var bottomFill = true
    val origin = MutableVec3f()

    var radius: Float
        get() = (bottomRadius + topRadius) / 2f
        set(value) {
            bottomRadius = value
            topRadius = value
        }

    fun fixNegativeSize() {
        if (height < 0) {
            origin.y += height
            height = -height
        }
    }
}

class TextProps(var font: Font) {
    var text = ""
    val origin = MutableVec3f()
    var roundOriginToUnits = true
    var autoWrapWidth = -1f

    var charTransform: (MeshBuilder.(Float) -> Unit)? = null
}