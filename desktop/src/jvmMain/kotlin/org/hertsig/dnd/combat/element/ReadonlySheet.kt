@file:OptIn(ExperimentalLayoutApi::class)

package org.hertsig.dnd.combat.element

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.google.accompanist.flowlayout.FlowRow
import org.hertsig.compose.append
import org.hertsig.compose.build
import org.hertsig.compose.component.*
import org.hertsig.compose.component.flow.ReorderStrategy
import org.hertsig.compose.component.flow.ScrollableFlowColumn
import org.hertsig.dnd.combat.component.displayForEach
import org.hertsig.dnd.combat.component.modifier
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.log
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.d
import org.hertsig.logger.logger
import org.hertsig.util.count
import org.hertsig.util.display
import org.hertsig.util.plural

@Composable
fun ReadonlySheet(statBlock: StatBlock, modifier: Modifier = Modifier) {
    Column(modifier) {
        Column(Modifier.padding(8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                RowTextLine(statBlock.name, style = MaterialTheme.typography.h4)
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
        Modifier.width(120.dp).border(4.dp, MaterialTheme.colors.primary, RoundedCornerShape(24.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextLine(ability)

        val modifier = statBlock.modifiers[stat]
        val modifierText = modifier(modifier)
        Roller(
            statBlock.scores[stat].toString(), MultiDice.D20 + modifier, statBlock.name,
            "${stat.display} ($modifierText)", MaterialTheme.typography.h3
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
                SpellBlock(statBlock.name, trait.spellsWithLimit, ::innateLabel)
            }
            is SpellListCasting -> {
                if (expand) {
                    Text("${statBlock.genericName(true)} is a ${trait.level.display}th-level spellcaster. " +
                        "${statBlock.pronoun(true)} spellcasting ability is ${trait.list.stat.display} " +
                        "(spell save DC ${8 + statBlock.modifierFor(trait.list.stat, true)}). " +
                        "${statBlock.genericName(true)} has the following ${trait.list.display} spells prepared:")
                }
                SpellBlock(statBlock.name, trait.spellsByLevel) { level ->
                    when (level) {
                        0 -> "Cantrip"
                        else -> "${count(level)} level (${plural(trait.level[level], "slot")})"
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellBlock(creatureName: String, spells: Map<Int, List<StatblockSpell>>, label: (Int) -> String) {
    spells.forEach { (key, value) ->
        if (value.isNotEmpty()) {
            FlowRow(mainAxisSpacing = 4.dp) {
                TextLine(label(key) + ":", style = LocalTextStyle.current.copy(fontStyle = FontStyle.Italic))
                value.forEachIndexed { index, spell ->
                    spell.resolve()?.let {
                        SpellDisplay(it, index + 1 == value.size, spell.comment, spellClickable(it, creatureName))
                    }
                }
            }
        }
    }
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

fun spellClickable(spell: Spell, creatureName: String): Modifier {
    val damage = spell.damage ?: return Modifier
    // TODO fix spell parsing & support cantrip damage and attack rolls
    return Modifier.clickable { log(LogEntry.Roll(spell.name, creatureName, damage.roll())) }
}

private inline fun AnnotatedString.Builder.appendIf(condition: Boolean, text: () -> String): AnnotatedString.Builder {
    if (condition) append(text())
    return this
}

@Composable
private fun SpellDetail(spell: Spell, maxWidth: Dp = 600.dp) {
    Column(tooltipModifier(Color(0xfffafad0)).widthIn(max = maxWidth), Arrangement.spacedBy(4.dp)) {
        TextLine(spell.name, style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
        TextLine("${spell.school.display} ${spell.type}")
        TextLine("${spell.time}, range ${spell.range}, duration ${spell.duration}")
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
            TextLine(label, style = MaterialTheme.typography.h6)
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
    val rangeText = listOfNotNull(
        attack.reach?.let { "reach $it ft." },
        attack.range?.let {
            var text = "range $it"
            attack.longRange?.let { lr -> if (lr != it) text += "/$lr" }
            text += " ft."
            text
        }
    ).joinToString(" or ")
    val modifier = statBlock.modifierFor(attack.stat, true) + attack.modifier
    val damage = attack.damage + statBlock.modifierFor(attack.stat, false)
    FlowRow {
        val attackRoll = MultiDice.D20 + modifier
        TextLine("$name$addToName${attack.costDisplay()}:", Modifier.clickable {
            log(LogEntry.Attack(statBlock.name, name, attackRoll.roll(), attackRoll.roll(), damage))
        }, style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
//        TextLine(" $type, ", style = style)
        Roller("${modifier(modifier)} to hit, ", attackRoll, statBlock.name, name)
        TextLine("$rangeText ")
//        TextLine("${attack.target}. ")
        val hitText = "Hit: ${damage.asString()} damage"
        if (attack.extra.isNotBlank() && expand) {
            Roller("$hitText, ", damage, statBlock.name, "$name damage", LocalTextStyle.current, false)
            Text(attack.extra)
        } else if (attack.extra.isNotBlank()) {
            TooltipText("$hitText, ${attack.extra}") {
                Roller(hitText, damage, statBlock.name, "$name damage", LocalTextStyle.current, false)
            }
        } else {
            Roller(hitText, damage, statBlock.name, "$name damage", LocalTextStyle.current, false)
        }
    }
}

@Composable
fun Trait(statBlock: StatBlock, ability: Ability.Trait, expand: Boolean = true, addToName: String = "") {
    val name = ability.name + addToName + ability.costDisplay()
    if (expand) {
        Column {
            val bold = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
            RichText(rememberRichText(ability, addToName) {
                paragraph {
                    val title = AnnotatedString.Builder()
                        .append(SpanStyle(fontWeight = FontWeight.Bold), name)
                        .build()
                    text(title, Modifier.roll(ability.roll, name, statBlock.name, false), bold)
                    if (ability.use is Use.Limited) text(ability.use.display)
                    if (ability.recharge != Recharge.NO) {
                        text(" (Recharge ${ability.recharge.display})",
                            Modifier.roll(MultiDice(1 d 6), statBlock.name, "${ability.name} recharge", false))
                    }
                    text(": ")
                    breakingText(ability.description)
                }
            })
        }
    } else {
        TooltipText(ability.description) {
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
