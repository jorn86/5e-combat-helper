package org.hertsig.dnd.combat.element

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import org.hertsig.compose.component.TextLine
import org.hertsig.compose.component.TooltipText

@Composable
fun TraitLine(
    name: String,
    text: String = "",
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    singleLine: Boolean = true,
    visible: Boolean = text.isNotBlank(),
    expand: Boolean = true,
) {
    if (visible) {
        if (expand) {
            val builder = AnnotatedString.Builder()
            builder.pushStyle(style.toSpanStyle().copy(fontWeight = FontWeight.Bold))
            builder.append(name)
            builder.append(": ")
            builder.pop()
            builder.append(text)
            if (singleLine) {
                TextLine(builder.toAnnotatedString(), modifier)
            } else {
                Text(builder.toAnnotatedString(), modifier)
            }
        } else {
            TooltipText(text) {
                TextLine(name, style = style.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}