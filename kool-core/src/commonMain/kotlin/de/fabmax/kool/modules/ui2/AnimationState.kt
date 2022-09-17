package de.fabmax.kool.modules.ui2

open class AnimationState(var duration: Float) : MutableValueState<Float>(0f) {
    var isActive = false
        private set
    var progressionTime = 0f
        private set
    val isFinished: Boolean get() = value == 1f

    fun start() {
        set(0f)
        stateChanged()
        progressionTime = 0f
        isActive = true
    }

    fun progress(deltaT: Float) {
        if (progressionTime < duration) {
            progressionTime += deltaT
            if (progressionTime >= duration) {
                set(1f)
                isActive = false
            } else {
                set(progressionTime / duration)
            }
        } else {
            isActive = false
        }
    }
}