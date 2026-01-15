package de.fabmax.kool.modules.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import de.fabmax.kool.modules.compose.composables.rendering.TextStyle
import de.fabmax.kool.modules.ui2.Colors
import de.fabmax.kool.modules.ui2.Sizes
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.util.Color

val LocalUiSurface = compositionLocalOf<UiSurface> { error("No UiSurface provided") }

val LocalColors = compositionLocalOf<Colors> { error("No Colors provided") }

val LocalSizes = compositionLocalOf<Sizes> { error("No Sizes provided") }

val LocalSurfaceContentCompat = compositionLocalOf<SurfaceContentCompat> { error("No SurfaceContentCompat provided") }

val Colors @Composable get() = LocalColors.current
val Sizes @Composable get() = LocalSizes.current

val LocalTextStyle = compositionLocalOf(structuralEqualityPolicy()) { TextStyle.Default }
val LocalContentColor = compositionLocalOf { Color.BLACK }

val LocalZLayer = compositionLocalOf { 0 }

@Composable
inline fun ProvideZLayer(offset: Int, crossinline content: @Composable () -> Unit) {
    val current = LocalZLayer.current
    CompositionLocalProvider(LocalZLayer provides current + offset) {
        content()
    }
}
