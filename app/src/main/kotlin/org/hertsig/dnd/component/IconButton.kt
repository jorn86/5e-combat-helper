package org.hertsig.dnd.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    buttonModifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = 24.dp,
    description: String = icon.name,
) {
    androidx.compose.material.IconButton(onClick, buttonModifier, enabled) {
        Icon(icon, description, Modifier.size(iconSize))
    }
}
