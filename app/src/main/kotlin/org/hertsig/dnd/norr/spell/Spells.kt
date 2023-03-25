package org.hertsig.dnd.norr.spell

import org.hertsig.magic.Magic

interface Spells {
    @Magic(elementType = NorrSpell::class)
    fun spell(): List<NorrSpell>
}
