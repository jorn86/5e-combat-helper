package org.hertsig.dnd.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.IconButton
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.TextLine
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.CombatEntry

@Composable
fun InitiativeList(state: AppState, modifier: Modifier = Modifier, showControls: Boolean = false, playerView: Boolean = false) {
    Column(modifier.padding(8.dp, 8.dp, 12.dp, 8.dp), Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            RowTextLine("Round ${state.round}")
            if (showControls) {
                IconButton({ state.previousInitiative() }, Icons.Default.KeyboardArrowUp)
                IconButton({ state.nextInitiative() }, Icons.Default.KeyboardArrowDown)
            }
        }

        state.initiative.forEach {
            val isCurrent = it == state.current
            Row(Modifier
                .background(if (isCurrent) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary, RoundedCornerShape(8.dp))
                .fillMaxWidth().padding(8.dp, 4.dp), Arrangement.SpaceBetween) {
                Row {
                    TextLine("${it.initiative} — ")
                    TextLine(it.name, if (playerView && it !is CombatEntry.Simple) Modifier.blur(8.dp) else Modifier)
                }
                if (!playerView) IconButton({ state.removeInitiative(it) }, Icons.Default.Close, iconSize = 16.dp)
            }
        }
    }
}
