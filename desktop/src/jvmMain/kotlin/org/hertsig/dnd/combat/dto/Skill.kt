package org.hertsig.dnd.combat.dto

import java.util.*

enum class Skill(val stat: Stat) {
    ACROBATICS(Stat.DEXTERITY),
    ANIMAL_HANDLING(Stat.WISDOM),
    ARCANA(Stat.INTELLIGENCE),
    ATHLETICS(Stat.STRENGTH),
    DECEPTION(Stat.CHARISMA),
    HISTORY(Stat.INTELLIGENCE),
    INSIGHT(Stat.WISDOM),
    INTIMIDATION(Stat.CHARISMA),
    INVESTIGATION(Stat.INTELLIGENCE),
    MEDICINE(Stat.WISDOM),
    NATURE(Stat.INTELLIGENCE),
    PERCEPTION(Stat.WISDOM),
    PERFORMANCE(Stat.CHARISMA),
    PERSUASION(Stat.CHARISMA),
    RELIGION(Stat.INTELLIGENCE),
    SLEIGHT_OF_HAND(Stat.DEXTERITY),
    STEALTH(Stat.DEXTERITY),
    SURVIVAL(Stat.WISDOM),
}

val StatBlock.allSkills: EnumSet<Skill> get() = EnumSet.copyOf(proficientSkills).apply { addAll(expertiseSkills) }
