package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btCompoundShape
import ammo.toBtTransform
import de.fabmax.kool.physics.Physics

actual class MultiShape actual constructor() : CommonMultiShape(), CollisionShape {

    private val mutShapes = mutableListOf<ChildShape>()
    override val children: List<ChildShape>
        get() = mutShapes

    override val btShape: btCompoundShape

    init {
        Physics.checkIsLoaded()

        btShape = Ammo.btCompoundShape()
    }

    actual constructor(childShapes: List<ChildShape>) : this() {
        childShapes.forEach { addShape(it) }
    }

    actual fun addShape(childShape: ChildShape) {
        mutShapes += childShape
        btShape.addChildShape(childShape.transform.toBtTransform(), childShape.shape.btShape)
    }

    actual fun removeShape(shape: CollisionShape) {
        mutShapes.removeAll { it.shape === shape }
        btShape.removeChildShape(shape.btShape)
    }
}