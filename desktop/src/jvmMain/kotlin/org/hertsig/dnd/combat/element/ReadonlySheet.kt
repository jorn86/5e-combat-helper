package org.hertsig.dnd.combat.element

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberCursorPositionProvider
import org.hertsig.compose.component.*
import org.hertsig.compose.component.flow.ReorderStrategy
import org.hertsig.compose.component.flow.ScrollableFlowColumn
import org.hertsig.compose.component.richtext.RichText
import org.hertsig.compose.component.richtext.Tooltip
import org.hertsig.compose.component.richtext.rememberRichString
import org.hertsig.dnd.combat.component.displayForEach
import org.hertsig.dnd.combat.component.modifier
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.log
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.d
import org.hertsig.util.count
import org.hertsig.util.display
import org.hertsig.util.plural

@Composable
fun ReadonlySheet(statBlock: StatBlock, modifier: Modifier = Modifier) {
    Column(modifier) {
        Column(Modifier.padding(8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                RowTextLine(statBlock.name, style = MaterialTheme.typography.headlineMedium)
                RowTextLine("${statBlock.size.display} ${statBlock.type}")
            }
            Row {
                TraitLine(
                    "Challenge rating", "${statBlock.challengeRating.display} (${statBlock.xp} XP)",
                    Modifier.weight(1f), visible = statBlock.challengeRating != ChallengeRating.NONE
                )
                TraitLine("Proficiency bonus", modifier(statBlock.proficiencyBonus), Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 20.dp), Arrangement.SpaceAround) {
                AbilityScore("Strength", statBlock, Stat.STRENGTH)
                AbilityScore("Dexterity", statBlock, Stat.DEXTERITY)
                AbilityScore("Constitution", statBlock, Stat.CONSTITUTION)
                AbilityScore("Intelligence", statBlock, Stat.INTELLIGENCE)
                AbilityScore("Wisdom", statBlock, Stat.WISDOM)
                AbilityScore("Charisma", statBlock, Stat.CHARISMA)

                var image by remember { mutableStateOf<ImageBitmap?>(null) }
                LaunchedEffect(statBlock) { image = statBlock.image() }
                image?.let {
                    Image(
                        it,
                        statBlock.name,
                        Modifier.sizeIn(maxHeight = 120.dp),
                        contentScale = ContentScale.Inside
                    )
                }
            }
        }

        ScrollableFlowColumn(16.dp, 16.dp, ReorderStrategy) {
            Column {
                TraitLine("Armor class", statBlock.armorClass, singleLine = false)
                TraitLine("Hit points", statBlock.maxHitPoints.toString())
                TraitLine("Condition immunities", statBlock.conditionImmunities, singleLine = false)
                TraitLine("Damage immunities", statBlock.damageImmunities, singleLine = false)
                TraitLine("Damage resistances", statBlock.damageResistances, singleLine = false)
                TraitLine("Speed", statBlock.speed, singleLine = false)
                TraitLine("Senses", statBlock.displaySenses(), singleLine = false)
                TraitLine("Languages", statBlock.languages)
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow {
                    val allSkills = statBlock.allSkills
                    TraitLine("Skills", visible = allSkills.isNotEmpty())
                    allSkills.displayForEach({ " ${it.display} (${modifier(statBlock.modifierFor(it))})" }) { text, it ->
                        Roller(text, MultiDice.D20 + statBlock.modifierFor(it), statBlock.name)
                    }
                }
            }

            SpellcastingBlock(statBlock)
            AbilityBlock("Traits", statBlock, statBlock.traits)
            AbilityBlock("Actions", statBlock, statBlock.actions)
            AbilityBlock("Bonus actions", statBlock, statBlock.bonusActions)
            AbilityBlock("Reactions", statBlock, statBlock.reactions)
            AbilityBlock("Legendary actions", statBlock, statBlock.legendaryActions) {
                Text("${statBlock.genericName(true)} can take ${statBlock.legendaryActionUses} legendary actions, " +
                        "choosing from the options below. Only one legendary action can be used at a time and only at the end " +
                        "of another creature's turn. ${statBlock.genericName(true)} regains spent legendary actions " +
                        "at the start of ${statBlock.pronoun()} turn.")
            }
            AbilityBlock("Lair actions", statBlock, statBlock.lairActions) {
                Text("When fighting inside ${statBlock.pronoun()} lair, ${statBlock.genericName()} can take " +
                        "lair actions. On initiative count 20 (losing initiative ties), ${statBlock.genericName()} can take one " +
                        "lair action to cause one of the following effects:")
            }
        }
    }
}

@Composable
private fun AbilityScore(ability: String, statBlock: StatBlock, stat: Stat) {
    Column(
        Modifier.width(120.dp).border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextLine(ability)

        val modifier = statBlock.modifiers[stat]
        val modifierText = modifier(modifier)
        Roller(
            statBlock.scores[stat].toString(), MultiDice.D20 + modifier, statBlock.name,
            "${stat.display} ($modifierText)", MaterialTheme.typography.displaySmall
        )

        val saveModifier = statBlock.saveModifierFor(stat)
        val saveModifierText = modifier(saveModifier)
        Roller(
            "Save $saveModifierText", MultiDice.D20 + saveModifier, statBlock.name,
            "${stat.display} saving throw ($saveModifierText)"
        )
    }
}

@Composable
private fun SpellcastingBlock(statBlock: StatBlock) {
    if (statBlock.spellcasting.isNotEmpty()) {
        TraitBlock("Spellcasting") {
            statBlock.spellcasting.forEach { SpellcastingTraitBlock(statBlock, it) }
        }
    }
}

@Composable
fun SpellcastingTraitBlock(statBlock: StatBlock, trait: SpellcastingTrait, expand: Boolean = true) {
    Column {
        TextLine(trait.name, style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
        when (trait) {
            is InnateSpellcasting -> {
                if (expand) {
                    Text("${statBlock.genericName(true)}'s innate spellcasting ability is ${trait.stat.display} " +
                        "(spell save DC ${8 + statBlock.modifierFor(trait.stat, true)}). " +
                        "${statBlock.genericName(true)} can innately cast the following spells, requiring no material components:")
                }
                trait.spellsWithLimit[0]?.let { SpellLine(innateLabel(0), statBlock.name, it) }
                trait.spellsWithLimit[3]?.let { SpellLine(innateLabel(3), statBlock.name, it) }
                trait.spellsWithLimit[2]?.let { SpellLine(innateLabel(2), statBlock.name, it) }
                trait.spellsWithLimit[1]?.let { SpellLine(innateLabel(1), statBlock.name, it) }
            }
            is SpellListCasting -> {
                if (expand) {
                    Text("${statBlock.genericName(true)} is a ${trait.level.display}th-level spellcaster. " +
                        "${statBlock.pronoun(true)} spellcasting ability is ${trait.list.stat.display} " +
                        "(spell save DC ${8 + statBlock.modifierFor(trait.list.stat, true)}). " +
                        "${statBlock.genericName(true)} has the following ${trait.list.display} spells prepared:")
                }
                for (level in 0..9) {
                    val slots = if (level == 0) 0 else trait.level[level]
                    val spells = trait.spellsByLevel[level].orEmpty()
                    val label = when (level) {
                        0 -> if (spells.size > 1) "Cantrips" else "Cantrip"
                        else -> "${count(level)} level (${plural(slots, "slot")})"
                    }
                    if (spells.isNotEmpty()) {
                        SpellLine(label, statBlock.name, spells)
                    } else if (slots > 0) {
                        TextLine(label, style = LocalTextStyle.current.copy(fontStyle = FontStyle.Italic))
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellLine(label: String, creatureName: String, spells: List<StatblockSpell>) {
    RichText(rememberRichString(spells) {
        append(label, SpanStyle(fontStyle = FontStyle.Italic))
        append(": ")

        spells.forEachIndexed { index, spell ->
            spell.resolved?.let {
                withTooltip(index.toString(), Tooltip { SpellDetail(it) }) {
                    // TODO improve spell parsing & support cantrip damage and attack rolls
                    if (it.damage != null) {
                        clickableText(spell.displayName, index.toString()) {
                            log(LogEntry.Roll(spell.displayName, creatureName, it.damage.roll()))
                        }
                    } else {
                        append(spell.displayName)
                    }
                }
                if (spell.comment.isNotBlank()) {
                    append(" ")
                    append(spell.comment, SpanStyle(fontStyle = FontStyle.Italic))
                }
                if (index != spells.lastIndex) {
                    append(", ")
                }
            }
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpellDisplay(
    spell: Spell,
    last: Boolean = true,
    comment: String? = null,
    textModifier: Modifier = Modifier,
    tooltipModifier: Modifier = Modifier,
) {
    TooltipArea(
        { SpellDetail(spell) },
        tooltipModifier,
        tooltipPlacement = TooltipPlacement.CursorPoint(DpOffset(0.dp, 16.dp), Alignment.BottomCenter)
    ) {
        val text = AnnotatedString.Builder()
            .append(spell.name as CharSequence)
            .withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                appendIf(!comment.isNullOrBlank()) { " $comment" }
            }
            .appendIf(!last) { "," }
            .toAnnotatedString()
        TextLine(text, textModifier)
    }
}

private inline fun AnnotatedString.Builder.appendIf(condition: Boolean, text: () -> String): AnnotatedString.Builder {
    if (condition) append(text())
    return this
}

@Composable
fun SpellDetail(spell: Spell, maxWidth: Dp = 600.dp) {
    Column(tooltipModifier(Color(0xfffafad0)).widthIn(max = maxWidth), Arrangement.spacedBy(4.dp)) {
        TextLine(spell.name, style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
        TextLine("${spell.school.display} ${spell.type}")
        Text("Components: ${spell.components}")
        Text(spell.time)
        TextLine("Range ${spell.range}, duration ${spell.duration}")
        Text(spell.text.filterIsInstance<SpellText.Text>().joinToString("\n") { it.text })
    }
}

fun innateLabel(perDay: Int) = when (perDay) {
    0 -> "At will"
    else -> "$perDay/day each"
}

@Composable
private fun AbilityBlock(
    label: String,
    statBlock: StatBlock,
    abilities: List<Ability>,
    extraContent: @Composable ColumnScope.() -> Unit = {}
) {
    if (abilities.isNotEmpty()) {
        TraitBlock(label) {
            extraContent()
            abilities.forEach { Ability(statBlock, it) }
        }
    }
}

@Composable
private fun TraitBlock(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Column {
            TextLine(label, style = MaterialTheme.typography.titleLarge)
            HorizontalDivider()
        }
        content()
    }
}

@Composable
private fun Ability(statBlock: StatBlock, ability: Ability) {
    when (ability) {
        is Ability.Attack -> Attack(statBlock, ability)
        is Ability.Trait -> Trait(statBlock, ability)
    }
}

@Composable
fun Attack(statBlock: StatBlock, attack: Ability.Attack, expand: Boolean = true, addToName: String = "") {
    val name = attack.name
    val modifier = statBlock.modifierFor(attack.stat, true) + attack.modifier
    val damage = attack.damage + statBlock.modifierFor(attack.stat, false)
    val positionProvider = rememberCursorPositionProvider(DpOffset(0.dp, -32.dp), Alignment.TopCenter)
    RichText(rememberRichString(attack, addToName, expand) {
        val attackRoll = MultiDice.D20 + modifier
        clickableText("$name$addToName", "title", SpanStyle(fontWeight = FontWeight.Bold)) {
            log(LogEntry.Attack(statBlock.name, name, attackRoll.roll(), attackRoll.roll(), damage))
        }
        if ((attack.legendaryCost ?: 0) > 1) {
            append(" ")
            append(attack.costDisplay())
        }
        if (attack.use is Use.Limited) {
            append(" ")
            append(attack.use.display)
        }
        append(": ")
        clickableText("${modifier(modifier)} to hit", "attack") {
            log(LogEntry.Roll(name, statBlock.name, attackRoll.roll(), attackRoll.roll()))
        }
        append(", ")
        append(attack.rangeDisplay())
        append(" ")
        val hitText = "Hit: ${damage.asString()} damage"
        fun appendToHit() {
            clickableText(hitText, "hit") {
                log(LogEntry.Roll("$name damage", statBlock.name, damage.roll()))
            }
        }
        if (attack.extra.isBlank()) {
            appendToHit()
        } else if (expand) {
            appendToHit()
            append("\n")
            append((attack.extra))
        } else {
            withTooltip("tooltip", Tooltip(positionProvider = positionProvider) { TextLine(attack.extra, tooltipModifier()) }) {
                appendToHit()
            }
        }
    })
}

@Composable
fun Trait(statBlock: StatBlock, ability: Ability.Trait, expand: Boolean = true, addToName: String = "") {
    val name = ability.name + addToName
    if (expand) {
        RichText(rememberRichString(ability, addToName) {
            if (ability.roll == null) {
                append(name, SpanStyle(fontWeight = FontWeight.Bold))
            } else {
                clickableText(name, "title", SpanStyle(fontWeight = FontWeight.Bold)) {
                    log(LogEntry.Roll(name, statBlock.name, ability.roll.roll()))
                }
            }
            if ((ability.legendaryCost ?: 0) > 1) {
                append(" ")
                append(ability.costDisplay())
            }
            if (ability.use is Use.Limited) {
                append(" ")
                append(ability.use.display)
            }
            if (ability.recharge != Recharge.NO) {
                append(" ")
                clickableText("(Recharge ${ability.recharge.display})", "recharge") {
                    log(LogEntry.Roll("${ability.name} recharge", statBlock.name, MultiDice(1 d 6).roll()))
                }
            }
            append(": ")
            append(ability.description)
        })
    } else {
        TooltipText(ability.description) {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow {
                AbilityName(ability, ability.roll, name, statBlock.name)
                Use(ability.use)
                Recharge(ability.recharge, ability.name, statBlock.name)
            }
        }
    }
}

@Composable
private fun AbilityName(ability: Ability, roll: MultiDice?, name: String, creatureName: String) {
    if (roll == null) {
        TextLine(name, style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
    } else {
        Roller(name, roll, creatureName, ability.name,
            LocalTextStyle.current.copy(fontWeight = FontWeight.Bold), false)
    }
}

@Composable
private fun Use(use: Use?) {
    if (use is Use.Limited) {
        TextLine(use.display)
    }
}

@Composable
private fun Recharge(recharge: Recharge, abilityName: String, creatureName: String) {
    if (recharge != Recharge.NO) {
        Roller(" (Recharge ${recharge.display})", MultiDice(1 d 6), creatureName, "$abilityName recharge", twice = false)
    }
}

private fun StatBlock.displaySenses(): String {
    val pp = "Passive perception ${10 + modifierFor(Skill.PERCEPTION)}"
    return if (senses.isBlank()) pp else "$senses, ${pp.lowercase()}"
}
