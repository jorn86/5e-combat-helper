package org.hertsig.dnd.combat.dto

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import org.hertsig.dnd.combat.Page
import org.hertsig.dnd.combat.service.CombatState
import org.hertsig.dnd.combat.service.StatBlockState
import org.hertsig.dnd.combat.service.rememberCombatState
import org.hertsig.dnd.combat.service.rememberStatBlocks

@Composable
fun rememberAppState(): AppState {
    val scope = rememberCoroutineScope()
    val page = remember { mutableStateOf<Page>(Page.Overview) }

    val statBlocks = rememberStatBlocks(scope)
    val combat = rememberCombatState()

    val state = remember { AppState(scope, page, statBlocks, combat) }
    LaunchedEffect(Unit) { statBlocks.load() }
    return state
}

data class AppState(
    private val scope: CoroutineScope,
    private val pageState: MutableState<Page>,
    val statBlocks: StatBlockState,
    val combat: CombatState,
) {
    var page by pageState
}
