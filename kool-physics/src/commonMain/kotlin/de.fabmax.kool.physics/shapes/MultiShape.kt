package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.util.MeshBuilder

expect class MultiShape() : CommonMultiShape, CollisionShape {

    constructor(childShapes: List<ChildShape>)

}

class ChildShape(val shape: CollisionShape, val transform: Mat4f)

abstract class CommonMultiShape {

    abstract val children: List<ChildShape>

    abstract fun addShape(childShape: ChildShape)

    abstract fun removeShape(shape: CollisionShape)

    fun addShape(shape: CollisionShape, transform: Mat4f) = addShape(ChildShape(shape, transform))

    open fun generateGeometry(target: MeshBuilder) {
        children.forEach { child ->
            target.withTransform {
                transform.mul(child.transform)
                child.shape.generateGeometry(this)
            }
        }
    }

}
