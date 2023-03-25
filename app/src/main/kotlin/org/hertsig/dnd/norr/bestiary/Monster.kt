package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.norr.Entry
import org.hertsig.dnd.norr.spell.Spellcasting
import org.hertsig.magic.DynamicList
import org.hertsig.magic.Magic

interface Monster {
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
    @Magic(elementType = Entry::class)
    fun trait(): List<Entry>
    @Magic(elementType = Entry::class)
    fun action(): List<Entry>
    @Magic(elementType = Entry::class)
    fun bonus(): List<Entry>
    @Magic(elementType = Entry::class)
    fun reaction(): List<Entry>
    @Magic(elementType = Entry::class)
    fun legendary(): List<Entry>
    fun legendaryHeader(): List<String>?
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
