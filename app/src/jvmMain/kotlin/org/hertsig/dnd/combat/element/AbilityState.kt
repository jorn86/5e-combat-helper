package org.hertsig.dnd.combat.element

import org.hertsig.dnd.combat.dto.Ability
import org.hertsig.dnd.combat.dto.StatBlock
import org.hertsig.dnd.combat.element.AbilityType.*

internal data class AbilityState(
    val traits: MutableList<Ability>,
    val actions: MutableList<Ability>,
    val bonusActions: MutableList<Ability>,
    val reactions: MutableList<Ability>,
    val legendaryActions: MutableList<Ability>,
    val lairActions: MutableList<Ability>,
) {
    operator fun get(type: AbilityType) = when (type) {
        TRAITS -> traits
        ACTIONS -> actions
        BONUS_ACTIONS -> bonusActions
        REACTIONS -> reactions
        LEGENDARY_ACTIONS -> legendaryActions
        LAIR_ACTIONS -> lairActions
    }
}

internal enum class AbilityType {
    TRAITS, ACTIONS, BONUS_ACTIONS, REACTIONS, LEGENDARY_ACTIONS, LAIR_ACTIONS,
}

internal fun StatBlock.copy(type: AbilityType, value: List<Ability>): StatBlock {
    val copy = value.toList()
    return when (type) {
        TRAITS -> copy(traits = copy)
        ACTIONS -> copy(actions = copy)
        BONUS_ACTIONS -> copy(bonusActions = copy)
        REACTIONS -> copy(reactions = copy)
        LEGENDARY_ACTIONS -> copy(legendaryActions = copy)
        LAIR_ACTIONS -> copy(lairActions = copy)
    }
}
