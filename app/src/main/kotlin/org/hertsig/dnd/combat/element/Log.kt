package org.hertsig.dnd.combat.element

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.ScrollableColumn
import org.hertsig.compose.component.SmallButton
import org.hertsig.compose.component.TextLine
import org.hertsig.core.error
import org.hertsig.core.logger
import org.hertsig.dnd.combat.dto.LogEntry
import org.hertsig.dnd.dice.MultiDieRolls

private val log = logger {}

@Composable
fun Log(logEntries: List<LogEntry>) {
    val listState = rememberLazyListState()
    LaunchedEffect(logEntries.size) { listState.scrollToItem(logEntries.size) }
    ScrollableColumn(Modifier.width(300.dp), Arrangement.spacedBy(4.dp), state = listState) {
        items(logEntries) {
            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colors.primaryVariant) {
                Row(Modifier.fillMaxWidth().padding(8.dp, 4.dp)) {
                    when (it) {
                        is LogEntry.Text -> TextLine(it.text)
                        is LogEntry.Roll -> RollEntry(it)
                        is LogEntry.Attack -> AttackEntry(it)
                        else -> log.error{"No renderer for $it"}
                    }
                }
            }
        }
    }
}

@Composable
private fun RollEntry(roll: LogEntry.Roll) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h4) {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                RollResult(roll.first, Modifier.sizeIn(minWidth = 70.dp))
                roll.second?.let {
                    TextLine("|")
                    RollResult(it, Modifier.sizeIn(minWidth = 70.dp))
                }
            }
        }
        if (roll.text.isNotBlank()) Text(roll.text, textAlign = TextAlign.Center)
        if (roll.name.isNotBlank()) TextLine(roll.name, Modifier.padding(vertical = 2.dp))
    }
}

@Composable
private fun AttackEntry(attack: LogEntry.Attack) {
    Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(2.dp), Alignment.CenterHorizontally) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h4) {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                RollResult(attack.firstHit, Modifier.sizeIn(minWidth = 70.dp))
                attack.secondHit?.let {
                    TextLine("|")
                    RollResult(it, Modifier.sizeIn(minWidth = 70.dp))
                }
            }
        }
        Row(Modifier.fillMaxWidth().height(24.dp), Arrangement.SpaceAround) {
            var damage by remember { mutableStateOf<MultiDieRolls?>(null) }
            damage.let {
                if (it == null) {
                    SmallButton({ damage = attack.damage.roll() }) { TextLine("Hit") }
                    val critDamage = attack.damage.doubleDice()
                    if ((attack.firstHit.isNatural20() || attack.secondHit.isNatural20()) && critDamage != attack.damage) {
                        SmallButton({ damage = critDamage.roll() }) { TextLine("Crit") }
                    }
                } else {
                    RollResult(it, style = MaterialTheme.typography.h5)
                }
            }
        }
        if (attack.text.isNotBlank()) TextLine(attack.text)
        if (attack.name.isNotBlank()) TextLine(attack.name)
    }
}

private fun MultiDieRolls?.isNatural20(): Boolean {
    val die = this?.rolls?.singleOrNull()?.dice?.singleOrNull() ?: return false
    return die.size == 20 && die.result == 20
}
