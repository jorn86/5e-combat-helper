package org.hertsig.dnd.combat

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.hertsig.core.logger
import org.hertsig.dnd.combat.dto.*
import java.util.*

private val log = logger {}
private val colors = lightColors(Color(0xff1775d1), Color(0xff63a3ff), Color(0xffcfff95), Color(0xff9ccc65))

@OptIn(ExperimentalMaterialApi::class)
fun main() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error("Uncaught exception", e)
        e.suppressed.forEach {
            if (it.stackTrace.isNotEmpty()) log.error("Suppressed", it)
        }
    }

    application {
        Window(::exitApplication, rememberWindowState(
            position = WindowPosition(2600.dp, 400.dp),
            width = 1800.dp,
            height = 1000.dp
        ), title = "Combat helper") {
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                MaterialTheme(colors) {
                    Surface {
                        CombatHelper()
                    }
                }
            }
        }
    }
}
