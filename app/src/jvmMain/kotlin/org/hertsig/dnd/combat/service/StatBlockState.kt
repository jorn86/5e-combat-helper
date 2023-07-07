package org.hertsig.dnd.combat.service

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.fasterxml.jackson.core.type.TypeReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.hertsig.dnd.combat.dto.StatBlock
import org.hertsig.logger.logger

private val log = logger {}

@Composable
fun rememberStatBlocks(scope: CoroutineScope = rememberCoroutineScope()): StatBlockState {
    val state = remember { mutableStateListOf<StatBlock>() }
    val order = remember { mutableStateOf(ListOrder.ALPHABETICAL) }
    LaunchedEffect(order.value) { state.sortWith(order.value.comparator) }
    return remember { StatBlockState(scope, state, order) }
}

class StatBlockState(
    private val scope: CoroutineScope,
    private val state: SnapshotStateList<StatBlock>,
    val orderState: MutableState<ListOrder>,
) {
    private val json = JsonService("statblocks", object : TypeReference<List<StatBlock>>() {})

    val statBlocks: List<StatBlock> get() = state
    fun visibleStatBlocks(or: Boolean = false) = statBlocks.filter { or || it.visible }
    private var order by orderState

    fun new(): StatBlock {
        val new = StatBlock(type = "Humanoid", armorClass = "13 (natural armor)",
            senses = "Darkvision 60 ft.", speed = "30 ft., fly 40 ft.",
            damageResistances = "Bludgeoning, piercing and slashing from nonmagical attacks")
        state.add(0, new)
        return new
    }

    fun update(original: StatBlock, updated: StatBlock): Boolean {
        val index = state.indexOf(original)
        if (index < 0) {
            log.warn("Invalid index $index for ${original.name}")
            return false
        }
        state[index] = updated
        state.sortWith(order.comparator)
        save()
        return true
    }

    fun copy(original: StatBlock): StatBlock {
        return add(original.copy(name = original.name + " (copy)", visible = true))
    }

    fun add(statBlock: StatBlock): StatBlock {
        val index = state.binarySearch(statBlock, order.comparator)
        val insert = if (index < 0) -index - 1 else index
        state.add(insert, statBlock)
        return statBlock
    }

    fun delete(statBlock: StatBlock) {
        state.remove(statBlock)
        save()
    }

    internal fun load() = scope.launch {
        state.clear()
        state.addAll(json.load())
        state.sortWith(order.comparator)
    }

    internal fun save() = scope.launch { json.save(statBlocks.sortedWith(alphabetical)) }
}

private val alphabetical = compareBy(String.CASE_INSENSITIVE_ORDER, StatBlock::name).thenBy(StatBlock::hashCode)
enum class ListOrder(val comparator: Comparator<StatBlock>) {
    ALPHABETICAL(alphabetical),
    CHALLENGE_RATING(compareBy(StatBlock::challengeRating).then(alphabetical)),
    CHALLENGE_RATING_REVERSE(compareByDescending(StatBlock::challengeRating).then(alphabetical)),
    TYPE(compareBy(StatBlock::type).then(alphabetical)),
}
