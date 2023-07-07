package org.hertsig.dnd.norr.spell

import org.hertsig.dnd.norr.SingleToListMapper
import org.hertsig.magic.DynamicList
import org.hertsig.magic.Magic

interface NorrSpell {
    fun name(): String
    fun source(): String
    fun page(): Int
    fun level(): Int
    fun school(): String
    @Magic(elementType = Time::class)
    fun time(): List<Time>
    fun range(): Range
    fun components(): Components
    @Magic(elementType = Duration::class)
    fun duration(): List<Duration>
    fun entries(): DynamicList
    fun entriesHigherLevel(): DynamicList
    fun damageInflict(): List<String>?
    fun savingThrow(): List<String>?
    fun spellAttack(): List<String>?
    @Magic(elementType = ScalingLevelDice::class, mapper = SingleToListMapper::class)
    fun scalingLevelDice(): List<ScalingLevelDice>
    fun miscTags(): List<Any>
    fun areaTags(): List<Any>
    fun hasFluffImages(): Boolean
}
