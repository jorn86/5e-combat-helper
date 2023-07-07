package org.hertsig.dnd.norr.spell

interface EntryList {
    fun isList() = type() == "list"

    fun type(): String
    fun items(): List<String>
}
