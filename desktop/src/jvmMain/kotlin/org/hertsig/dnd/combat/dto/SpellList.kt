package org.hertsig.dnd.combat.dto

enum class SpellList(val stat: Stat) {
    ARTIFICER(Stat.INTELLIGENCE),
    BARD(Stat.CHARISMA),
    CLERIC(Stat.WISDOM),
    DRUID(Stat.WISDOM),
    PALADIN(Stat.WISDOM),
    RANGER(Stat.WISDOM),
    SORCERER(Stat.CHARISMA),
    WARLOCK(Stat.CHARISMA),
    WIZARD(Stat.INTELLIGENCE),
}
