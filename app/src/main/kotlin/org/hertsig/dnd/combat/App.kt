package org.hertsig.dnd.combat

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.hertsig.compose.registerExceptionHandler
import org.hertsig.dnd.combat.dto.*
import java.util.*

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
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                MaterialTheme(colors) {
                    CombatHelper(state)
                }
            }
        }
    }
}
