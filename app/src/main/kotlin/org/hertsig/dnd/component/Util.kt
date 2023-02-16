package org.hertsig.dnd.component

import androidx.compose.runtime.Composable

fun modifier(modifier: Int) = if (modifier < 0) modifier.toString() else "+$modifier"

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