package org.hertsig.dnd.norr

import org.hertsig.magic.Analyzable
import org.hertsig.magic.DynamicList
import org.hertsig.magic.Magic

interface Monster: Analyzable {
    fun name(): String
    fun isNpc(): Boolean?
    fun isNamedCreature(): Boolean?
    fun source(): String
    fun page(): Int
    fun size(): List<String>
    @Magic(mapper = TypeMapper::class)
    fun type(): Type
    fun alignment(): List<String> //?
    @Magic(elementType = ArmorClass::class)
    fun ac(): List<ArmorClass>
    fun hp(): HitPoints
    fun speed(): Speeds
    fun str(): Int
    fun dex(): Int
    fun con(): Int
    fun int(): Int
    fun wis(): Int
    fun cha(): Int
    fun save(): SavingThrows?
    fun skill(): Skills?
    fun senses(): List<String>?
    fun passive(): Int
    @Magic(elementType = ConditionImmune::class)
    fun conditionImmune(): List<ConditionImmune>?
    @Magic(elementType = DamageResist::class)
    fun immune(): DynamicList
    @Magic(elementType = DamageResist::class)
    fun resist(): DynamicList
    fun languages(): List<String>?
    fun cr(): String
    @Magic(elementType = Spellcasting::class)
    fun spellcasting(): List<Spellcasting>?
    @Magic(elementType = Named::class)
    fun trait(): DynamicList
    @Magic(elementType = Named::class)
    fun action(): DynamicList
    @Magic(elementType = Named::class)
    fun bonus(): DynamicList
    @Magic(elementType = Named::class)
    fun reaction(): DynamicList
    fun legendaryHeader(): List<String>?
    @Magic(elementType = Named::class)
    fun legendary(): DynamicList
    fun legendaryGroup(): LegendaryGroup
    fun traitTags(): List<Any>
    fun senseTags(): List<Any>
    fun actionTags(): List<Any>
    fun languageTags(): List<Any>
    fun damageTags(): List<Any>
    fun spellcastingTags(): List<Any>?
    fun miscTags(): List<Any>
    fun conditionInflict(): List<Any>
    fun hasToken(): Boolean?
    fun hasFluff(): Boolean?
    fun hasFluffImages(): Boolean?
}

interface Named {
    fun name(): String?
    fun entries(): List<String>
}
