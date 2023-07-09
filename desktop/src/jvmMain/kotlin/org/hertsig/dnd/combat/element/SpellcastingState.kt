package org.hertsig.dnd.combat.element

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.hertsig.dnd.combat.dto.Stat
import org.hertsig.dnd.combat.dto.StatblockSpell

data class InnateSpellcastingState(
    val name: MutableState<String>,
    val stat: MutableState<Stat>,
    val atWill: SnapshotStateList<StatblockSpell>,
    val threePerDay: SnapshotStateList<StatblockSpell>,
    val twoPerDay: SnapshotStateList<StatblockSpell>,
    val onePerDay: SnapshotStateList<StatblockSpell>,
)
