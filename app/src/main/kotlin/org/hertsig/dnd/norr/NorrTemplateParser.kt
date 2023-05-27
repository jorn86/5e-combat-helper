package org.hertsig.dnd.norr

import com.google.common.annotations.VisibleForTesting
import org.hertsig.dnd.combat.component.modifier
import org.hertsig.dnd.combat.dto.Recharge
import org.hertsig.dnd.combat.element.toEnumSet
import org.hertsig.dnd.dice.Dice
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.parse
import java.util.*

private val templateRegex = Regex("\\{@(\\w+) (.+?)}")

fun String.parseNorrTemplateText(replacement: (MatchResult) -> String = { templateValue(it).text }) =
    replace(templateRegex, replacement)

fun String.parseNorrTemplate(): Pair<String, MutableList<Template>> {
    val templates = mutableListOf<Template>()
    val text = parseNorrTemplateText {
        val template = templateValue(it)
        templates.add(template)
        template.text
    }
    return text to templates
}

sealed interface Template {
    val text: String

    data class Attack(val types: EnumSet<Type>): Template {
        override val text = ""

        fun isSpell() = any(Type.MELEE_SPELL, Type.RANGED_SPELL)
        fun isMelee() = any(Type.MELEE_SPELL, Type.MELEE_WEAPON)
        fun isRanged() = any(Type.RANGED_SPELL, Type.RANGED_WEAPON)
        private fun any(vararg types: Type) = types.any { this.types.contains(it) }

        enum class Type(private val text: String) {
            MELEE_SPELL("ms"),
            MELEE_WEAPON("mw"),
            RANGED_SPELL("rs"),
            RANGED_WEAPON("rw"),
            ;

            companion object {
                fun forText(text: String) = values().singleOrNull { it.text == text }
                    ?: error("No attack type for $text")
            }
        }
    }

    data class Damage(val dice: org.hertsig.dnd.dice.Dice): Template {
        override val text get() = DAMAGE_MARKER
    }

    data class Dice(val dice: org.hertsig.dnd.dice.Dice): Template {
        override val text get() = dice.asString(short = true)
    }

    data class ToHit(val modifier: Int): Template {
        override val text get() = modifier(modifier)
    }

    data class Recharge(val recharge: org.hertsig.dnd.combat.dto.Recharge): Template {
        override val text get() = ""
    }

    data class DC(val value: Int): Template {
        override val text get() = "DC $value"
    }

    data class Spell(val name: String): Template {
        override val text get() = name
    }

    data class Other(val type: String, override val text: String): Template
}

@VisibleForTesting
fun templateValue(match: MatchResult): Template {
    val text = match.groupValues[2].split("|").map { it.trim() }.filter { it.isNotBlank() }
    return when(val type = match.groupValues[1]) {
        "atk" -> Template.Attack(text.single().split(",").map(Template.Attack.Type::forText).toEnumSet())
        "condition" -> Template.Other(type, text.single())
        "damage" -> Template.Damage(parse(text.single()).singleUntyped())
        "dc" -> Template.DC(text.single().toInt())
        "dice" -> Template.Dice(parse(text.single()).singleUntyped())
        "h" -> Template.Other(type, "")
        "hit" -> Template.ToHit(text.single().toInt())
        "item" -> Template.Other(type, text.first())
        "recharge" -> Template.Recharge(Recharge.forValue(text.single().toInt()))
        "quickref" -> Template.Other(type, text.first())
        "spell" -> Template.Spell(text.single()) // make own implementation when needed
        "skill" -> Template.Other(type, text.single()) // make own implementation when needed
        else -> Template.Other(type, match.groupValues[0])
    }
}

private fun MultiDice.singleUntyped(): Dice {
    require(extra.isEmpty())
    require(main.type.isEmpty())
    return main
}

const val DAMAGE_MARKER = "<DMG>"
internal val DAMAGE_TYPE_PAREN = Regex("\\($DAMAGE_MARKER\\) (\\w+)")
internal val DAMAGE_TYPE = Regex("$DAMAGE_MARKER (\\w+)")
