package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.clamp

abstract class AnimatedState<T: Any>(initValue: T) : MutableStateValue<T>(initValue) {
    abstract val isActive: Boolean

    abstract fun progress(deltaT: Float)
}

class AnimatedFloat(var duration: Float, initValue: Float = 1f) : AnimatedState<Float>(initValue) {
    override var isActive = false
        private set
    var progressionTime = duration
        private set

    fun start() {
        set(0f)
        stateChanged()
        progressionTime = 0f
        isActive = true
    }

    override fun progress(deltaT: Float) {
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

class AnimatedFloatBidir(var fwdDuration: Float, var bwdDuration: Float = fwdDuration, initValue: Float = 0f) : AnimatedState<Float>(initValue) {
    private var target = 0f
    override val isActive: Boolean get() = value != target

    val isForward: Boolean
        get() = value <= target

    init {
        target = if (initValue > 0f) 1f else 0f
    }

    fun start(target: Float) {
        this.target = if (target > 0f) 1f else 0f
        stateChanged()
    }

    fun toggle() {
        if (value > 0f) {
            start(0f)
        } else {
            start(1f)
        }
    }

    override fun progress(deltaT: Float) {
        if (isActive) {
            val newValue = if (value < target) {
                value + deltaT / fwdDuration
            } else {
                value - deltaT / bwdDuration
            }
            set(newValue.clamp())
        }
    }
}
