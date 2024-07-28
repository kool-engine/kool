package de.fabmax.kool.editor.util

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Time

class UiThumbnails<T: Any>(
    val renderer: ThumbnailRenderer,
    val thumbnailProvider: ThumbnailRenderer.(T) -> ThumbnailRenderer.Thumbnail
) {
    val loadedThumbnails = mutableMapOf<T, ThumbnailRenderer.Thumbnail>()

    context(UiScope)
    fun getThumbnail(key: T): ThumbnailRenderer.Thumbnail {
        var thumbnail = loadedThumbnails.getOrPut(key) {
            renderer.thumbnailProvider(key)
        }

        if (thumbnail.isReleased.use()) {
            thumbnail = renderer.thumbnailProvider(key)
            loadedThumbnails[key] = thumbnail
        }
        thumbnail.lastUsed = Time.frameCount
        return thumbnail
    }

    context(UiScope)
    fun getThumbnailComposable(key: T): Composable? {
        val thumbnail = getThumbnail(key)
        return if (!thumbnail.isLoaded.use()) null else Composable {
            Image {
                modifier
                    .size(Dp.fromPx(renderer.tileSize.x.toFloat()), Dp.fromPx(renderer.tileSize.y.toFloat()))
                    .alignX(AlignmentX.Center)
                    .margin(sizes.smallGap)
                    .imageProvider(thumbnail)
            }
        }
    }
}