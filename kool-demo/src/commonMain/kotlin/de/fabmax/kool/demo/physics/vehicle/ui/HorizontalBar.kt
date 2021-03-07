package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.scene.ui.ComponentUi
import de.fabmax.kool.scene.ui.UiComponent
import de.fabmax.kool.scene.ui.UiRoot
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient

class HorizontalBar(name: String, root: UiRoot) : UiComponent(name, root) {
    var value = 0.0f
        set(value) {
            field = value
            isTrackUpdate = true
        }

    var trackColor = ColorGradient(Color.MD_ORANGE, Color.MD_ORANGE_100, Color.MD_ORANGE)
        set(value) {
            field = value
            isTrackUpdate = true
        }

    private var isTrackUpdate = false

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return HorizontalBarUi(this)
    }

    override fun updateComponent(ctx: KoolContext) {
        super.updateComponent(ctx)
        if (isTrackUpdate) {
            isTrackUpdate = false
            val barUi = ui.prop
            if (barUi is HorizontalBarUi) {
                barUi.updateTrack()
            } else {
                requestUiUpdate()
            }
        }
    }
}

class HorizontalBarUi(private val horizontalBar: HorizontalBar) : BarUi(horizontalBar) {
    init {
        // compute track positions
        val w = 0.15f
        centerTrack += Vec2f(tilt * w/2f, w/2f)
        centerTrack += Vec2f(1f - tilt * w/2f, w/2f)

        // compute track length
        var len = 0f
        for (i in 0 until centerTrack.lastIndex) {
            len += centerTrack[i].distance(centerTrack[i+1])
        }
        var pos = 0f
        for (i in 0 until centerTrack.lastIndex) {
            centerTrackRelPos += pos / len
            pos += centerTrack[i].distance(centerTrack[i+1])
        }
        centerTrackRelPos += 1f

        // compute left directions
        centerTrackLefts += Vec2f(w/2 * tilt, w/2f)
        centerTrackLefts += Vec2f(w/2 * tilt, w/2f)

        numIntervals = 16
    }

    override fun updateUi(ctx: KoolContext) {
        trackScale = horizontalBar.width
        super.updateUi(ctx)
        updateTrack()
    }

    fun updateTrack() {
        horizontalBar.setupBuilder(trackBuilder)

        val nrmVal = 0.5f + horizontalBar.value * 0.5f
        val from = if (horizontalBar.value < 0f) nrmVal else 0.5f
        val to = if (horizontalBar.value > 0f) nrmVal else 0.5f
        trackBuilder.color = horizontalBar.trackColor.getColor(nrmVal).withAlpha(0.75f)
        trackBuilder.fillTrack(from - 0.015f, to + 0.015f) { trackBuilder.color }
    }
}