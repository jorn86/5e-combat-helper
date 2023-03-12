package org.hertsig.dnd.norr

import org.hertsig.magic.Analyzable
import org.hertsig.magic.Magic

interface Spells: Analyzable {
    @Magic(elementType = Spell::class)
    fun spell(): List<Spell>
}

interface Spell: Analyzable {
    fun name(): String
    fun source(): String
    fun page(): Int
    fun level(): Int
    fun school(): String
    fun time(): List<Any>
    fun range(): Map<String, Any>
    fun components(): Map<String, Any>
    fun duration(): List<Any>
    @Magic(elementType = Entry::class)
    fun entries(): List<Entry>
    @Magic(elementType = Entry::class)
    fun entriesHigherLevel(): List<Entry>
    fun miscTags(): List<Any>
    fun hasFluffImages(): Boolean
}
