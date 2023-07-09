package org.hertsig.dnd.combat.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hertsig.dnd.combat.dto.CasterLevel.*
import org.hertsig.dnd.combat.dto.SpellList.*
import org.hertsig.dnd.norr.spell.getSpell
import org.hertsig.logger.logger

private val log = logger {}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(InnateSpellcasting::class, name = "innate"),
    JsonSubTypes.Type(SpellListCasting::class, name = "list"),
)
sealed interface SpellcastingTrait {
    val name: String
}

data class InnateSpellcasting(
    override val name: String,
    val stat: Stat,
    val spellsWithLimit: Map<Int, List<StatblockSpell>>,
): SpellcastingTrait

data class SpellListCasting(
    override val name: String,
    val list: SpellList,
    val level: CasterLevel,
    val spellsByLevel: Map<Int, List<StatblockSpell>>,
): SpellcastingTrait {
    fun updateList(newList: SpellList): SpellListCasting {
        val newLevel = when (newList) {
            WARLOCK -> level.asWarlock()
            else -> level.asFull()
        }
        return copy(list = newList, level = newLevel)
    }

    private fun CasterLevel.asFull() = when(this) {
        WARLOCK_01 -> ONE
        WARLOCK_02 -> TWO
        WARLOCK_03 -> THREE
        WARLOCK_05 -> FIVE
        WARLOCK_07 -> SEVEN
        WARLOCK_09 -> NINE
        WARLOCK_11 -> ELEVEN
        WARLOCK_17 -> SEVENTEEN
        else -> this
    }

    private fun CasterLevel.asWarlock() = when(this) {
        ONE -> WARLOCK_01
        TWO -> WARLOCK_02
        THREE, FOUR -> WARLOCK_03
        FIVE, SIX -> WARLOCK_05
        SEVEN, EIGHT -> WARLOCK_07
        NINE, TEN -> WARLOCK_09
        ELEVEN, TWELVE, THIRTEEN, FOURTEEN, FIFTEEN, SIXTEEN -> WARLOCK_11
        SEVENTEEN, EIGHTEEN, NINETEEN, TWENTY -> WARLOCK_17
        else -> this
    }
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class StatblockSpell(val name: String, val comment: String = "") {
    fun resolve(): Spell? {
        log.trace("Resolving spell $name")
        return getSpell(name)
    }
}

fun List<StatblockSpell>.resolve() = mapNotNull(StatblockSpell::resolve)
fun List<Spell>.groupByLevel() = associateBy { it.level }
fun List<SpellcastingTrait>.resolveAll(): Set<Spell> = flatMap { when (it) {
    is InnateSpellcasting -> it.spellsWithLimit.values
    is SpellListCasting -> it.spellsByLevel.values
} }.flatten().resolve().toSortedSet(Spell.order)
