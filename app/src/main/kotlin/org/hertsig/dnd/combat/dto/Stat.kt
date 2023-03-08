package org.hertsig.dnd.combat.dto

enum class Stat {
    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    INTELLIGENCE,
    WISDOM,
    CHARISMA,
    ;
}

val StatBlock.scores get() = ScoresDelegate(this)
val StatBlock.modifiers get() = ModsDelegate(this)

class ScoresDelegate(private val statBlock: StatBlock) {
    operator fun get(stat: Stat) = when (stat) {
        Stat.STRENGTH -> statBlock.strength
        Stat.DEXTERITY -> statBlock.dexterity
        Stat.CONSTITUTION -> statBlock.constitution
        Stat.INTELLIGENCE -> statBlock.intelligence
        Stat.WISDOM -> statBlock.wisdom
        Stat.CHARISMA -> statBlock.charisma
    }
}

class ModsDelegate(private val statBlock: StatBlock) {
    operator fun get(stat: Stat) = when (stat) {
        Stat.STRENGTH -> statBlock.strMod
        Stat.DEXTERITY -> statBlock.dexMod
        Stat.CONSTITUTION -> statBlock.conMod
        Stat.INTELLIGENCE -> statBlock.intMod
        Stat.WISDOM -> statBlock.wisMod
        Stat.CHARISMA -> statBlock.chaMod
    }
}

fun StatBlock.modifierFor(skill: Skill) = modifiers[skill.stat] + when (skill) {
    in expertiseSkills -> proficiencyBonus * 2
    in proficientSkills -> proficiencyBonus
    else -> 0
}
fun StatBlock.saveModifierFor(save: Stat) = modifierFor(save, save in proficientSaves)
fun StatBlock.modifierFor(stat: Stat?, proficient: Boolean = false) = (if (stat == null) 0 else modifiers[stat]) +
        if (proficient) proficiencyBonus else 0

val StatBlock.strMod get() = mod(strength)
val StatBlock.dexMod get() = mod(dexterity)
val StatBlock.conMod get() = mod(constitution)
val StatBlock.intMod get() = mod(intelligence)
val StatBlock.wisMod get() = mod(wisdom)
val StatBlock.chaMod get() = mod(charisma)

fun mod(score: Int) = score / 2 - 5
