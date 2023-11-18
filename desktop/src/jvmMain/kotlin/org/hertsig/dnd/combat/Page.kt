package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.StatBlock
import org.hertsig.dnd.combat.dto.statBlock
import org.hertsig.dnd.combat.element.EditableSheet
import org.hertsig.dnd.combat.element.ReadonlySheet
import org.hertsig.dnd.combat.page.*

sealed interface Page {
    val subtitle get() = this::class.simpleName
    val active: StatBlock? get() = null
    @Composable fun drawToolbarButtons(state: AppState) {}
    @Composable fun drawList(state: AppState)
    @Composable fun RowScope.drawPage(state: AppState)

    data object Overview: Page {
        @Composable
        override fun drawToolbarButtons(state: AppState) {
            BarButton(Icons.Default.Save) { state.statBlocks.save() }
        }

        @Composable
        override fun drawList(state: AppState) {
            StatBlockList(state)
        }

        @Composable
        override fun RowScope.drawPage(state: AppState) {
            OverviewPage(state.statBlocks.statBlocks, Modifier.weight(1f))
        }
    }

    class Show(override val active: StatBlock): Page {
        override val subtitle get() = active.name

        @Composable
        override fun drawToolbarButtons(state: AppState) {
            BarButton(Icons.Default.FileCopy) { state.page = Edit(state.statBlocks.copy(active)) }
            BarButton(Icons.Default.Save) { state.statBlocks.save() }
            BarButton(Icons.Default.Edit) { state.page = Edit(active) }
            BarButton(Icons.Default.Delete) { state.statBlocks.delete(active) } // TODO change page
        }

        @Composable
        override fun drawList(state: AppState) {
            StatBlockList(state, active)
        }

        @Composable
        override fun RowScope.drawPage(state: AppState) {
            ReadonlySheet(active, Modifier.weight(1f))
        }
    }

    class Edit(override val active: StatBlock): Page {
        val updated = mutableStateOf(active)

        override val subtitle get() = "Edit ${active.name}"

        @Composable
        override fun drawToolbarButtons(state: AppState) {
            BarButton(Icons.Default.Save) {
                if (state.statBlocks.update(active, updated.value)) {
                    state.page = Show(updated.value)
                }
            }
            BarButton(Icons.Default.Cancel) { state.page = Show(active) }
            BarButton(Icons.Default.Delete) { state.statBlocks.delete(active) } // TODO change page
        }

        @Composable
        override fun drawList(state: AppState) {
            StatBlockList(state, active)
        }

        @Composable
        override fun RowScope.drawPage(state: AppState) {
            EditableSheet(state, this@Edit, Modifier.weight(1f))
        }
    }

    data object Encounters: Page {
        override val subtitle = "Encounter builder"

        @Composable
        override fun drawList(state: AppState) {
            EncounterList(state, Modifier.width(250.dp))
        }

        @Composable
        override fun RowScope.drawPage(state: AppState) {
            
        }
    }

    data object PrepareCombat: Page {
        override val subtitle = "Prepare combat"

        @Composable
        override fun drawToolbarButtons(state: AppState) {
            BarButton(Icons.Default.Start, enabled = state.combat.entries.isNotEmpty()) {
                state.combat.startCombat()
                state.page = Combat
            }
        }

        @Composable
        override fun drawList(state: AppState) {
            InitiativeList(state.combat, Modifier.width(250.dp))
        }

        @Composable
        override fun RowScope.drawPage(state: AppState) {
            PrepareCombatPage(state, Modifier.weight(1f))
        }
    }

    data object Combat: Page {
        @Composable
        override fun drawToolbarButtons(state: AppState) {
            val statBlock = state.combat.current?.statBlock
            BarButton(Icons.Default.Visibility, statBlock != null) { statBlock?.let { state.page = Show(it) } }
            BarButton(Icons.Default.Edit, statBlock != null) { statBlock?.let { state.page = Edit(it) } }
            BarButton(Icons.Default.RestartAlt) { state.combat.startCombat() }
            BarButton(Icons.Default.KeyboardArrowUp) { state.combat.previousInitiative() }
            BarButton(Icons.Default.KeyboardArrowDown) { state.combat.nextInitiative() }
            BarButton(Icons.Default.Settings) { state.page = PrepareCombat }
        }

        @Composable
        override fun drawList(state: AppState) {
            InitiativeList(state.combat, Modifier.width(250.dp), showControls = true)
        }

        @Composable
        override fun RowScope.drawPage(state: AppState) {
            CombatPage(state, Modifier.weight(1f))
        }
    }
}
