package de.fabmax.kool.modules.compose.composables.rendering

import androidx.compose.runtime.Composable
import de.fabmax.kool.modules.compose.composables.Layout
import de.fabmax.kool.modules.compose.modifiers.edit
import de.fabmax.kool.modules.compose.modifiers.optionalEdit
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color
import me.dvyy.compose.mini.modifier.Modifier

@Composable
fun Image(
    texture: Texture2d,
    tint: Color? = null,
    size: ImageSize? = null,
    imageZ: Int? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        ::ImageNode, modifier
            .edit<ImageModifier> { it.image(texture) }
            .optionalEdit<ImageModifier>(tint != null) { it.tint(tint!!).customShader }
            .optionalEdit<ImageModifier>(size != null) { it.imageSize(size!!) }
            .optionalEdit<ImageModifier>(imageZ != null) { it.imageZ(imageZ!!) }
    ) {
        content()
    }
}
