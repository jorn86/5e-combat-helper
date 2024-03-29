package org.hertsig.dnd.combat.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.BasicEditNumber
import org.hertsig.compose.component.HorizontalDivider
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.TextLine
import org.hertsig.compose.component.flow.ReorderStrategy
import org.hertsig.compose.component.flow.ScrollableFlowColumn
import org.hertsig.dnd.combat.component.displayForEach
import org.hertsig.dnd.combat.component.modifier
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.element.*
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.util.display

@Composable
fun OverviewPage(statBlocks: List<StatBlock>, modifier: Modifier, active: StatBlock? = null) {
    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
        Column(modifier.padding(8.dp)) {
            val columns = remember { mutableStateOf((statBlocks.size / 3).coerceIn(2, 4)) }
            var expand by remember { mutableStateOf(true) }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(expand, { expand = !expand })
                RowTextLine("Expand")
                BasicEditNumber(columns, 1, 6, suffix = "columns", width = 70.dp)
            }
            ScrollableFlowColumn(4.dp, 4.dp, ReorderStrategy, columns.value) {
                statBlocks.filter { it.visible }.forEach {
                    SmallStatBlock(it, it == active, expand)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun SmallStatBlock(statBlock: StatBlock, active: Boolean = false, expand: Boolean = true) {
    val color = if (active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    Column(Modifier.border(2.dp, color, RoundedCornerShape(8.dp)).padding(8.dp, 4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            TextLine(statBlock.name, style = MaterialTheme.typography.headlineSmall)
            TextLine(statBlock.type)
        }

        HorizontalDivider()
        TraitLine("Armor class", statBlock.armorClass)
        TraitLine("Hit points", statBlock.maxHitPoints.toString())
        TraitLine("Speed", statBlock.speed, singleLine = false)

        HorizontalDivider()
        FlowRow(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Stat.entries.displayForEach({ "${it.shortDisplay} ${statBlock.scores[it]}" }) { text, it ->
                Roller(text, MultiDice.D20 + statBlock.modifierFor(it), statBlock.name, "${it.display} check")
            }
        }
        if (statBlock.proficientSaves.isNotEmpty()) {
            FlowRow(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                TraitLine("Saving throws", visible = true)
                statBlock.proficientSaves.displayForEach({ "${it.shortDisplay} ${modifier(statBlock.saveModifierFor(it))}" }) { text, it ->
                    Roller(text, MultiDice.D20 + statBlock.saveModifierFor(it), statBlock.name, "${it.display} saving throw")
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

        if (statBlock.spellcasting.isNotEmpty()) {
            HorizontalDivider()
            statBlock.spellcasting.forEach { SpellcastingTraitBlock(statBlock, it, expand) }
        }
        DisplayAbilities(statBlock.traits, statBlock, expand)
        DisplayAbilities(statBlock.actions, statBlock, expand)
        DisplayAbilities(statBlock.bonusActions, statBlock, expand, " (bonus action)")
        DisplayAbilities(statBlock.reactions, statBlock, expand, " (reaction)")
        DisplayAbilities(statBlock.legendaryActions, statBlock, expand) {
            TraitLine("Legendary actions", statBlock.legendaryActionUses.toString())
        }
        DisplayAbilities(statBlock.lairActions, statBlock, expand) {
            TextLine("Lair actions", style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))
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
        is Ability.Trait  ->  Trait(statBlock, ability, expand, addToName)
    }
}
