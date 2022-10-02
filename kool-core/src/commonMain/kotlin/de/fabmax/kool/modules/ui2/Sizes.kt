package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps

data class Sizes(
    val normalText: FontProps,
    val largeText: FontProps,
    val sliderHeight: Dp,
    val checkboxSize: Dp,
    val radioButtonSize: Dp,
    val switchSize: Dp,
    val gap: Dp,
    val smallGap: Dp,
    val largeGap: Dp,
) {

    companion object {
        fun small(
            normalText: FontProps = FontProps(Font.SYSTEM_FONT, 12f, isScaledByWindowScale = false, sampleScale = 2f),
            largeText: FontProps = FontProps(Font.SYSTEM_FONT, 16f, isScaledByWindowScale = false, sampleScale = 2f),
            sliderHeight: Dp = Dp(14f),
            checkboxSize: Dp = Dp(14f),
            radioButtonSize: Dp = Dp(14f),
            switchSize: Dp = Dp(14f),
            gap: Dp = Dp(8f),
            smallGap: Dp = Dp(4f),
            largeGap: Dp = Dp(16f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            sliderHeight,
            checkboxSize,
            radioButtonSize,
            switchSize,
            gap,
            smallGap,
            largeGap
        )

        fun medium(
            normalText: FontProps = FontProps(Font.SYSTEM_FONT, 15f, isScaledByWindowScale = false, sampleScale = 2f),
            largeText: FontProps = FontProps(Font.SYSTEM_FONT, 20f, isScaledByWindowScale = false),
            sliderHeight: Dp = Dp(17f),
            checkboxSize: Dp = Dp(17f),
            radioButtonSize: Dp = Dp(17f),
            switchSize: Dp = Dp(17f),
            gap: Dp = Dp(10f),
            smallGap: Dp = Dp(5f),
            largeGap: Dp = Dp(20f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            sliderHeight,
            checkboxSize,
            radioButtonSize,
            switchSize,
            gap,
            smallGap,
            largeGap
        )

        fun large(
            normalText: FontProps = FontProps(Font.SYSTEM_FONT, 18f, isScaledByWindowScale = false),
            largeText: FontProps = FontProps(Font.SYSTEM_FONT, 24f, isScaledByWindowScale = false),
            sliderHeight: Dp = Dp(20f),
            checkboxSize: Dp = Dp(20f),
            radioButtonSize: Dp = Dp(20f),
            switchSize: Dp = Dp(20f),
            gap: Dp = Dp(12f),
            smallGap: Dp = Dp(6f),
            largeGap: Dp = Dp(24f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            sliderHeight,
            checkboxSize,
            radioButtonSize,
            switchSize,
            gap,
            smallGap,
            largeGap
        )
    }
}