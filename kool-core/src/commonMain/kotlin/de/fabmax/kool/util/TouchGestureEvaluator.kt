package de.fabmax.kool.util

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f

/**
 * Evaluates standard touch gestures (pinch-to-zoom, two-finger drag)
 */

open class TouchGestureEvaluator {

    var currentGesture = Gesture()
        protected set

    private val activePointers = mutableListOf<InputManager.Pointer>()
    private val tmpVec1 = MutableVec2f()
    private val tmpVec2 = MutableVec2f()

    protected val startPositions = mutableMapOf<Int, Vec2f>()
    protected var screenDpi = 96f

    fun evaluate(ctx: KoolContext) {
        screenDpi = ctx.screenDpi
        ctx.inputMgr.getActivePointers(activePointers)

        if (activePointers.size > 1) {
            when (currentGesture.type) {
                INVALID -> onGestureInit(activePointers)
                INDETERMINATE -> onDetermineGesture(activePointers)
                PINCH -> handleGesture(activePointers)
                TWO_FINGER_DRAG -> handleGesture(activePointers)
            }

        } else {
            // not enough valid pointers for a multi-touch gesture
            currentGesture.type = INVALID
            startPositions.clear()
        }
    }

    protected open fun onGestureInit(pointers: MutableList<InputManager.Pointer>) {
        pointers.forEach { startPositions[it.id] = Vec2f(it.x, it.y) }
        currentGesture.type = INDETERMINATE
    }

    protected open fun onDetermineGesture(pointers: MutableList<InputManager.Pointer>) {
        // remove any missing pointer
        startPositions.keys.removeAll { ptrId -> pointers.find { it.id == ptrId } == null }

        // add any new pointer
        pointers.filter { !startPositions.containsKey(it.id) }
                .forEach { startPositions[it.id] = Vec2f(it.x, it.y) }

        // try to match gesture
        when {
            isPinch(pointers) -> currentGesture.type = PINCH
            isTwoFingerDrag(pointers) -> currentGesture.type = TWO_FINGER_DRAG
        }
    }

    protected open fun isPinch(pointers: MutableList<InputManager.Pointer>): Boolean {
        // two pointers moving in opposing direction
        if (pointers.size == 2) {
            tmpVec1.set(pointers[0].x, pointers[0].y).subtract(startPositions[pointers[0].id]!!)
            tmpVec2.set(pointers[1].x, pointers[1].y).subtract(startPositions[pointers[1].id]!!)

            tmpVec1.scale(96f / screenDpi)
            tmpVec2.scale(96f / screenDpi)

            if (tmpVec1.length() > 5f && tmpVec2.length() > 5f && tmpVec1 * tmpVec2 < 0) {
                tmpVec1.set(startPositions[pointers[0].id]!!)
                tmpVec2.set(startPositions[pointers[1].id]!!)

                currentGesture.init(PINCH, tmpVec1, tmpVec2, screenDpi)
                handleGesture(pointers)
                return true
            }
        }
        return false
    }

    protected open fun isTwoFingerDrag(pointers: MutableList<InputManager.Pointer>): Boolean {
        // two pointers moving in same direction
        if (pointers.size == 2) {
            tmpVec1.set(pointers[0].x, pointers[0].y).subtract(startPositions[pointers[0].id]!!)
            tmpVec2.set(pointers[1].x, pointers[1].y).subtract(startPositions[pointers[1].id]!!)

            tmpVec1.scale(96f / screenDpi)
            tmpVec2.scale(96f / screenDpi)

            if (tmpVec1.length() > 5f && tmpVec2.length() > 5f && tmpVec1 * tmpVec2 > 0) {
                tmpVec1.set(startPositions[pointers[0].id]!!)
                tmpVec2.set(startPositions[pointers[1].id]!!)

                currentGesture.init(TWO_FINGER_DRAG, tmpVec1, tmpVec2, screenDpi)
                handleGesture(pointers)
                return true
            }
        }
        return false
    }

    protected open fun handleGesture(pointers: MutableList<InputManager.Pointer>) {
        if (pointers.size == 2) {
            tmpVec1.set(pointers[0].x, pointers[0].y)
            tmpVec2.set(pointers[1].x, pointers[1].y)
            currentGesture.update(tmpVec1, tmpVec2, screenDpi)
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

    open class Gesture {
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

        internal fun init(type: Int, ptr1: Vec2f, ptr2: Vec2f, dpi: Float) {
            this.type = type
            centerStart.set(ptr1).add(ptr2).scale(0.5f)
            centerCurrent.set(centerStart)
            centerShift.set(Vec2f.ZERO)
            dCenter.set(Vec2f.ZERO)

            pinchAmountStart = ptr1.distance(ptr2) * 96f / dpi
            pinchAmountCurrent = pinchAmountStart
            dPinchAmount = 0f

            numUpdates = 0
        }

        internal fun update(ptr1: Vec2f, ptr2: Vec2f, dpi: Float) {
            dCenter.set(ptr1).add(ptr2).scale(0.5f).subtract(centerCurrent)
            centerCurrent.set(ptr1).add(ptr2).scale(0.5f)

            centerShift.set(centerCurrent).subtract(centerStart)

            val pinch = ptr1.distance(ptr2) * 96f / dpi
            dPinchAmount = pinch - pinchAmountCurrent
            pinchAmountCurrent = pinch

            numUpdates++
        }
    }
}
