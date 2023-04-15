package org.hertsig.dnd.norr.book

import org.hertsig.magic.Magic

interface ClassSpellList {
    fun type(): String
    fun name(): String
    @Magic(elementType = SpellListEntry::class)
    fun entries(): List<SpellListEntry>
}

interface SpellListEntry {
    fun type(): String
    fun name(): String
    fun items(): List<String>
}
