package org.hertsig.dnd.norr

import org.hertsig.magic.Magic

interface Bestiary {
    @Magic(elementType = Monster::class)
    fun monster(): List<Monster>
}
