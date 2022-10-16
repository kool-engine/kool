package de.fabmax.kool.modules.ui2

interface Composable {
    fun UiScope.compose(): Any
}

fun Composable(block: UiScope.() -> Any): Composable {
    return object : Composable {
        override fun UiScope.compose() = block
    }
}
