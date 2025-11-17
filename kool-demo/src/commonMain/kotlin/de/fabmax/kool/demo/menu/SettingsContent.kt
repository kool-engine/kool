package de.fabmax.kool.demo.menu

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.demo.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.L10n
import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.l

class SettingsContent(val menu: DemoMenu) : Composable {
    override fun UiScope.compose() = Column {
        modifier
            .height(Grow.Std)
            .width(Grow.Std)

        Text("Settings".l) {
            modifier
                .width(Grow.Std)
                .height(UiSizes.baseSize)
                .padding(horizontal = UiSizes.hGap)
                .textAlignY(AlignmentY.Center)
                .background(TitleBgRenderer(DemoMenu.titleBgMesh, 0.75f, 0.95f))
                .font((sizes.largeText as MsdfFont).copy(glowColor = DemoMenu.titleTextGlowColor))
                .textColor(Color.WHITE)
        }

        MenuRow {
            Text("Language".l) { labelStyle() }
            ComboBox {
                val languageOptions = L10n.availableLanguages
                val selected = Settings.language.use()
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(languageOptions.map { it.languageName })
                    .selectedIndex(languageOptions.indexOfFirst { it.languageKey == selected })
                    .onItemSelected {
                        Settings.language.set(languageOptions[it].languageKey)
                        L10n.selectedLanguageState.set(languageOptions[it].languageKey)
                    }
            }
        }
        MenuRow {
            Text("UI size".l) { labelStyle() }
            ComboBox {
                val uiSizeOptions = Settings.defaultUiSizes.values.toList()
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(uiSizeOptions.map { it.name.l })
                    .selectedIndex(uiSizeOptions.indexOf(Settings.uiSize.use()))
                    .onItemSelected { Settings.uiSize.set(uiSizeOptions[it]) }
            }
        }
        MenuRow {
            Text("Render scale".l) { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(renderScales.map { "$it %" })
                    .selectedIndex(renderScales.indexOf(Settings.renderScale.use()))
                    .onItemSelected {
                        Settings.renderScale.set(renderScales[it])
                        KoolSystem.requireContext().window.renderResolutionFactor = renderScales[it] / 100f
                    }
            }
        }
        LabeledSwitch("Menu initially expanded".l, Settings.showMenuOnStartup)
        LabeledSwitch("Debug overlay".l, Settings.showDebugOverlay)
        LabeledSwitch("Fullscreen".l, Settings.isFullscreen)
        LabeledSwitch("Hidden demos".l, Settings.showHiddenDemos)

        menu.demoLoader.activeDemo?.let {
            LabeledSwitch("Show demo menus".l, it.isMenu)
        }
    }

    companion object {
        val renderScales = listOf(
            50,
            67,
            75,
            100
        )
    }
}