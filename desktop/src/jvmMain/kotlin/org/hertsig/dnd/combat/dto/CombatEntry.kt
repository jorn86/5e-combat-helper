package org.hertsig.dnd.combat.dto

import org.hertsig.logger.logger

private val log = logger {}

sealed interface CombatEntry {
    val initiative: Int
    val name: String
    val dexMod: Int
    val active: Boolean
    val groupSize: Int get() = 1
    val totalXp: Int

    fun toggleActive(): CombatEntry

    data class Simple(
        override val name: String,
        override val initiative: Int,
        override val dexMod: Int = 0,
        override val active: Boolean = true,
    ): CombatEntry {
        override val totalXp get() = 0
        override fun toggleActive() = copy(active = !active)

        override fun equals(other: Any?) = this === other
        override fun hashCode() = System.identityHashCode(this)
    }

    open class GroupEntry(
        val statBlock: StatBlock,
        override val initiative: Int,
        val label: String = "",
        override val groupSize: Int,
        override val active: Boolean = true,
    ): CombatEntry {
        override fun toggleActive() = GroupEntry(statBlock, initiative, label, groupSize, !active)

        override val name get() = "${statBlock.name}${printLabel()}"
        override val dexMod get() = statBlock.dexMod
        override val totalXp get() = statBlock.xp * groupSize

        private fun printLabel() = if (label.isBlank()) "" else " ($label)"

        override fun equals(other: Any?) = this === other
        override fun hashCode() = System.identityHashCode(this)


    }

    class Creature(
        statBlock: StatBlock,
        initiative: Int,
        label: String = "",
        active: Boolean = true,
        val currentHp: Int = statBlock.maxHitPoints,
    ): GroupEntry(statBlock, initiative, label, 1, active) {
        override fun toggleActive() = Creature(statBlock, initiative, label, !active, currentHp)
    }
}

val CombatEntry?.statBlock get() = (this as? CombatEntry.GroupEntry)?.statBlock
