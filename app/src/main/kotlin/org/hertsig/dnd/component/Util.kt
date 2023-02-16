package org.hertsig.dnd.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.google.common.base.CaseFormat

val Enum<*>.display get() = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name.replace("_", " "))

fun modifier(modifier: Int) = if (modifier < 0) modifier.toString() else "+$modifier"

@Composable
fun Modifier.autoFocus(requester: FocusRequester = remember { FocusRequester() }): Modifier {
    LaunchedEffect(Unit) { requester.requestFocus() }
    return focusRequester(requester)
}
