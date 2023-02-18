package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.IconButton
import org.hertsig.compose.component.TextLine
import org.hertsig.compose.component.VerticalDivider
import org.hertsig.dnd.combat.dto.AppState

@Composable
fun CombatHelper(state: AppState) {
    Column {
        TitleBar(state)

        val page = state.page
        Row {
            when (page) {
                Page.PrepareCombat -> InitiativeList(state, Modifier.width(250.dp))
                Page.Combat -> InitiativeList(state, Modifier.width(250.dp), showControls = true)
                else -> StatBlockList(state)
            }
            VerticalDivider()
            val modifier = Modifier.weight(1f)
            when (page) {
                is Page.Show -> ReadonlySheet(page.statBlock, modifier.padding(8.dp))
                is Page.Edit -> EditableSheet(page.statBlock, state, modifier)
                Page.Overview -> OverviewPage(state, modifier)
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
        Page.Overview -> " — Overview"
        Page.PrepareCombat -> " — Prepare combat"
        Page.Combat -> " — Combat"
    }
    TopAppBar({ TextLine("Combat helper$subtitle", style = MaterialTheme.typography.h5) }, actions = {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val none = page == null
            val show = page is Page.Show
            val edit = page is Page.Edit
            val overview = page == Page.Overview
            val prepareCombat = page == Page.PrepareCombat
            val combat = page == Page.Combat
            BarButton(Icons.Default.KeyboardArrowUp, combat) { state.previousInitiative() }
            BarButton(Icons.Default.KeyboardArrowDown, combat) { state.nextInitiative() }
            BarButton(Icons.Default.Shield, none || show || overview) { state.toCombat() }
            BarButton(Icons.Default.Settings, combat) { state.toPrepareCombat() }
            BarButton(Icons.Default.RestartAlt, prepareCombat || combat) { state.restartInitiative() }
            BarButton(Icons.Default.Start, prepareCombat) { state.startCombat() }
            BarButton(Icons.Default.Visibility, !show) { state.show() }
            BarButton(Icons.Default.Edit, !edit) { state.edit() }
            BarButton(Icons.Default.GridView, !overview) { state.toOverview() }
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
