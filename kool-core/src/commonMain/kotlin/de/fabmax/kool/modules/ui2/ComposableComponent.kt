package de.fabmax.kool.modules.ui2

interface ComposableComponent {
    fun UiScope.compose(): Any
}

fun ComposableComponent(block: UiScope.() -> Any): ComposableComponent {
    return object : ComposableComponent {
        override fun UiScope.compose() = block
    }
}
