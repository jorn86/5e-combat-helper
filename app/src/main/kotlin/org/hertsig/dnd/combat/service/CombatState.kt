package org.hertsig.dnd.combat.service

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.hertsig.dnd.combat.dto.CombatEntry

@Composable
fun rememberCombatState(): CombatState {
    val entries = remember { mutableStateListOf<CombatEntry>() }
    val current = remember { mutableStateOf<CombatEntry?>(null) }
    val round = remember { mutableStateOf(1) }
    return remember { CombatState(entries, current, round) }
}

class CombatState(
    private val entriesState: SnapshotStateList<CombatEntry>,
    currentState: MutableState<CombatEntry?>,
    roundState: MutableState<Int>
) {
    val entries: List<CombatEntry> get() = entriesState
    var current by currentState; private set
    var round by roundState; private set

    fun addInitiative(entry: CombatEntry) {
        entriesState.insert(entry, initiativeOrder)
    }

    fun removeInitiative(entry: CombatEntry) {
        val index = entries.indexOf(entry)
        entriesState.removeAt(index)
        when {
            entriesState.isEmpty() -> moveInitiative(null)
            current == entry -> moveInitiative(entries[index.coerceIn(0, entries.size - 1)])
        }
    }

    fun previousInitiative() {
        if (entries.isEmpty()) return moveInitiative(null, Round.DECREMENT)
        when (val it = current) {
            null -> moveInitiative(entries.first(), Round.RESET)
            entries.first() -> moveInitiative(entries.last(), Round.DECREMENT)
            else -> moveInitiative(entries.previous(it))
        }
    }

    fun nextInitiative() {
        if (entries.isEmpty()) return moveInitiative(null, Round.INCREMENT)
        when (val it = current) {
            null -> moveInitiative(entries.first(), Round.RESET)
            entries.last() -> moveInitiative(entries.first(), Round.INCREMENT)
            else -> moveInitiative(entries.next(it))
        }
    }

    fun startCombat() {
        moveInitiative(entries.firstOrNull(), Round.RESET)
    }

    private enum class Round { RESET, INCREMENT, DECREMENT, NO_CHANGE }
    private fun moveInitiative(entry: CombatEntry?, roundChange: Round = Round.NO_CHANGE) {
        current = entry
        when (roundChange) {
            Round.RESET -> round = 1
            Round.DECREMENT -> if (round > 1) round--
            Round.INCREMENT -> round++
            Round.NO_CHANGE -> {}
        }
    }

    companion object {
        private val initiativeOrder = compareByDescending(CombatEntry::initiative)
            .then(compareByDescending(CombatEntry::dexMod))
            .thenComparing(CombatEntry::name, String.CASE_INSENSITIVE_ORDER)
    }
}

private fun <T> MutableList<T>.insert(element: T, comparator: Comparator<T>) {
    val index = binarySearch(element, comparator)
    add(if (index < 0) -index - 1 else index, element)
}

private fun <T: Any> List<T>.previous(element: T): T = this[indexOf(element) - 1]
private fun <T: Any> List<T>.next(element: T): T = this[indexOf(element) + 1]
