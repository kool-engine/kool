package de.fabmax.kool.scene.animation

import de.fabmax.kool.scene.Model

class AnimationController(val model: Model) {
    var initialState = ""
    private val states = mutableListOf<AnimationState>()

    private var currentState = initialState

    fun update(deltaTime: Float) {
        for (i in states.indices) {
            val state = states[i]
            val newState = state.update(deltaTime, state.name == currentState)
            if (newState != currentState) {
                currentState = newState
                state.onExit()
                states.find { it.name == newState }?.onEnter?.invoke()
            }
        }
        model.applyAnimation(deltaTime)
    }

    fun state(stateName: String, body: AnimationState.() -> Unit) = AnimationState(model, stateName).apply {
        body()
        states.add(this)
    }
}

class AnimationState(val model: Model, var name: String) {
    private val animations = mutableListOf<Animation>()
    private val transitions = mutableListOf<Transition>()
    private var weight = 0f

    var blendTransition = 0.2f
    var onEnter: () -> Unit = {}
    var onExit: () -> Unit = {}
    var onUpdate: (Float) -> Unit = {}

    fun update(deltaTime: Float, isActiveState: Boolean): String {
        updateWeight(deltaTime, isActiveState)
        onUpdate(deltaTime)
        for (i in animations.indices) {
            animations[i].update(deltaTime, weight)
        }
        return transitions.firstOrNull { it.predicate() }?.nextState ?: name
    }

    private fun updateWeight(deltaTime: Float, activeState: Boolean) {
        if (activeState) weight += deltaTime / blendTransition
        else weight -= deltaTime / blendTransition
        weight = weight.coerceIn(0f, 1f)
    }

    fun animations(vararg names: String, blendTransition: Float = 0.2f, condition: () -> Boolean = { true }) {
        names.forEach {
            animations.add(Animation(model).apply {
                this.blendTransition = blendTransition
                this.predicate = condition
                this.name = it
            })
        }
    }

    fun transition(transitionState: String, condition: () -> Boolean) = Transition().apply {
        nextState = transitionState
        predicate = condition
        transitions.add(this)
    }

    class Animation(val model: Model) {
        var name = ""
            set(value) {
                field = value
                animation = model.animations.find { it.name == this.name } ?: error("Animation $name not found!")
            }
        lateinit var animation: de.fabmax.kool.scene.animation.Animation
        var predicate: () -> Boolean = { true }
        var blendTransition = 0.2f
        private var weight = 0f

        fun update(deltaTime: Float, stateWeight: Float) {
            updateWeight(deltaTime, predicate())
            animation.weight = stateWeight * weight
        }

        private fun updateWeight(deltaTime: Float, activeState: Boolean) {
            if (activeState) weight += deltaTime / blendTransition
            else weight -= deltaTime / blendTransition
            weight = weight.coerceIn(0f, 1f)
        }
    }

    class Transition {
        var nextState = ""
        var predicate: () -> Boolean = { true }
    }
}