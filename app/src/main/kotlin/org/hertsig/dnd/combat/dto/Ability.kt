package org.hertsig.dnd.combat.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hertsig.dnd.dice.Dice
import org.hertsig.dnd.dice.MultiDice

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(Ability.Attack::class, name = "attack"),
    JsonSubTypes.Type(Ability.Trait::class, name = "trait"),
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
interface Ability {
    val name: String
    val use: Use
    val legendaryCost: Int?

    fun costDisplay() = if (legendaryCost ?: 0 > 1) " (costs $legendaryCost actions)" else ""
    fun baseCopy(name: String = this.name, use: Use = this.use, legendaryCost: Int? = this.legendaryCost): Ability

    data class Trait(
        override val name: String = "",
        val recharge: Recharge = Recharge.NO,
        val description: String = "",
        val roll: MultiDice? = null,
        override val use: Use = Use.Unlimited,
        override val legendaryCost: Int? = null,
    ): Ability {
        override fun baseCopy(name: String, use: Use, legendaryCost: Int?) =
            copy(name = name, use = use, legendaryCost = legendaryCost)
    }

    data class Attack(
        override val name: String = "",
        val stat: Stat? = Stat.DEXTERITY,
        val modifier: Int = 0,
        val reach: Int? = null,
        val range: Int? = null,
        val longRange: Int? = null,
        val damage: MultiDice = MultiDice(Dice.NONE),
        val extra: String = "",
        override val use: Use = Use.Unlimited,
        override val legendaryCost: Int? = null,
    ) : Ability {
        override fun baseCopy(name: String, use: Use, legendaryCost: Int?) =
            copy(name = name, use = use, legendaryCost = legendaryCost)
    }
}
