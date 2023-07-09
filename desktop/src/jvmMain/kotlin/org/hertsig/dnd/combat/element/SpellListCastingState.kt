package org.hertsig.dnd.combat.element

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.hertsig.dnd.combat.dto.CasterLevel
import org.hertsig.dnd.combat.dto.SpellList
import org.hertsig.dnd.combat.dto.StatblockSpell

data class SpellListCastingState(
    val name: MutableState<String>,
    val list: MutableState<SpellList>,
    val level: MutableState<CasterLevel>,
    val spells: SnapshotStateList<StatblockSpell>,
)
