package org.hertsig.dnd.combat.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.*
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
        var hideInvisible by rememberSaveable { mutableStateOf(true) }
        val padding = PaddingValues(start = 8.dp, end = 12.dp)
        Column(Modifier.padding(padding), Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(hideInvisible, { hideInvisible = !hideInvisible }, colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary))
                TextLine("Hide inactive")
            }
            BasicDropdown(state.statBlocks.orderState, Modifier.weight(1f))
            Button(
                { state.page = Page.Edit(state.statBlocks.new()) },
                Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
            ) {
                RowTextLine("Add...", style = MaterialTheme.typography.titleMedium)
            }
        }
        ScrollableColumn(Modifier.width(250.dp), arrangement = Arrangement.spacedBy(4.dp), padding = padding) {
            items(state.statBlocks.visibleStatBlocks(!hideInvisible), { Key(it == active, it) }) {
                val colors = MaterialTheme.colorScheme
                val isCurrent by remember { derivedStateOf { it == active } }
                val backgroundColor by remember { derivedStateOf { if (isCurrent) colors.primary else colors.secondary } }
                SmallButton(
                    { onClick(it) },
                    Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor),
                    padding = PaddingValues(6.dp),
                ) {
                    if (!hideInvisible) {
                        Checkbox(
                            it.visible,
                            { _ -> state.statBlocks.update(it, it.copy(visible = !it.visible)) },
                            colors = CheckboxDefaults.colors(colors.primary)
                        )
                    }
                    RowTextLine(
                        it.name,
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (it.challengeRating != ChallengeRating.NONE) RowTextLine("  (${it.challengeRating.display})")
                }
            }
        }
    }
}

private data class Key(val active: Boolean, val self: StatBlock)
