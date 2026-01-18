package de.fabmax.kool.modules.compose.composables.toolkit

import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.modules.compose.LocalUiSurface
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.ui2.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun rememberFocusRequester(): FocusRequester {
    return remember { FocusRequester() }
}

@Composable
fun Focusable(
    focusRequester: FocusRequester = rememberFocusRequester(),
    //TODO remove in favor of a modifier
    onFocusChange: (Boolean) -> Unit = {},
    onKeyboardInput: (KeyEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val surface = LocalUiSurface.current
    val onFocusChanged by rememberUpdatedState(onFocusChange)
    val onKeyboardInput by rememberUpdatedState(onKeyboardInput)
    val focusable = remember(surface) { FocusableComposeNode(null, surface) }
    // TODO bad pattern, these should be modifiers, avoiding too much refactoring for now.
    focusable.onFocusChanged = onFocusChanged
    focusable.onKeyEvent = onKeyboardInput
    LaunchedEffect(focusable, focusRequester) {
        focusRequester.request.collect {
            surface.requestFocus(focusable)
        }
    }

    Layout({ _, _ -> focusable }, modifier) {
        content()
    }
}

class FocusRequester {
    val request = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun requestFocus() {
        request.tryEmit(Unit)
    }
}

class FocusableComposeNode(
    parent: UiNode?,
    surface: UiSurface,
) : UiNode(parent, surface), Focusable {
    var onFocusChanged: ((Boolean) -> Unit)? = null
    var onKeyEvent: ((KeyEvent) -> Unit)? = null
    override val modifier: UiModifier = UiModifier(surface)
    override val isFocused: MutableStateValue<Boolean> = MutableStateValue(false)

    override fun onFocusGain() {
        super.onFocusGain()
        onFocusChanged?.invoke(true)
    }

    override fun onFocusLost() {
        super.onFocusLost()
        onFocusChanged?.invoke(false)
    }

    override fun onKeyEvent(keyEvent: KeyEvent) {
        onKeyEvent?.invoke(keyEvent)
    }
}
