package org.hertsig.dnd.norr

import org.hertsig.magic.Analyzable
import org.hertsig.magic.Magic

interface Spellcasting: Analyzable {
    fun name(): String
    fun headerEntries(): List<String>
    fun ability(): String
    @Magic(elementType = SpellList::class)
    fun daily(): Map<String, SpellList>
    @Magic(elementType = SpellList::class)
    fun spells(): Map<String, SpellList>
}

interface SpellList: Analyzable {
    fun slots(): Int?
    fun spells(): List<String>
}