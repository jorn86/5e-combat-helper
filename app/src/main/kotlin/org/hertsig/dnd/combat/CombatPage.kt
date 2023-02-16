package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.CombatEntry
import org.hertsig.compose.component.TextLine

@Composable
fun CombatPage(state: AppState, modifier: Modifier = Modifier) {
    Column(modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        state.current?.let {
            if (it is CombatEntry.Creature) {
                TextLine(it.currentHp.toString())
                ReadonlySheet(it.statBlock)
            }
        }
    }
}
