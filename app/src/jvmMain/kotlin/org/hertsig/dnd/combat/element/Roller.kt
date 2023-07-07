package org.hertsig.dnd.combat.element

import androidx.compose.foundation.clickable
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import org.hertsig.compose.component.TextLine
import org.hertsig.dnd.combat.dto.LogEntry
import org.hertsig.dnd.combat.log
import org.hertsig.dnd.dice.MultiDice

@Composable
fun Roller(text: String, dice: MultiDice, name: String, rollText: String = text, style: TextStyle = LocalTextStyle.current, twice: Boolean = true) {
    TextLine(text, Modifier.clickable {
        log(LogEntry.Roll(name, rollText, dice.roll(), if (twice) dice.roll() else null))
    }, style = style)
}
