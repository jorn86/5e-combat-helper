package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.ChallengeRating
import org.hertsig.dnd.combat.dto.StatBlock
import org.hertsig.dnd.component.DropDown
import org.hertsig.dnd.component.RowTextLine
import org.hertsig.dnd.component.ScrollableColumn

@Composable
fun StatBlockList(state: AppState) {
    Column(Modifier.width(250.dp).padding(8.dp, 8.dp, 12.dp, 8.dp), Arrangement.spacedBy(4.dp)) {
        DropDown(state.orderState, Modifier.weight(1f))
        Button(
            { state.new() },
            Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondaryVariant)
        ) {
            RowTextLine("Add...", style = MaterialTheme.typography.subtitle1)
        }
        ScrollableColumn(Modifier.weight(1f), Arrangement.spacedBy(4.dp), PaddingValues(0.dp)) {
            items(state.statBlocks.filter { it.visible }, { Key(state.active, it) }) {
                val colors = MaterialTheme.colors
                val isCurrent by remember { derivedStateOf { it == state.active } }
                val backgroundColor by remember { derivedStateOf { if (isCurrent) colors.primary else colors.secondary } }
                Button(
                    { state.show(it) },
                    Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor),
                ) {
                    RowTextLine(it.name, style = MaterialTheme.typography.subtitle1)
                    if (it.challengeRating != ChallengeRating.NONE) RowTextLine("  (${it.challengeRating.display})")
                }
            }
        }
    }
}

private data class Key(val active: StatBlock?, val self: StatBlock)
