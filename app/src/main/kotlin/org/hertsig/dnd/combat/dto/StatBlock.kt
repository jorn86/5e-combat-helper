package org.hertsig.dnd.combat.dto

import java.util.*

data class StatBlock(
    val name: String,
    val size: Size = Size.MEDIUM,
    val type: String = "Beast",
    val challengeRating: ChallengeRating = ChallengeRating(1),
    val proficiencyBonus: Int = challengeRating.proficiencyBonus,
    val maxHitPoints: Int = 9,
    val strength: Int = 10,
    val dexterity: Int = 10,
    val constitution: Int = 10,
    val intelligence: Int = 10,
    val wisdom: Int = 10,
    val charisma: Int = 10,
    val armorClass: String = "13 (natural armor)",
    val speed: String = "30 ft.",
    val senses: String = "Darkvision 60 ft.",
    val languages: String = "",
    val proficientSaves: EnumSet<Stat> = EnumSet.noneOf(Stat::class.java),
    val proficientSkills: EnumSet<Skill> = EnumSet.noneOf(Skill::class.java),
    val expertiseSkills: EnumSet<Skill> = EnumSet.noneOf(Skill::class.java),
    val conditionImmunities: String = "",
    val damageImmunities: String = "",
    val damageResistances: String = "Bludgeoning, piercing and slashing from nonmagical attacks",
    val traits: List<Ability> = emptyList(),
    val actions: List<Ability> = emptyList(),
    val bonusActions: List<Ability> = emptyList(),
    val reactions: List<Ability> = emptyList(),
    val legendaryActions: List<Ability> = emptyList(),
    val legendaryActionUses: Int = 0,
    val casterLevel: CasterLevel = CasterLevel.NONE,
    val casterAbility: Stat = Stat.INTELLIGENCE,
    val minion: Boolean = false,
    val visible: Boolean = true,
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