package org.hertsig.dnd.combat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.CombatEntry

@Composable
fun CombatPage(state: AppState, modifier: Modifier = Modifier) {
    OverviewPage(state, modifier, state.initiative.mapNotNull { (it as? CombatEntry.StatBlockEntry)?.statBlock }.distinctBy { it.name })
//    Column(modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
//        state.current?.let {
//            if (it is CombatEntry.Creature) {
//                TextLine(it.currentHp.toString())
//            }
//            if (it is CombatEntry.StatBlockEntry) {
//                ReadonlySheet(it.statBlock)
//            }
//        }
//    }
}
