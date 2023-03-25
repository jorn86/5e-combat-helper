package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.IconButton
import org.hertsig.compose.component.TextLine
import org.hertsig.compose.component.VerticalDivider
import org.hertsig.compose.component.dividerAlpha
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.element.Log

@Composable
fun CombatHelper(state: AppState) {
    Column {
        TitleBar(state)

        Row {
            state.page.drawList(state)
            VerticalDivider()
            with(state.page) { drawPage(state) }
            VerticalDivider()
            Log(logEntries)
        }
    }
}

@Composable
private fun TitleBar(state: AppState) {
    var page by state::page
    TopAppBar({ TextLine("Combat helper — ${page.subtitle}", style = MaterialTheme.typography.h5) }, actions = {
        Row(Modifier.padding(end = 16.dp), Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
            page.drawToolbarButtons(state)
            VerticalDivider(MaterialTheme.colors.onPrimary.copy(alpha = dividerAlpha))
            BarButton(Icons.Default.GridView, page != Page.Overview) { page = Page.Overview }
            BarButton(Icons.Default.Build, page != Page.Encounters) { page = Page.Encounters }
            BarButton(Icons.Default.Shield, page != Page.PrepareCombat && page != Page.Combat) {
                page = if (state.combat.current == null) Page.PrepareCombat else Page.Combat
            }
        }
    })
}

@Composable
fun BarButton(icon: ImageVector, enabled: Boolean = true, visible: Boolean = true, onClick: () -> Unit) {
    if (visible) IconButton(onClick, icon, enabled = enabled)
}
