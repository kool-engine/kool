package de.fabmax.kool.modules.compose.modifiers

import de.fabmax.kool.modules.compose.InternalKoolComposeAPI
import de.fabmax.kool.modules.ui2.UiModifier
import me.dvyy.compose.mini.modifier.Modifier

/**
 * A Compose [Modifier] that applies changes to a Kool [UiModifier] when recomposed.
 *
 * This is currently the entrypoint for most modifiers, as we can reuse the existing modifier system without
 * changing anything.
 */
@InternalKoolComposeAPI
fun interface UiModifierWrapper : Modifier.Element {
    fun applyTo(uiModifier: UiModifier)
}

/**
 * Defines a Compose Modifier which will apply [edit] to a Kool [UiModifier] when recomposed.
 */
@InternalKoolComposeAPI
inline fun <reified T : UiModifier> Modifier.edit(crossinline edit: (T) -> Unit) = then(UiModifierWrapper {
    if (it is T) edit(it)
})

/**
 * Defines a Compose Modifier which will apply [edit] to a Kool [UiModifier] when recomposed, if [shouldApply] is
 * true, skipping otherwise.
 */
@InternalKoolComposeAPI
inline fun <reified T : UiModifier> Modifier.optionalEdit(
    shouldApply: Boolean,
    crossinline edit: (T) -> Unit,
): Modifier {
    return if (shouldApply) edit(edit) else this
}
