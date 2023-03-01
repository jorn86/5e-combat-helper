package org.hertsig.dnd.combat.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class StatBlock(
    val name: String = "",
    val size: Size = Size.MEDIUM,
    val type: String = "",
    val challengeRating: ChallengeRating = ChallengeRating(1),
    val proficiencyBonus: Int = challengeRating.proficiencyBonus,
    val maxHitPoints: Int = 1,
    val strength: Int = 10,
    val dexterity: Int = 10,
    val constitution: Int = 10,
    val intelligence: Int = 10,
    val wisdom: Int = 10,
    val charisma: Int = 10,
    val armorClass: String = "",
    val speed: String = "",
    val senses: String = "",
    val languages: String = "",
    val proficientSaves: EnumSet<Stat> = EnumSet.noneOf(Stat::class.java),
    val proficientSkills: EnumSet<Skill> = EnumSet.noneOf(Skill::class.java),
    val expertiseSkills: EnumSet<Skill> = EnumSet.noneOf(Skill::class.java),
    val conditionImmunities: String = "",
    val damageImmunities: String = "",
    val damageResistances: String = "",
    val traits: List<Ability> = emptyList(),
    val actions: List<Ability> = emptyList(),
    val bonusActions: List<Ability> = emptyList(),
    val reactions: List<Ability> = emptyList(),
    val legendaryActions: List<Ability> = emptyList(),
    val legendaryActionUses: Int = 0,
    val casterAbility: Stat = Stat.INTELLIGENCE,
    val spellSlots: CasterLevel = CasterLevel.NONE,
) {
    fun copy(stat: Stat, value: Int) = when (stat) {
        Stat.STRENGTH -> copy(strength = value)
        Stat.DEXTERITY -> copy(dexterity = value)
        Stat.CONSTITUTION -> copy(constitution = value)
        Stat.INTELLIGENCE -> copy(intelligence = value)
        Stat.WISDOM -> copy(wisdom = value)
        Stat.CHARISMA -> copy(charisma = value)
    }

    override fun equals(other: Any?) = this === other
    override fun hashCode() = System.identityHashCode(this)
}
