package org.hertsig.dnd.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalDivider(thickness: Dp = 1.dp) {
    Divider(Modifier.fillMaxWidth().height(thickness))
}

@Composable
fun VerticalDivider(thickness: Dp = 1.dp) {
    Divider(Modifier.width(thickness).fillMaxHeight())
}
