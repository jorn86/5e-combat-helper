package org.hertsig.dnd.combat

import org.hertsig.dnd.dice.DieRolls

interface LogEntry {
    data class Text(val text: String): LogEntry
    data class Roll(val name: String, val text: String, val first: DieRolls, val second: DieRolls? = null): LogEntry
    data class Attack(val name: String, val text: String, val firstHit: DieRolls, val secondHit: DieRolls? = null, val damage: DieRolls): LogEntry
}
