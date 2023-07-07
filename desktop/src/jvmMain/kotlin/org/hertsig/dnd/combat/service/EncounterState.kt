package org.hertsig.dnd.combat.service

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.fasterxml.jackson.core.type.TypeReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.hertsig.dnd.combat.dto.Encounter
import org.hertsig.dnd.combat.dto.StatBlock
import org.hertsig.logger.logger

private val log = logger {}

@Composable
fun rememberEncounters(statBlocks: List<StatBlock>, scope: CoroutineScope = rememberCoroutineScope()): EncounterState {
    val state = remember { mutableStateListOf<Encounter>() }
    return remember { EncounterState(scope, statBlocks, state) }
}

class EncounterState(
    private val scope: CoroutineScope,
    private val statBlocks: List<StatBlock>,
    private val state: SnapshotStateList<Encounter>,
) {
    private val json = JsonService("encounters", object : TypeReference<List<Encounter>>() {})

    val encounters: List<Encounter> get() = state

    fun new(): Encounter {
        val new = Encounter()
        state.add(0, new)
        return new
    }

    fun update(original: Encounter, updated: Encounter): Boolean {
        val index = state.indexOf(original)
        if (index < 0) {
            log.warn("Invalid index $index for ${original.name}")
            return false
        }
        state[index] = updated
        state.sortWith(alphabetical)
        save()
        return true
    }

    fun copy(original: Encounter): Encounter {
        return add(original.copy(name = original.name + " (copy)"))
    }

    fun add(encounter: Encounter): Encounter {
        val index = state.binarySearch(encounter, alphabetical)
        val insert = if (index < 0) -index - 1 else index
        state.add(insert, encounter)
        return encounter
    }

    fun delete(encounter: Encounter) {
        state.remove(encounter)
        save()
    }

    fun getStatBlock(name: String) = statBlocks.singleOrNull { it.name == name }
        ?: throw IllegalArgumentException("No stat block found for $name")

    internal fun load() = scope.launch {
        state.clear()
        state.addAll(json.load())
        state.sortWith(alphabetical)
    }

    internal fun save() = scope.launch { json.save(encounters.sortedWith(alphabetical)) }
}

private val alphabetical = compareBy(String.CASE_INSENSITIVE_ORDER, Encounter::name)
