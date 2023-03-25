package org.hertsig.dnd.norr

interface Entry {
    // Other possible types: (table), list, inset, quote
    fun isEntry() = type() == "entries"

    fun type(): String
    fun name(): String?
    fun entries(): List<String>
}
