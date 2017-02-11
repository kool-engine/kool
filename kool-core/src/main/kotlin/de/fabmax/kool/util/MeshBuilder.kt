package de.fabmax.kool.util

import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.shading.*

fun mesh(withNormals: Boolean, withColors: Boolean, withTexCoords: Boolean, name: String? = null,
         block: MeshBuilder.() -> Unit): Mesh {

    val mesh = Mesh(withNormals, withColors, withTexCoords, name)
    val builder = MeshBuilder(mesh)

    mesh.batchUpdate = true
    builder.block()
    if (builder.shader != null) {
        mesh.shader = builder.shader
    }
    mesh.batchUpdate = false

    return mesh

}

fun textureMesh(name: String? = null, block: MeshBuilder.() -> Unit): Mesh {
    return mesh(true, false, true, name, block)
}

fun colorMesh(name: String? = null, block: MeshBuilder.() -> Unit): Mesh {
    return mesh(true, true, false, name, block)
}

/**
 * @author fabmax
 */
open class MeshBuilder(val mesh: Mesh) {

    val transform = Mat4fStack()

    var color = Color.BLACK
    var vertexModFun: (IndexedVertexList.Item.() -> Unit)? = null

    var shader: BasicShader? = null

    private val tmpPos = MutableVec3f()
    private val tmpNrm = MutableVec3f()

    private val circleProps = CircleProps()
    private val cubeProps = CubeProps()
    private val cylinderProps = CylinderProps()
    private val rectProps = RectProps()
    private val sphereProps = SphereProps()

    init {
        shader = basicShader {
            if (mesh.hasNormals) {
                lightModel = LightModel.PHONG_LIGHTING
            } else {
                lightModel = LightModel.NO_LIGHTING
            }

            if (mesh.hasTexCoords) {
                colorModel = ColorModel.TEXTURE_COLOR
            } else if (mesh.hasColors) {
                colorModel = ColorModel.VERTEX_COLOR
            } else {
                colorModel = ColorModel.STATIC_COLOR
            }
        }
    }

    protected open fun vertex(pos: Vec3f, nrm: Vec3f, uv: Vec2f = Vec2f.ZERO): Int {
        return mesh.addVertex {
            position.set(pos)
            normal.set(nrm)
            texCoord.set(uv)
            color.set(this@MeshBuilder.color)
            vertexModFun?.invoke(this)
            transform.transform(position)
            if (mesh.hasNormals) {
                transform.transform(normal, 0f)
                normal.norm()
            }
        }
    }

    fun withTransform(block: MeshBuilder.() -> Unit) {
        transform.push()
        this.block()
        transform.pop()
    }

    fun withColor(color: Color?, block: MeshBuilder.() -> Unit) {
        val c = this.color
        if (color != null) {
            this.color = color
        }
        this.block()
        this.color = c
    }

    fun translate(t: Vec3f) = transform.translate(t.x, t.y, t.z)

    fun translate(x: Float, y: Float, z: Float) = transform.translate(x, y, z)

    fun rotate(angleDeg: Float, axis: Vec3f) = transform.rotate(angleDeg, axis)

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float)  = transform.rotate(angleDeg, axX, axY, axZ)

    fun rotateEuler(xDeg: Float, yDeg: Float, zDeg: Float)  = transform.rotateEuler(xDeg, yDeg, zDeg)

    fun scale(x: Float, y: Float, z: Float) = transform.scale(x, y, z)

    fun setCoordSystem(origin: Vec3f, right: Vec3f, up: Vec3f, top: Vec3f? = null) {
        var topV = top
        if (topV == null) {
            topV = cross(right, up)
        }

        transform.setIdentity()
        transform[0, 0] = right.x
        transform[0, 1] = right.y
        transform[0, 2] = right.z

        transform[1, 0] = up.x
        transform[1, 1] = up.y
        transform[1, 2] = up.z

        transform[2, 0] = topV.x
        transform[2, 1] = topV.y
        transform[2, 2] = topV.z

        transform[3, 0] = origin.x
        transform[3, 1] = origin.y
        transform[3, 2] = origin.z
    }

    fun circle(props: CircleProps.() -> Unit) {
        circleProps.defaults().props()
        circle(circleProps)
    }

    fun circle(props: CircleProps) {
        var i0 = 0
        var i1 = 0
        for (i in 0..props.steps) {
            val px = props.center.x + props.radius * Math.cos(i * Math.PI * 2 / props.steps).toFloat()
            val py = props.center.y + props.radius * Math.sin(i * Math.PI * 2 / props.steps).toFloat()
            val idx = vertex(tmpPos.set(px, py, props.center.z), Vec3f.Z_AXIS)

            if (i == 0) {
                i0 = idx
            } else if(i == 1) {
                i1 = idx
            } else {
                mesh.addTriIndices(i0, i1, idx)
                i1 = idx
            }
        }
    }

    fun sphere(props: SphereProps.() -> Unit) {
        sphereProps.defaults().props()
        sphere(sphereProps)
    }

    fun sphere(props: SphereProps) {
        val steps = Math.max(props.steps / 2, 4)
        var prevIndices = IntArray(steps * 2 + 1)
        var rowIndices = IntArray(steps * 2 + 1)

        // bottom cap
        var iCenter = vertex(tmpPos.set(props.center.x, props.center.y-props.radius, props.center.z), Vec3f.NEG_Y_AXIS)
        var r = Math.sin(Math.PI / steps).toFloat() * props.radius
        var y = -Math.cos(Math.PI / steps).toFloat() * props.radius
        for (i in 0..(steps * 2)) {
            val x = Math.cos(Math.PI * i / steps).toFloat() * r
            val z = Math.sin(Math.PI * i / steps).toFloat() * r
            rowIndices[i] = vertex(tmpPos.set(x, y, z), tmpNrm.set(x, y, z).scale(1f / props.radius))
            if (i > 0) {
                mesh.addTriIndices(iCenter, rowIndices[i - 1], rowIndices[i])
            }
        }

        // belt
        for (row in 2..steps-1) {
            val tmp = prevIndices
            prevIndices = rowIndices
            rowIndices = tmp

            r = Math.sin(Math.PI * row / steps).toFloat() * props.radius
            y = -Math.cos(Math.PI * row / steps).toFloat() * props.radius
            for (i in 0..(steps * 2)) {
                val x = Math.cos(Math.PI * i / steps).toFloat() * r
                val z = Math.sin(Math.PI * i / steps).toFloat() * r
                rowIndices[i] = vertex(tmpPos.set(x, y, z), tmpNrm.set(x, y, z).scale(1f / props.radius))

                if (i > 0) {
                    mesh.addTriIndices(prevIndices[i - 1], rowIndices[i - 1], rowIndices[i])
                    mesh.addTriIndices(prevIndices[i - 1], rowIndices[i], prevIndices[i])
                }
            }
        }

        // top cap
        iCenter = vertex(tmpPos.set(props.center.x, props.center.y + props.radius, props.center.z), Vec3f.Y_AXIS)
        for (i in 1..(steps * 2)) {
            mesh.addTriIndices(iCenter, rowIndices[i], rowIndices[i - 1])
        }
    }

    fun rect(props: RectProps.() -> Unit) {
        rectProps.defaults().props()
        rect(rectProps)
    }

    fun rect(props: RectProps) {
        props.fixNegativeSize()
        val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z),
                Vec3f.Z_AXIS, props.texCoordLowerLeft)
        val i1 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y, props.origin.z),
                Vec3f.Z_AXIS, props.texCoordLowerRight)
        val i2 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y + props.height, props.origin.z),
                Vec3f.Z_AXIS, props.texCoordUpperRight)
        val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.height, props.origin.z),
                Vec3f.Z_AXIS, props.texCoordUpperLeft)
        mesh.addTriIndices(i0, i1, i2)
        mesh.addTriIndices(i0, i2, i3)
    }

    fun cube(props: CubeProps.() -> Unit) {
        cubeProps.defaults().props()
        cube(cubeProps)
    }

    fun cube(props: CubeProps) {
        props.fixNegativeSize()

        // front
        withColor(props.frontColor) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z + props.depth), Vec3f.Z_AXIS)
            val i1 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y, props.origin.z + props.depth), Vec3f.Z_AXIS)
            val i2 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y + props.height, props.origin.z + props.depth), Vec3f.Z_AXIS)
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.height, props.origin.z + props.depth), Vec3f.Z_AXIS)
            mesh.addTriIndices(i0, i1, i2)
            mesh.addTriIndices(i0, i2, i3)
        }

        // right
        withColor(props.rightColor) {
            val i0 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y, props.origin.z), Vec3f.X_AXIS)
            val i1 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y + props.height, props.origin.z), Vec3f.X_AXIS)
            val i2 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y + props.height, props.origin.z + props.depth), Vec3f.X_AXIS)
            val i3 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y, props.origin.z + props.depth), Vec3f.X_AXIS)
            mesh.addTriIndices(i0, i1, i2)
            mesh.addTriIndices(i0, i2, i3)
        }

        // back
        withColor(props.backColor) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.height, props.origin.z), Vec3f.NEG_Z_AXIS)
            val i1 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y + props.height, props.origin.z), Vec3f.NEG_Z_AXIS)
            val i2 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y, props.origin.z), Vec3f.NEG_Z_AXIS)
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z), Vec3f.NEG_Z_AXIS)
            mesh.addTriIndices(i0, i1, i2)
            mesh.addTriIndices(i0, i2, i3)
        }

        // left
        withColor(props.leftColor) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z + props.depth), Vec3f.NEG_X_AXIS)
            val i1 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.height, props.origin.z + props.depth), Vec3f.NEG_X_AXIS)
            val i2 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.height, props.origin.z), Vec3f.NEG_X_AXIS)
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z), Vec3f.NEG_X_AXIS)
            mesh.addTriIndices(i0, i1, i2)
            mesh.addTriIndices(i0, i2, i3)
        }

        // top
        withColor(props.topColor) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.height, props.origin.z + props.depth), Vec3f.Y_AXIS)
            val i1 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y + props.height, props.origin.z + props.depth), Vec3f.Y_AXIS)
            val i2 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y + props.height, props.origin.z), Vec3f.Y_AXIS)
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y + props.height, props.origin.z), Vec3f.Y_AXIS)
            mesh.addTriIndices(i0, i1, i2)
            mesh.addTriIndices(i0, i2, i3)
        }

        // bottom
        withColor(props.bottomColor) {
            val i0 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z), Vec3f.NEG_Y_AXIS)
            val i1 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y, props.origin.z), Vec3f.NEG_Y_AXIS)
            val i2 = vertex(tmpPos.set(props.origin.x + props.width, props.origin.y, props.origin.z + props.depth), Vec3f.NEG_Y_AXIS)
            val i3 = vertex(tmpPos.set(props.origin.x, props.origin.y, props.origin.z + props.depth), Vec3f.NEG_Y_AXIS)
            mesh.addTriIndices(i0, i1, i2)
            mesh.addTriIndices(i0, i2, i3)
        }
    }

    fun cylinder(props: CylinderProps.() -> Unit) {
        cylinderProps.defaults().props()
        cylinder(cylinderProps)
    }

    fun cylinder(props: CylinderProps) {
        props.fixNegativeSize()

        // bottom
        withTransform {
            translate(props.origin)
            rotate(90f, Vec3f.X_AXIS)
            circle {
                steps = props.steps
                radius = props.bottomRadius
            }
        }
        // top
        withTransform {
            translate(props.origin.x, props.origin.y + props.height, props.origin.z)
            rotate(-90f, Vec3f.X_AXIS)
            circle {
                steps = props.steps
                radius = props.topRadius
            }
        }

        val dr = props.bottomRadius - props.topRadius
        val nrmAng = 90f - toDeg(Math.acos(dr / Math.sqrt(dr.toDouble() * dr + props.height * props.height)).toFloat())
        var i0 = 0
        var i1 = 0
        for (i in 0..props.steps) {
            val c = Math.cos(i * Math.PI * 2 / props.steps).toFloat()
            val s = Math.sin(i * Math.PI * 2 / props.steps).toFloat()

            val px2 = props.origin.x + props.bottomRadius * c
            val pz2 = props.origin.z + props.bottomRadius * s
            val px3 = props.origin.x + props.topRadius * c
            val pz3 = props.origin.z + props.topRadius * s

            tmpNrm.set(c, 0f, s).rotate(nrmAng, s, 0f, c)
            val i2 = vertex(tmpPos.set(px2, props.origin.y, pz2), tmpNrm)
            val i3 = vertex(tmpPos.set(px3, props.origin.y + props.height, pz3), tmpNrm)

            if (i > 0) {
                mesh.addTriIndices(i0, i1, i2)
                mesh.addTriIndices(i1, i3, i2)
            }
            i0 = i2
            i1 = i3
        }
    }
}

open class CircleProps {
    var radius = 1f
    var steps = 20
    val center = MutableVec3f()

    open fun defaults(): CircleProps {
        radius = 1f
        steps = 20
        center.set(Vec3f.ZERO)
        return this
    }
}

class SphereProps : CircleProps() {
    override fun defaults(): SphereProps {
        super.defaults()
        return this
    }
}

open class RectProps {
    var width = 1f
    var height = 1f
    val origin = MutableVec3f()

    val texCoordUpperLeft = MutableVec2f(0f, 0f)
    val texCoordUpperRight = MutableVec2f(1f, 0f)
    val texCoordLowerLeft = MutableVec2f(0f, 1f)
    val texCoordLowerRight = MutableVec2f(1f, 1f)

    open fun fixNegativeSize() {
        if (width < 0) {
            origin.x += width
            width = -width
        }
        if (height < 0) {
            origin.y += height
            height = -height
        }
    }

    open fun defaults(): RectProps {
        width = 1f
        height = 1f
        origin.set(Vec3f.ZERO)
        return this
    }
}

class CubeProps : RectProps() {
    var depth = 1f

    var topColor: Color? = null
    var bottomColor: Color? = null
    var leftColor: Color? = null
    var rightColor: Color? = null
    var frontColor: Color? = null
    var backColor: Color? = null

    override fun fixNegativeSize() {
        super.fixNegativeSize()
        if (depth < 0) {
            origin.z += depth
            depth = -depth
        }
    }

    override fun defaults(): CubeProps {
        super.defaults()
        depth = 1f

        topColor = null
        bottomColor = null
        leftColor = null
        rightColor = null
        frontColor = null
        backColor = null

        return this
    }
}

open class CylinderProps {
    var bottomRadius = 1f
    var topRadius = 1f
    var steps = 20
    var height = 1f
    val origin = MutableVec3f()

    fun defaults(): CylinderProps {
        bottomRadius = 1f
        topRadius = 1f
        steps = 20
        height = 1f
        origin.set(Vec3f.ZERO)
        return this
    }

    fun fixNegativeSize() {
        if (height < 0) {
            origin.y += height
            height = -height
        }
    }
}
