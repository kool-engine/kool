package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.scene.ui.ComponentUi
import de.fabmax.kool.scene.ui.UiComponent
import de.fabmax.kool.scene.ui.UiRoot
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor

class VerticalBar(name: String, root: UiRoot) : UiComponent(name, root) {
    var value = 0f
        set(value) {
            field = value
            isTrackUpdate = true
        }

    var trackColor = ColorGradient(Color.WHITE.withAlpha(0.5f), MdColor.ORANGE)
        set(value) {
            field = value
            isTrackUpdate = true
        }

    private var isTrackUpdate = false

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return VerticalBarUi(this)
    }

    override fun updateComponent(ctx: KoolContext) {
        super.updateComponent(ctx)
        if (isTrackUpdate) {
            isTrackUpdate = false
            val barUi = ui.prop
            if (barUi is VerticalBarUi) {
                barUi.updateTrack()
            } else {
                requestUiUpdate()
            }
        }
    }
}

class VerticalBarUi(private val verticalBar: VerticalBar) : BarUi(verticalBar) {

    init {
        // compute track positions
        val w = 0.3f
        centerTrack += Vec2f(w/2f, 0f)
        centerTrack += Vec2f(w/2f + tilt, 1f)

        centerTrack.forEach {
            centerTrackLefts += Vec2f(-w/2f, 0f)
        }

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
    }

    override fun updateUi(ctx: KoolContext) {
        trackScale = verticalBar.height
        super.updateUi(ctx)
        updateTrack()
    }

    fun updateTrack() {
        verticalBar.setupBuilder(trackBuilder)
        trackBuilder.fillTrack(0f, verticalBar.value) { verticalBar.trackColor.getColor(it) }
    }
}