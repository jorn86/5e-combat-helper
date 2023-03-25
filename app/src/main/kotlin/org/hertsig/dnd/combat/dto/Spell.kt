package org.hertsig.dnd.combat.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hertsig.dnd.dice.MultiDice

data class Spell(
    val name: String,
    val level: Int,
    val text: List<SpellText>,
    val time: String,
    val duration: String,
    val components: String,
    val range: String,
    val attack: String,
    val savingThrow: String,
    val scaling: Boolean = false,
    val damage: MultiDice? = null,
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(SpellText.Text::class, name = "text"),
    JsonSubTypes.Type(SpellText.Roll::class, name = "roll"),
)
sealed interface SpellText {
    data class Text(val text: String): SpellText
    data class Roll(val dice: MultiDice): SpellText
}
