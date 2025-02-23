package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f

/**
 * Evaluates standard touch gestures (pinch-to-zoom, two-finger drag)
 */
class TouchGestureEvaluator {

    val currentGesture = Gesture()

    private val activePointers = mutableListOf<Pointer>()
    private val tmpVec1 = MutableVec2f()
    private val tmpVec2 = MutableVec2f()

    private val startPositions = mutableMapOf<Int, Vec2f>()
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
        pointers.forEach { startPositions[it.id] = Vec2f(it.pos) }
        currentGesture.type = INDETERMINATE
    }

    private fun onDetermineGesture(pointers: MutableList<Pointer>) {
        // remove any missing pointer
        startPositions.keys.removeAll { ptrId -> pointers.find { it.id == ptrId } == null }

        // add any new pointer
        pointers.filter { !startPositions.containsKey(it.id) }
                .forEach { startPositions[it.id] = Vec2f(it.pos) }

        // try to match gesture
        when {
            isPinch(pointers) -> currentGesture.type = PINCH
            isTwoFingerDrag(pointers) -> currentGesture.type = TWO_FINGER_DRAG
        }
    }

    private fun isPinch(pointers: MutableList<Pointer>): Boolean {
        // two pointers moving in opposing direction
        if (pointers.size == 2) {
            tmpVec1.set(pointers[0].pos).subtract(startPositions[pointers[0].id]!!)
            tmpVec2.set(pointers[1].pos).subtract(startPositions[pointers[1].id]!!)

            tmpVec1.mul(1.0f / scale)
            tmpVec2.mul(1.0f / scale)

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
            tmpVec1.set(pointers[0].pos).subtract(startPositions[pointers[0].id]!!)
            tmpVec2.set(pointers[1].pos).subtract(startPositions[pointers[1].id]!!)

            tmpVec1.mul(1.0f / scale)
            tmpVec2.mul(1.0f / scale)

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
            tmpVec1.set(pointers[0].pos)
            tmpVec2.set(pointers[1].pos)
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
        val centerStart = MutableVec2f()
        val centerCurrent = MutableVec2f()
        val centerShift = MutableVec2f()
        val dCenter = MutableVec2f()

        var pinchAmountStart = 0f
        var pinchAmountCurrent = 0f
        var dPinchAmount = 0f
        val pinchAmountRel: Float
            get() = (pinchAmountCurrent - pinchAmountStart) / pinchAmountStart + 1f

        var type = INVALID

        var numUpdates = 0

        internal fun init(type: Int, ptr1: Vec2f, ptr2: Vec2f, scale: Float) {
            this.type = type
            centerStart.set(ptr1).add(ptr2).mul(0.5f)
            centerCurrent.set(centerStart)
            centerShift.set(Vec2f.ZERO)
            dCenter.set(Vec2f.ZERO)

            pinchAmountStart = ptr1.distance(ptr2) * 1f / scale
            pinchAmountCurrent = pinchAmountStart
            dPinchAmount = 0f

            numUpdates = 0
        }

        internal fun update(ptr1: Vec2f, ptr2: Vec2f, scale: Float) {
            dCenter.set(ptr1).add(ptr2).mul(0.5f).subtract(centerCurrent)
            centerCurrent.set(ptr1).add(ptr2).mul(0.5f)

            centerShift.set(centerCurrent).subtract(centerStart)

            val pinch = ptr1.distance(ptr2) * 1f / scale
            dPinchAmount = pinch - pinchAmountCurrent
            pinchAmountCurrent = pinch

            numUpdates++
        }
    }
}
