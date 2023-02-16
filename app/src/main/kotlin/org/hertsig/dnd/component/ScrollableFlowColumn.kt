package org.hertsig.dnd.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableFlowColumn(
    columnSpacing: Dp = 0.dp,
    itemSpacing: Dp = 0.dp,
    strategy: FlowLayoutStrategy = VerticalThenHorizontalStrategy(),
    columns: Int = 2,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ScrollableColumn(modifier) {
        item {
            Layout(content) { measurables, outerConstraints ->
                val columnSpacingPx = columnSpacing.roundToPx()
                val itemSpacingPx = itemSpacing.roundToPx()

                val availableContentWidth = outerConstraints.maxWidth - spacingSize(columnSpacingPx, columns)
                val columnWidthConstraint = Constraints(maxWidth = availableContentWidth / columns)
                val placeables = measurables.map { it.measure(columnWidthConstraint) }
                val columnWidth = placeables.maxOf { it.width }
                require(columnWidthConstraint.maxWidth <= columnWidth) { "Doesn't fit: $columnWidth > ${columnWidthConstraint.maxWidth}" }
                val finalLayout = strategy.layout(columns, itemSpacingPx, placeables)
                require(finalLayout.size == columns)

                layout(
                    spacedSize(columnWidth * columns, columnSpacingPx, columns),
                    finalLayout.maxOf { spacedSize(it.sumOf { p -> p.height }, itemSpacingPx, it.size) },
                ) {
                    var x = 0
                    finalLayout.forEach { placeables ->
                        var y = 0
                        placeables.forEach {
                            it.place(x, y)
                            y += it.height + itemSpacingPx
                        }
                        x += columnWidth + columnSpacingPx
                    }
                }
            }
        }
    }
}

interface FlowLayoutStrategy {
    fun layout(columns: Int, spacing: Int, placeables: List<Placeable>): List<List<Placeable>>
}

open class VerticalThenHorizontalStrategy: FlowLayoutStrategy {
    override fun layout(columns: Int, spacing: Int, placeables: List<Placeable>): List<List<Placeable>> {
        val averageColumnHeight = spacedSize(placeables.sumOf { it.height }, spacing, placeables.size, columns) / columns
        val layout = List(columns) { mutableListOf<Placeable>() }

        var currentColumnHeight = 0
        var column = 0
        placeables.forEach {
            layout[column].add(it)
            currentColumnHeight += it.height + spacing
            if (column < columns - 1 && shouldUseNextColumn(currentColumnHeight, averageColumnHeight)) {
                column++
                currentColumnHeight = 0
            }
        }
        return layout
    }

    protected open fun shouldUseNextColumn(currentColumnHeight: Int, averageColumnHeight: Int) =
        currentColumnHeight > averageColumnHeight
}

open class HorizontalThenVerticalStrategy : FlowLayoutStrategy {
    override fun layout(columns: Int, spacing: Int, placeables: List<Placeable>): List<List<Placeable>> {
        val layout = List(columns) { mutableListOf<Placeable>() }
        placeables.forEachIndexed { index, it ->
            layout[index % layout.size].add(it)
        }
        return layout
    }
}

class ReorderStrategy: FlowLayoutStrategy {
    override fun layout(columns: Int, spacing: Int, placeables: List<Placeable>): List<List<Placeable>> {
        val layout = MutableList(columns) { mutableListOf<Placeable>() }
        placeables.sortedByDescending { it.height }.forEach { placeable ->
            layout.minBy { column -> spacedSize(column.sumOf { it.height }, spacing, column.size) }.add(placeable)
        }
        layout.forEach { column -> column.sortBy { placeables.indexOf(it) } }
        layout.sortBy { if (it.isEmpty()) placeables.size else placeables.indexOf(it.first()) }
        return layout
    }
}

private fun spacedSize(contentSize: Int, spacing: Int, items: Int, unspacedItems: Int = 1) =
    spacingSize(spacing, items, unspacedItems) + contentSize
private fun spacingSize(spacing: Int, size: Int, unspaced: Int = 1) =
    (size - unspaced).coerceAtLeast(0) * spacing
