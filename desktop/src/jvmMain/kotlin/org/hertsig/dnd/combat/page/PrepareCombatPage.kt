package org.hertsig.dnd.combat.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.*
import org.hertsig.compose.util.autoFocus
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.log
import org.hertsig.dnd.dice.DieRoll
import org.hertsig.dnd.dice.DieRolls
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.MultiDieRolls
import kotlin.math.roundToInt

@Composable
fun PrepareCombatPage(state: AppState, modifier: Modifier = Modifier) {
    Row(modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        val labelState = remember { mutableStateOf("") }
        var label by labelState
        val focusRequester = remember { FocusRequester() }

        fun finish(name: String, roll: MultiDieRolls, entry: CombatEntry) {
            log(LogEntry.Roll("Initiative", name, roll))
            state.combat.addInitiative(entry)
            label = ""
            focusRequester.requestFocus()
        }

        val groupSize = remember { mutableStateOf(4) }
        ScrollableColumn(arrangement = Arrangement.spacedBy(4.dp)) {
            item {
                BasicDropdown(state.statBlocks.orderState, Modifier.width(200.dp))
            }
            item {
                SpacedRow {
                    TextLine("Group size:")
                    BasicEditNumber(groupSize, 2, 20)
                }
            }
            items(state.statBlocks.visibleStatBlocks()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    val initiativeRoll = MultiDice.D20 + it.modifierFor(Stat.DEXTERITY)
                    RowTextLine(it.name, Modifier.width(200.dp))
                    SmallButton({
                        val roll = initiativeRoll.roll()
                        finish(it.name, roll, CombatEntry.GroupEntry(it, roll.total, label, groupSize.value))
                    }) { TextLine("Group") }
                    SmallButton({
                        val roll = initiativeRoll.roll()
                        finish(it.name, roll, CombatEntry.Creature(it, roll.total, label))
                    }) { TextLine("Single") }
                }
            }
        }

        val totalCreatures = state.combat.entries.sumOf { it.groupSize }
        val totalXp = state.combat.entries.sumOf { it.totalXp }
        val multiplier = when (totalCreatures) {
            1 -> 1.0
            2 -> 1.5
            in 3..6 -> 2.0
            in 7..10 -> 2.5
            in 11..14 -> 3.0
            else -> 4.0
        }
        SpacedColumn {
            BasicEditText(labelState, Modifier.autoFocus(focusRequester).width(200.dp), "Label")

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                val initiativeModifierState = remember { mutableStateOf(2) }
                val initiativeModifier by initiativeModifierState
                val initiativeState = remember { mutableStateOf(12) }
                val initiative by initiativeState

                Row(Modifier.width(200.dp), Arrangement.spacedBy(4.dp)) {
                    BasicEditNumber(initiativeState, -4, 30)
                    BasicEditNumber(initiativeModifierState, -5, 10)
                }
                Button({
                    finish(label, MultiDieRolls(DieRolls(listOf(DieRoll(20, initiative - initiativeModifier)), initiativeModifier)),
                        CombatEntry.Simple(label, initiative, initiativeModifier))
                }) { TextLine("Simple") }
                RowTextLine("1d20 + $initiativeModifier = $initiative")
            }
        }

        SpacedColumn(Modifier.width(400.dp)) {
            val partyCount = remember { mutableStateOf(5) }
            val partyLevel = remember { mutableStateOf(10) }
            TextLine("Difficulty")
            SpacedRow {
                TextLine("Party:")
                BasicEditNumber(partyCount, 1, 10)
                TextLine("level")
                BasicEditNumber(partyLevel, 1, 20)
            }

            fun xp(difficulty: EncounterDifficulty) = xpTreshold(partyLevel.value, difficulty) * partyCount.value
            val deadlyTotal = xp(EncounterDifficulty.DEADLY)
            TextLine("Hard: ${xp(EncounterDifficulty.HARD)}, Deadly: $deadlyTotal")

            TextLine("$totalCreatures creatures (x$multiplier)")
            TextLine("$totalXp XP / ${(totalXp * multiplier).roundToInt()} adjusted XP")
            val daily = xp(EncounterDifficulty.DAILY)
            Text("Good single encounter day is XP around 2x deadly (${2 * deadlyTotal}) or adjusted XP around 1.5x daily (${(1.5 * daily).roundToInt()})")
        }
    }
}

enum class EncounterDifficulty {
    EASY, MEDIUM, HARD, DEADLY, DAILY
}

private val table = arrayOf(
    arrayOf(25, 50, 75, 100, 300),
    arrayOf(50, 100, 150, 200, 600),
    arrayOf(75, 150, 225, 400, 1200),
    arrayOf(125, 250, 375, 500, 1700),
    arrayOf(250, 500, 750, 1100, 3500),
    arrayOf(300, 600, 900, 1400, 4000),
    arrayOf(350, 750, 1100, 1700, 5000),
    arrayOf(450, 900, 1400, 2100, 6000),
    arrayOf(550, 1100, 1600, 2400, 7500),
    arrayOf(600, 1200, 1900, 2800, 9000),
    arrayOf(800, 1600, 2400, 3600, 10500),
    arrayOf(1000, 2000, 3000, 4500, 11500),
    arrayOf(1100, 2200, 3400, 5100, 13500),
    arrayOf(1250, 2500, 3800, 5700, 15000),
    arrayOf(1400, 2800, 4300, 6400, 18000),
    arrayOf(1600, 3200, 4800, 7200, 20000),
    arrayOf(2000, 3900, 5900, 8800, 25000),
    arrayOf(2100, 4200, 6300, 9500, 27000),
    arrayOf(2400, 4900, 7300, 10900, 30000),
    arrayOf(2800, 5700, 8500, 12700, 40000),
)

fun xpTreshold(characterLevel: Int, difficulty: EncounterDifficulty) = table.getOrNull(characterLevel-1)?.getOrNull(difficulty.ordinal)
    ?: throw IllegalArgumentException("Data for character level $characterLevel not available")
