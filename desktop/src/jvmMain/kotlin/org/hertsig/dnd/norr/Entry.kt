package org.hertsig.dnd.norr

import org.hertsig.magic.DynamicList

interface Entry {
    // Other possible types: (table), list, inset, quote
    fun isEntry() = type() == "entries"

    fun type(): String
    fun name(): String?
    fun entries(): DynamicList
}
