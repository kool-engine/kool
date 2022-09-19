package de.fabmax.kool.modules.ui2

import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps

data class Sizes(
    val normalText: FontProps,
    val largeText: FontProps,
    val buttonHeight: Dp,
    val sliderHeight: Dp,
    val checkboxHeight: Dp,
    val radioButtonHeight: Dp,
    val switchHeight: Dp,
    val gap: Dp,
    val smallGap: Dp,
    val largeGap: Dp,
) {

    companion object {
        fun small(
            normalText: FontProps = FontProps(Font.SYSTEM_FONT, 12f),
            largeText: FontProps = FontProps(Font.SYSTEM_FONT, 18f),
            buttonHeight: Dp = Dp(28f),
            sliderHeight: Dp = Dp(16f),
            checkboxHeight: Dp = Dp(16f),
            radioButtonHeight: Dp = Dp(16f),
            switchHeight: Dp = Dp(16f),
            gap: Dp = Dp(8f),
            smallGap: Dp = Dp(4f),
            largeGap: Dp = Dp(16f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            buttonHeight,
            sliderHeight,
            checkboxHeight,
            radioButtonHeight,
            switchHeight,
            gap,
            smallGap,
            largeGap
        )

        fun normal(
            normalText: FontProps = FontProps(Font.SYSTEM_FONT, 15f),
            largeText: FontProps = FontProps(Font.SYSTEM_FONT, 22f),
            buttonHeight: Dp = Dp(35f),
            sliderHeight: Dp = Dp(20f),
            checkboxHeight: Dp = Dp(20f),
            radioButtonHeight: Dp = Dp(20f),
            switchHeight: Dp = Dp(20f),
            gap: Dp = Dp(10f),
            smallGap: Dp = Dp(5f),
            largeGap: Dp = Dp(20f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            buttonHeight,
            sliderHeight,
            checkboxHeight,
            radioButtonHeight,
            switchHeight,
            gap,
            smallGap,
            largeGap
        )

        fun large(
            normalText: FontProps = FontProps(Font.SYSTEM_FONT, 18f),
            largeText: FontProps = FontProps(Font.SYSTEM_FONT, 27f),
            buttonHeight: Dp = Dp(42f),
            sliderHeight: Dp = Dp(24f),
            checkboxHeight: Dp = Dp(24f),
            radioButtonHeight: Dp = Dp(24f),
            switchHeight: Dp = Dp(24f),
            gap: Dp = Dp(12f),
            smallGap: Dp = Dp(6f),
            largeGap: Dp = Dp(24f)
        ): Sizes = Sizes(
            normalText,
            largeText,
            buttonHeight,
            sliderHeight,
            checkboxHeight,
            radioButtonHeight,
            switchHeight,
            gap,
            smallGap,
            largeGap
        )
    }
}