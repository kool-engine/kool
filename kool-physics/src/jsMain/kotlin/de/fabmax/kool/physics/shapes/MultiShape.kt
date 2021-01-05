package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btCompoundShape
import ammo.toBtTransform
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.util.BoundingBox

actual class MultiShape actual constructor() : CommonMultiShape(), CollisionShape {

    private val mutShapes = mutableListOf<ChildShape>()
    override val children: List<ChildShape>
        get() = mutShapes

    override val btShape: btCompoundShape

    init {
        Physics.checkIsLoaded()

        btShape = Ammo.btCompoundShape()
    }

    private val boundsHelper = ShapeBoundsHelper(btShape)
    override fun getAabb(result: BoundingBox) = boundsHelper.getAabb(result)
    override fun getBoundingSphere(result: MutableVec4f) = boundsHelper.getBoundingSphere(result)

    actual constructor(childShapes: List<ChildShape>) : this() {
        childShapes.forEach { addShape(it) }
    }

    override fun addShape(childShape: ChildShape) {
        mutShapes += childShape
        btShape.addChildShape(childShape.transform.toBtTransform(), childShape.shape.btShape)
    }

    override fun removeShape(shape: CollisionShape) {
        mutShapes.removeAll { it.shape === shape }
        btShape.removeChildShape(shape.btShape)
    }
}