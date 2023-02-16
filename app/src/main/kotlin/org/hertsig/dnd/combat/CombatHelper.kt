package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.IconButton
import org.hertsig.compose.component.TextLine
import org.hertsig.compose.component.VerticalDivider
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.rememberAppState
import org.hertsig.dnd.dice.Dice

@Composable
fun CombatHelper() {
    logEntries = remember { mutableStateListOf(LogEntry.Roll("Border test", "", Dice(listOf(4,6,8,10,12,20), 1).roll())) }
//    logEntries = remember { mutableStateListOf() }
    val state = rememberAppState()
    Column {
        TitleBar(state)

        val page = state.page
        Row {
            when (page) {
                Page.PrepareCombat -> InitiativeList(state, false)
                Page.Combat -> InitiativeList(state, true)
                else -> StatBlockList(state)
            }
            VerticalDivider()
            val modifier = Modifier.weight(1f)
            when (page) {
                is Page.Show -> ReadonlySheet(page.statBlock, modifier.padding(8.dp))
                is Page.Edit -> EditableSheet(page.statBlock, state, modifier)
                Page.PrepareCombat -> PrepareCombatPage(state, modifier)
                Page.Combat -> CombatPage(state, modifier)
                else -> Spacer(modifier)
            }
            VerticalDivider()
            Log(logEntries)
        }
    }
}

@Composable
private fun TitleBar(state: AppState) {
    val page = state.page
    val subtitle = when (page) {
        null -> ""
        is Page.Show -> " — ${page.statBlock.name}"
        is Page.Edit -> " — Edit ${page.statBlock.name}"
        Page.PrepareCombat -> " — Prepare combat"
        Page.Combat -> " — Combat"
    }
    TopAppBar({ TextLine("Combat helper$subtitle", style = MaterialTheme.typography.h5) }, actions = {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val none = page == null
            val show = page is Page.Show
            val edit = page is Page.Edit
            val prepareCombat = page is Page.PrepareCombat
            val combat = page is Page.Combat
            BarButton(Icons.Default.KeyboardArrowUp, combat) { state.previousInitiative() }
            BarButton(Icons.Default.KeyboardArrowDown, combat) { state.nextInitiative() }
            BarButton(Icons.Default.Shield, none || show) { state.toCombat() }
            BarButton(Icons.Default.Settings, combat) { state.toPrepareCombat() }
            BarButton(Icons.Default.RestartAlt, prepareCombat || combat) { state.restartInitiative() }
            BarButton(Icons.Default.Start, prepareCombat) { state.startCombat() }
            BarButton(Icons.Default.Visibility, !show) { state.show() }
            BarButton(Icons.Default.Edit, !edit) { state.edit() }
            BarButton(Icons.Default.Save, none || show) { state.save() }
            BarButton(Icons.Default.Save, edit) { state.saveUpdated() }
            BarButton(Icons.Default.FileCopy, show || edit) { state.copyCurrent() }
            BarButton(Icons.Default.Delete, show || edit) { state.deleteCurrent() }
        }
    })
}

@Composable
private fun BarButton(icon: ImageVector, visible: Boolean = true, enabled: Boolean = true, onClick: () -> Unit) {
    if (visible) IconButton(onClick, icon, enabled = enabled)
}
