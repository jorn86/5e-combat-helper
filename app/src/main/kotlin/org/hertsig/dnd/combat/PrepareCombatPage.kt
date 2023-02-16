package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import org.hertsig.compose.autoFocus
import org.hertsig.compose.component.BasicEditNumber
import org.hertsig.compose.component.BasicEditText
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.TextLine
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.CombatEntry
import org.hertsig.dnd.combat.dto.Stat
import org.hertsig.dnd.combat.dto.modifierFor
import org.hertsig.dnd.dice.DieRoll
import org.hertsig.dnd.dice.DieRolls
import org.hertsig.dnd.dice.d

@Composable
fun PrepareCombatPage(state: AppState, modifier: Modifier = Modifier) {
    Column(modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val labelState = remember { mutableStateOf("") }
        var label by labelState
        val focusRequester = remember { FocusRequester() }

        fun finish(name: String, roll: DieRolls, entry: CombatEntry) {
            log(LogEntry.Roll(name, "Initiative", roll))
            state.addInitiative(entry)
            label = ""
            focusRequester.requestFocus()
        }

        BasicEditText(labelState, Modifier.autoFocus(focusRequester).width(200.dp), "Label")

        state.statBlocks.forEach {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                val initiativeRoll = (1 d 20) + it.modifierFor(Stat.DEXTERITY)
                RowTextLine(it.name, Modifier.width(200.dp))
                Button({
                    val roll = initiativeRoll.roll()
                    finish(it.name, roll, CombatEntry.StatBlockEntry(it, roll.total, label))
                }, contentPadding = PaddingValues(16.dp, 0.dp)) {
                    TextLine("Group")
                }
                Button({
                    val roll = initiativeRoll.roll()
                    finish(it.name, roll, CombatEntry.Creature(it, roll.total, label))
                }, contentPadding = PaddingValues(16.dp, 0.dp)) {
                    TextLine("Single")
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            val initiativeModifierState = remember { mutableStateOf(2) }
            val modifier by initiativeModifierState
            val initiativeState = remember { mutableStateOf(12) }
            val initiative by initiativeState

            Row(Modifier.width(200.dp), Arrangement.spacedBy(4.dp)) {
                BasicEditNumber(initiativeState, -4, 30)
                BasicEditNumber(initiativeModifierState, -5, 10)
            }
            Button({
                finish(label, DieRolls(listOf(DieRoll(20, initiative - modifier)), modifier),
                    CombatEntry.Simple(label, initiative, modifier))
            }) { TextLine("Simple") }
            RowTextLine("1d20 + $modifier = $initiative")
        }
    }
}
