package org.hertsig.dnd.combat.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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

@Composable
fun PrepareCombatPage(state: AppState, modifier: Modifier = Modifier) {
    Row(modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        val labelState = remember { mutableStateOf("") }
        var label by labelState
        val focusRequester = remember { FocusRequester() }

        fun finish(name: String, roll: MultiDieRolls, entry: CombatEntry) {
            log(LogEntry.Roll("Initiative", name, roll))
            state.combat.addInitiative(entry)
            label = ""
            focusRequester.requestFocus()
        }

        ScrollableColumn(arrangement = Arrangement.spacedBy(4.dp)) {
            item {
                BasicDropdown(state.statBlocks.orderState, Modifier.width(200.dp))
            }
            items(state.statBlocks.visibleStatBlocks()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    val initiativeRoll = MultiDice.D20 + it.modifierFor(Stat.DEXTERITY)
                    RowTextLine(it.name, Modifier.width(200.dp))
                    SmallButton({
                        val roll = initiativeRoll.roll()
                        finish(it.name, roll, CombatEntry.GroupEntry(it, roll.total, label))
                    }) { TextLine("Group") }
                    SmallButton({
                        val roll = initiativeRoll.roll()
                        finish(it.name, roll, CombatEntry.Creature(it, roll.total, label))
                    }) { TextLine("Single") }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
    }
}
