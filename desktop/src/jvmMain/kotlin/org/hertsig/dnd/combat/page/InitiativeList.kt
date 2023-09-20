package org.hertsig.dnd.combat.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.IconButton
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.TextLine
import org.hertsig.dnd.combat.dto.CombatEntry
import org.hertsig.dnd.combat.service.CombatState

@Composable
fun InitiativeList(state: CombatState, modifier: Modifier = Modifier, showControls: Boolean = false, playerView: Boolean = false) {
    Column(modifier.padding(8.dp, 8.dp, 12.dp, 8.dp), Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            RowTextLine("Round ${state.round}")
            if (showControls) {
                IconButton({ state.previousInitiative() }, Icons.Default.KeyboardArrowUp)
                IconButton({ state.nextInitiative() }, Icons.Default.KeyboardArrowDown)
            }
        }


        state.entries.forEach {
            val isCurrent = it == state.current
            Row(Modifier
                .background(if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)) // FIXME tertiary was secondaryVariant
                .fillMaxWidth().padding(8.dp, 4.dp), Arrangement.SpaceBetween) {
                var text = "%2d".format(it.initiative)
                if (!playerView || it is CombatEntry.Simple) text += " — ${it.name}"
                TextLine(text, Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (showControls) {
                        Checkbox(it.active, { _ -> state.toggleActive(it) }, Modifier.size(16.dp),
                            colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary))
                    } else if (!it.active) {
                        Icon(painterResource("/skull.svg"), "Unconscious", Modifier.size(16.dp))
                    }
                    if (!playerView) {
                        IconButton({ state.removeInitiative(it) }, Icons.Default.Close, iconSize = 16.dp)
                    }
                }
            }
        }
    }
}
