package org.hertsig.dnd.combat.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.util.count

data class Spell(
    val name: String,
    val source: String,
    val level: Int,
    val text: List<SpellText>,
    val time: String,
    val duration: String,
    val components: String,
    val school: SpellSchool,
    val range: String,
    val attack: String,
    val savingThrow: String,
    val scaling: Boolean = false,
    val damage: MultiDice? = null,
) {
    companion object {
        val order = compareBy<Spell> { it.level }.thenBy { it.name }
    }
}

enum class SpellSchool {
    ABJURATION, CONJURATION, DIVINATION, ENCHANTMENT, EVOCATION, ILLUSION, NECROMANCY, PSIONIC, TRANSMUTATION,
    ;

    companion object {
        fun get(text: String) = when (text) {
            "A" -> ABJURATION
            "C" -> CONJURATION
            "D" -> DIVINATION
            "E" -> ENCHANTMENT
            "V" -> EVOCATION
            "I" -> ILLUSION
            "N" -> NECROMANCY
            "P" -> PSIONIC
            "T" -> TRANSMUTATION
            else -> error("Unknown spell school text $text")
        }
    }
}

val Spell.type get() = if (level == 0) "cantrip" else "${count(level)} level spell"

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(SpellText.Text::class, name = "text"),
    JsonSubTypes.Type(SpellText.Roll::class, name = "roll"),
)
sealed interface SpellText {
    data class Text(val text: String): SpellText
    data class Roll(val dice: MultiDice): SpellText
}
