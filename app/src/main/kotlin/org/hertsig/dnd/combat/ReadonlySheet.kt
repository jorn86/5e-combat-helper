package org.hertsig.dnd.combat

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import org.hertsig.compose.component.HorizontalDivider
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.TextLine
import org.hertsig.compose.component.flow.ReorderStrategy
import org.hertsig.compose.component.flow.ScrollableFlowColumn
import org.hertsig.compose.display
import org.hertsig.compose.modifier
import org.hertsig.core.error
import org.hertsig.core.logger
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.dice.Dice
import org.hertsig.dnd.dice.d

private val log = logger {}

@Composable
fun ReadonlySheet(statBlock: StatBlock, modifier: Modifier = Modifier) {
    Column(modifier) {
        Column {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                RowTextLine(statBlock.name, style = MaterialTheme.typography.h4)
                RowTextLine("${statBlock.size.display} ${statBlock.type}")
            }
            Row {
                TraitLine("Challenge rating", "${statBlock.challengeRating.display} (${statBlock.xp} XP)",
                    Modifier.weight(1f), visible = statBlock.challengeRating != ChallengeRating.NONE)
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
                image?.let { Image(it, statBlock.name, Modifier.sizeIn(maxHeight = 120.dp), contentScale = ContentScale.Inside) }
            }
        }

        ScrollableFlowColumn(16.dp, 16.dp, ReorderStrategy()) {
            Column {
                TraitLine("Armor class", statBlock.armorClass, singleLine = false)
                TraitLine("Hit points", statBlock.maxHitPoints.toString())
                TraitLine("Condition immunities", statBlock.conditionImmunities, singleLine = false)
                TraitLine("Damage immunities", statBlock.damageImmunities, singleLine = false)
                TraitLine("Damage resistances", statBlock.damageResistances, singleLine = false)
                TraitLine("Speed", statBlock.speed, singleLine = false)

                val pp = "Passive perception ${ 10 + statBlock.modifierFor(Skill.PERCEPTION) }"
                val senses = if (statBlock.senses.isBlank()) pp else "${statBlock.senses}, ${pp.lowercase()}"
                TraitLine("Senses", senses, singleLine = false)

                TraitLine("Languages", statBlock.languages)
                TraitLine("Caster level", "${statBlock.casterLevel.display} (${statBlock.casterAbility.display})",
                    visible = statBlock.casterLevel != CasterLevel.NONE)
                FlowRow {
                    val allSkills = statBlock.allSkills
                    TraitLine("Skills", visible = allSkills.isNotEmpty())
                    allSkills.forEachIndexed { index, it ->
                        val modifier = statBlock.modifierFor(it)
                        var text = "${it.display} (${modifier(modifier)})"
                        if (index + 1 < allSkills.size) text += ", "
                        Roller(text, (1 d 20) + modifier, statBlock.name)
                    }
                }
            }

            AbilityBlock("Traits", statBlock, statBlock.traits)
            AbilityBlock("Actions", statBlock, statBlock.actions)
            AbilityBlock("Bonus actions", statBlock, statBlock.bonusActions)
            AbilityBlock("Reactions", statBlock, statBlock.reactions)
            AbilityBlock("Legendary actions", statBlock, statBlock.legendaryActions) {
                Text("The ${statBlock.name} can take ${statBlock.legendaryActionUses} legendary actions, choosing from the options below. " +
                        "Only one legendary action can be used at a time and only at the end of another creature's turn. " +
                        "The ${statBlock.name} regains spent legendary actions at the start of its turn.")
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
        Roller(statBlock.scores[stat].toString(), (1 d 20) + modifier, statBlock.name,
            "${stat.display} ($modifierText)", MaterialTheme.typography.h3)

        val saveModifier = statBlock.saveModifierFor(stat)
        val saveModifierText = modifier(saveModifier)
        Roller("Save $saveModifierText", (1 d 20) + saveModifier, statBlock.name,
            "${stat.display} saving throw ($saveModifierText)")
    }
}

@Composable
private fun AbilityBlock(label: String, statBlock: StatBlock, abilities: List<Ability>, extraContent: @Composable ColumnScope.() -> Unit = {}) {
    if (abilities.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Column {
            TextLine(label, style = MaterialTheme.typography.h6)
            HorizontalDivider()
        }
        extraContent()
        abilities.forEach { Ability(statBlock, it) }
    }
}

@Composable
private fun Ability(statBlock: StatBlock, ability: Ability) {
    val name = ability.name + ability.use.display
    when (ability) {
        is Ability.Trait -> TraitLine(name, ability.description, singleLine = false)
        is Ability.MeleeAttack -> MeleeAttack(statBlock, name, ability)
        is Ability.RangedAttack -> RangedAttack(statBlock, name, ability)
        is Ability.Custom -> Custom(statBlock, name, ability)
        is LegendaryAbility -> Legendary(statBlock, ability)
        else -> log.error{"No renderer for $ability"}
    }
}

@Composable
private fun MeleeAttack(statBlock: StatBlock, name: String, attack: Ability.MeleeAttack) {
    val rangeText = "reach ${attack.reach} ft."
    AttackRoller(
        name,
        "Melee weapon attack",
        statBlock.modifierFor(attack.stat, attack.proficient) + attack.modifier,
        rangeText,
        attack.target,
        attack.damage + statBlock.modifierFor(attack.stat, false),
        statBlock.name
    )
}

@Composable
private fun RangedAttack(statBlock: StatBlock, name: String, attack: Ability.RangedAttack) {
    val rangeText = if (attack.range >= attack.longRange) "range ${attack.range} ft." else "range ${attack.range} / ${attack.longRange} ft."
    AttackRoller(
        name,
        "Ranged weapon attack",
        statBlock.modifierFor(attack.stat, attack.proficient) + attack.modifier,
        rangeText,
        attack.target,
        attack.damage + statBlock.modifierFor(attack.stat, false),
        statBlock.name
    )
}

@Composable
private fun Custom(statBlock: StatBlock, name: String, ability: Ability.Custom) {
    Column {
        Row {
            if (ability.roll == null) {
                TextLine(name, style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
            } else {
                Roller(
                    name,
                    ability.roll,
                    statBlock.name,
                    "${ability.name}: ${ability.roll.type}",
                    LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                    false
                )
            }
            if (ability.recharge != Recharge.NO) {
                TextLine(" ")
                Roller(
                    "(Recharge ${ability.recharge.display})",
                    1 d 6,
                    statBlock.name,
                    "${ability.name} recharge",
                    twice = false
                )
            }
            TextLine(": ")
        }
        Text(ability.description)
    }
}

@Composable
private fun Legendary(statBlock: StatBlock, ability: LegendaryAbility) {
    val real = ability.ability
    val name = real.name + ability.costDisplay() + real.use.display
    when (real) {
        is Ability.Trait -> TraitLine(name, real.description, singleLine = false)
        is Ability.MeleeAttack -> MeleeAttack(statBlock, name, real)
        is Ability.RangedAttack -> RangedAttack(statBlock, name, real)
        is Ability.Custom -> Custom(statBlock, name, real)
        else -> log.error { "No renderer for $real (inside legendary)" }
    }
}

@Composable
private fun TraitLine(
    name: String,
    text: String = "",
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    singleLine: Boolean = true,
    visible: Boolean = text.isNotBlank()
) {
    if (visible) {
        val builder = AnnotatedString.Builder()
        builder.pushStyle(style.toSpanStyle().copy(fontWeight = FontWeight.Bold))
        builder.append("$name: ")
        builder.pop()
        builder.append(text)
        if (singleLine) {
            TextLine(builder.toAnnotatedString(), modifier)
        } else {
            Text(builder.toAnnotatedString(), modifier)
        }
    }
}

@Composable
private fun AttackRoller(
    name: String,
    type: String,
    modifier: Int,
    rangeText: String,
    target: String,
    damage: Dice,
    creatureName: String,
    style: TextStyle = LocalTextStyle.current
) {
    FlowRow {
        val attackRoll = (1 d 20) + modifier
        TextLine("$name:", Modifier.clickable {
            log(LogEntry.Attack(creatureName, name, attackRoll.roll(), attackRoll.roll(), damage.roll()))
        }, style = style.copy(fontWeight = FontWeight.Bold))
//        TextLine(" $type, ", style = style)
        Roller("${modifier(modifier)} to hit, ", attackRoll, creatureName, name, style)
        TextLine("$rangeText, $target. ", style = style)
        Roller("Hit: $damage damage", damage, creatureName, "$name: ${damage.type} damage", style, false)
    }
}
