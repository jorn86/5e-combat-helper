package org.hertsig.dnd.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun EditText(textState: MutableState<String>, modifier: Modifier = Modifier, hint: String? = null, onUpdate: (String) -> Unit = {}) {
    var text by textState
    BasicTextField(
        text,
        { text = it; onUpdate(it) },
        modifier.border(1.dp, Color.Black).padding(2.dp),
        singleLine = true,
        maxLines = 1
    ) { innerTextField ->
        if (hint != null && text.isEmpty()) {
            TextLine(hint, Modifier.alpha(.5f), style = TextStyle.Default)
        }
        innerTextField()
    }
}
