package org.hertsig.dnd.combat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.hertsig.dnd.component.RowTextLine
import org.hertsig.dnd.component.Shapes
import org.hertsig.dnd.component.TextLine
import org.hertsig.dnd.component.modifier
import org.hertsig.dnd.dice.DieRoll
import org.hertsig.dnd.dice.DieRolls

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.RollResult(
    roll: DieRolls,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    suffix: String = ""
) {
    TooltipArea({
        CompositionLocalProvider(LocalTextStyle.provides(MaterialTheme.typography.body1)) {
            Row {
                roll.dice.forEach { Die(it, it.size) }
                if (roll.modifier != 0) TextWithIcon(modifier(roll.modifier))
            }
        }
    }) {
        val color = when {
            roll.dice.all { it.result == 1 } -> Color.Red
            roll.dice.all { it.result == it.size } -> Color.Green
            else -> MaterialTheme.colors.onPrimary
        }
        RowTextLine("${roll.total} $suffix", modifier, color, style, TextAlign.Center)
    }
}

@Composable
private fun Die(roll: DieRoll, size: Int) {
    val icon = when (size) {
        4 -> Shapes.D4
        6 -> Shapes.D6
        8 -> Shapes.D8
        10 -> Shapes.D10
        12 -> Shapes.D12
        20 -> Shapes.D20
        else -> null
    }
    val color = when (roll.result) {
        1 -> Color.Red
        roll.size -> Color.Green
        else -> MaterialTheme.colors.onBackground
    }
    TextWithIcon(roll.result.toString(), icon, color)
}

@Composable
private fun TextWithIcon(text: String, icon: Shape? = null, color: Color = MaterialTheme.colors.onBackground) {
    Box(Modifier.sizeIn(50.dp, 50.dp)
            .border(1.dp, MaterialTheme.colors.secondaryVariant)
            .background(MaterialTheme.colors.background)
            .padding(4.dp)
            .ifNotNull(icon) { border(2.dp, MaterialTheme.colors.primaryVariant, it) }
            .padding(4.dp), contentAlignment = Alignment.Center
    ) {
        TextLine(text, color = color)
    }
}

private inline fun <T> T.runIf(condition: Boolean, action: T.() -> T): T = if (condition) action() else this
private inline fun <T, V> T.ifNotNull(value: V?, action: T.(V) -> T) = runIf(value != null) { action(value!!) }
