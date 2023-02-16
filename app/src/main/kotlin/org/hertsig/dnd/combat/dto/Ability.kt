package org.hertsig.dnd.combat.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hertsig.dnd.dice.Dice
import org.hertsig.dnd.dice.d

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(Ability.Trait::class, name = "trait"),
    JsonSubTypes.Type(Ability.MeleeAttack::class, name = "meleeAttack"),
    JsonSubTypes.Type(Ability.RangedAttack::class, name = "rangedAttack"),
    JsonSubTypes.Type(Ability.Custom::class, name = "custom"),
    JsonSubTypes.Type(LegendaryAbility::class, name = "legendary"),
)
interface Ability {
    val name: String
    val use: Use

    data class Trait(
        override val name: String = "",
        val description: String = "",
        override val use: Use = Use.Unlimited,
    ): Ability

    data class MeleeAttack(
        override val name: String = "",
        val stat: Stat? = Stat.STRENGTH,
        val modifier: Int = 0,
        val proficient: Boolean = true,
        val reach: Int = 5,
        val target: String = "one target",
        val damage: Dice = (1 d 8)("bludgeoning"),
        override val use: Use = Use.Unlimited,
    ) : Ability

    data class RangedAttack(
        override val name: String = "",
        val stat: Stat? = Stat.DEXTERITY,
        val modifier: Int = 0,
        val proficient: Boolean = true,
        val range: Int = 30,
        val longRange: Int = 120,
        val target: String = "one target",
        val damage: Dice = (1 d 8)("piercing"),
        override val use: Use = Use.Unlimited,
    ) : Ability

    data class Custom(
        override val name: String = "",
        val recharge: Recharge = Recharge.NO,
        val description: String = "",
        val roll: Dice? = null,
        override val use: Use = Use.Unlimited,
    ): Ability
}

class LegendaryAbility(val ability: Ability, val cost: Int = 1): Ability by ability {
    fun costDisplay() = if (cost == 1) "" else " (costs $cost actions)"
}
