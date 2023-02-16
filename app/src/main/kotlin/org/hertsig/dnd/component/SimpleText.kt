package org.hertsig.dnd.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TextLine(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    align: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(text, modifier, color, textAlign = align, overflow = overflow, softWrap = false, maxLines = 1, style = style)
}

@Composable
fun TextLine(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    align: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(text, modifier, color, textAlign = align, overflow = overflow, softWrap = false, maxLines = 1, style = style)
}

@Composable
fun RowScope.RowTextLine(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    align: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(text, modifier.align(Alignment.CenterVertically),
        color, textAlign = align, overflow = overflow, softWrap = false, maxLines = 1, style = style)
}
