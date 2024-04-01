package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2d
import de.fabmax.kool.math.Vec2d

/**
 * Evaluates standard touch gestures (pinch-to-zoom, two-finger drag)
 */
class TouchGestureEvaluator {

    val currentGesture = Gesture()

    private val activePointers = mutableListOf<Pointer>()
    private val tmpVec1 = MutableVec2d()
    private val tmpVec2 = MutableVec2d()

    private val startPositions = mutableMapOf<Int, Vec2d>()
    private var scale = 1f

    fun evaluate(pointerState: PointerState, ctx: KoolContext) {
        scale = ctx.windowScale
        pointerState.getActivePointers(activePointers)

        if (activePointers.size > 1) {
            when (currentGesture.type) {
                INVALID -> onGestureInit(activePointers)
                INDETERMINATE -> onDetermineGesture(activePointers)
                PINCH -> handleGesture(activePointers)
                TWO_FINGER_DRAG -> handleGesture(activePointers)
            }
        } else {
            // not enough valid pointers for a multitouch gesture
            currentGesture.type = INVALID
            startPositions.clear()
        }
    }

    private fun onGestureInit(pointers: MutableList<Pointer>) {
        pointers.forEach { startPositions[it.id] = Vec2d(it.x, it.y) }
        currentGesture.type = INDETERMINATE
    }

    private fun onDetermineGesture(pointers: MutableList<Pointer>) {
        // remove any missing pointer
        startPositions.keys.removeAll { ptrId -> pointers.find { it.id == ptrId } == null }

        // add any new pointer
        pointers.filter { !startPositions.containsKey(it.id) }
                .forEach { startPositions[it.id] = Vec2d(it.x, it.y) }

        // try to match gesture
        when {
            isPinch(pointers) -> currentGesture.type = PINCH
            isTwoFingerDrag(pointers) -> currentGesture.type = TWO_FINGER_DRAG
        }
    }

    private fun isPinch(pointers: MutableList<Pointer>): Boolean {
        // two pointers moving in opposing direction
        if (pointers.size == 2) {
            tmpVec1.set(pointers[0].x, pointers[0].y).subtract(startPositions[pointers[0].id]!!)
            tmpVec2.set(pointers[1].x, pointers[1].y).subtract(startPositions[pointers[1].id]!!)

            tmpVec1.mul(1.0 / scale)
            tmpVec2.mul(1.0 / scale)

            if (tmpVec1.length() > 5.0 && tmpVec2.length() > 5.0 && tmpVec1.dot(tmpVec2) < 0.0) {
                tmpVec1.set(startPositions[pointers[0].id]!!)
                tmpVec2.set(startPositions[pointers[1].id]!!)

                currentGesture.init(PINCH, tmpVec1, tmpVec2, scale)
                handleGesture(pointers)
                return true
            }
        }
        return false
    }

    private fun isTwoFingerDrag(pointers: MutableList<Pointer>): Boolean {
        // two pointers moving in same direction
        if (pointers.size == 2) {
            tmpVec1.set(pointers[0].x, pointers[0].y).subtract(startPositions[pointers[0].id]!!)
            tmpVec2.set(pointers[1].x, pointers[1].y).subtract(startPositions[pointers[1].id]!!)

            tmpVec1.mul(1.0 / scale)
            tmpVec2.mul(1.0 / scale)

            if (tmpVec1.length() > 5.0 && tmpVec2.length() > 5.0 && tmpVec1.dot(tmpVec2) > 0.0) {
                tmpVec1.set(startPositions[pointers[0].id]!!)
                tmpVec2.set(startPositions[pointers[1].id]!!)

                currentGesture.init(TWO_FINGER_DRAG, tmpVec1, tmpVec2, scale)
                handleGesture(pointers)
                return true
            }
        }
        return false
    }

    private fun handleGesture(pointers: MutableList<Pointer>) {
        if (pointers.size == 2) {
            tmpVec1.set(pointers[0].x, pointers[0].y)
            tmpVec2.set(pointers[1].x, pointers[1].y)
            currentGesture.update(tmpVec1, tmpVec2, scale)

            pointers[0].consume()
            pointers[1].consume()
        } else {
            currentGesture.type = INVALID
        }
    }

    companion object {
        const val INVALID = 0
        const val INDETERMINATE = -1
        const val PINCH = 1
        const val TWO_FINGER_DRAG = 2
    }

    class Gesture {
        val centerStart = MutableVec2d()
        val centerCurrent = MutableVec2d()
        val centerShift = MutableVec2d()
        val dCenter = MutableVec2d()

        var pinchAmountStart = 0.0
        var pinchAmountCurrent = 0.0
        var dPinchAmount = 0.0
        val pinchAmountRel: Double
            get() = (pinchAmountCurrent - pinchAmountStart) / pinchAmountStart + 1f

        var type = INVALID

        var numUpdates = 0

        internal fun init(type: Int, ptr1: Vec2d, ptr2: Vec2d, scale: Float) {
            this.type = type
            centerStart.set(ptr1).add(ptr2).mul(0.5)
            centerCurrent.set(centerStart)
            centerShift.set(Vec2d.ZERO)
            dCenter.set(Vec2d.ZERO)

            pinchAmountStart = ptr1.distance(ptr2) * 1f / scale
            pinchAmountCurrent = pinchAmountStart
            dPinchAmount = 0.0

            numUpdates = 0
        }

        internal fun update(ptr1: Vec2d, ptr2: Vec2d, scale: Float) {
            dCenter.set(ptr1).add(ptr2).mul(0.5).subtract(centerCurrent)
            centerCurrent.set(ptr1).add(ptr2).mul(0.5)

            centerShift.set(centerCurrent).subtract(centerStart)

            val pinch = ptr1.distance(ptr2) * 1f / scale
            dPinchAmount = pinch - pinchAmountCurrent
            pinchAmountCurrent = pinch

            numUpdates++
        }
    }
}
