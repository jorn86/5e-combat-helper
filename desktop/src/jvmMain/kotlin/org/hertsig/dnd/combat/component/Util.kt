package org.hertsig.dnd.combat.component

import androidx.compose.runtime.Composable

fun modifier(modifier: Int, suffix: String = "") = if (modifier < 0) "$modifier$suffix" else "+$modifier$suffix"

@Composable
fun <T> Collection<T>.displayForEach(
    display: (T) -> String,
    separator: String = ", ",
    content: @Composable (String, T) -> Unit
) = forEachIndexed { index, it ->
    var text = display(it)
    if (index + 1 < size) text += separator
    content(text, it)
}
