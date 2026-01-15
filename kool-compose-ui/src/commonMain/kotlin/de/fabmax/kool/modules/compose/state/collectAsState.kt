package de.fabmax.kool.modules.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import de.fabmax.kool.modules.ui2.MutableStateValue

/**
 * Collects a Kool [MutableStateValue] as a Compose [State].
 */
@Composable
fun <T> MutableStateValue<T>.collectAsState(): State<T> {
    return produceState(this.value) {
        val producer = this
        producer.value = this@collectAsState.value // Emit initial state
        onChange { old, new -> producer.value = new } // Subscribe to state changes
    }
}
