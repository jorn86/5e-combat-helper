package org.hertsig.dnd.combat.page

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
import org.hertsig.compose.component.BasicDropdown
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.ScrollableColumn
import org.hertsig.dnd.combat.Page
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.ChallengeRating
import org.hertsig.dnd.combat.dto.StatBlock

@Composable
fun StatBlockList(
    state: AppState,
    active: StatBlock? = null,
    onClick: (StatBlock) -> Unit = { state.page = Page.Show(it) }
) {
    Column(Modifier.width(250.dp).padding(vertical = 8.dp), Arrangement.spacedBy(4.dp)) {
        val padding = PaddingValues(start = 8.dp, end = 12.dp)
        Column(Modifier.padding(padding), Arrangement.spacedBy(4.dp)) {
            BasicDropdown(state.statBlocks.orderState, Modifier.weight(1f))
            Button(
                { state.page = Page.Edit(state.statBlocks.new()) },
                Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondaryVariant)
            ) {
                RowTextLine("Add...", style = MaterialTheme.typography.subtitle1)
            }
        }
        ScrollableColumn(Modifier.width(250.dp), Arrangement.spacedBy(4.dp), padding) {
            items(state.statBlocks.statBlocks, { Key(it == active, it) }) {
                val colors = MaterialTheme.colors
                val isCurrent by remember { derivedStateOf { it == active } } // TODO remember probably not needed
                val backgroundColor by remember { derivedStateOf { if (isCurrent) colors.primary else colors.secondary } }
                Button({ onClick(it) }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(backgroundColor)) {
                    RowTextLine(it.name, style = MaterialTheme.typography.subtitle1)
                    if (it.challengeRating != ChallengeRating.NONE) RowTextLine("  (${it.challengeRating.display})")
                }
            }
        }
    }
}

private data class Key(val active: Boolean, val self: StatBlock)
