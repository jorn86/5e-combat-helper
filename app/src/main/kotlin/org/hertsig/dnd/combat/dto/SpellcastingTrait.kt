package org.hertsig.dnd.combat.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hertsig.dnd.norr.spell.getSpell

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(InnateSpellcasting::class, name = "innate"),
    JsonSubTypes.Type(SpellListCasting::class, name = "list"),
)
sealed interface SpellcastingTrait {
    val name: String
    val stat: Stat
}

data class InnateSpellcasting(
    override val name: String,
    override val stat: Stat,
    val spellsWithLimit: Map<Int, List<StatblockSpell>>,
): SpellcastingTrait

data class SpellListCasting(
    override val name: String,
    val list: String,
    override val stat: Stat,
    val level: CasterLevel,
    val spellsByLevel: Map<Int, List<StatblockSpell>>,
): SpellcastingTrait

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class StatblockSpell(val name: String, val comment: String = "") {
    fun resolve() = getSpell(name)
}

fun List<StatblockSpell>.resolve() = mapNotNull(StatblockSpell::resolve)
fun List<Spell>.groupByLevel() = associateBy { it.level }
fun List<SpellcastingTrait>.resolveAll(): Set<Spell> = flatMap { when (it) {
    is InnateSpellcasting -> it.spellsWithLimit.values
    is SpellListCasting -> it.spellsByLevel.values
} }.flatten().resolve().toSortedSet(Spell.order)
