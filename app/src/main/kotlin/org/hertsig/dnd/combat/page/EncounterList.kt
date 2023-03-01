package org.hertsig.dnd.combat.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.ScrollableColumn
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.Encounter
import org.hertsig.dnd.combat.service.rememberEncounters

@Composable
fun EncounterList(state: AppState, modifier: Modifier) {
    val encounters = rememberEncounters(state.statBlocks.statBlocks)
    var active by remember { mutableStateOf(encounters.encounters.firstOrNull()) }
    Column(modifier.width(250.dp).padding(8.dp, 8.dp, 12.dp, 8.dp), Arrangement.spacedBy(4.dp)) {
//        BasicDropdown(state.orderState, Modifier.weight(1f))
        Button(
            { encounters.add(Encounter()) },
            Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondaryVariant)
        ) {
            RowTextLine("Add...", style = MaterialTheme.typography.subtitle1)
        }
        ScrollableColumn(Modifier.weight(1f), Arrangement.spacedBy(4.dp), PaddingValues(0.dp)) {
            items(encounters.encounters) {
                val colors = MaterialTheme.colors
                val isCurrent by remember { derivedStateOf { it == active } }
                val backgroundColor by remember { derivedStateOf { if (isCurrent) colors.primary else colors.secondary } }
                Button(
                    { active = it },
                    Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor),
                ) {
                    RowTextLine(it.name, style = MaterialTheme.typography.subtitle1)
                }
            }
        }
    }
}