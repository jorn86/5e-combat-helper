package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import org.hertsig.compose.component.*
import org.hertsig.dnd.combat.dto.AppState
import org.hertsig.dnd.combat.dto.Spell
import org.hertsig.dnd.combat.element.Log
import org.hertsig.dnd.combat.element.SpellDetail
import org.hertsig.dnd.norr.spell.findNorrSpells
import org.hertsig.dnd.norr.spell.getSpell

@Composable
fun CombatHelper(state: AppState) {
    Column {
        TitleBar(state)

        Row {
            state.page.drawList(state)
            VerticalDivider()
            with(state.page) { drawPage(state) }
            VerticalDivider()
            Log(logEntries)
        }
    }
}

@Composable
private fun TitleBar(state: AppState) {
    var page by state::page
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar({
        SpacedRow {
            TextLine("Combat helper — ${page.subtitle}", Modifier.padding(end = 400.dp), style = MaterialTheme.typography.headlineSmall)

            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                var quickSpellLookup by remember { mutableStateOf<Spell?>(null) }
                Autocompleter(
                    { if (it.isBlank()) emptyList() else findNorrSpells(it.trim()) },
                    Modifier.width(400.dp),
                    "Spell lookup",
                ) { quickSpellLookup = getSpell(it) }
                quickSpellLookup?.let {
                    Popup(object : PopupPositionProvider {
                        override fun calculatePosition(
                            anchorBounds: IntRect,
                            windowSize: IntSize,
                            layoutDirection: LayoutDirection,
                            popupContentSize: IntSize
                        ): IntOffset {
                            return IntOffset((windowSize.width - popupContentSize.width) / 2, anchorBounds.bottom + 8)
                        }
                    }, { quickSpellLookup = null }) {
                        SpellDetail(it)
                    }
                }
            }
        }
    }, actions = {
        Row(Modifier.padding(end = 16.dp), Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
            page.drawToolbarButtons(state)
            VerticalDivider(MaterialTheme.colorScheme.onPrimary.copy(alpha = dividerAlpha))
            BarButton(Icons.Default.GridView, page != Page.Overview) { page = Page.Overview }
//            BarButton(Icons.Default.Build, page != Page.Encounters) { page = Page.Encounters }
            BarButton(Icons.Default.Shield, page != Page.PrepareCombat && page != Page.Combat) {
                page = if (state.combat.current == null) Page.PrepareCombat else Page.Combat
            }
        }
    }, colors = TopAppBarDefaults.smallTopAppBarColors(MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary))
}

@Composable
fun BarButton(icon: ImageVector, enabled: Boolean = true, visible: Boolean = true, onClick: () -> Unit) {
    if (visible) IconButton(onClick, icon, enabled = enabled)
}
