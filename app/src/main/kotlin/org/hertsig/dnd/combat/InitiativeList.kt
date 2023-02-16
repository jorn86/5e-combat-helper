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
import androidx.compose.ui.unit.dp
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.component.IconButton
import org.hertsig.dnd.component.RowTextLine
import org.hertsig.dnd.component.TextLine

@Composable
fun InitiativeList(state: AppState, showControls: Boolean) {
    Column(Modifier.width(250.dp).padding(8.dp, 8.dp, 12.dp, 8.dp), Arrangement.spacedBy(4.dp)) {
        if (showControls) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RowTextLine("Round ${state.round}")
                IconButton({ state.previousInitiative() }, Icons.Default.KeyboardArrowUp)
                IconButton({ state.nextInitiative() }, Icons.Default.KeyboardArrowDown)
            }
        }

        state.initiative.forEach {
            val isCurrent = it == state.current
            Row(Modifier
                .background(if (isCurrent) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary, RoundedCornerShape(8.dp))
                .fillMaxWidth().padding(8.dp, 4.dp)) {
                TextLine("${it.initiative} — ${it.name}")
                IconButton({ state.removeInitiative(it) }, Icons.Default.Close)
            }
        }
    }
}
