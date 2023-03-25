package org.hertsig.dnd.combat.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import org.hertsig.compose.component.BasicEditNumber
import org.hertsig.compose.component.HorizontalDivider
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.TextLine
import org.hertsig.compose.component.flow.ReorderStrategy
import org.hertsig.compose.component.flow.ScrollableFlowColumn
import org.hertsig.compose.display
import org.hertsig.core.logger
import org.hertsig.dnd.combat.component.displayForEach
import org.hertsig.dnd.combat.component.modifier
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.element.Attack
import org.hertsig.dnd.combat.element.Roller
import org.hertsig.dnd.combat.element.Trait
import org.hertsig.dnd.combat.element.TraitLine
import org.hertsig.dnd.dice.MultiDice

private val log = logger {}

@Composable
fun OverviewPage(statBlocks: List<StatBlock>, modifier: Modifier, active: StatBlock? = null) {
    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
        Column(modifier.padding(8.dp)) {
            val columns = remember { mutableStateOf((statBlocks.size / 3).coerceIn(2, 4)) }
            var expand by remember { mutableStateOf(true) }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(expand, { expand = !expand })
                RowTextLine("Expand")
                BasicEditNumber(columns, 1, 6, suffix = "columns", width = 70.dp)
            }
            ScrollableFlowColumn(4.dp, 4.dp, ReorderStrategy, columns.value) {
                statBlocks.forEach {
                    SmallStatBlock(it, it == active, expand)
                }
            }
        }
    }
}

@Composable
fun SmallStatBlock(statBlock: StatBlock, active: Boolean = false, expand: Boolean = true) {
    val color = if (active) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.primary
    Column(Modifier.border(2.dp, color, RoundedCornerShape(8.dp)).padding(8.dp, 4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            TextLine(statBlock.name, style = MaterialTheme.typography.h6)
            TextLine(statBlock.type)
        }

        HorizontalDivider()
        TraitLine("Armor class", statBlock.armorClass)
        TraitLine("Hit points", statBlock.maxHitPoints.toString())
        TraitLine("Speed", statBlock.speed, singleLine = false)

        HorizontalDivider()
        FlowRow(crossAxisSpacing = 2.dp) {
//            TraitLine("Abilities", visible = true)
            Stat.values().asList().displayForEach({ "${it.display.substring(0, 3)} ${statBlock.scores[it]}" }) { text, it ->
                Roller(text, MultiDice.D20 + statBlock.modifierFor(it), statBlock.name, "${it.display} check")
            }
        }
        if (statBlock.proficientSaves.isNotEmpty()) {
            FlowRow(crossAxisSpacing = 2.dp) {
                TraitLine("Saving throws", visible = true)
                statBlock.proficientSaves.displayForEach({ "${it.display.substring(0, 3)} ${modifier(statBlock.saveModifierFor(it))}" }) { text, it ->
                    Roller(text, MultiDice.D20 + statBlock.saveModifierFor(Stat.STRENGTH), statBlock.name, "${it.display} saving throw")
                }
            }
        }
        if (expand && statBlock.allSkills.isNotEmpty()) {
            FlowRow {
                TraitLine("Skills", visible = true)
                statBlock.allSkills.displayForEach({ it.display }) { text, it ->
                    Roller(text, MultiDice.D20 + statBlock.modifierFor(it), statBlock.name, "${it.display} check")
                }
            }
        }

        TraitLine("Condition immunities", statBlock.conditionImmunities, singleLine = false)
        TraitLine("Damage immunities", statBlock.damageImmunities, singleLine = false)
        TraitLine("Damage resistances", statBlock.damageResistances, singleLine = false)
        if (expand) {
            TraitLine("Senses", statBlock.senses, singleLine = false)
            TraitLine("Languages", statBlock.languages, singleLine = false)
        }

        DisplayAbilities(statBlock.traits, statBlock, expand)
        DisplayAbilities(statBlock.actions, statBlock, expand)
        DisplayAbilities(statBlock.bonusActions, statBlock, expand, " (bonus action)")
        DisplayAbilities(statBlock.reactions, statBlock, expand, " (reaction)")
        DisplayAbilities(statBlock.legendaryActions, statBlock, expand) {
            TraitLine("Legendary actions", statBlock.legendaryActionUses.toString())
        }
    }
}

@Composable
private fun DisplayAbilities(
    abilities: List<Ability>,
    statBlock: StatBlock,
    expand: Boolean,
    addToName: String = "",
    extraContent: @Composable () -> Unit = {}
) {
    if (abilities.isNotEmpty()) {
        HorizontalDivider()
        extraContent()
        abilities.forEach {
            DisplayAbility(it, statBlock, expand, addToName)
        }
    }
}

@Composable
private fun DisplayAbility(ability: Ability, statBlock: StatBlock, expand: Boolean, addToName: String = "") {
    when (ability) {
        is Ability.Attack -> Attack(statBlock, ability, expand, addToName)
        is Ability.Trait -> Trait(statBlock, ability, expand, addToName)
    }
}
