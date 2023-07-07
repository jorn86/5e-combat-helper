package org.hertsig.dnd.combat.dto

import org.hertsig.logger.logger

private val log = logger {}

sealed interface CombatEntry {
    val initiative: Int
    val name: String
    val dexMod: Int
    val active: Boolean

    fun toggleActive(): CombatEntry

    data class Simple(
        override val name: String,
        override val initiative: Int,
        override val dexMod: Int = 0,
        override val active: Boolean = true,
    ): CombatEntry {
        override fun toggleActive() = copy(active = !active)

        override fun equals(other: Any?) = this === other
        override fun hashCode() = System.identityHashCode(this)
    }

    open class GroupEntry(
        val statBlock: StatBlock,
        override val initiative: Int,
        val label: String = "",
        override val active: Boolean = true,
    ): CombatEntry {
        override fun toggleActive() = GroupEntry(statBlock, initiative, label, !active)

        override val name get() = "${statBlock.name}${printLabel()}"
        override val dexMod get() = statBlock.dexMod
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
    ): GroupEntry(statBlock, initiative, label, active) {
        override fun toggleActive() = Creature(statBlock, initiative, label, !active, currentHp)
    }
}

val CombatEntry?.statBlock get() = (this as? CombatEntry.GroupEntry)?.statBlock
