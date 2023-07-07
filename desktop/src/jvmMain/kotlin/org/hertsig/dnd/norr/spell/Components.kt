package org.hertsig.dnd.norr.spell

import org.hertsig.magic.Magic

interface Components {
    fun display(): String {
        return listOfNotNull(
            if (verbal() == true) "v" else null,
            if (somatic() == true) "s" else null,
            material()?.let { "m" }
        ).joinToString("")
    }

    @Magic(name = "v")
    fun verbal(): Boolean?
    @Magic(name = "s")
    fun somatic(): Boolean?
    @Magic(name = "m", mapper = MaterialComponentMapper::class)
    fun material(): MaterialComponent? // String or Entry
}
