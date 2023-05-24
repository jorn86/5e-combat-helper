package org.hertsig.dnd.combat

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.hertsig.compose.registerExceptionHandler
import org.hertsig.dnd.combat.dto.rememberAppState

private val colors = lightColors(Color(0xff1775d1), Color(0xff63a3ff), Color(0xffcfff95), Color(0xff9ccc65))

@OptIn(ExperimentalMaterialApi::class)
fun main() {
    registerExceptionHandler()

    application {
//        logEntries = remember { mutableStateListOf(LogEntry.Roll("Border test", "", Dice(listOf(4,6,8,10,12,20), 1).roll())) }
        logEntries = remember { mutableStateListOf() }
        val state = rememberAppState()

        Window(::exitApplication, rememberWindowState(
            position = WindowPosition(2600.dp, 400.dp),
            width = 1800.dp,
            height = 1000.dp,
        ), title = "Combat helper") {
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                MaterialTheme(colors) {
                    CombatHelper(state)
                }
            }
        }
    }
}
