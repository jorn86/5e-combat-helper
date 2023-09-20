package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.norr.Entry
import org.hertsig.dnd.norr.spell.Spellcasting
import org.hertsig.magic.DynamicList
import org.hertsig.magic.Magic
import org.hertsig.magic.Mapper

interface Monster {
    fun name(): String
    @Magic(name = "_copy")
    fun delegate(): Delegate?
    @Magic(mapper = NullToFalse::class)
    fun isNpc(): Boolean
    @Magic(mapper = NullToFalse::class)
    fun isNamedCreature(): Boolean
    fun source(): String
    fun page(): Int
    @Magic(mapper = NullToEmptyList::class)
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
    @Magic(mapper = NullToEmptyList::class)
    fun senses(): List<String>
    fun passive(): Int
    @Magic(mapper = NullToEmptyList::class, elementType = ConditionImmune::class)
    fun conditionImmune(): List<ConditionImmune>
    @Magic(elementType = DamageResist::class)
    fun immune(): DynamicList
    @Magic(elementType = DamageResist::class)
    fun resist(): DynamicList
    @Magic(mapper = NullToEmptyList::class)
    fun languages(): List<String>
    @Magic(mapper = ChallengeRatingMapper::class)
    fun cr(): CR
    @Magic(mapper = NullToEmptyList::class, elementType = Spellcasting::class)
    fun spellcasting(): List<Spellcasting>
    @Magic(elementType = Entry::class)
    fun trait(): List<Entry>
    @Magic(elementType = Entry::class)
    fun variant(): List<Entry>
    @Magic(elementType = Entry::class)
    fun action(): List<Entry>
    @Magic(elementType = Entry::class)
    fun bonus(): List<Entry>
    @Magic(elementType = Entry::class)
    fun reaction(): List<Entry>
    @Magic(elementType = Entry::class)
    fun legendary(): List<Entry>
    @Magic(mapper = NullToEmptyList::class)
    fun legendaryHeader(): List<String>
    fun legendaryGroup(): LegendaryGroup
    @Magic(mapper = NullToEmptyList::class)
    fun traitTags(): List<Any>
    @Magic(mapper = NullToEmptyList::class)
    fun senseTags(): List<Any>
    @Magic(mapper = NullToEmptyList::class)
    fun actionTags(): List<Any>
    @Magic(mapper = NullToEmptyList::class)
    fun languageTags(): List<Any>
    @Magic(mapper = NullToEmptyList::class)
    fun damageTags(): List<Any>
    @Magic(mapper = NullToEmptyList::class)
    fun spellcastingTags(): List<Any>
    @Magic(mapper = NullToEmptyList::class)
    fun miscTags(): List<Any>
    @Magic(mapper = NullToEmptyList::class)
    fun conditionInflict(): List<Any>
    @Magic(mapper = NullToFalse::class)
    fun hasToken(): Boolean
    @Magic(mapper = NullToFalse::class)
    fun hasFluff(): Boolean
    @Magic(mapper = NullToFalse::class)
    fun hasFluffImages(): Boolean
}

object NullToFalse : Mapper {
    override fun invoke(value: Any?) = value ?: false
}

object NullToEmptyList : Mapper {
    override fun invoke(value: Any?) = value ?: emptyList<Nothing>()
}

object NullToEmptyMap : Mapper {
    override fun invoke(value: Any?) = value ?: emptyList<Nothing>()
}

object IsNotNull : Mapper {
    override fun invoke(value: Any?) = value != null
}

interface Delegate {
    fun name(): String
    fun source(): String
}
