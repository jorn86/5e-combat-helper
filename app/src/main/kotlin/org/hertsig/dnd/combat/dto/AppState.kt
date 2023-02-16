package org.hertsig.dnd.combat.dto

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.hertsig.dnd.combat.Page
import org.hertsig.dnd.combat.StatBlockService

@Composable
fun rememberAppState(): AppState {
    val scope = rememberCoroutineScope()
    val page = remember { mutableStateOf<Page?>(null) }
    val statBlocks = remember { mutableStateListOf<StatBlock>().also { scope.launch { it.addAll(StatBlockService.load()) } } }
    val active = remember { mutableStateOf<StatBlock?>(null) }
    val forUpdate = remember { mutableStateOf<StatBlock?>(null) }
    val order = remember { mutableStateOf(ListOrder.ALPHABETICAL) }

    val combat = remember { mutableStateListOf<CombatEntry>() }
    val combatCurrent = remember { mutableStateOf<CombatEntry?>(null) }
    val combatRound = remember { mutableStateOf(1) }
    LaunchedEffect(order.value) { statBlocks.sortWith(order.value.comparator) }
    return remember { AppState(scope, page, statBlocks, order, active, forUpdate, combat, combatCurrent, combatRound) }
}

private val alphabetical = compareBy(String.CASE_INSENSITIVE_ORDER, StatBlock::name)
enum class ListOrder(val comparator: Comparator<StatBlock>) {
    ALPHABETICAL(alphabetical),
    CHALLENGE_RATING(compareBy(StatBlock::challengeRating).then(alphabetical)),
    CHALLENGE_RATING_REVERSE(compareByDescending(StatBlock::challengeRating).then(alphabetical)),
    TYPE(compareBy(StatBlock::type).then(alphabetical)),
}

data class AppState(
    private val scope: CoroutineScope,
    private val pageState: MutableState<Page?>,
    val statBlocks: MutableList<StatBlock>,
    val orderState: MutableState<ListOrder>,
    private val activeState: MutableState<StatBlock?>,
    private val forUpdateState: MutableState<StatBlock?>,

    private val combat: SnapshotStateList<CombatEntry>,
    private val currentState: MutableState<CombatEntry?>,
    private val roundState: MutableState<Int>,
) {
    var page by pageState; private set
    var active by activeState; private set
    private val order by orderState
    val initiative: List<CombatEntry> get() = combat
    var current by currentState; private set
    var round by roundState; private set

    fun show(block: StatBlock? = null) {
        active = block ?: active ?: statBlocks.first()
        page = Page.Show(active!!)
    }

    fun edit(block: StatBlock? = null) {
        active = block ?: active ?: statBlocks.first()
        page = Page.Edit(active!!)
    }

    fun new() = add(StatBlock(""))

    private fun add(statBlock: StatBlock) {
        statBlocks.insert(statBlock, order.comparator)
        active = statBlock
        page = Page.Edit(statBlock)
    }

    fun update(statBlock: StatBlock) {
        forUpdateState.value = statBlock
    }

    fun saveUpdated() {
        forUpdateState.value?.let {
            statBlocks[statBlocks.indexOf(active)] = it
            statBlocks.sortWith(order.comparator)
            active = it
            save()
        }
        show()
        forUpdateState.value = null
    }

    fun copyCurrent() {
        active?.let { add(it.copy(name = "${it.name} (copy)")) }
    }

    fun deleteCurrent() {
        statBlocks.remove(active)
        active = null
        page = null
        save()
    }

    fun toPrepareCombat() {
        page = Page.PrepareCombat
    }

    fun toCombat() {
        if (currentState.value == null) {
            toPrepareCombat()
        } else {
            page = Page.Combat
        }
    }

    fun addInitiative(entry: CombatEntry) {
        combat.insert(entry, initiativeOrder)
    }

    fun removeInitiative(entry: CombatEntry) {
        if (current == entry) previousInitiative()
        combat.remove(entry)
    }

    fun previousInitiative() {
        when (val it = current) {
            null -> moveInitiative(initiative.first(), Round.RESET)
            initiative.first() -> moveInitiative(initiative.last(), Round.DECREMENT)
            else -> moveInitiative(initiative.previous(it))
        }
    }

    fun nextInitiative() {
        when (val it = current) {
            null -> moveInitiative(initiative.first(), Round.RESET)
            initiative.last() -> moveInitiative(initiative.first(), Round.INCREMENT)
            else -> moveInitiative(initiative.next(it))
        }
    }

    private enum class Round { RESET, INCREMENT, DECREMENT, NO_CHANGE }
    private fun moveInitiative(entry: CombatEntry, roundChange: Round = Round.NO_CHANGE) {
        current = entry
        if (entry is CombatEntry.Creature) active = entry.statBlock
        when (roundChange) {
            Round.RESET -> round = 1
            Round.DECREMENT -> if (round > 1) round--
            Round.INCREMENT -> round++
            Round.NO_CHANGE -> {}
        }
    }

    fun startCombat() {
        if (currentState.value == null) {
            nextInitiative()
        }
        page = Page.Combat
    }

    fun restartInitiative() {
        current = null
        nextInitiative()
    }

    internal fun save() {
        scope.launch { StatBlockService.save(statBlocks.sortedWith(alphabetical)) }
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
