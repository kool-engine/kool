package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.util.ThumbnailRenderer
import de.fabmax.kool.editor.util.ThumbnailState
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time

class BrowserThumbnails<T: Any>(
    val renderer: ThumbnailRenderer,
    val thumbnailProvider: ThumbnailRenderer.(T) -> ThumbnailRenderer.Thumbnail
) {
    val loadedThumbnails = mutableMapOf<T, ComposableThumbnail>()

    fun getThumbnail(key: T): ThumbnailRenderer.Thumbnail? {
        return loadedThumbnails[key]?.thumbnail
    }

    fun getThumbnailComposable(key: T): BrowserItemComposable {
        return loadedThumbnails.getOrPut(key) { ComposableThumbnail(key) }
    }

    inner class ComposableThumbnail(val key: T) : BrowserItemComposable {
        var thumbnail: ThumbnailRenderer.Thumbnail? = null

        override fun getComposable(sizeDp: Vec2f?, alpha: Float) = Composable {
            var thumb = thumbnail
            var thumbNailState = thumb?.state?.use()
            if (thumb == null || thumbNailState == null || thumbNailState == ThumbnailState.DESTROYED) {
                thumb = renderer.thumbnailProvider(key).also { thumbnail = it }
                thumbNailState = thumb.state.use()

            } else if (thumbNailState == ThumbnailState.USABLE_OUTDATED) {
                thumb.update()
            }

            val width = sizeDp?.x?.dp ?: Dp.fromPx(renderer.tileSize.x.toFloat())
            val height = sizeDp?.y?.dp ?: Dp.fromPx(renderer.tileSize.y.toFloat())

            if (thumbNailState.isUsable) {
                thumb.lastUsed = Time.frameCount
                Image {
                    modifier
                        .size(width, height)
                        .alignX(AlignmentX.Center)
                        .margin(sizes.smallGap)
                        .imageProvider(thumb)
                        .tint(Color.WHITE.withAlpha(alpha))
                }
            } else {
                Box {
                    modifier
                        .size(width, height)
                        .alignX(AlignmentX.Center)
                        .margin(sizes.smallGap)
                        .background(RoundRectBackground(MdColor.GREY.withAlpha(alpha), sizes.gap))
                }
            }
        }
    }
}