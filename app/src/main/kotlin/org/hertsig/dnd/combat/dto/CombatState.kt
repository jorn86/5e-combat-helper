package org.hertsig.dnd.combat.dto

sealed interface CombatEntry {
    val initiative: Int
    val name: String
    val dexMod: Int

    data class Simple(
        override val name: String,
        override val initiative: Int,
        override val dexMod: Int = 0
    ): CombatEntry {
        override fun equals(other: Any?) = this === other
        override fun hashCode() = System.identityHashCode(this)
    }

    open class GroupEntry(
        val statBlock: StatBlock,
        override val initiative: Int,
        val label: String = "",
    ): CombatEntry {
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
        val currentHp: Int = statBlock.maxHitPoints,
    ): GroupEntry(statBlock, initiative, label) {
    }
}

val CombatEntry?.statBlock get() = (this as? CombatEntry.GroupEntry)?.statBlock
