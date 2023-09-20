package org.hertsig.dnd.combat

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.hertsig.compose.component.Theme
import org.hertsig.compose.registerExceptionHandler
import org.hertsig.dnd.combat.dto.LogEntry
import org.hertsig.dnd.combat.dto.rememberAppState
import org.hertsig.dnd.dice.Dice
import org.hertsig.dnd.dice.MultiDieRolls


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

fun main() {
    registerExceptionHandler()

    application {
        logEntries = remember { mutableStateListOf(LogEntry.Roll("Border test", "", MultiDieRolls(Dice(listOf(4,6,8,10,12,20), 1).roll()))) }
//        logEntries = remember { mutableStateListOf() }
        val state = rememberAppState()

        Window(::exitApplication, rememberWindowState(
            position = WindowPosition(2600.dp, 400.dp),
            width = 1800.dp,
            height = 1000.dp,
        ), title = "Combat helper") {
            @OptIn(ExperimentalMaterial3Api::class)
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                Theme(LightColors) {
                    CombatHelper(state)
                }
            }
        }
    }
}
