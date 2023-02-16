package org.hertsig.dnd.component

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableColumn(
    modifier: Modifier = Modifier,
    arrangement: Arrangement.Vertical = Arrangement.Top,
    padding: PaddingValues = PaddingValues(8.dp, 8.dp, 12.dp, 8.dp),
    vertical: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    Box {
        LazyColumn(modifier, vertical, padding, verticalArrangement = arrangement, content = content)
        VerticalScrollbar(rememberScrollbarAdapter(vertical), Modifier.align(Alignment.CenterEnd).padding(2.dp))
    }
}

@Composable
fun ScrollableRow(
    modifier: Modifier = Modifier,
    arrangement: Arrangement.Horizontal = Arrangement.Start,
    padding: PaddingValues = PaddingValues(0.dp),
    vertical: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    Box {
        LazyRow(modifier, vertical, padding, horizontalArrangement = arrangement, content = content)
        HorizontalScrollbar(rememberScrollbarAdapter(vertical), Modifier.align(Alignment.BottomCenter))
    }
}
