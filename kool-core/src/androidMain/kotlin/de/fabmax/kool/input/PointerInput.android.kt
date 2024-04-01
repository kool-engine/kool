package de.fabmax.kool.input

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

internal actual fun PlatformInput(): PlatformInput = PlatformInputAndroid

object PlatformInputAndroid : PlatformInput, View.OnTouchListener {

    private val tmpCoords = MotionEvent.PointerCoords()

    override fun setCursorMode(cursorMode: CursorMode) { }

    override fun applyCursorShape(cursorShape: CursorShape) { }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val pointerId = event.getPointerId(event.actionIndex)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                event.getPointerCoords(0, tmpCoords)
                PointerInput.handleTouchStart(pointerId, tmpCoords.x.toDouble(), tmpCoords.y.toDouble())
            }
            MotionEvent.ACTION_UP -> {
                event.getPointerCoords(0, tmpCoords)
                PointerInput.handleTouchEnd(pointerId)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                event.getPointerCoords(event.actionIndex, tmpCoords)
                PointerInput.handleTouchStart(pointerId, tmpCoords.x.toDouble(), tmpCoords.y.toDouble())
            }
            MotionEvent.ACTION_POINTER_UP -> {
                PointerInput.handleTouchEnd(pointerId)
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    event.getPointerCoords(i, tmpCoords)
                    val ptrId = event.getPointerId(i)
                    PointerInput.handleTouchMove(ptrId, tmpCoords.x.toDouble(), tmpCoords.y.toDouble())
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                PointerInput.handleTouchCancel(pointerId)
            }
        }
        return true
    }
}