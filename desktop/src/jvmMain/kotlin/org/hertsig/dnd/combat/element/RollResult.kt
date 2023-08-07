package org.hertsig.dnd.combat.element

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.RowTextLine
import org.hertsig.compose.component.TextLine
import org.hertsig.dnd.combat.component.DiceShapes
import org.hertsig.dnd.combat.component.modifier
import org.hertsig.dnd.dice.DieRoll
import org.hertsig.dnd.dice.MultiDieRolls

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.RollResult(
    roll: MultiDieRolls,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    suffix: String = ""
) {
    TooltipArea({
        ProvideTextStyle(MaterialTheme.typography.body1) {
            Row(Modifier.background(MaterialTheme.colors.background)) {
                roll.rolls.forEach {
                    Column {
                        Row {
                            it.dice.forEach { die -> Die(die, die.size) }
                            if (it.modifier != 0) TextWithIcon(modifier(it.modifier))
                        }
                        if (it.type.isNotBlank() || roll.rolls.size > 1) {
                            TextLine(
                                "${it.total} ${it.type}",
                                Modifier.align(Alignment.CenterHorizontally).padding(2.dp),
                                MaterialTheme.colors.onBackground,
                            )
                        }
                    }
                }
            }
        }
    }) {
        val text = AnnotatedString.Builder()
        roll.rolls.forEachIndexed { index, it ->
            if (index > 0) text.append(" + ")
            val add: AnnotatedString.Builder.() -> Unit = { append(it.total.toString()) }
            when {
                it.dice.all { it.result == 1 } -> text.withStyle(style.toSpanStyle().copy(color = Color.Red), add)
                it.dice.all { it.result == it.size } -> text.withStyle(style.toSpanStyle().copy(color = Color.Green), add)
                else -> text.add()
            }
        }
        text.append(" ")
        text.append(suffix)
        RowTextLine(text.toAnnotatedString(), modifier, MaterialTheme.colors.onPrimary, style, TextAlign.Center)
    }
}

@Composable
private fun Die(roll: DieRoll, size: Int) {
    val icon = when (size) {
//        4 -> Polygon(3, 30f)
//        6 -> Polygon(4, 45f)
//        8 -> Polygon(4)
//        10 -> Shapes.D10
//        12 -> Polygon(5, 54f)
//        20 -> Polygon(6, 30f)
        4 -> DiceShapes.D4
        6 -> DiceShapes.D6
        8 -> DiceShapes.D8
        10 -> DiceShapes.D10
        12 -> DiceShapes.D12
        20 -> DiceShapes.D20
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
