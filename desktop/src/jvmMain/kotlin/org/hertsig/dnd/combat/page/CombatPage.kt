package org.hertsig.dnd.combat.page

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.statBlock

@Composable
fun CombatPage(state: AppState, modifier: Modifier = Modifier) {
    OverviewPage(
        state.combat.entries.mapNotNull { it.statBlock }.distinctBy { it.name },
        modifier,
        state.combat.current.statBlock
    )
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
