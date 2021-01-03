package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.util.MeshBuilder

expect class MultiShape() : CommonMultiShape, CollisionShape {

    constructor(childShapes: List<ChildShape>)

    fun addShape(childShape: ChildShape)

    fun removeShape(shape: CollisionShape)
}

class ChildShape(val shape: CollisionShape, val transform: Mat4f)

abstract class CommonMultiShape {

    abstract val children: List<ChildShape>

    open fun generateGeometry(target: MeshBuilder) {
        children.forEach { child ->
            target.withTransform {
                transform.mul(child.transform)
                child.shape.generateGeometry(this)
            }
        }
    }

}
