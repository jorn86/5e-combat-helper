package org.hertsig.dnd.norr.spell

import org.hertsig.magic.Magic

interface Spellcasting {
    fun name(): String
    fun headerEntries(): List<String>
    fun ability(): String
    @Magic(elementType = SpellList::class)
    fun daily(): Map<String, SpellList>
    @Magic(elementType = SpellList::class)
    fun spells(): Map<String, SpellList>
}

interface SpellList {
    fun slots(): Int?
    fun spells(): List<String>
}
