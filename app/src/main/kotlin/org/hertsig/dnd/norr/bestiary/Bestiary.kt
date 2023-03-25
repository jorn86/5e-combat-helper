package org.hertsig.dnd.norr.bestiary

import org.hertsig.magic.Magic

interface Bestiary {
    @Magic(elementType = Monster::class)
    fun monster(): List<Monster>
}
