package de.fabmax.kool.demo

import de.fabmax.kool.audio.Kick
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Scene

/**
 * @author fabmax
 */

fun synthieScene(): Scene {
    return SynthieScene()
}

private class SynthieScene: Scene() {

    private val audioGen = Platform.getAudioImpl().newAudioGenerator { t -> nextSample(t) }

    private val kick = Kick(120f)

    private fun nextSample(t: Double): Float {
        return kick.clockAndPlay(t)
    }

    override fun dispose(ctx: RenderContext) {
        audioGen.stop()
        super.dispose(ctx)
    }
}
