package org.hertsig.dnd.combat.dto

import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.MultiDieRolls

interface LogEntry {
    data class Text(val text: String): LogEntry
    data class Roll(val name: String, val text: String, val first: MultiDieRolls, val second: MultiDieRolls? = null): LogEntry
    data class Attack(val name: String, val text: String, val firstHit: MultiDieRolls, val secondHit: MultiDieRolls? = null, val damage: MultiDice): LogEntry
}
