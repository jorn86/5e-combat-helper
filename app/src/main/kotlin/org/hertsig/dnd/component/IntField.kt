package org.hertsig.dnd.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IntField(
    value: MutableState<Int>,
    label: String,
    min: Int = 0,
    max: Int = 99,
    step: Int = 1,
    width: Dp = 160.dp,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    fun setValue(it: Int) {
        value.value = it.coerceIn(min, max)
    }

    TextField(
        value.value.toString(),
        { it.toIntOrNull()?.run(::setValue) },
        Modifier.width(width),
        label = { Text(label, maxLines = 1) },
        maxLines = 1,
        leadingIcon = leadingIcon,
        trailingIcon = {
            Column {
                IconButton(
                    { setValue(value.value + step) },
                    Icons.Default.KeyboardArrowUp,
                    iconSize = 16.dp,
                    description = "Increase"
                )
                IconButton(
                    { setValue(value.value - step) },
                    Icons.Default.KeyboardArrowDown,
                    iconSize = 16.dp,
                    description = "Decrease"
                )
            }
        },
    )
}
