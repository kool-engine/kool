package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MsdfFont

data class Sizes(
    val normalText: Font,
    val largeText: Font,
    val sliderHeight: Dp,
    val checkboxSize: Dp,
    val radioButtonSize: Dp,
    val switchSize: Dp,
    val gap: Dp,
    val smallGap: Dp,
    val largeGap: Dp,
    val borderWidth: Dp,
) {

    companion object {
        val small = small()
        val medium = medium()
        val large = large()

        fun small(
            normalText: Font = MsdfFont(sizePts = 12f),
            largeText: Font = MsdfFont(sizePts = 16f),
            sliderHeight: Dp = Dp(14f),
            checkboxSize: Dp = Dp(14f),
            radioButtonSize: Dp = Dp(14f),
            switchSize: Dp = Dp(14f),
            gap: Dp = Dp(8f),
            smallGap: Dp = Dp(4f),
            largeGap: Dp = Dp(16f),
            borderWidth: Dp = Dp.roundToWholePx(1f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            sliderHeight,
            checkboxSize,
            radioButtonSize,
            switchSize,
            gap,
            smallGap,
            largeGap,
            borderWidth
        )

        fun medium(
            normalText: Font = MsdfFont(sizePts = 15f),
            largeText: Font = MsdfFont(sizePts = 20f),
            sliderHeight: Dp = Dp(17f),
            checkboxSize: Dp = Dp(17f),
            radioButtonSize: Dp = Dp(17f),
            switchSize: Dp = Dp(17f),
            gap: Dp = Dp(10f),
            smallGap: Dp = Dp(5f),
            largeGap: Dp = Dp(20f),
            borderWidth: Dp = Dp.roundToWholePx(1f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            sliderHeight,
            checkboxSize,
            radioButtonSize,
            switchSize,
            gap,
            smallGap,
            largeGap,
            borderWidth
        )

        fun large(
            normalText: Font = MsdfFont(sizePts = 18f),
            largeText: Font = MsdfFont(sizePts = 24f),
            sliderHeight: Dp = Dp(20f),
            checkboxSize: Dp = Dp(20f),
            radioButtonSize: Dp = Dp(20f),
            switchSize: Dp = Dp(20f),
            gap: Dp = Dp(12f),
            smallGap: Dp = Dp(6f),
            largeGap: Dp = Dp(24f),
            borderWidth: Dp = Dp.roundToWholePx(1.5f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            sliderHeight,
            checkboxSize,
            radioButtonSize,
            switchSize,
            gap,
            smallGap,
            largeGap,
            borderWidth
        )
    }
}