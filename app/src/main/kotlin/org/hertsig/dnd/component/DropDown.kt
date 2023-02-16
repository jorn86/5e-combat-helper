package org.hertsig.dnd.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
inline fun <reified E: Enum<E>> DropDown(
    state: MutableState<E>,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    itemAlign: TextAlign = TextAlign.Start,
    noinline onUpdate: (E) -> Unit = {},
    noinline display: (E) -> String = { it.display }
) {
    DropDown(state, enumValues<E>().asList(), modifier, textAlign, itemAlign, onUpdate, display)
}

@Composable
fun <V> DropDown(
    state: MutableState<V>,
    values: Collection<V>,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    itemAlign: TextAlign = TextAlign.Start,
    onUpdate: (V) -> Unit = {},
    display: (V) -> String = { it.toString() }
) {
    var value by state
    var show by remember { mutableStateOf(false) }
    Row {
        BasicTextField(display(value), {},
            modifier.border(1.dp, Color.Black).padding(2.dp).clickable { show = true },
            textStyle = TextStyle(textAlign = textAlign),
            // enabled = false is required to make clickable work (???)
            enabled = false, readOnly = true, singleLine = true, maxLines = 1)
        CursorDropdownMenu(show, { show = false }) {
            values.forEach { Item(display(it), itemAlign) { value = it; show = false; onUpdate(it) } }
        }
    }
}

@Composable
fun Item(text: String, align: TextAlign = TextAlign.Start, onClick: () -> Unit) {
    TextLine(text, Modifier.padding(2.dp).defaultMinSize(40.dp).fillMaxWidth().clickable { onClick() }, align = align)
}
